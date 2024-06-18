package net.runelite.client.plugins.microbot.lunartanner.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Hides {

    GREEN_DRAGONHIDE("green dragonhide", "Green dragon leather"),
    BLUE_DRAGONHIDE("blue dragonhide", "Blue dragon leather"),
    RED_DRAGONHIDE("red dragonhide", "Red dragon leather"),
    BLACK_DRAGONHIDE("black dragonhide", "Black dragon leather");

    private final String name;
    @Getter
    private final String finished;

    @Override
    public String toString() {
        return name;
    }
}
