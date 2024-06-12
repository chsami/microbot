package net.runelite.client.plugins.hoseaplugins.Trapper.data;

import lombok.Getter;

public enum Salamander {

    SWAMP_LIZARD("Swamp lizard", 14133),
    ORANGE_SALAMANDER("Orange salamander", 13616),
    RED_SALAMANDER("Red salamander", 9778),
    BLACK_SALAMANDER("Black salamander", 13113);
    @Getter
    private String name;
    @Getter
    private int regionId;

    Salamander(String name, int regionId) {
        this.name = name;
        this.regionId = regionId;
    }
}
