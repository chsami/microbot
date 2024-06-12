package net.runelite.client.plugins.microbot.playerassist.loot;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemComposition;
import net.runelite.api.TileItem;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.ItemSpawned;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;

import java.util.concurrent.TimeUnit;

@Slf4j
public class LootScript extends Script {

    private String[] lootItems;

    public LootScript() {

    }


    public void run(ItemSpawned itemSpawned) {
        mainScheduledFuture = scheduledExecutorService.schedule((() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;
                log.info("Item spawned: " + itemSpawned.getItem().getId());
                // This makes sure that the bot don't get tricked by other players dropping items
                if(itemSpawned.getItem().getOwnership() == TileItem.OWNERSHIP_GROUP) return;
                if (Microbot.getClientThread().runOnClientThread(Rs2Inventory::isFull)) return;
                final ItemComposition itemComposition = Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getItemDefinition(itemSpawned.getItem().getId()));
                for (String item : lootItems) {
                    LocalPoint itemLocation = itemSpawned.getTile().getLocalLocation();
                    int distance = itemSpawned.getTile().getWorldLocation().distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation());
                    if (item.equalsIgnoreCase(itemComposition.getName()) && distance < 14) {
                        Rs2GroundItem.interact(item, "Take");
                        Microbot.pauseAllScripts = true;
                        sleepUntilOnClientThread(() -> Microbot.getClient().getLocalPlayer().getWorldLocation() == itemSpawned.getTile().getWorldLocation(), 5000);
                        Microbot.pauseAllScripts = false;
                    }
                }
            } catch(Exception ex) {
                System.out.println(ex.getMessage());
            }
        }), 2000, TimeUnit.MILLISECONDS);
    }

    public boolean run(PlayerAssistConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay((() -> {
            if (!super.run()) return;
            if (config.toggleLootArrows()) {
                if (Rs2GroundItem.lootItemsBasedOnNames(config.attackRadius(), config.toggleOnlyLootMyItems(), 1, 14, config.toggleDelayedLooting(), "arrow"))
                    Microbot.pauseAllScripts = false;
            }
            if (!config.toggleLootItems()) return;
            if(config.toggleBuryBones()){
                if (Rs2GroundItem.lootItemsBasedOnNames(config.attackRadius(), config.toggleOnlyLootMyItems(), 3, 1, config.toggleDelayedLooting(), "bones"))
                    Microbot.pauseAllScripts = false;
            }
            if(config.toggleScatter()){
                if (Rs2GroundItem.lootItemsBasedOnNames(config.attackRadius(), config.toggleOnlyLootMyItems(), 3, 1, config.toggleDelayedLooting(), "ashes"))
                    Microbot.pauseAllScripts = false;
            }

            if (Rs2GroundItem.lootItemBasedOnValue(config.minPriceOfItemsToLoot(), config.maxPriceOfItemsToLoot(), config.attackRadius(), 1, config.toggleDelayedLooting(), config.toggleOnlyLootMyItems())) {
                Microbot.pauseAllScripts = false;
            }


        }), 0, 200, TimeUnit.MILLISECONDS);
        return true;
    }
    public void shutdown() {
        super.shutdown();
        lootItems = null;
    }
}
