package net.runelite.client.plugins.microbot.util.inventory;

public enum DropOrder {
    STANDARD,
    EFFICIENT_ROW,
    COLUMN,
    EFFICIENT_COLUMN,
    RANDOM;

    // return a random DropOrder
    public static DropOrder random() {
        return values()[(int) (Math.random() * values().length - 1)];
    }
}