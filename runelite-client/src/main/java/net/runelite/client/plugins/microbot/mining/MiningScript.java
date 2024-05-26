package net.runelite.client.plugins.microbot.mining;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;

import java.util.concurrent.TimeUnit;

public class MiningScript extends Script {

    public static String version = "1.0";

    public boolean run(int objectId) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;

                if (Microbot.isAnimating()) return;

                if (Rs2Inventory.isFull()) {
                    Rs2Inventory.dropAll();
                    return;
                }

                if (Rs2GameObject.interact(objectId)) {
                    Microbot.status = "Wait for animation";
                    sleepUntil(() -> Microbot.isAnimating());
                    Microbot.status = "Wait for animation to stop";
                    sleepUntil(() -> !Microbot.isAnimating());
                }

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }
}
