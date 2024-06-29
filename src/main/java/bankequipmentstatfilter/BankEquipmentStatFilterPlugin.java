package bankequipmentstatfilter;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.http.api.item.ItemStats;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.stream.Collectors;

@PluginDescriptor(
		name = "Bank Equipment Stat Filter",
		description = "Allows to filter/sort for equipment slot/stat",
		tags = {"bank", "stat", "equipment", "filter"}
)
@Slf4j
public class BankEquipmentStatFilterPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ItemManager itemManager;

	@Inject
	private ClientToolbar clientToolbar;

	private BankEquipmentStatFilterPanel panel;

	private NavigationButton navButton;

	private ItemWithStat[] items;

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getItemContainer() == client.getItemContainer(InventoryID.BANK))
		{
			Item[] bankItems = event.getItemContainer().getItems();

			items = Arrays.stream(bankItems)
					.map(item -> {
						ItemStats stats = itemManager.getItemStats(item.getId(), false);
						ItemComposition composition = itemManager.getItemComposition(item.getId());
						if (stats == null || !stats.isEquipable()) {
							return null;
						}
						return new ItemWithStat(item.getId(), stats, composition.getName());
					})
					.filter(Objects::nonNull)
					.toArray(ItemWithStat[]::new);
		}
	}

	@Override
	protected void startUp()
	{
		panel = injector.getInstance(BankEquipmentStatFilterPanel.class);

		final BufferedImage icon = ImageUtil.loadImageResource(BankEquipmentStatFilterPlugin.class, "pluginIcon.png");

		navButton = NavigationButton.builder()
				.tooltip("Bank Equipment Stat Filtering")
				.icon(icon)
				.panel(panel)
				.priority(6)
				.build();

		clientToolbar.addNavigation(navButton);
	}

	@Override
	protected void shutDown()
	{
		clientToolbar.removeNavigation(navButton);
	}

	public void bankFilter(EquipmentInventorySlot slot, EquipmentStat statType, boolean allSlots)
	{
		if (items == null) {
			panel.displayMessage("You need to open your bank once so the plugin can sync with it");
			return;
		}


		Map<Integer, List<ItemWithStat>> sortedItems = Arrays.stream(items)
				.filter(item -> getItemStat(item.getStats(), statType) > 0 && (item.getStats().getEquipment().getSlot() == slot.getSlotIdx() || allSlots))
				.collect(Collectors.groupingBy(item -> item.getStats().getEquipment().getSlot()));

		sortedItems.forEach((slotIdx, slotItems) -> {
			// Mutate the list to sort it by the stat
			slotItems.sort(Comparator.comparing(item -> getItemStat(item.getStats(), statType), Comparator.reverseOrder()));
		});
		panel.displayItems(sortedItems, statType);
	}

	public int getItemStat(ItemStats stats, EquipmentStat stat)
	{
		if (stats == null || !stats.isEquipable())
		{
			return -1;
		}
		switch (stat) {
			case STAB_ATTACK:
				return stats.getEquipment().getAstab();
			case SLASH_ATTACK:
				return stats.getEquipment().getAslash();
			case CRUSH_ATTACK:
				return stats.getEquipment().getAcrush();
			case MAGIC_ATTACK:
				return stats.getEquipment().getAmagic();
			case RANGE_ATTACK:
				return stats.getEquipment().getArange();
			case STAB_DEFENCE:
				return stats.getEquipment().getDstab();
			case SLASH_DEFENCE:
				return stats.getEquipment().getDslash();
			case CRUSH_DEFENCE:
				return stats.getEquipment().getDcrush();
			case MAGIC_DEFENCE:
				return stats.getEquipment().getDmagic();
			case RANGE_DEFENCE:
				return stats.getEquipment().getDrange();
			case MELEE_STRENGTH:
				return stats.getEquipment().getStr();
			case RANGE_STRENGTH:
				return stats.getEquipment().getRstr();
			case MAGIC_DAMAGE:
				return stats.getEquipment().getMdmg();
			case PRAYER:
				return stats.getEquipment().getPrayer();
			default:
				return -1;
		}
	}

	@Provides
	BankEquipmentStatFilterConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BankEquipmentStatFilterConfig.class);
	}
}
