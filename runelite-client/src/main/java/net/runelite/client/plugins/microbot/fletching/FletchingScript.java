package net.runelite.client.plugins.microbot.fletching;


import net.runelite.api.NPC;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.MicrobotConfig;
import net.runelite.client.plugins.microbot.fletching.enums.FletchingItem;
import net.runelite.client.plugins.microbot.fletching.enums.FletchingMaterial;
import net.runelite.client.plugins.microbot.fletching.enums.FletchingMode;
import net.runelite.client.plugins.microbot.scripts.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;

public class FletchingScript extends Script {



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
                if (hasRequirementsToFletch) {
                    Inventory.useItemOnItem(itemToFletchWith, logsToFletch);
                    sleepUntilOnClientThread(() -> Rs2Widget.getWidget(17694736) != null);
                    keyPress((char) config.fletchingItem().getOption());
                    sleepUntilOnClientThread(() -> Rs2Widget.getWidget(17694736) == null);
                    sleepUntilOnClientThread(() -> !Inventory.hasItem(logsToFletch), 60000);
                }
                if (hasRequirementsToBank) {
                    NPC npc = Rs2Npc.getNpc("banker");
                    if (!depositAndWithdrawItems(npc)) return;
                    sleepUntilOnClientThread(() -> Inventory.hasItem(logsToFletch));
                    sleep(600, 3000);
                    Rs2Bank.closeBank();
                    sleep(600, 4000);
                }
            } catch(Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
    }

    private boolean configChecks(FletchingConfig config) {
        if (config.fletchingMaterial() == FletchingMaterial.REDWOOD && config.fletchingItem() != FletchingItem.SHIELD) {
            Microbot.getNotifier().notify("[Wrong Configuration] You can only make shields with redwood logs.");
            shutdown();
            return false;
        }
        return true;
    }

    private boolean depositAndWithdrawItems(NPC npc) {
        if (npc == null) return false;
        Microbot.getMouse().click(npc.getCanvasTilePoly().getBounds());
        sleepUntilOnClientThread(() -> Microbot.getClient().getWidget(786445) != null);
        //deposit
        Inventory.useItemSlot(2);
        sleep(600, 1200);
        // withdraw
        int bankItemWidget = 786445;
        Rs2Widget.clickChildWidget(bankItemWidget, 17);
        return true;
    }

    private boolean depositAndWithdrawItemsForBowString(NPC npc) {
        if (!depositAndWithdrawItems(npc)) return false;

        sleep(600, 3000);
        int bankItemWidget = 786445;
        Rs2Widget.clickChildWidget(bankItemWidget, 18);
        return true;
    }

    public void runBowstrings(MicrobotConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            super.run();
            boolean hasRequirementsToFletch = Inventory.findItem(config.PrimaryFletchItem()) != null
                    && Inventory.findItem(config.logsToFletch()) != null;
            boolean hasRequirementsToBank = Inventory.findItem(config.PrimaryFletchItem()) == null
                    && Inventory.findItem(config.logsToFletch()) == null;

            if (hasRequirementsToFletch) {
                Inventory.useItem(config.PrimaryFletchItem());
                Inventory.useItem(config.logsToFletch());
                sleepUntilOnClientThread(() -> Rs2Widget.getWidget(17694736) != null);
                VirtualKeyboard.keyPress(KeyEvent.VK_SPACE);
                sleepUntilOnClientThread(() -> Rs2Widget.getWidget(17694736) == null);
                sleepUntilOnClientThread(() -> Inventory.findItem(config.logsToFletch()) == null, 20000);
            }
            if (hasRequirementsToBank) {
                NPC npc = Rs2Npc.getNpc("banker");
                if (!depositAndWithdrawItemsForBowString(npc)) return;
                sleepUntilOnClientThread(() -> Inventory.findItem(config.logsToFletch()) == null);
                sleep(600, 3000);
                //close bank
                Rs2Bank.closeBank();
                sleep(600, 4000);
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}

