package net.runelite.client.plugins.microbot.playerassist.loot;

import net.runelite.api.ItemComposition;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.ItemSpawned;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.menu.Rs2Menu;

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
            if (Microbot.getClientThread().runOnClientThread(() -> Inventory.isFull())) return;
            final ItemComposition itemComposition = Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getItemDefinition(itemSpawned.getItem().getId()));
            for (String item : lootItems) {
                LocalPoint itemLocation = itemSpawned.getTile().getLocalLocation();
                int distance = itemSpawned.getTile().getWorldLocation().distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation());
                if (item.toLowerCase().equals(itemComposition.getName().toLowerCase()) && distance < 14) {
                    LocalPoint groundPoint = LocalPoint.fromWorld(Microbot.getClient(), itemSpawned.getTile().getWorldLocation());
                    Polygon poly = Perspective.getCanvasTilePoly(Microbot.getClient(), groundPoint, itemSpawned.getTile().getItemLayer().getHeight());
                    if (Rs2Camera.isTileOnScreen(itemLocation)) {
                        if (Rs2Menu.doAction("Take", poly, new String[]{item.toLowerCase()})) {
                            Microbot.pauseAllScripts = true;
                            sleepUntilOnClientThread(() -> Microbot.getClient().getLocalPlayer().getWorldLocation() == itemSpawned.getTile().getWorldLocation(), 5000);
                            Microbot.pauseAllScripts = false;
                        }
                    } else {
                        Rs2Camera.turnTo(itemLocation);
                    }
                }
            }
        }), 2000, TimeUnit.MILLISECONDS);
    }

    public boolean run(PlayerAssistConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay((() -> {
            if (!super.run()) return;
            if (config.toggleLootArrows()) {
                for (String lootItem : Arrays.asList("bronze arrow", "iron arrow", "steel arrow", "mithril arrow", "adamant arrow", "rune arrow", "dragon arrow")) {
                    if (Rs2GroundItem.loot(lootItem, 13, 14))
                        break;
                }
            }
            if (!config.toggleLootItems()) return;
            boolean result = Rs2GroundItem.lootItemBasedOnValue(config.priceOfItemsToLoot(), 14);
            if (result) {
                Global.sleep(2000, 4000);
                Microbot.pauseAllScripts = false;
            }
        }), 0, 2000, TimeUnit.MILLISECONDS);
        return true;
    }

    public void shutdown() {
        super.shutdown();
        lootItems = null;
    }
}
