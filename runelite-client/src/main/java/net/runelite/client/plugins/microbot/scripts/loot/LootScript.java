package net.runelite.client.plugins.microbot.scripts.loot;

import net.runelite.api.ItemComposition;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ItemSpawned;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.scripts.Script;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.camera.Camera;
import net.runelite.client.plugins.microbot.util.grounditem.GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.math.Calculations;
import net.runelite.client.plugins.microbot.util.menu.Menu;

import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class LootScript extends Script {

    private String[] lootItems;

    public LootScript() {

    }

    public void run(ItemSpawned itemSpawned) {
        mainScheduledFuture = scheduledExecutorService.schedule((() -> {
            if (!super.run()) return;
            if (Microbot.getClientThread().runOnClientThread(() -> Inventory.isInventoryFull())) return;
            final ItemComposition itemComposition = Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getItemDefinition(itemSpawned.getItem().getId()));
            for (String item : lootItems) {
                LocalPoint itemLocation = itemSpawned.getTile().getLocalLocation();
                WorldPoint worldLocation = itemSpawned.getTile().getWorldLocation();
                boolean canReachItem = Microbot.getClientThread().runOnClientThread(() -> Calculations.canReach(Microbot.getClient().getLocalPlayer().getWorldLocation().toWorldArea(), worldLocation.toWorldArea()));
                if (!canReachItem) return;
                int distance = itemSpawned.getTile().getWorldLocation().distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation());
                if (item.toLowerCase().equals(itemComposition.getName().toLowerCase()) && distance < 14) {
                    LocalPoint groundPoint = LocalPoint.fromWorld(Microbot.getClient(), itemSpawned.getTile().getWorldLocation());
                    Polygon poly = Perspective.getCanvasTilePoly(Microbot.getClient(), groundPoint, itemSpawned.getTile().getItemLayer().getHeight());
                    if (Camera.isTileOnScreen(itemLocation)) {
                        if (Menu.doAction("Take", poly, new String[]{item.toLowerCase()})) {
                            Microbot.pauseAllScripts = true;
                            sleepUntilOnClientThread(() -> Microbot.getClient().getLocalPlayer().getWorldLocation() == itemSpawned.getTile().getWorldLocation(), 5000);
                            Microbot.pauseAllScripts = false;
                        }
                    } else {
                        Camera.turnTo(itemLocation);
                    }
                }
            }
        }), 600, TimeUnit.MILLISECONDS);
    }

    public void run(String itemNames) {
        lootItems = Arrays.stream(itemNames.split(",")).map(x -> x.trim()).toArray(String[]::new);
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay((() -> {
            if (!super.run()) return;
            for (String lootItem : lootItems) {
                if (GroundItem.loot(lootItem, 14))
                    break;
            }
            Global.sleep(2000, 4000);
            Microbot.pauseAllScripts = false;
        }), 0, 1000, TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        super.shutdown();
        lootItems = null;
    }
}
