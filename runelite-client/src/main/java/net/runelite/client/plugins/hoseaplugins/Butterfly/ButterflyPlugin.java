package net.runelite.client.plugins.hoseaplugins.Butterfly;


import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Inventory;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.NPCs;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.EthanApiPlugin;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.*;
import com.google.inject.Inject;
import com.google.inject.Provides;
import net.runelite.client.plugins.hoseaplugins.PiggyUtils.API.PlayerUtil;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.HotkeyListener;

import java.util.List;
import java.util.Optional;

@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> Butterfly Catcher</html>",
        description = "Catches and releases butterflies",
        enabledByDefault = false,
        tags = {"poly", "plugin"}
)
@Slf4j
public class ButterflyPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private ButterflyConfig config;
    @Inject
    private PlayerUtil playerUtil;
    @Inject
    private KeyManager keyManager;
    private boolean started = false;
    public int timeout = 0;

    @Provides
    private ButterflyConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(ButterflyConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        keyManager.registerKeyListener(toggle);
        timeout = 0;
    }

    @Override
    protected void shutDown() throws Exception {
        keyManager.unregisterKeyListener(toggle);
        timeout = 0;
        started = false;
    }


    @Subscribe
    private void onGameTick(GameTick event) {
        if (timeout > 0) {
            timeout--;
            return;
        }
        if (client.getGameState() != GameState.LOGGED_IN || !started) {
            return;
        }
        checkRunEnergy();
        doButterfly();

    }

    private void doButterfly() {
        Optional<NPC> butterfly = NPCs.search().withName(config.butterfly().getName()).withAction("Catch").nearestToPlayer();
        List<Widget> filledJars = Inventory.search().withAction("Release").withName(config.butterfly().getName()).result();
        Optional<Widget> emptyJar = Inventory.search().withName("Butterfly jar").first();

        checkRunEnergy();

        if (!filledJars.isEmpty()) {
            filledJars.forEach(jar -> {
//                log.info("RELEASING BUTTERFLY");
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetAction(jar, "Release");
            });
        }

        if (EthanApiPlugin.isMoving()) return;

        if (client.getLocalPlayer().getInteracting() == null && emptyJar.isPresent()) {
            if (butterfly.isPresent()) {
//                log.info("CATCHING BUTTERFLY");
                MousePackets.queueClickPacket();
                NPCPackets.queueNPCAction(butterfly.get(), "Catch");
            }
        }
        //1 tick delay after sipping stamina
        if (timeout == 0)
            timeout = config.tickDelay();
    }


    private void checkRunEnergy() {
        if (playerUtil.isRunning() && playerUtil.runEnergy() <= 10) {
            log.info("Run");
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, 10485787, -1, -1);
        }
        checkStamina();
    }

    private void checkStamina() {
        if (!playerUtil.isStaminaActive() && playerUtil.runEnergy() <= 70) {
            log.info("Stamina");
            Inventory.search().onlyUnnoted().nameContains("Stamina pot").withAction("Drink").first().ifPresent(stamina -> {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetAction(stamina, "Drink");
                timeout = 1;
            });
        }
    }

    private final HotkeyListener toggle = new HotkeyListener(() -> config.toggle()) {
        @Override
        public void hotkeyPressed() {
            toggle();
        }
    };

    public void toggle() {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        started = !started;
    }
}