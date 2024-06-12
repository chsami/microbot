package net.runelite.client.plugins.hoseaplugins.strategyexample.tasks;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Bank;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Inventory;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.NPCs;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.TileObjects;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.EthanApiPlugin;
import net.runelite.client.plugins.hoseaplugins.ethanapi.InteractionApi.NPCInteraction;
import net.runelite.client.plugins.hoseaplugins.ethanapi.InteractionApi.TileObjectInteraction;
import net.runelite.client.plugins.hoseaplugins.PiggyUtils.strategy.AbstractTask;
import net.runelite.client.plugins.hoseaplugins.strategyexample.StrategySmithConfig;
import net.runelite.client.plugins.hoseaplugins.strategyexample.StrategySmithPlugin;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.NPC;
import net.runelite.api.TileObject;
import net.runelite.client.config.Config;
import net.runelite.client.plugins.Plugin;

import java.util.Optional;

@Slf4j
public class OpenBank extends AbstractTask<StrategySmithPlugin, StrategySmithConfig> {

    public OpenBank(StrategySmithPlugin plugin, StrategySmithConfig config) {
        super(plugin, config);
    }

    @Override
    public boolean validate() {
        return !Bank.isOpen() && (!plugin.hasEnoughBars() || plugin.hasBarsButNotEnough() || !plugin.hasHammer());
    }

    @Override
    public void execute() {
        log.info("Open Bank");
        findBank();
    }

    private void findBank() {
        Optional<NPC> banker = NPCs.search().withAction("Bank").withId(2897).nearestToPlayer();
        Optional<TileObject> bank = TileObjects.search().withAction("Bank").nearestToPlayer();
        if (!Bank.isOpen()) {
            if (banker.isPresent()) {
//                NPCInteraction.interact(banker.get(), "Bank");
                interactNpc(banker.get(), "Bank");
                plugin.timeout = config.tickDelay() == 0 ? 1 : config.tickDelay();
            } else if (bank.isPresent()) {
//                TileObjectInteraction.interact(bank.get(), "Bank");
                interactObject(bank.get(), "Bank");
                plugin.timeout = config.tickDelay() == 0 ? 1 : config.tickDelay();
            } else {
                EthanApiPlugin.sendClientMessage("Couldn't find bank or banker");
                EthanApiPlugin.stopPlugin(plugin);
            }
        }
    }
}
