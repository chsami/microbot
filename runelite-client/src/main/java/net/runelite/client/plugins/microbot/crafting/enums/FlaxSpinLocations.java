package net.runelite.client.plugins.microbot.crafting.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;

@Getter
@RequiredArgsConstructor
public enum FlaxSpinLocations {
    FALADOR(new WorldPoint(2982, 3314, 0), ObjectID.SPINNING_WHEEL_14889),
    // LUMBRIDGE_CASTLE(new WorldPoint(3209, 3213, 1), ObjectID.SPINNING_WHEEL_14889), Issue with web-walker when banking
    SEERS_VILLAGE(new WorldPoint(2711, 3471, 1), ObjectID.SPINNING_WHEEL_25824),
    RELLEKKA(new WorldPoint(2617, 3660, 0), ObjectID.SPINNING_WHEEL),
    TREE_GNOME_STRONGHOLD(new WorldPoint(2488, 3409, 1), ObjectID.SPINNING_WHEEL_14889);

    private final WorldPoint worldPoint;
    private final int objectID;
}
