package net.runelite.client.plugins.microbot.woodcutting.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WoodcuttingTree {
    TREE("tree", 1),
    OAK_LOG("oak tree", 15),
    WILLOW("willow tree", 30),
    MAPLE("maple tree", 45),
    YEW("yew tree", 60),
    MAGIC("magic tree", 75),
    REDWOOD("redwood tree", 90);


    private final String name;
    private final int woodcuttingLevel;

    @Override
    public String toString() {
        return name;
    }
}
