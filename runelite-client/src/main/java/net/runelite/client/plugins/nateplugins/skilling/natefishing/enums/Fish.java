package net.runelite.client.plugins.nateplugins.skilling.natefishing.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.game.FishingSpot;

@Getter
@RequiredArgsConstructor
public enum Fish {
    SHRIMP("shrimp", FishingSpot.SHRIMP.getIds(),"net"),
    TROUT("trout/salmon", FishingSpot.SALMON.getIds(),"lure"),
    TUNA("tuna/swordfish", FishingSpot.LOBSTER.getIds(),"harpoon"),
    LOBSTER("lobster", FishingSpot.LOBSTER.getIds(),"cage"),
    MONKFISH("monkfish", FishingSpot.MONKFISH.getIds(),"net"),
    KARAMBWANJI("karambwanji", FishingSpot.KARAMBWANJI.getIds(), "net"),
    LAVA_EEL("lava eel", FishingSpot.LAVA_EEL.getIds(), "lure"),
    SHARK("shark", FishingSpot.SHARK.getIds(),"harpoon");

    private final String name;
    private final int[] fishingSpot;
    private final String action;

    @Override
    public String toString() {
        return name;
    }
}
