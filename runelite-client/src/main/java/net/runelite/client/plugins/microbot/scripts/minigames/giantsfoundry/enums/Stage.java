package net.runelite.client.plugins.microbot.scripts.minigames.giantsfoundry.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public enum Stage
{
    TRIP_HAMMER("Hammer", Heat.HIGH, 20, -25),
    GRINDSTONE("Grind", Heat.MED, 10, 15),
    POLISHING_WHEEL("Polish", Heat.LOW, 10, -17);

    private final String name;
    private final Heat heat;
    private final int progressPerAction;
    private final int heatChange;
}
