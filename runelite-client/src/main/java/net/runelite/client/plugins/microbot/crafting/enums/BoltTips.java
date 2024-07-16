package net.runelite.client.plugins.microbot.crafting.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BoltTips {
    NONE(" ", "", 1),
    OPAL("Opal", "Opal Bolt Tips", 11),
    JADE("Jade", "Jade Bolt Tips", 26),
    RED_TOPAZ("Red Topaz", "Red Topaz Bolt Tips", 48),
    SAPPHIRE("Sapphire", "Sapphire Bolt Tips", 56),
    EMERALD("Emerald", "Emerald Bolt Tips", 58),
    RUBY("Ruby", "Ruby Bolt Tips", 63),
    DIAMOND("Diamond", "Diamond Bolt Tips", 65),
    DRAGONSTONE("Dragonstone", "Dragonstone Bolt Tips", 71),
    ONYX("Onyx", "Onyx Bolt Tips", 73);

    private final String gemName;
    private final String boltTipName;
    private final int fletchingLevelRequired;

    @Override
    public String toString() {
        return boltTipName;
    }
}