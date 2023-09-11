package net.runelite.client.plugins.microbot.crafting.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Activities {
    NONE("None"),
    // TODO: Should refactor the default option, now this runs a leather script
    DEFAULT("Default script"),
    GEM_CUTTING("Cutting gems"),
    GLASSBLOWING("Glassblowing");

    private final String name;

    @Override
    public String toString()
    {
        return name;
    }
}
