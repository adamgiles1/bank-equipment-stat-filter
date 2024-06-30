package bankequipmentstatfilter;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.AsyncBufferedImage;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Singleton
@Slf4j
public class BankEquipmentStatFilterPanel extends PluginPanel
{
    private static final int COLUMN_SIZE = 5;
    private static final int ICON_WIDTH = 36;
    private static final int ICON_HEIGHT = 32;

    @Inject
    ItemManager itemManager;

    @Inject
    BankEquipmentStatFilterPlugin plugin;

    @Inject
    BankEquipmentStatFilterConfig config;

    JPanel itemsPanel;

    JComboBox<EquipmentStat> statDropDown;

    JComboBox<EquipmentInventorySlot> slotDropDown;

    BankEquipmentStatFilterPanel()
    {
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        JPanel selectionPanel = new JPanel();
        selectionPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        selectionPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        selectionPanel.setLayout(new GridLayout(0, 2));

        // Select Stat
        JLabel statLabel = new JLabel();
        statLabel.setText("Stat for equipment: ");
        selectionPanel.add(statLabel);

        statDropDown = new JComboBox<>(EquipmentStat.values());
        statDropDown.setFocusable(false);
        selectionPanel.add(statDropDown);

        // Select Slot
        JLabel slotLabel = new JLabel();
        slotLabel.setText("Slot for equipment: ");
        selectionPanel.add(slotLabel);

        slotDropDown = new JComboBox<>(EquipmentInventorySlot.values());
        slotDropDown.setFocusable(false);
        selectionPanel.add(slotDropDown);

        // All Options at once
        JCheckBox allOptions = new JCheckBox();
        allOptions.setFocusable(false);
        allOptions.setText("Show all slots");

        allOptions.addActionListener((actionEvent) ->
        {
            if (allOptions.isSelected())
            {
                slotDropDown.setEnabled(false);
            }
            else
            {
                slotDropDown.setEnabled(true);
            }
        });
        allOptions.setSelected(true);

        selectionPanel.add(allOptions);

        // Button to search bank
        JButton filterButton = new JButton();
        filterButton.addActionListener((actionEvent) ->
        {
            plugin.bankFilter((EquipmentInventorySlot) slotDropDown.getSelectedItem(), (EquipmentStat) statDropDown.getSelectedItem(), allOptions.isSelected());
            repaint();
        });
        filterButton.setText("View Items");
        filterButton.setFocusable(false);
        selectionPanel.add(filterButton);

        add(selectionPanel, BorderLayout.NORTH);

        itemsPanel = new JPanel();
        itemsPanel.setBackground(ColorScheme.DARK_GRAY_COLOR);
        itemsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));

        add(itemsPanel);
    }

    public void displayItems(Map<Integer, List<ItemWithStat>> items, EquipmentStat statType, boolean allSlots)
    {

        itemsPanel.removeAll();

        int itemCount = 0;
        // loop through items
        for(Map.Entry<Integer, List<ItemWithStat>> entry : items.entrySet()) {
            Integer slotIdx = entry.getKey();
            List<ItemWithStat> slotItems;

            // Only apply the limit when we are showing all slots
            if (allSlots) {
                slotItems = entry.getValue()
                        .stream().limit(config.maxItemsPerSlot())
                        .collect(Collectors.toList());
            } else {
                slotItems = entry.getValue();
            }

            PaintGroup(slotItems, statType, slotIdx);
            itemCount += slotItems.size();
        }
        if (itemCount == 0){
            displayMessage("No items found.");
        }


        repaint();
        revalidate();
    }

    public EquipmentInventorySlot getEquipmentInventorySlot(int slotIdx) {
        for (EquipmentInventorySlot slot : EquipmentInventorySlot.values()) {
            if (slot.getSlotIdx() == slotIdx) {
                return slot;
            }
        }
        return null;
    }

    private void PaintGroup(List<ItemWithStat> items, EquipmentStat statType, Integer slotIdx){
        if (!items.isEmpty())
        {
            //print item names

            JPanel titlePanel = new JPanel();
            titlePanel.setLayout(new BorderLayout());

            //Get slot name
            JLabel slotLabel = new JLabel();
            slotLabel.setText(getEquipmentInventorySlot(slotIdx).name());
            slotLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Add to panel
            titlePanel.add(slotLabel, BorderLayout.CENTER);
            itemsPanel.add(titlePanel);

            JPanel itemContainer = new JPanel();
            itemContainer.setBackground(ColorScheme.DARK_GRAY_COLOR);
            itemContainer.setBorder(new EmptyBorder(10, 10, 10, 10));
            itemContainer.setLayout(new GridLayout(0, COLUMN_SIZE, 1, 1));

            for (ItemWithStat item : items) {
                JPanel itemPanel = new JPanel();
                JLabel itemLabel = new JLabel();

                itemLabel.setHorizontalAlignment(SwingConstants.CENTER);
                itemLabel.setVerticalAlignment(SwingConstants.CENTER);
                AsyncBufferedImage icon = itemManager.getImage(item.getId());
                icon.addTo(itemLabel);
                itemLabel.setSize(icon.getWidth(), icon.getHeight());
                itemLabel.setMaximumSize(new Dimension(ICON_WIDTH, ICON_HEIGHT));
                itemLabel.setToolTipText(String.format("%s (+%s)", item.getName(), plugin.getItemStat(item.getStats(), statType)));

                itemPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

                itemPanel.add(itemLabel);
                itemContainer.add(itemPanel);
            }
            if (items.size() % COLUMN_SIZE != 0)
            {
                for (int i = 0; i < COLUMN_SIZE - (items.size() % COLUMN_SIZE); i++)
                {
                    JPanel panel = new JPanel();
                    panel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
                    itemContainer.add(panel);
                }
            }

            itemsPanel.add(itemContainer);
        }
    }

    public void displayMessage(final String message)
    {
        itemsPanel.removeAll();

        final JTextArea textArea = new JTextArea();
        textArea.setText(message);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFocusable(false);
        textArea.setEditable(false);
        textArea.setBackground(ColorScheme.DARK_GRAY_COLOR);
        itemsPanel.add(textArea);

        repaint();
        revalidate();
    }
}
