package net.runelite.client.plugins.microbot.qualityoflife.scripts.pouch;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

public enum Pouch {
    SMALL(new int[]{ItemID.SMALL_POUCH}, new int[]{3}, 3, 1),
    MEDIUM(new int[]{ItemID.MEDIUM_POUCH, ItemID.MEDIUM_POUCH_5511}, new int[]{6}, 3, 25),
    LARGE(new int[]{ItemID.LARGE_POUCH, ItemID.LARGE_POUCH_5513}, new int[]{9}, 7, 50),
    GIANT(new int[]{ItemID.GIANT_POUCH, ItemID.GIANT_POUCH_5515}, new int[]{12}, 9, 75),
    // degradedBaseHoldAmount for colossal pouch is dynamic, it starts at 35 and lowers
    // each time you use the degraded pouch. We'll see it to 25 to be safe
    // holdAmount -1 for colossal pouch because it is calculated based on the rc level
    COLOSSAL(new int[]{ItemID.COLOSSAL_POUCH, ItemID.COLOSSAL_POUCH_26786}, new int[]{8, 16, 27, 40}, 8, 25);


    private final int[] baseHoldAmount;

    private int getBaseHoldAmount() {
        if (this == COLOSSAL) {
            return degraded ? degradedBaseHoldAmount : baseHoldAmount[getColossalHoldAmountIndex()];
        } else {
            return degraded ? degradedBaseHoldAmount : baseHoldAmount[0];
        }
    }

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


    Pouch(int[] itemIds, int[] holdAmount, int degradedHoldAmount, int levelRequired) {
        this.itemIds = itemIds;
        this.baseHoldAmount = holdAmount;
        this.degradedBaseHoldAmount = degradedHoldAmount;
        this.levelRequired = levelRequired;
    }

    public int getHoldAmount() {
        return degraded ? degradedBaseHoldAmount : getBaseHoldAmount();
    }

    public int getRemaining() {
        final int holdAmount = degraded ? degradedBaseHoldAmount : getBaseHoldAmount();
        return holdAmount - holding;
    }

    void addHolding(int delta) {
        holding += delta;

        final int holdAmount = degraded ? degradedBaseHoldAmount : getBaseHoldAmount();
        if (holding < 0) {
            holding = 0;
        }
        System.out.println(delta + " " + holding + " " + holdAmount);
        if (holding > holdAmount) {
            holding = holdAmount;
        }
    }

    void degrade(boolean state) {
        if (state != degraded) {
            degraded = state;
            final int holdAmount = degraded ? degradedBaseHoldAmount : getBaseHoldAmount();
            holding = Math.min(holding, holdAmount);
        }
    }

    static Pouch forItem(int itemId) {
        switch (itemId) {
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
            case ItemID.COLOSSAL_POUCH:
            case ItemID.COLOSSAL_POUCH_26786:
            case ItemID.COLOSSAL_POUCH_26906:
                return COLOSSAL;
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

        if (getHolding() > 0) {
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

    public int getColossalHoldAmountIndex() {
        int runecraftLevel = Rs2Player.getBoostedSkillLevel(Skill.RUNECRAFT);

        if (runecraftLevel >= 85) {
            return 3;
        } else if (runecraftLevel >= 75) {
            return 2;
        } else if (runecraftLevel >= 50) {
            return 1;
        } else {
            return 0;
        }
    }
}