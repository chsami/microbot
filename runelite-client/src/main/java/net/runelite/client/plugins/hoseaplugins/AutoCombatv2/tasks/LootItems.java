package net.runelite.client.plugins.hoseaplugins.AutoCombatv2.tasks;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.*;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.EthanApiPlugin;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.MousePackets;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.TileItemPackets;
import net.runelite.client.plugins.hoseaplugins.AutoCombatv2.AutoCombatv2Config;
import net.runelite.client.plugins.hoseaplugins.PiggyUtils.strategy.AbstractTask;
import net.runelite.client.plugins.hoseaplugins.AutoCombatv2.AutoCombatv2Plugin;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import java.util.Optional;

@Slf4j
public class LootItems extends AbstractTask<AutoCombatv2Plugin, AutoCombatv2Config> {

    public LootItems(AutoCombatv2Plugin plugin, AutoCombatv2Config config) {
        super(plugin, config);
    }

    @Override
    public boolean validate() {
        return !plugin.getLootQueue().isEmpty();
    }

    @Override
    public void execute() {
        Pair<TileItem, Tile> lootPair = plugin.getLootQueue().poll();
        if (lootPair != null) {
            TileItem loot = lootPair.getLeft();
            Tile lootTile = lootPair.getRight();
            log.info("Processing loot: {} at {}", plugin.getItemManager().getItemComposition(loot.getId()).getName(), lootTile.getWorldLocation());
                if(!Inventory.full()) {
                    MousePackets.queueClickPacket();
                    TileItemPackets.queueTileItemAction(new ETileItem(lootTile.getWorldLocation(), loot), false);
                    plugin.timeout = 3;
                }

            }

        }


    }
