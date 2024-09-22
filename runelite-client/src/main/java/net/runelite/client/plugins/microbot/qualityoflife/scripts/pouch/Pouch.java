package net.runelite.client.plugins.microbot.qualityoflife.scripts.pouch;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.Arrays;

public enum Pouch
{
	SMALL(new int[] {ItemID.SMALL_POUCH}, 3, 3, 1),
	MEDIUM(new int[] { ItemID.MEDIUM_POUCH, ItemID.MEDIUM_POUCH_5511}, 6, 3, 25),
	LARGE(new int[] {ItemID.LARGE_POUCH, ItemID.LARGE_POUCH_5513}, 9, 7, 50),
	GIANT(new int[] {ItemID.GIANT_POUCH, ItemID.GIANT_POUCH_5515}, 12, 9, 75);

	private final int baseHoldAmount;
	private final int degradedBaseHoldAmount;

	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private int[] itemIds;
	@Getter(AccessLevel.PUBLIC)
	@Setter(AccessLevel.PACKAGE)
	private int holding;
	@Getter(AccessLevel.PACKAGE)
	private boolean degraded;
	@Getter(AccessLevel.PUBLIC)
	@Setter(AccessLevel.PACKAGE)
	private boolean unknown = true;
	@Getter(AccessLevel.PACKAGE)
	@Setter(AccessLevel.PACKAGE)
	private int levelRequired;


	Pouch(int[] itemIds, int holdAmount, int degradedHoldAmount, int levelRequired)
	{
		this.itemIds = itemIds;
		this.baseHoldAmount = holdAmount;
		this.degradedBaseHoldAmount = degradedHoldAmount;
		this.levelRequired = levelRequired;
	}

	public int getHoldAmount()
	{
		return degraded ? degradedBaseHoldAmount : baseHoldAmount;
	}

	public int getRemaining()
	{
		final int holdAmount = degraded ? degradedBaseHoldAmount : baseHoldAmount;
		return holdAmount - holding;
	}

	void addHolding(int delta)
	{
		holding += delta;

		final int holdAmount = degraded ? degradedBaseHoldAmount : baseHoldAmount;
		if (holding < 0)
		{
			holding = 0;
		}
		if (holding > holdAmount)
		{
			holding = holdAmount;
		}
	}

	void degrade(boolean state)
	{
		if (state != degraded)
		{
			degraded = state;
			final int holdAmount = degraded ? degradedBaseHoldAmount : baseHoldAmount;
			holding = Math.min(holding, holdAmount);
		}
	}

	static Pouch forItem(int itemId)
	{
		switch (itemId)
		{
			case ItemID.SMALL_POUCH:
				return SMALL;
			case ItemID.MEDIUM_POUCH:
			case ItemID.MEDIUM_POUCH_5511:
				return MEDIUM;
			case ItemID.LARGE_POUCH:
			case ItemID.LARGE_POUCH_5513:
				return LARGE;
			case ItemID.GIANT_POUCH:
			case ItemID.GIANT_POUCH_5515:
				return GIANT;
			default:
				return null;
		}
	}

	public boolean fill() {
		if (!hasRequiredRunecraftingLevel()) return false;
		if (!hasItemsToFillPouch()) return false;
		if (!hasPouchInInventory()) return false;

		if (getRemaining() > 0) {
			for (int i = 0; i < itemIds.length; i++) {
				if (Rs2Inventory.interact(itemIds[i], "fill"))
					return true;
			}
		}

		return false;
	}

	public boolean empty() {
		if (!hasRequiredRunecraftingLevel()) return false;
		if (!hasPouchInInventory()) return false;

		if (getHolding() > 0)  {
			for (int i = 0; i < itemIds.length; i++) {
				if (Rs2Inventory.interact(itemIds[i], "empty"))
					return true;
			}
		}

		return false;
	}

	public boolean check() {
		if (!hasRequiredRunecraftingLevel()) return false;

		for (int i = 0; i < itemIds.length; i++) {
			if (Rs2Inventory.interact(itemIds[i], "check"))
				return true;
		}

		return false;
	}

	public boolean hasRequiredRunecraftingLevel() {
		return Rs2Player.getSkillRequirement(Skill.RUNECRAFT, getLevelRequired());
	}

	public boolean hasItemsToFillPouch() {
		return Rs2Inventory.hasItem(ItemID.PURE_ESSENCE) || Rs2Inventory.hasItem(ItemID.DAEYALT_ESSENCE) || Rs2Inventory.hasItem(ItemID.GUARDIAN_ESSENCE);
	}

	public boolean hasPouchInInventory() {
		return Rs2Inventory.hasItem(itemIds);
	}

	public boolean isDegraded() {
		return degraded;
	}
}