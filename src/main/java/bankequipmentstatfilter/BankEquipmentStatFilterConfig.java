package bankequipmentstatfilter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("bankequipmentstatfilter")
public interface BankEquipmentStatFilterConfig extends Config
{
    @ConfigItem(
            keyName = "maxItemsPerSlot",
            name = "Max Items Per Slot",
            description = "Specify the maximum number of items displayed per slot"
    )
    default int maxItemsPerSlot()
    {
        return 7; // Default value
    }
}