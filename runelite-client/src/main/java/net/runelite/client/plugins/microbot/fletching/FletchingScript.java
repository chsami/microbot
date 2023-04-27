package net.runelite.client.plugins.microbot.fletching;


import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.fletching.enums.FletchingItem;
import net.runelite.client.plugins.microbot.fletching.enums.FletchingMaterial;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.util.concurrent.TimeUnit;

public class FletchingScript extends Script {

    public static double version = 1.0;

    public void run(FletchingConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            if (!configChecks(config)) return;
            if (config.Afk() && Random.random(1, 100) == 2)
                sleep(1000, 60000);
            try {
                String itemToFletchWith = config.fletchingMode().getItemName();
                String logsToFletch = config.fletchingMaterial().getName();
                boolean hasRequirementsToFletch = Inventory.hasItem(itemToFletchWith)
                        && Inventory.findItem(logsToFletch) != null;
                boolean hasRequirementsToBank = Inventory.hasItem(itemToFletchWith)
                        && Inventory.findItem(logsToFletch) == null;

                if (!Inventory.hasItem(itemToFletchWith)) {
                    Rs2Bank.withdrawItem(itemToFletchWith);
                }

                if (hasRequirementsToFletch) {
                    fletch(config, itemToFletchWith, logsToFletch);
                }
                if (hasRequirementsToBank) {
                    bankItems(config, logsToFletch);
                }

            } catch(Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
    }

    private void bankItems(FletchingConfig config, String logsToFletch) {
        Rs2Bank.openBank();
        Rs2Bank.depositAllContains(config.fletchingItem().getContainsInventoryName());
        sleepUntilOnClientThread(() -> !Inventory.hasItemContains(config.fletchingItem().getContainsInventoryName()));
        if (!Rs2Bank.hasItem(config.fletchingMaterial().getName())) {
            Microbot.getNotifier().notify("[Shutting down] - Reason: " + config.fletchingMaterial().getName() + " not found in the bank.");
            shutdown();
            return;
        }
        Rs2Bank.withdrawItemsAll(config.fletchingMaterial().getName());
        sleepUntilOnClientThread(() -> Inventory.hasItem(logsToFletch));
        sleep(600, 3000);
        Rs2Bank.closeBank();
    }

    private void fletch(FletchingConfig config, String itemToFletchWith, String logsToFletch) {
        Inventory.useItemOnItem(itemToFletchWith, logsToFletch);
        sleepUntilOnClientThread(() -> Rs2Widget.getWidget(17694736) != null);
        keyPress(config.fletchingItem().getOption());
        sleepUntilOnClientThread(() -> Rs2Widget.getWidget(17694736) == null);
        sleepUntilOnClientThread(() -> !Inventory.hasItem(logsToFletch), 60000);
    }

    private boolean configChecks(FletchingConfig config) {
        if (config.fletchingMaterial() == FletchingMaterial.REDWOOD && config.fletchingItem() != FletchingItem.SHIELD) {
            Microbot.getNotifier().notify("[Wrong Configuration] You can only make shields with redwood logs.");
            shutdown();
            return false;
        }
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}

