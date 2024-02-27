package net.runelite.client.plugins.microbot.example;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;

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
               // Rs2Bank.withdrawAll("void ranger helm");
              //  Rs2Bank.wearItem(ItemID.VOID_RANGER_HELM);
                Rs2Inventory.interact("super antifire");
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1500, TimeUnit.MILLISECONDS);
        return true;
    }
}
