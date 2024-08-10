package net.runelite.client.plugins.microbot.mining.amethyst.enums;

import lombok.Getter;

public enum AmethystCraftingOption {
    BOLT_TIPS(83, "Bolt tips", '1'),
    ARROW_TIPS(85, "Arrow tips", '2'),
    JAVELIN_HEADS(87, "Javelin heads", '3'),
    DART_TIPS(89, "Dart tips", '4');

    @Getter
    private final int requiredLevel;
    @Getter
    private final String displayName;
    private final char dialogOption;

    AmethystCraftingOption(int requiredLevel, String displayName, char dialogOption) {
        this.requiredLevel = requiredLevel;
        this.displayName = displayName;
        this.dialogOption = dialogOption;
    }

    public char getDialogOption() {
        return dialogOption;
    }

    @Override
    public String toString() {
        return displayName + " (Level " + requiredLevel + ")";
    }
}
