package net.runelite.client.plugins.microbot.nateplugins.skilling.arrowmaker.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Bolts {
    BRONZE_BOLT("feather", "bronze bolts (unf)",9),
    IRON_BOLT("feather", "iron bolts (unf)",39),
    STEEL_BOLT("feather", "steel bolts (unf)",46),
    MITHRIL_BOLT("feather", "mithril bolts (unf)",54),
    BROAD_BOLT("feather", "unfinished broad bolts",55),
    ADAMANT_BOLT("feather", "adamant bolts (unf)",61),
    RUNE_BOLT("feather", "runite bolts (unf)",69),
    DRAGON_BOLT("feather", "dragon bolts (unf)",84);

    private final String item1;
    private final String item2;
    private final int lvlreq;
    @Override
    public String toString() {
        return item2;
    }
}