package net.runelite.client.plugins.microbot.nateplugins.skilling.arrowmaker.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Tipping {
    OPAL_TIPPED_BRONZE("opal bolt tips", "bronze bolts",11),
    PEAL_TIPPED_IRON("pearl bolt tips", "iron bolts",41),
    RED_TOPAZ_TIPPED_STEEL("red topaz bolt tips", "steel bolts",48),
    BARBED_TIPPED_BRONZE("barb bolttips", "bronze bolts",51),
    SAPPHIRE_TIPPED_MITHRIL("sapphire bolt tips", "mithril bolts",56),
    EMERALD_TIPPED_MITHRIL("emerald bolt tips", "mithril bolts",58),
    RUBY_TIPPED_ADAMANT("ruby bolt tips", "adamant bolts",63),
    DIAMOND_TIPPED_ADAMANT("diamond bolt tips", "adamant bolts",65),
    DRAGONSTONE_TIPPED_RUNITE("dragonstone bolt tips", "runite bolts",71),
    ONYX_TIPPED_RUNITE("onyx bolt tips", "runite bolts",73),
    AMETHYST_TIPPED_BROAD("amethyst bolt tips", "broad bolts",76);

    private final String item1;
    private final String item2;
    private final int lvlreq;
    @Override
    public String toString() {
        return item1;
    }
}
