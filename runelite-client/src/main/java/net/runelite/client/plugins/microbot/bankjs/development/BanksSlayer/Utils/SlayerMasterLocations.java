package net.runelite.client.plugins.microbot.bankjs.development.BanksSlayer.Utils;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.bankjs.development.BanksSlayer.enums.SlayerMasters;

public class SlayerMasterLocations {
    public static WorldPoint Turael = new WorldPoint(2931, 3535, 0); // Example location for Turael
    public static WorldPoint Steve = new WorldPoint(2433, 3423, 0); // Example location for Steve
    public static WorldPoint Nieve = new WorldPoint(2433, 3423, 0); // Example location for Nieve

    public static WorldPoint getLocation(SlayerMasters slayerMaster) {
        switch (slayerMaster) {
            case TURAEL:
                return Turael;
            case STEVE:
                return Steve;
            case NIEVE:
                return Nieve;
            default:
                return null;
        }
    }
}
