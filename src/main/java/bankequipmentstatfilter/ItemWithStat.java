package bankequipmentstatfilter;

import lombok.Value;
import net.runelite.http.api.item.ItemStats;

@Value
public class ItemWithStat
{
    int id;
    ItemStats stats;
    String name;
}
