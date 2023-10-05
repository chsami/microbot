package net.runelite.client.plugins.microbot.example;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.grandexchange.GrandExchange;

import java.util.concurrent.TimeUnit;


public class ExampleScript extends Script {
    public static double version = 1.0;

    public boolean run(ExampleConfig config) {
        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                /*
                 * Important classes:
                 * Inventory
                 * Rs2GameObject
                 * Rs2GroundObject
                 * Rs2NPC
                 * Rs2Bank
                 * etc...
                 */
             /*  WorldResult worldResult = Microbot.getWorldService().getWorlds();
                World currentWorld = worldResult.findWorld(Microbot.getClient().getWorld());
                System.out.println(currentWorld);*/
          //      System.out.println(Login.getRandomMembersWorld());
                GrandExchange.collect(true);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
        return true;
    }
}
