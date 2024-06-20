package net.runelite.client.plugins.microbot.mining;

import net.runelite.api.GameObject;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AutoMiningScript extends Script {

    public static String version = "1.4.0";

    public boolean run(AutoMiningConfig config) {
        initialPlayerLocation = null;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run()) return;
                if (!Microbot.isLoggedIn()) return;

                if (Rs2Player.isMoving() || Rs2Player.isAnimating()) return;

                if (initialPlayerLocation == null) {
                    initialPlayerLocation = Rs2Player.getWorldLocation();
                }

                GameObject rock = Rs2GameObject.findObject(config.ORE().getName(), true, config.distanceToStray(), true, getInitialPlayerLocation());


                List<String> itemNames = Arrays.stream(config.itemsToBank().split(",")).map(String::toLowerCase).collect(Collectors.toList());

                if (config.useBank()) {
                    if (rock == null || Rs2Inventory.isFull()) {
                        if (!Rs2Bank.bankItemsAndWalkBackToOriginalPosition(itemNames, initialPlayerLocation))
                            return;
                    }
                } else if (Rs2Inventory.isFull()) {
                    Rs2Inventory.dropAllExcept("pickaxe");
                    return;
                }

                if (Rs2GameObject.interact(rock)) {
                    Rs2Player.waitForAnimation();
                }

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        return true;
    }
}
