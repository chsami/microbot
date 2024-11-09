package net.runelite.client.plugins.microbot.combathotkeys;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class CombatHotkeysScript extends Script {
    public boolean isSwitchingGear = false;
    public ArrayList<Rs2Item> gearToSwitch = new ArrayList<>();

    public boolean run(CombatHotkeysConfig config) {
        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;

                if (isSwitchingGear) {
                    for (Rs2Item item : gearToSwitch) {
                        Rs2Inventory.equip(item.id);
                    }
                    isSwitchingGear = false;
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}