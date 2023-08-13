package net.runelite.client.plugins.microbot.tanner.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HideType {
    LEATHER("Leather", "cow hide", "soft leather"),
    HARD_LEATHER("Hard leather", "cow hide", "hard d'hide"),
    GREEN("Green dragon leather", "green dragonhide", "green d'hide"),
    BLUE("Blue dragon leather", "blue dragonhide", "blue d'hide"),
    RED("Red dragon leather", "red dragonhide", "red d'hide"),
    BLACK("Black dragon leather", "black dragonhide", "black d'hide");

    private final String name;
    private final String itemName;
    private final String widgetName;

    @Override
    public String toString() {
        return name;
    }
}
