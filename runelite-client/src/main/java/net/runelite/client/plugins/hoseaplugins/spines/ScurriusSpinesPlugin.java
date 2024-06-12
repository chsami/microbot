package net.runelite.client.plugins.hoseaplugins.spines;


import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.*;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.EthanApiPlugin;
import net.runelite.client.plugins.hoseaplugins.ethanapi.InteractionApi.BankInteraction;
import net.runelite.client.plugins.hoseaplugins.ethanapi.InteractionApi.InventoryInteraction;
import net.runelite.client.plugins.hoseaplugins.ethanapi.InteractionApi.NPCInteraction;
import net.runelite.client.plugins.hoseaplugins.ethanapi.InteractionApi.TileObjectInteraction;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.*;
import com.google.inject.Inject;
import com.google.inject.Provides;
import net.runelite.client.plugins.hoseaplugins.PiggyUtils.API.PlayerUtil;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import java.util.Optional;

@PluginDescriptor(
        name = "<html><font color=\"#7ecbf2\">[PJ] </font>Scurrius Spines</html>",
        description = "",
        enabledByDefault = false,
        tags = {"poly", "plugin"}
)
@Slf4j
public class ScurriusSpinesPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    public ScurriusSpinesConfig config;
    @Inject
    private ScurriusSpinesOverlay overlay;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private ClientThread clientThread;
    @Inject
    PlayerUtil playerUtil;
    public boolean started = false;
    public int timeout = 0;
    public int spinesRedeemed = 0;

    public static final WorldPoint SEWER_MANHOLE_TILE = new WorldPoint(3237, 3458, 0);
    public static final WorldPoint VARROCK_EAST_BANK_TILE = new WorldPoint(3253, 3420, 0);
    public static final WorldPoint LADDER_TILE = new WorldPoint(3237, 9858, 0);
    public static final WorldPoint HISTORIAN_ALDO_TILE = new WorldPoint(3275, 9870, 0);
    public static final WorldArea VARROCK_EAST_BANK_AREA = new WorldArea(3248, 3414, 11, 14, 0);


    NPC aldo = null;
    TileObject manhole = null, ladder = null;
    Widget spine = null, lamp = null, tradeAllSpines = null, skillChoice = null;

    @Provides
    private ScurriusSpinesConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(ScurriusSpinesConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        keyManager.registerKeyListener(toggle);
        overlayManager.add(overlay);
        timeout = 0;
    }

    @Override
    protected void shutDown() throws Exception {
        keyManager.unregisterKeyListener(toggle);
        overlayManager.remove(overlay);
        timeout = 0;
        started = false;
        aldo = null;
        spine = null;
        lamp = null;
        manhole = null;
    }


    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN || !started) {
            return;
        }
        checkRunEnergy();
        aldo = NPCs.search().withName("Historian Aldo").withAction("Trade").first().orElse(null);
        spine = Inventory.search().onlyUnnoted().withId(ItemID.SCURRIUS_SPINE).first().orElse(null);
        lamp = Inventory.search().onlyUnnoted().withName("Antique lamp").first().orElse(null);
        manhole = TileObjects.search().atLocation(SEWER_MANHOLE_TILE).withName("Manhole").first().orElse(null);
        ladder = TileObjects.search().withAction("Climb-up").nearestToPoint(LADDER_TILE).orElse(null);
        tradeAllSpines = Widgets.search().withTextContains("your Scurrius spines for combat").first().orElse(null);
        skillChoice = Widgets.search().withTextContains("Choose the skill you wish to gain ").first().orElse(null);

        if (config.stopAtSpinesRedeemed() > 0 && spinesRedeemed >= config.stopAtSpinesRedeemed()) {
            started = false;
            return;
        }

        if (timeout > 0) {
            timeout--;
            return;
        }
        if (tradeAllSpines != null) {
            WidgetPackets.queueResumePause(tradeAllSpines.getId(), 1);
            timeout = 2;
            return;
        }
        if (skillChoice != null) {
            log.info("skill choice screen "+skillChoice.getId());
            if (config.spineSkill().getPage() == 2) {
                log.info("Page 2");
                WidgetPackets.queueResumePause(skillChoice.getId(), 5);
            }
            log.info("Skill choice: " + config.spineSkill().getIndex());
            WidgetPackets.queueResumePause(skillChoice.getId(), config.spineSkill().getIndex());
            spinesRedeemed++;
            return;
        }
        if (lamp != null) {
            InventoryInteraction.useItem(lamp, "Rub");
            return;
        }
        if (inVarrockSewer()) {
            if (spine == null) {
                if (ladder == null) {
                    walkTowardsLadder();
                    return;
                }
                TileObjectInteraction.interact(ladder, "Climb-up");
                return;
            }
            if (aldo == null) {
                walkTowardsAldo();
                return;
            }
            NPCInteraction.interact(aldo, "Trade");

        } else {
            if (spine == null) {
                if (!openBank()) {
                    walkTowardsBank();
                    return;
                }
                handleBanking();
                return;
            }
            if (manhole == null) {
                walkTowardsManhole();
                return;
            }
            TileObjectInteraction.interact(manhole, "Open", "Climb-down");
            timeout = 2;
        }

    }

    public void handleBanking() {
        if (!Bank.isOpen()) return;
        if (Bank.search().withId(ItemID.SCURRIUS_SPINE).empty()) {
            EthanApiPlugin.sendClientMessage("No spines left in bank");
            started = false;
            return;
        }
        Bank.search().withId(ItemID.SCURRIUS_SPINE).first().ifPresent(spine -> {
            BankInteraction.useItem(spine, "Withdraw-All");
        });
    }

    public void walkTowardsAldo() {
        MousePackets.queueClickPacket();
        MovementPackets.queueMovement(HISTORIAN_ALDO_TILE);
        timeout = 2;
    }

    public void walkTowardsLadder() {
        MousePackets.queueClickPacket();
        MovementPackets.queueMovement(LADDER_TILE);
        timeout = 2;
    }

    public void walkTowardsManhole() {
        MousePackets.queueClickPacket();
        MovementPackets.queueMovement(SEWER_MANHOLE_TILE);
        timeout = 2;
    }

    public void walkTowardsBank() {
        MousePackets.queueClickPacket();
        MovementPackets.queueMovement(VARROCK_EAST_BANK_TILE);
        timeout = 2;
    }

    public boolean openBank() {
        if (Bank.isOpen()) return true;
        Optional<NPC> banker = NPCs.search().withAction("Bank").withinWorldArea(VARROCK_EAST_BANK_AREA).nearestToPlayer();
        if (banker.isPresent()) {
            NPCInteraction.interact(banker.get(), "Bank");
            timeout = 2;
            return true;
        }
        return false;
    }

    public boolean inVarrockSewer() {
        return playerUtil.inRegion(12954, 13210);
    }

    private void checkRunEnergy() {
        if (runIsOff() && client.getEnergy() >= 30 * 100) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, 10485787, -1, -1);
        }
    }

    private boolean runIsOff() {
        return EthanApiPlugin.getClient().getVarpValue(173) == 0;
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