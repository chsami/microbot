package net.runelite.client.plugins.hoseaplugins.KittenFeeder;


import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Inventory;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.NPCs;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.TileItems;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Widgets;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.EthanApiPlugin;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.*;
import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.Timer;
import net.runelite.client.util.HotkeyListener;
import net.runelite.client.task.Scheduler;

import java.util.Objects;
import java.util.Optional;


@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> Kitten Feeder</html>",
        description = "Feeds your kitten every few minutes",
        enabledByDefault = false,
        tags = {"piggy", "plugin", "kitten"}
)
@Slf4j
public class KittenFeederPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private KittenFeederConfig config;
    public int timeout = 0;
    private boolean interactNext = false;
    private boolean strokeNext = false;
    @Inject
    public ItemManager itemManager;


    @Provides
    private KittenFeederConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(KittenFeederConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        clientThread.invokeLater(() -> {
            timeout = minutesToGameTicks(config.frequency());
            EthanApiPlugin.sendClientMessage("Kitten Feeder started");
            EthanApiPlugin.sendClientMessage("ONLY FEEDS EVERY `X` MINUTES - DONT BLAME ME IF YOUR CAT DIES");
        });
    }

    @Override
    protected void shutDown() throws Exception {
        timeout = 0;
    }


    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        if (!hasFollower()) {
            EthanApiPlugin.sendClientMessage("NO FOLLOWER, STOPPING");
            EthanApiPlugin.stopPlugin(this);
        }
        log.info("timeout: " + timeout);
        if (timeout > 0) {
            timeout--;
            return;
        }
        if (interactNext) {
            interactNext = false;
            log.info("interact");
            kitten().ifPresent(npc -> {
                MousePackets.queueClickPacket();
                NPCPackets.queueNPCAction(npc, "Interact");
                strokeNext = true;
            });
            timeout = 2;
            return;
        }
        if (strokeNext) {
            strokeNext = false;
            log.info("stroke");
            Widgets.search().withText("Interact with kitten").hiddenState(false).first().ifPresent(widget -> {
                MousePackets.queueClickPacket();
                WidgetPackets.queueResumePause(widget.getId(), 1);
            });
            timeout = minutesToGameTicks(config.frequency());
            return;
        }


        kitten().ifPresent(npc -> {
            Inventory.search().onlyUnnoted().withName(config.food()).first().ifPresentOrElse(item -> {
                MousePackets.queueClickPacket();
                MousePackets.queueClickPacket();
                NPCPackets.queueWidgetOnNPC(npc, item);
                if (config.stroke()) {
                    interactNext = true;
                }
                timeout = interactNext ? 3 : minutesToGameTicks(config.frequency());
            }, () -> {
                EthanApiPlugin.sendClientMessage(String.format("NO %s FOUND, STOPPING", config.food()));
                EthanApiPlugin.stopPlugin(this);
            });
        });


    }

    private Optional<NPC> kitten() {
      return  NPCs.search().interactingWithLocal().filter(k ->Objects.requireNonNull(k.getName()).contains("itten")).first();

    }

    private boolean hasFollower() {
        return client.getVarpValue(447) > 0;
    }

    public int minutesToGameTicks(int minutes) {
        return (minutes * 60000) / 600;
    }


}