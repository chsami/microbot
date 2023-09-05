package net.runelite.client.plugins.microbot.woodcutting;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.concurrent.TimeUnit;

public class WoodcuttingScript  extends Script {

    public static double version = 1.1;

    public boolean run(WoodcuttingConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                if (Rs2Equipment.hasEquipped("Dragon axe"))
                    Rs2Player.toggleSpecialAttack(1000);
                if (Microbot.isWalking() || Microbot.isAnimating() || Microbot.pauseAllScripts) return;
                if (Inventory.isFull()) {
                    if (config.hasAxeInventory()) {
                        Inventory.dropAllStartingFrom(1);
                    } else {
                        Inventory.dropAll();
                    }
                    return;
                }
                Rs2GameObject.interact(config.TREE().getName(), config.TREE().getAction(), true);
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }
}
