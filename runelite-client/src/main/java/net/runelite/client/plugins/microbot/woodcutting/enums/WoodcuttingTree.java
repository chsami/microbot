package net.runelite.client.plugins.microbot.woodcutting.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WoodcuttingTree {
    TREE("tree", 1),
    OAK_LOG("oak", 15),
    WILLOW("willow", 30),
    MAPLE("maple", 45),
    YEW("yew", 60),
    MAGIC("magic", 75);


    private final String name;
    private final int woodcuttingLevel;

    @Override
    public String toString() {
        return name;
    }
}
