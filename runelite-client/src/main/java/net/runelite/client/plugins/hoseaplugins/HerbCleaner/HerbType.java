package net.runelite.client.plugins.hoseaplugins.HerbCleaner;

import lombok.Getter;

@Getter
public enum HerbType {
    GUAM("Grimy guam"),
    MARRENTILL("Grimy marrentill"),
    TARROMIN("Grimy tarromin"),
    HARRALANDER("Grimy harralander"),
    RANARR_WEED("Grimy ranarr weed"),
    TOADFLAX("Grimy toadflax"),
    IRIT("Grimy irit"),
    AVANTOE("Grimy avantoe"),
    KWUARM("Grimy kwuarm"),
    SNAPDRAGON("Grimy snapdragon"),
    CADANTINE("Grimy cadantine"),
    LANTADYME("Grimy lantadyme"),
    DWARF_WEED("Grimy dwarf weed"),
    TORSTOL("Grimy torstol");

    final String itemName;

    HerbType(String itemName) {
        this.itemName = itemName;
    }
}
