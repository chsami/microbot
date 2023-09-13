package net.runelite.client.plugins.nateplugins.cluehunter;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;

import java.util.concurrent.TimeUnit;

public class ClueScript extends Script {

    public static double version = 1.1;

    WorldPoint ClueAreaGloves = new WorldPoint(2579,3378,0);
    WorldPoint ClueAreaCloak = new WorldPoint(2614,3064,0);
    WorldPoint ClueAreaHelm = new WorldPoint(2590,3231,0);
    private State getState() {
        if (!Inventory.contains("Spade") && !Inventory.contains("Nature rune") && !Inventory.contains("leather boots") && !Inventory.contains("Superantipoison (1)"))
            return State.ERROR;
        if (!Inventory.contains("Clue hunter gloves") && !Inventory.contains("Clue hunter boots"))
            return State.CLUE1;
        if (!Inventory.contains("Clue hunter cloak"))
            return State.CLUE2;
        if (!Inventory.contains("Helm of raedwald"))
            return State.CLUE3;
        return State.DONE;
    }
    public boolean run(ClueConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                if (Microbot.pauseAllScripts) return;

                switch (getState()) {
                    case ERROR:
                        System.out.println("Error - Make sure you have all the required items- Spade,Nature Runes, Leather Boots, SuperAntiPoison (1)");
                        break;
                    case CLUE1:
                        if (Microbot.getClient().getLocalPlayer().getWorldLocation() == ClueAreaGloves) {
                            Inventory.useItemAction("Spade","Dig");
                            sleep(1000, 2000);
                        } else {
                            Microbot.getWalker().hybridWalkTo(ClueAreaGloves);
                        }
                        break;
                    case CLUE2:
                        if (Microbot.getClient().getLocalPlayer().getWorldLocation() == ClueAreaCloak) {
                            Inventory.useItemAction("Spade","Dig");
                            sleep(1000, 2000);
                        } else {
                            Microbot.getWalker().hybridWalkTo(ClueAreaCloak);
                        }
                        break;
                    case CLUE3:
                        if (Microbot.getClient().getLocalPlayer().getWorldLocation() == ClueAreaHelm) {
                            Inventory.useItemAction("Spade","Dig");
                            sleep(1000, 2000);
                        } else {
                            Microbot.getWalker().hybridWalkTo(ClueAreaHelm);
                        }
                        break;
                    case DONE:
                        System.out.println("Got 4 pieces of warm gear");
                        shutdown();
                        break;
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

}
