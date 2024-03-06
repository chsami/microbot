package net.runelite.client.plugins.microbot.vorkath;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CROSSBOW {
    DRAGON_HUNTER_CROSSBOW("Dragon hunter crossbow"),
    RUNE_CROSSBOW("Rune crossbow"),
    DRAGON_CROSSBOW("Dragon crossbow");

    private final String crossbowName;

    @Override
    public String toString() { return crossbowName; }
}
