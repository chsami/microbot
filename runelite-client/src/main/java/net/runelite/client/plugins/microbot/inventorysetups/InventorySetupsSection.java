package net.runelite.client.plugins.microbot.inventorysetups;

import java.awt.Color;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

// A section is a collection of inventory setups that show up when setups are sorted by section
public class InventorySetupsSection implements InventorySetupsDisplayAttributes
{

	public InventorySetupsSection(final String name)
	{
		this.name = name;
		this.displayColor = null;
		this.isMaximized = false;
		setups = new ArrayList<>();
	}

	@Getter
	@Setter
	private String name;

	// The list of setups
	@Getter
	@Setter
	private List<String> setups;

	@Getter
	@Setter
	private Color displayColor;

	// Whether the section is maximized (showing the collection of setups or not)
	@Getter
	@Setter
	private boolean isMaximized;
}
