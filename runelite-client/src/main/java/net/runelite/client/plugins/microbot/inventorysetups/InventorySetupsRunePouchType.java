package net.runelite.client.plugins.microbot.inventorysetups;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum InventorySetupsRunePouchType
{
	// None
	NONE(0),

	// 3 slots
	NORMAL(3),

	// 4 slots
	DIVINE(4);

	private final int size;

	private static final List<InventorySetupsRunePouchType> VALUES;

	static
	{
		VALUES = new ArrayList<>();
		Collections.addAll(VALUES, InventorySetupsRunePouchType.values());
	}

	InventorySetupsRunePouchType(int size)
	{
		this.size = size;
	}

	public int getSize()
	{
		return size;
	}

	public static List<InventorySetupsRunePouchType> getValues()
	{
		return VALUES;
	}
}
