package net.runelite.client.plugins.microbot.sandcrabs.models;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@Getter
public class ScanLocation {
    public WorldPoint worldPoint;
    public boolean scanned;
    public int triedWalking = 0;

    public ScanLocation(WorldPoint worldPoint) {
        this.worldPoint = worldPoint;
    }

    public void reset() {
        scanned = false;
        triedWalking = 0;
    }
}