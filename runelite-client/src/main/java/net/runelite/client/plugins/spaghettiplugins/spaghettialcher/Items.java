package net.runelite.client.plugins.spaghettiplugins.spaghettialcher;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Items {
    AUTOMATIC("Automatic"),
    LIST("List"),
    BLACK_DHIDE_BODY("Black d'hide body"),
    MITHRIL_PLATEBODY("Mithril platebody"),
    ORANGE("Oranges(5)"),
    BLUE_DHIDE_BODY("Blue d'hide body"),
    RUNE_AXE("Rune axe"),
    RUNE_JAVELIN_HEADS("Rune javelin heads"),
    ADAMANT_PLATEBODY("Adamant platebody"),
    BLUE_DHIDE_VAMBRACES("Blue d'hide vambraces"),
    RED_DHIDE_BODY("Red d'hide body"),
    FIRE_BATTLESTAFF("Fire battlestaff");

    private final String name;

    @Override
    public String toString() {
        return name;
    }
}
