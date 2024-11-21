package net.runelite.client.plugins.microbot.inventorysetups;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InventorySetupsPanelViewID
{
	STANDARD("STANDARD", 0),
	COMPACT("COMPACT", 1),
	ICON("ICON", 1);

	@Override
	public String toString()
	{
		return name;
	}

	private static final List<InventorySetupsPanelViewID> VALUES;

	static
	{
		VALUES = new ArrayList<>();
		Collections.addAll(VALUES, InventorySetupsPanelViewID.values());
	}

	public static List<InventorySetupsPanelViewID> getValues()
	{
		return VALUES;
	}

	private final String name;
	private final int identifier;
}
