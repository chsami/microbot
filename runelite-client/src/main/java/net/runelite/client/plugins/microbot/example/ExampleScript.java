package net.runelite.client.plugins.microbot.example;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.grounditem.LootingParameters;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;

import java.util.concurrent.TimeUnit;


public class ExampleScript extends Script {

    public static boolean test = false;
    public boolean run(ExampleConfig config) {
        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;
                long startTime = System.currentTimeMillis();

                test = false;

                LootingParameters valueParams = new LootingParameters(
                        5000,
                        Integer.MAX_VALUE,
                        20,
                        1,
                        0,
                        false,
                        false
                );

                Rs2GroundItem.loot("Vorkath's head", 20);
                if (Rs2GroundItem.lootItemBasedOnValue(valueParams)) {
                    System.out.println("Looing succesfull!");
                }
                /*for (int i = 0; i < 2; i++) {
                    System.out.println("loop:= " + i);
                    Rs2Inventory.waitForInventoryChanges();
                }*/

                long endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                System.out.println("Total time for loop " + totalTime);

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
