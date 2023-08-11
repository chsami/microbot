package net.runelite.client.plugins.nateplugins.natewinemaker;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.util.concurrent.TimeUnit;

public class WineScript extends Script {

    public static double version = 1.1;

    public boolean run(WineConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;

            try {
                if (Microbot.pauseAllScripts) return;
                if (Inventory.getAmountForItem("grapes") > 0 && (Inventory.getAmountForItem("jug of water") > 0)) {
                    Inventory.useItemOnItem("jug of water", "grapes");
                    sleepUntilOnClientThread(() -> Rs2Widget.getWidget(17694734) != null);
                    keyPress('1');
                    sleepUntilOnClientThread(() -> !Inventory.hasItem("jug of water"),25000);
                    return;
                } else {
                    bank();
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    private void bank(){
        Rs2Bank.openBank();
        if(Rs2Bank.isOpen()){
            Rs2Bank.depositAll();
            if(Rs2Bank.hasItem("jug of water") &&  Rs2Bank.hasItem("grapes")) {
                Rs2Bank.withdrawItemX(true, "jug of water", 14);
                sleepUntilOnClientThread(() -> Inventory.hasItem("jug of water"));
                Rs2Bank.withdrawItemX(true, "grapes", 14);
                sleepUntilOnClientThread(() -> Inventory.hasItem("grapes"));
            } else {
                Microbot.getNotifier().notify("Run out of Materials");
                shutdown();
            }
        }
        Rs2Bank.closeBank();
        sleepUntilOnClientThread(() -> !Rs2Bank.isOpen());
    }
}
