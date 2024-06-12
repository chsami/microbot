package net.runelite.client.plugins.hoseaplugins.Firemaking;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.*;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.query.TileObjectQuery;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.EthanApiPlugin;
import net.runelite.client.plugins.hoseaplugins.ethanapi.InteractionApi.BankInteraction;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.*;
import com.google.inject.Inject;
import com.google.inject.Provides;
import net.runelite.client.plugins.hoseaplugins.PiggyUtils.API.InventoryUtil;
import net.runelite.client.plugins.hoseaplugins.PiggyUtils.API.MathUtil;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> AutoFiremaking</html>",
        description = "",
        enabledByDefault = false,
        tags = {"poly", "plugin"}
)
@Slf4j
public class FiremakingPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private FiremakingConfig config;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ClientThread clientThread;

    private boolean started = false;

    @Provides
    private FiremakingConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(FiremakingConfig.class);
    }

    @Inject
    private FiremakingOverlay overlay;

    public int timeout = 0;
    public String logName = "Maple logs";
    public ArrayList<WorldPoint> startTiles;
    public FiremakingLocation location;
    public int lastStartTile = -1;
    private boolean firstFire = true;

    @Override
    protected void startUp() throws Exception {
        keyManager.registerKeyListener(toggle);
        overlayManager.add(overlay);
        timeout = 0;
        lastStartTile++;
        clientThread.invoke(() -> {
            location = config.getLocation();
            startTiles = location.getStartTiles();
            logName = config.getLogs();
        });
    }

    @Override
    protected void shutDown() throws Exception {
        keyManager.unregisterKeyListener(toggle);
        overlayManager.remove(overlay);
        started = false;
        timeout = 0;
        lastStartTile = -1;
        firstFire = true;

    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (!event.getGroup().equals("AutoFiremaking"))
            return;
//        location = config.getLocation();
//        startTiles = location.getStartTiles();
//        logName = config.getLogs();
    }


    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN || !started || EthanApiPlugin.isMoving() || client.getLocalPlayer().getAnimation() != -1) {
            return;
        }

        location = config.getLocation();
        startTiles = location.getStartTiles();
        logName = config.getLogs();

        if (timeout > 0) {
            timeout--;
            return;
        }
        if (startTiles == null) {
            startTiles = config.getLocation().getStartTiles();
            return;
        }

        if (!hasLogs() || !hasTinderbox()) {
            Optional<NPC> banker = NPCs.search().nameContains("Banker").withAction("Bank").nearestToPlayer();
            if (!Bank.isOpen()) {
                if (banker.isPresent()) {
                    MousePackets.queueClickPacket();
                    NPCPackets.queueNPCAction(banker.get(), "Bank");
                    timeout = MathUtil.random(1, 3);
                    return;
                }
            }
            if (Inventory.getEmptySlots() < 27) {
                depositInventory();
                timeout = 1;
                return;
            }
            if (!hasTinderbox()) {
                Bank.search().withName("Tinderbox").first().ifPresentOrElse(item -> {
                    MousePackets.queueClickPacket();
                    BankInteraction.withdrawX(item, 1);
                    timeout = ThreadLocalRandom.current().nextInt(2, 4);
                }, () -> {
                    started = false;
                    client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "No tinderbox found", null);
                });
            }
            if (!hasLogs()) {
                Bank.search().withName(logName).first().ifPresentOrElse(item -> {
                    MousePackets.queueClickPacket();
                    BankInteraction.useItem(item, "Withdraw-all");
                    timeout = MathUtil.random(1, 3);
                }, () -> {
                    started = false;
                    client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Out of logs", null);
                });
            }
            if (!firstFire) {
                lastStartTile = lastStartTile >= startTiles.size() - 1 ? 0 : lastStartTile + 1;
                firstFire = true;
            }

        }
        if (firstFire) {
            MousePackets.queueClickPacket();
            MovementPackets.queueMovement(startTiles.get(lastStartTile));
            handleStartFire();
        } else {
            handleStartFire();
        }


    }

    public void depositInventory() {
        Widget depositInventory = client.getWidget(WidgetInfo.BANK_DEPOSIT_INVENTORY);
        if (depositInventory != null) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(depositInventory, "Deposit inventory");
        }
    }


    public boolean hasTinderbox() {
        return Inventory.search().nameContains("inderbox").first().isPresent();
    }

    public boolean hasLogs() {
        return Inventory.search().nameContains(logName).first().isPresent();
    }


    public boolean isStandingOnFire() {
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        return TileObjects.search().nameContains("ire").result().stream()
                .anyMatch(fire -> fire.getWorldLocation().equals(playerLocation));
    }

    private void handleStartFire() {

        if (isStandingOnFire()) {
            getNextFreeTileInLine().ifPresent(freeTile -> {
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(freeTile);
                timeout = 3;
            });
        }
        Inventory.search().onlyUnnoted().nameContains("inderbox").first().ifPresent(tinderbox -> {
            Inventory.search().onlyUnnoted().nameContains(logName).first().ifPresent(logs -> {
                MousePackets.queueClickPacket();
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetOnWidget(tinderbox, logs);
                firstFire = false;
            });
        });
    }

    private Optional<WorldPoint> getNextFreeTileInLine() {
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        TileObjectQuery fires = TileObjects.search().nameContains("ire");
        List<WorldPoint> fireLocations = fires.result().stream().map(TileObject::getWorldLocation).collect(Collectors.toList());
        for (WorldPoint startPoint : startTiles) {
            if (startPoint.getY() != playerLocation.getY()) {
                continue;
            }
            for (int i = 0; i < 27; i++) {
                WorldPoint checkPoint = startPoint.dx(-i);
                if (!fireLocations.contains(checkPoint)) {
                    log.info("Free tile: " + checkPoint);
                    return Optional.of(checkPoint);
                }
            }
        }
        return Optional.empty();
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
