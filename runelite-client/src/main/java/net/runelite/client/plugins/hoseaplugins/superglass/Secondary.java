package net.runelite.client.plugins.hoseaplugins.superglass;

import net.runelite.api.ItemID;

public enum Secondary {
    SODA_ASH(ItemID.SODA_ASH, 13),
    SEAWEED(ItemID.SEAWEED, 13),
    GIANT_SEAWEED(ItemID.GIANT_SEAWEED, 18);

    Secondary(int id, int sandAmount) {
        this.id = id;
        this.sandAmount = sandAmount;
    }

    public int getId() {
        return id;
    }

    public int getSandAmount() {
        return sandAmount;
    }

    private final int id;
    private final int sandAmount;
}
