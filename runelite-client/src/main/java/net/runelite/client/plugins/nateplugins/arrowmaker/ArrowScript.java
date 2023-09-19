package net.runelite.client.plugins.nateplugins.arrowmaker;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.util.concurrent.TimeUnit;

public class ArrowScript extends Script {

    public static double version = 1.0;

    public boolean run(ArrowConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;

            try {
                if (Microbot.pauseAllScripts) return;
                if (Inventory.getAmountForItem("feather") > 0 && (Inventory.getAmountForItem("arrow shaft") > 0)) {
                    Inventory.useItemOnItem("feather", "arrow shaft");
                    sleepUntilOnClientThread(() -> Rs2Widget.getWidget(17694733) != null);
                    keyPress('1');
                    sleep(5000,7500);
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }


}
