package net.runelite.client.plugins.microbot.bankjs.development.BanksSlayer.Utils;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

public class SpecialMonsterTravelling {

    public static boolean hasSpecialTravel(String taskName) {
        switch (taskName) {
            case "Brine Rats":
                return true;
            default:
                return false;
        }
    }

    public static boolean handleSpecialTravel(String taskName) {
        switch (taskName) {
            case "Brine Rats":
                return handleBrineRats();
            default:
                return false;
        }
    }


    private static boolean handleBrineRats() {
        WorldPoint digLocation = new WorldPoint(2748, 3733, 0);

        if (Rs2Walker.walkTo(digLocation)) {
            sleep(2400,3600);
            if (Rs2Inventory.contains("Spade")) {
                Rs2Inventory.interact("Spade", "Dig");
                sleep(1200, 2400);
                return true;
            } else {
                System.out.println("No Spade in Inventory.");
            }
        } else {
            System.out.println("Failed to walk to Brine Rat Dig Location");
        }
        return false;
    }

    private static void sleep(int min, int max) {
        try {
            Thread.sleep((long) (Math.random() * (max - min) + min));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
