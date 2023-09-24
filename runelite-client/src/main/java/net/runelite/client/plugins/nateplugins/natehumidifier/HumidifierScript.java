package net.runelite.client.plugins.nateplugins.natehumidifier;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;

import java.util.concurrent.TimeUnit;

public class HumidifierScript extends Script {

    public static double version = 1.1;

    public boolean run(HumidifierConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;

            try {
                if (Microbot.pauseAllScripts) return;
                if (Rs2Inventory.hasItem(config.ITEM().getName())  && (Rs2Inventory.hasItemAmountStackable("Astral Rune",0))) {
                    Rs2Magic.cast(MagicAction.HUMIDIFY);
                    sleepUntilOnClientThread(() -> Rs2Inventory.hasItem(config.ITEM().getFinished()));
                    return;
                } else {
                    bank(config);
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    private void bank(HumidifierConfig config){
        if(Rs2Bank.isOpen()){
            System.out.println("finished item name: "+ config.ITEM().getFinished());
            sleep(200,400);
            Rs2Bank.depositAll(config.ITEM().getFinished());
            sleep(200,300);
            if(Rs2Bank.hasItem(config.ITEM().getName())) {
                Rs2Bank.withdrawItemsAll(true, config.ITEM().getName());
                sleepUntilOnClientThread(() -> Rs2Inventory.hasItem(config.ITEM().getName()));
                Rs2Bank.closeBank();
                sleepUntilOnClientThread(() -> !Rs2Bank.isOpen());
            } else {
                Microbot.getNotifier().notify("Run out of Materials");
                shutdown();
            }
        } else {
            Rs2Bank.openBank();
        }

    }
}
