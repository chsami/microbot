package net.runelite.client.plugins.hoseaplugins.ItemDropper;

import net.runelite.client.config.*;

@ConfigGroup(ItemDropperConfig.GROUP)
public interface ItemDropperConfig extends Config {
	String GROUP = "itemdropper";

	@ConfigItem(
			keyName = "hotkey",
			name = "Hotkey",
			description = "Hotkey to drop all selected items",
			position = 0
	)
	default Keybind getHotkey() {
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "dropIfInvFull",
			name = "Drop if inventory is full",
			description = "* Drop all selected items when the inventory is full",
			position = 1
	)
	default boolean dropIfInvFull() {
		return false;
	}

	@ConfigItem(
			keyName = "itemIds",
			name = "Item IDs or names",
			description = "<html>* Item IDs or names of the items you want to drop<br>" +
					"* Names support wildcards (i.e. *karam* or *salmon" +
					"* Separate with comma or comma and space</html>",
			position = 2
	)
	default String itemIdsOrNames() {
		return "100, 200, trout, *salmon";
	}

	@Range(
			min = 2,
			max = 10
	)
	@ConfigItem(
			keyName = "maxPerTick",
			name = "Max items per tick",
			description = "<html>* The max amount of items to drop each tick<br>" +
					"* The minimum amount is always 2</html>",
			position = 3
	)
	default int maxPerTick() {
		return 10;
	}
}
