package net.runelite.client.plugins.nateplugins.moneymaking.natehumidifier;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
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
                if (Inventory.hasItem(config.ITEM().getName())  && (Inventory.hasItemAmountStackable("Astral Rune",0))) {
                    Rs2Magic.cast(MagicAction.HUMIDIFY);
                    sleepUntilOnClientThread(() -> Inventory.hasItem(config.ITEM().getFinished()));
                } else {
                    bank(config);
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
        return true;
    }

    private void bank(HumidifierConfig config){
        if(Rs2Bank.isOpen()){
            System.out.println("finished item name: "+ config.ITEM().getFinished());
            Rs2Bank.depositAll(config.ITEM().getFinished());
            sleepUntil(() -> !Inventory.hasItem(config.ITEM().getFinished()));
            if(Rs2Bank.hasItem(config.ITEM().getName())) {
                Rs2Bank.withdrawItemsAll(true, config.ITEM().getName());
                sleepUntilOnClientThread(() -> Inventory.hasItem(config.ITEM().getName()));
                Rs2Bank.closeBank();
                sleepUntilOnClientThread(() -> !Rs2Bank.isOpen());
            } else {
                Microbot.showMessage("Ran out of Materials");
                shutdown();
            }
        } else {
            Rs2Bank.openBank();
        }

    }
}
