package net.runelite.client.plugins.microbot.inventorysetups;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
* Enum that determines which setups show up when the "show worn items" button is right clicked
*/
public enum InventorySetupsShowWornItemsFilterID
{
	// Show all. This is the default
	All(0),

	// Show only the setups that are bank filtered
	BANK_FILTERED(1),

	// Show only the setups that favorited
	FAVORITED(2);

	private final int type;

	private static final List<InventorySetupsShowWornItemsFilterID> VALUES;

	static
	{
		VALUES = new ArrayList<>();
		Collections.addAll(VALUES, InventorySetupsShowWornItemsFilterID.values());
	}

	InventorySetupsShowWornItemsFilterID(int type)
	{
		this.type = type;
	}

	public int getType()
	{
		return type;
	}

	public static List<InventorySetupsShowWornItemsFilterID> getValues()
	{
		return VALUES;
	}
}
