package net.runelite.client.plugins.microbot.thieving.summergarden;

import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;

import java.util.concurrent.TimeUnit;

public class SummerGardenScript extends Script {

    public static double version = 1.0;

    public static WorldPoint startingPosition = new WorldPoint(2910, 5481, 0);

    @Override
    public boolean run() {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {

                if (!Inventory.hasItem("beer glass") || !Inventory.hasItem("Pestle and mortar")) {
                    Microbot.showMessage("You need a pestle and mortar and beer glass.");
                }

                if (Inventory.hasItemAmount("Summer sq'irk", 2)) {
                    Inventory.useItem("Pestle and mortar");
                    Inventory.useItem("Summer sq'irk");
                }

                if (Microbot.getClient().getLocalPlayer().getWorldLocation().equals(startingPosition)) {
                    if (ElementalCollisionDetector.getTicksUntilStart() == 0) {
                        Rs2GameObject.interact(12943);
                        sleepUntil(() -> Microbot.isWalking());
                        sleepUntil(() -> !Microbot.isWalking(), 30000);
                        sleepUntilOnClientThread(() ->Microbot.getClient().getLocalPlayer().getWorldLocation().getY() < 5481);
                        sleep(1500);//caught or success timeout
                    }
                    return;
                }

                if (Microbot.getClient().getLocalPlayer().getWorldLocation().getY() >= 5481) return;

                TileObject gate = Rs2GameObject.findObjectById(ObjectID.GATE_11987);

                if (gate != null) {
                    Rs2GameObject.interact(gate);
                    sleepUntil(() -> Microbot.isWalking());
                    sleepUntil(() -> !Microbot.isWalking());
                    sleepUntilOnClientThread(() -> Microbot.getClient().getLocalPlayer().getWorldLocation().equals(startingPosition));
                }


            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
