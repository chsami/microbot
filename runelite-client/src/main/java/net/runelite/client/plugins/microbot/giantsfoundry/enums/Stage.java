package net.runelite.client.plugins.microbot.giantsfoundry.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@Getter
@AllArgsConstructor
public enum Stage
{
    TRIP_HAMMER("Hammer", Heat.HIGH, 20, -25, new WorldPoint(3367, 11497, 0)),
    GRINDSTONE("Grind", Heat.MED, 10, 15, new WorldPoint(3364, 11492, 0)),
    POLISHING_WHEEL("Polish", Heat.LOW, 10, -17, new WorldPoint(3365, 11485, 0));

    private final String name;
    private final Heat heat;
    private final int progressPerAction;
    private final int heatChange;
    private final WorldPoint location;

    public boolean isHeating()
    {
        return heatChange > 0;
    }

    public boolean isCooling()
    {
        return heatChange < 0;
    }
}
