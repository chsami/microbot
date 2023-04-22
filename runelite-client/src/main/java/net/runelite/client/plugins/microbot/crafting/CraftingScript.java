package net.runelite.client.plugins.microbot.crafting;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.scripts.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.math.Random;

import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;

public class CraftingScript extends Script {
    public boolean run(CraftingConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run()) return;
                if (Random.random(1, 255) == 2)
                    sleep(3000, 60000);
                String leather = "green dragon leather";
                String craftedItem = "green d'hide body";
                if (Microbot.isGainingExp) return;
                if (!Inventory.hasItem(craftedItem)) {
                    if (!Inventory.isInventoryFull()) {
                        Rs2Bank.openBank();
                        sleepUntil(() -> Rs2Bank.isBankOpen(), 5000);
                        if (!Rs2Bank.isBankOpen()) return;
                        Rs2Bank.withdrawItem(true, "needle");
                        Rs2Bank.withdrawItemsAll(true, "thread");
                        if (!Inventory.hasItem("needle") || !Inventory.hasItem("thread")) return;
                        Rs2Bank.withdrawItemsAll(leather);
                    } else if (Inventory.hasItem(leather)) {
                        Rs2Bank.closeBank();
                        Inventory.useItemOnItem("needle", leather);
                        VirtualKeyboard.keyPress(KeyEvent.VK_SPACE);
                        sleep(3000);
                    } else {
                        shutDown();
                    }
                } else {
                    Rs2Bank.openBank();
                    Rs2Bank.depositAll(craftedItem);
                    Rs2Bank.withdrawItemsAll(leather);
                    Rs2Bank.closeBank();
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                //Microbot.getNotifier().notify("Script failure");
            }

        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }

    public void shutDown() {
        super.shutdown();
    }
}
