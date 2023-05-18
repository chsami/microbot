package net.runelite.client.plugins.microbot.farming.tithefarm.farming.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FarmingMaterial {
    OAK_TREE("Oak trees", "oak sapling", "tomatoes(5)"),
    WILLOW_TREE("Willow trees", "willow sapling", "dunno"),
    MAPLE_TREE("Maple trees", "maple sapling", "dunno");


    private final String name;
    private final String itemName;
    private final String protectionItem;

    @Override
    public String toString() {
        return name;
    }
}
