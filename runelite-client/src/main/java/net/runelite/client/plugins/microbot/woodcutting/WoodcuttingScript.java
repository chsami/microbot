package net.runelite.client.plugins.microbot.woodcutting;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class WoodcuttingScript  extends Script {

    public static double version = 1.2;

    public boolean run(WoodcuttingConfig config) {
        var startingPosition = Microbot.getClient().getLocalPlayer().getWorldLocation();
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
                var trees = Rs2GameObject.getGameObjects().stream().filter(x-> {
                    var obj = Rs2GameObject.convertGameObjectToObjectComposition(x);
                    if (obj == null){
                        return false;
                    }
                    var objDefinition = Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getObjectDefinition(obj.getId()));
                    if (!objDefinition.getName().equalsIgnoreCase(config.TREE().getName())) {
                        return false;
                    }
                    return x.getWorldLocation().distanceTo(startingPosition) < config.distanceToStray();
                }).collect(Collectors.toList());

                if (trees.size() > 0){
                    Rs2GameObject.interact(trees.get(0), config.TREE().getAction());
                }else {
                    System.out.println("No trees in zone");
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }
}
