package net.runelite.client.plugins.microbot.nateplugins.skilling.arrowmaker.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Darts {
    BRONZE_DART("feather", "bronze dart tip",10),
    IRON_DART("feather", "iron dart tip",22),
    STEEL_DART("feather", "steel dart tip",37),
    MITHRIL_DART("feather", "mithril dart tip",52),
    ADAMANT_DART("feather", "adamant dart tip",67),
    RUNE_DART("feather", "rune dart tip",81),
    AMETHYST_DART("feather", "amethyst dart tip",90),
    DRAGON_DART("feather", "dragon dart tip",95);

    private final String item1;
    private final String item2;
    private final int lvlreq;

    @Override
    public String toString() {
        return item2;
    }
}
