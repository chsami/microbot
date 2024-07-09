package net.runelite.client.plugins.inventorysetups;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Prayer;
import net.runelite.client.plugins.microbot.util.prayer.Rs2PrayerEnum;

import java.awt.*;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class InventorySetup implements InventorySetupsDisplayAttributes
{
	@Getter
	private List<InventorySetupsItem> inventory;

	@Getter
	private List<InventorySetupsItem> equipment;

	@Getter
	private List<InventorySetupsItem> rune_pouch;

	@Getter
	private List<InventorySetupsItem> boltPouch;

	@Getter
	private Map<Integer, InventorySetupsItem> additionalFilteredItems;

	@Getter
	private List<QuickPrayerSetup> quickPrayers;

	@Getter
	@Setter
	private String name;

	@Getter
	@Setter
	private String notes;

	@Getter
	@Setter
	private Color highlightColor;

	@Getter
	@Setter
	private boolean highlightDifference;

	@Getter
	@Setter
	private Color displayColor;

	@Getter
	@Setter
	private boolean filterBank;

	@Getter
	@Setter
	private boolean unorderedHighlight;

	/*
        0 = Standard
        1 = Ancient
        2 = Lunar
        3 = Arceuus
        4 = NONE

        Avoiding Enum because won't work well with GSON (defaults to null)
    */
	@Getter
	@Setter
	private int spellBook;

	@Getter
	@Setter
	private boolean favorite;

	@Getter
	@Setter
	private int iconID;

	public void updateInventory(final List<InventorySetupsItem> inv)
	{
		inventory = inv;
	}

	public void updateEquipment(final List<InventorySetupsItem> eqp)
	{
		equipment = eqp;
	}

	public void updateRunePouch(final List<InventorySetupsItem> rp)
	{
		rune_pouch = rp;
	}

	public void updateBoltPouch(final List<InventorySetupsItem> bp)
	{
		boltPouch = bp;
	}

	public void updateAdditionalItems(final Map<Integer, InventorySetupsItem> ai)
	{
		additionalFilteredItems = ai;
	}

	public void updateSpellbook(final int sb)
	{
		spellBook = sb;
	}

	public void updateQuickPrayers(final List<QuickPrayerSetup> qp)
	{
		quickPrayers = qp;
	}

	public void updateNotes(final String text)
	{
		notes = text;
	}
}
