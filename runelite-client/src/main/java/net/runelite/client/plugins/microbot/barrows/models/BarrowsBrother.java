package net.runelite.client.plugins.microbot.barrows.models;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.barrows.enums.STATE;
import net.runelite.client.plugins.microbot.bossassist.models.PRAYSTYLE;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;

public class BarrowsBrother {

    public int id;
    public PRAYSTYLE praystyle;

    public WorldPoint location;

    public boolean isDead = false;

    public STATE stateToExecute;


    public BarrowsBrother(int id, PRAYSTYLE praystyle, WorldPoint location, STATE state) {
        this.id = id;
        this.praystyle = praystyle;
        this.location = location;
        this.stateToExecute = state;
    }

}
