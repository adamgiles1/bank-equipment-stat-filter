package bankequipmentstatfilter;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class BankEquipmentStatFilterPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(BankEquipmentStatFilterPlugin.class);
		RuneLite.main(args);
	}
}