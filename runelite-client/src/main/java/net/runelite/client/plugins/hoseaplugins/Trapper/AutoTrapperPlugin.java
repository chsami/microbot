package net.runelite.client.plugins.hoseaplugins.Trapper;


import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Inventory;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.TileItems;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.TileObjects;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.EthanApiPlugin;
import net.runelite.client.plugins.hoseaplugins.ethanapi.InteractionApi.InventoryInteraction;
import net.runelite.client.plugins.hoseaplugins.ethanapi.InteractionApi.TileObjectInteraction;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.*;
import com.google.inject.Inject;
import com.google.inject.Provides;
import net.runelite.client.plugins.hoseaplugins.PiggyUtils.API.PlayerUtil;
import net.runelite.client.plugins.hoseaplugins.Trapper.data.Salamander;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import java.util.LinkedList;
import java.util.Queue;

@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> AutoTrapper</html>",
        description = "Traps shit",
        enabledByDefault = false,
        tags = {"poly", "plugin"}
)
@Slf4j
public class AutoTrapperPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    public AutoTrapperConfig config;
    @Inject
    private AutoTrapperOverlay overlay;
    @Inject
    private TrapTileOverlay trapTileOverlay;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ClientThread clientThread;
    @Inject
    private PlayerUtil playerUtil;
    @Inject
    public AutoTrapperHelper helper;
    public boolean started = false;
    public int timeout = 0;
    public int maxTraps = 1;
    public int ticksNotInRegion = 0;

    public WorldPoint startTile = null;
    Queue<WorldPoint> droppedSupplies = new LinkedList<>();

    @Provides
    private AutoTrapperConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoTrapperConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        keyManager.registerKeyListener(toggle);
        overlayManager.add(overlay);
        overlayManager.add(trapTileOverlay);
        timeout = 0;
    }

    @Override
    protected void shutDown() throws Exception {
        keyManager.unregisterKeyListener(toggle);
        overlayManager.remove(overlay);
        overlayManager.remove(trapTileOverlay);
        timeout = 0;
        started = false;
        startTile = null;
        ticksNotInRegion = 0;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        GameState state = event.getGameState();
        if (state == GameState.HOPPING || state == GameState.LOGGED_IN) return;
        EthanApiPlugin.stopPlugin(this);
    }

    @Subscribe
    public void onItemSpawned(ItemSpawned event) {
        if (client.getGameState() != GameState.LOGGED_IN || !started) {
            return;
        }
        droppedSupplies.add(event.getTile().getWorldLocation());
    }


    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN || !started) {
            return;
        }
        if (ticksNotInRegion >= 20) {
            EthanApiPlugin.sendClientMessage("Not in correct region, stopping plugin");
            EthanApiPlugin.stopPlugin(this);
        }
        if (timeout > 0) {
            timeout--;
            return;
        }
        if (startTile == null)
            startTile = client.getLocalPlayer().getWorldLocation();
        ticksNotInRegion = helper.inRegion(config.salamander().getRegionId()) ? 0 : ticksNotInRegion + 1;
        maxTraps = helper.getMaxTraps();
        if (config.salamander() == Salamander.BLACK_SALAMANDER) maxTraps++; //+1 for wildy
        dropSalamanders();
        checkRunEnergy();

        TileObjects.search().filter(t -> startTile.distanceTo(t.getWorldLocation()) <= config.maxDist())
                .withName("Net trap").withAction("Check").nearestToPlayer().ifPresent(trap -> {
                    TileObjectInteraction.interact(trap, "Check");
                    timeout = 1 + config.tickDelay();
                });

        //this will eventually cause lost ropes and nets if another player is setting traps
        //maybe do something ab this eventually
        if (helper.getSetTraps() + helper.getCaughtTraps() >= maxTraps) {
                if (EthanApiPlugin.playerPosition().distanceTo(startTile) > 0 && !EthanApiPlugin.isMoving()) {
                    MousePackets.queueClickPacket();
                    MovementPackets.queueMovement(startTile);
                }
        }

        TileObjects.search().filter(t -> startTile.distanceTo(t.getWorldLocation()) <= config.maxDist())
                .withName("Young tree").withAction("Set-trap").nearestToPlayer().ifPresent(tree -> {
                    if (helper.hasTrapSupplies()) {
                        TileObjectInteraction.interact(tree, "Set-trap");
                        timeout = 1 + config.tickDelay();
                    }
                });

        if (!droppedSupplies.isEmpty()) {
            WorldPoint point = droppedSupplies.peek();
            TileItems.search().itemsMatchingWildcardsNoCase("small fishing net", "rope")
                    .withinDistanceToPoint(1, point).first().ifPresentOrElse(item -> {
                        item.interact(false);
                        timeout = 1;
                    }, () -> {
                        droppedSupplies.remove();
                    });
            return;
        }
    }

    public void dropSalamanders() {
        Inventory.search().withName(config.salamander().getName()).withAction("Release").first().ifPresent(salamander -> {
            InventoryInteraction.useItem(salamander, "Release");
        });
    }

    private void checkRunEnergy() {
        if (playerUtil.isRunning() && playerUtil.runEnergy() <= 10) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, 10485787, -1, -1);
        }
        checkStamina();
    }

    private void checkStamina() {
        if (!playerUtil.isStaminaActive() && playerUtil.runEnergy() <= 60) {
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