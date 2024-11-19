package net.runelite.client.plugins.microbot.nateplugins.skilling.arrowmaker.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DragonTipping {
    OPAL_TIPPED_BRONZE("opal bolt tips", "dragon bolts",84),
    PEAL_TIPPED_IRON("pearl bolt tips", "dragon bolts",84),
    RED_TOPAZ_TIPPED_STEEL("red topaz bolt tips", "dragon bolts",84),
    SAPPHIRE_TIPPED_MITHRIL("sapphire bolt tips", "dragon bolts",84),
    EMERALD_TIPPED_MITHRIL("emerald bolt tips", "dragon bolts",84),
    RUBY_TIPPED_ADAMANT("ruby bolt tips", "dragon bolts",84),
    DIAMOND_TIPPED_ADAMANT("diamond bolt tips", "dragon bolts",84),
    DRAGONSTONE_TIPPED_RUNITE("dragonstone bolt tips", "dragon bolts",84),
    ONYX_TIPPED_RUNITE("onyx bolt tips", "dragon bolts",84);
    private final String item1;
    private final String item2;
    private final int lvlreq;
    @Override
    public String toString() {
        return item1;
    }
}
