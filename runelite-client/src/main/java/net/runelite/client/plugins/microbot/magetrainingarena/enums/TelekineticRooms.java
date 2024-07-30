package net.runelite.client.plugins.microbot.magetrainingarena.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

@Getter
@AllArgsConstructor
public enum TelekineticRooms {
    A(new WorldArea(3324, 9697, 34, 30, 0), new WorldPoint(3334, 9718, 0), new WorldPoint(3339, 9713, 0)),
    B(new WorldArea(3358, 9704, 34, 23, 0), new WorldPoint(3379, 9716, 0), new WorldPoint(3373, 9716, 0)),
    C(new WorldArea(3327, 9669, 31, 27, 0), new WorldPoint(3352, 9690, 0), new WorldPoint(3346, 9683, 0)),
    D(new WorldArea(3361, 9664, 28, 39, 0), new WorldPoint(3373, 9696, 0), new WorldPoint(3373, 9681, 0)),
    E(new WorldArea(3331, 9702, 37, 24, 1), new WorldPoint(3362, 9713, 1), new WorldPoint(3349, 9713, 1)),
    F(new WorldArea(3368, 9704, 22, 22, 1), new WorldPoint(3377, 9706, 1), new WorldPoint(3382, 9714, 1)),
    G(new WorldArea(3332, 9671, 35, 29, 1), new WorldPoint(3355, 9693, 1), new WorldPoint(3354, 9688, 1)),
    H(new WorldArea(3367, 9669, 25, 34, 1), new WorldPoint(3382, 9698, 1), new WorldPoint(3381, 9685, 1)),
    I(new WorldArea(3332, 9696, 42, 31, 2), new WorldPoint(3359, 9701, 2), new WorldPoint(3351, 9710, 2)),
    J(new WorldArea(3331, 9667, 45, 27, 2), new WorldPoint(3368, 9680, 2), new WorldPoint(3347, 9679, 2));

    private final WorldArea area;
    private final WorldPoint exit;
    private final WorldPoint maze;
}
