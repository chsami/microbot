package net.runelite.client.plugins.microbot.mining;

import net.runelite.api.GameObject;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.example.ExampleConfig;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;

import java.util.concurrent.TimeUnit;

public class MiningScript extends Script {

    public static String version = "1.0";

    public boolean run(int objectId) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {


                if (Microbot.isAnimating()) return;

                if (Inventory.isFull()) {
                    Inventory.dropAll();
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
