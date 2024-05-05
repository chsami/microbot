package net.runelite.client.plugins.microbot.woodcutting.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WoodcuttingTree {
    TREE("tree", 1, "Chop down"),
    OAK("oak tree", 15, "Chop down"),
    WILLOW("willow tree", 30, "Chop down"),
    TEAK_TREE("teak tree", 35, "Chop down"),
    MAPLE("maple tree", 45, "Chop down"),
    MAHOGANY("mahogany tree", 50, "Chop down"),
    YEW("yew tree", 60, "Chop down"),
    MAGIC("magic tree", 75, "Chop down"),
    REDWOOD("redwood tree", 90, "Cut");


    private final String name;
    private final int woodcuttingLevel;
    private final String action;

    @Override
    public String toString() {
        return name;
    }
}
