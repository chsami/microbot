package net.runelite.client.plugins.microbot.util.inventory;

public enum InteractOrder {
    STANDARD,
    EFFICIENT_ROW,
    COLUMN,
    EFFICIENT_COLUMN,
    ZIGZAG,
    RANDOM;

    // return a random DropOrder
    public static InteractOrder random() {
        return values()[(int) (Math.random() * values().length - 1)];
    }
}