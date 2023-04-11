package net.runelite.client.plugins.microbot.scripts.fletching;

import net.runelite.api.NPC;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.MicrobotConfig;
import net.runelite.client.plugins.microbot.scripts.Scripts;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.npc.Npc;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;

public class Fletcher extends Scripts {
    public void run(MicrobotConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            super.run();
            String knife = config.PrimaryFletchItem();
            String logsToFletch = config.logsToFletch();
            if (Random.random(1, 100) == 2)
                sleep(1000, 60000);

            boolean hasRequirementsToFletch = Inventory.findItem(knife) != null
                    && Inventory.findItem(logsToFletch) != null;
            boolean hasRequirementsToBank = Inventory.findItem(knife) != null
                    && Inventory.findItem(logsToFletch) == null;
            if (hasRequirementsToFletch) {
                Inventory.useItem(knife);
                sleep(600, 1200);
                Inventory.useItem(logsToFletch);
                sleepUntilOnClientThread(() -> Rs2Widget.getWidget(17694736) != null);
                VirtualKeyboard.keyHold(KeyEvent.VK_SPACE);
                VirtualKeyboard.keyRelease(KeyEvent.VK_SPACE);
                sleepUntilOnClientThread(() -> Rs2Widget.getWidget(17694736) == null);
                sleepUntilOnClientThread(() -> Inventory.findItem(logsToFletch) == null, 60000);
            }
            if (hasRequirementsToBank) {
                NPC npc = Npc.getNpc("banker");
                if (!depositAndWithdrawItems(npc)) return;
                sleepUntilOnClientThread(() -> Inventory.findItem(logsToFletch) == null);
                sleep(600, 3000);
                //close bank
                int closeWidget = 786434;
                Rs2Widget.clickChildWidget(closeWidget, 11);
                sleepUntilOnClientThread(() -> Rs2Widget.getWidget(786445) == null);
                sleep(600, 4000);
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
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
                NPC npc = Npc.getNpc("banker");
                if (!depositAndWithdrawItemsForBowString(npc)) return;
                sleepUntilOnClientThread(() -> Inventory.findItem(config.logsToFletch()) == null);
                sleep(600, 3000);
                //close bank
                int closeWidget = 786434;
                Rs2Widget.clickChildWidget(closeWidget, 11);
                sleepUntilOnClientThread(() -> Rs2Widget.getWidget(786445) == null);
                sleep(600, 4000);
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
