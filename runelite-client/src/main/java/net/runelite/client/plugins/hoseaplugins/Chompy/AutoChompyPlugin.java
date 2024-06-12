package net.runelite.client.plugins.hoseaplugins.Chompy;


import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Equipment;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Inventory;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.NPCs;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.TileObjects;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.query.EquipmentItemQuery;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.query.ItemQuery;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.query.NPCQuery;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.EthanApiPlugin;
import net.runelite.client.plugins.hoseaplugins.ethanapi.InteractionApi.TileObjectInteraction;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.*;
import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> AutoChompy</html>",
        description = "Kills chompys",
        enabledByDefault = false,
        tags = {"poly", "plugin"}
)
@Slf4j
public class AutoChompyPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private AutoChompyConfig config;
    @Inject
    private AutoChompyOverlay overlay;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ClientThread clientThread;
    public boolean started = false;
    public int timeout = 0;
    public State state = State.WAITING;
    public NPCQuery swampToads;
    public NPCQuery bloatedToads;
    public ItemQuery bloatedToadsItem;
    public NPCQuery birds;
    private int ammoId = -1;

    @Provides
    private AutoChompyConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoChompyConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        keyManager.registerKeyListener(toggle);
        overlayManager.add(overlay);
        timeout = 0;
        clientThread.invoke(this::setVals);
    }

    @Override
    protected void shutDown() throws Exception {
        keyManager.unregisterKeyListener(toggle);
        overlayManager.remove(overlay);
        timeout = 0;
        started = false;
        unsetVals();
    }


    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN || !started) {
            return;
        }
        if (timeout > 0) {
            timeout--;
            return;
        }
        setVals();
        determineNextState();
        doChompy();
    }

    private void unsetVals() {
        swampToads = null;
        bloatedToads = null;
        bloatedToadsItem = null;
        birds = null;
        ammoId = -1;
    }

    public void setVals() {
        if (ammoId < 0) {
            Equipment.search().filter(item -> {
                String name = item.getName();
                return name.contains("gre arrow") || name.contains("brutal");
            }).first().ifPresentOrElse(item -> {
                ammoId = item.getEquipmentItemId();
            }, () -> {
                EthanApiPlugin.sendClientMessage("No Ogre arrows or Brutal arrows found");
                EthanApiPlugin.stopPlugin(this);
            });
        }
        swampToads = NPCs.search().nameContains("wamp toad").withAction("Inflate");
        bloatedToads = NPCs.search().nameContains("loated Toad");
        bloatedToadsItem = Inventory.search().nameContains("loated toad");
        birds = NPCs.search().alive().nameContains("ompy bird").withAction("Attack");
    }

    private void doChompy() {
        checkRunEnergy();
        log.info(state.toString());
        log.info("Ammo id: " + ammoId);
//        log.info(getNearestFreeTile().toString());
        if (Equipment.search().withId(ammoId).empty()) {
            EthanApiPlugin.sendClientMessage("No Ogre arrows or Brutal arrows left");
            EthanApiPlugin.stopPlugin(this);
        }
        switch (state) {
            case KILL_BIRD:
                handleKillBird();
                break;
            case FILL_BELLOWS:
                handleFillBellows();
                break;
            case DROP_TOAD:
                handleDropToad();
                break;
            case INFLATE_TOAD:
                handleInflateToad();
                break;
            default:
                determineNextState();
                break;
        }
    }

    private void determineNextState() {
        if (!birds.empty()) {
            state = State.KILL_BIRD;
        } else if (!hasFilledBellows() && !TileObjects.search().nameContains("wamp bubble").empty()) {
            state = State.FILL_BELLOWS;
        } else if (!bloatedToadsItem.empty()) {
            state = State.DROP_TOAD;
        } else if (bloatedToadsItem.empty() && hasFilledBellows()) {
            state = State.INFLATE_TOAD;
        }
    }

    private void handleKillBird() {
        birds.first().ifPresent(npc -> {
            MousePackets.queueClickPacket();
            NPCPackets.queueNPCAction(npc, "Attack");
        });
    }

    private void handleFillBellows() {
        TileObjects.search().nameContains("wamp bubble").nearestToPlayer().ifPresent(tileObject -> {
            MousePackets.queueClickPacket();
            TileObjectInteraction.interact(tileObject, "Suck");
            timeout = Inventory.search().nameContains("bellows").result().size() * 4;
        });
    }

    public boolean hasFilledBellows() {
        return Inventory.search().nameContains("bellows (").first().isPresent();
    }

    public boolean isStandingOnToad() {
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        return NPCs.search().nameContains("loated Toad").result().stream()
                .anyMatch(toad -> toad.getWorldLocation().equals(playerLocation));
    }

    private void handleDropToad() {
        if (isStandingOnToad()) {
            MousePackets.queueClickPacket();
            MovementPackets.queueMovement(getNearestFreeTile().get());
            timeout = 3;
        } else {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(bloatedToadsItem.first().get(), "Drop");
            timeout = 2;
        }
    }

    private Optional<WorldPoint> getNearestFreeTile() {
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        List<WorldPoint> surroundingTiles = new ArrayList<>();
        for (int dx = -5; dx <= 5; dx++) {
            for (int dy = -5; dy <= 5; dy++) {
                if (dx != 0 || dy != 0) { // Exclude the player's current tile
                    surroundingTiles.add(playerLocation.dx(dx).dy(dy));
                }
            }
        }
        List<WorldPoint> toadLocations = NPCs.search().nameContains("loated toad")
                .result().stream().map(NPC::getWorldLocation).collect(Collectors.toList());
        List<WorldPoint> freeTiles = surroundingTiles.stream().filter(tile -> !toadLocations.contains(tile))
                .collect(Collectors.toList());
        return freeTiles.stream().min(Comparator.comparingInt(tile -> tile.distanceTo(playerLocation)));
    }

    private void handleInflateToad() {
        swampToads.nearestToPlayer().ifPresent(npc -> {
            MousePackets.queueClickPacket();
            NPCPackets.queueNPCAction(npc, "Inflate");
            timeout = 1;
        });
    }

    private void checkRunEnergy() {
        if (runIsOff() && client.getEnergy() >= 30 * 100) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, 10485787, -1, -1);
        }
    }

    private enum State {
        FILL_BELLOWS, INFLATE_TOAD, DROP_TOAD, WAITING, KILL_BIRD
    }

    private boolean runIsOff() {
        return EthanApiPlugin.getClient().getVarpValue(173) == 0;
    }

    private boolean isMovingOrInteracting() {
        //a-1026 fill toad & bellow
        return EthanApiPlugin.isMoving() || client.getLocalPlayer().getAnimation() == 1026 || client.getLocalPlayer().getInteracting() != null;
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
