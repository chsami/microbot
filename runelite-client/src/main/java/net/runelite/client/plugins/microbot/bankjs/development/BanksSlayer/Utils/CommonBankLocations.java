package net.runelite.client.plugins.microbot.bankjs.development.BanksSlayer.Utils;

import net.runelite.api.coords.WorldPoint;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

public class CommonBankLocations {
    // Define bank locations
    public static final WorldPoint Grand_Exchange = new WorldPoint(3162, 3490, 0);
    public static final WorldPoint Edgeville = new WorldPoint(3096, 3494, 0);
    //public static final WorldPoint Tree_Gnome_Village = new WorldPoint(3429, 3523, 0);
    //public static final WorldPoint Lumbridge = new WorldPoint(2699, 3331, 0);
    public static final WorldPoint Kourend = new WorldPoint(3230, 3296, 0);
    public static final WorldPoint Varrock_West = new WorldPoint(3184, 3436, 0);
    public static final WorldPoint Tzhaar = new WorldPoint(2445, 5180, 0);

    // Method to get all bank locations
    public static List<WorldPoint> getAllBankLocations() {
        return Arrays.asList(Grand_Exchange, Edgeville, Kourend, Varrock_West, Tzhaar);
    }

    // Method to find the closest bank to the player location
    public static WorldPoint findClosestBank(WorldPoint playerLocation) {

        if (playerLocation == null) {
            System.out.println("Player location is null. Cannot find the closest bank.");
            return null;
        }

        System.out.println("Finding closest bank to player location: " + playerLocation);

        WorldPoint closestBank = getAllBankLocations().stream()
                .peek(bank -> System.out.println("Considering bank location: " + bank + " at distance: " + bank.distanceTo(playerLocation)))
                .min(Comparator.comparingInt(bank -> bank.distanceTo(playerLocation)))
                .orElse(null);

        if (closestBank != null) {
            System.out.println("Closest bank found: " + closestBank);
        } else {
            System.out.println("No bank locations found.");
        }

        return closestBank;
    }
}
