package net.runelite.client.plugins.hoseaplugins.AutoRifts;

import net.runelite.client.plugins.hoseaplugins.AutoRifts.data.Constants;
import net.runelite.client.plugins.hoseaplugins.AutoRifts.data.CellMapper;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.*;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.query.TileObjectQuery;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.EthanApiPlugin;
import net.runelite.client.plugins.hoseaplugins.ethanapi.InteractionApi.InventoryInteraction;
import net.runelite.client.plugins.hoseaplugins.ethanapi.InteractionApi.NPCInteraction;
import net.runelite.client.plugins.hoseaplugins.ethanapi.InteractionApi.TileObjectInteraction;
import net.runelite.client.plugins.hoseaplugins.ethanapi.PacketUtils.WidgetInfoExtended;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.MousePackets;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.MovementPackets;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.WidgetPackets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Provides;
import net.runelite.client.plugins.hoseaplugins.PiggyUtils.API.InventoryUtil;
import net.runelite.client.plugins.hoseaplugins.PiggyUtils.BreakHandler.ReflectBreakHandler;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;
import org.apache.commons.lang3.RandomUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> Auto Rifts</html>",
        description = "Guardians of the Rift",
        enabledByDefault = false,
        tags = {"ethan", "piggy"}
)
@Slf4j
public class AutoRiftsPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private AutoRiftsConfig config;
    @Inject
    private AutoRiftsOverlay overlay;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private PouchManager pouchManager;
    @Inject
    public GOTRState riftState;
    @Inject
    private ClientThread clientThread;
    @Inject
    private ReflectBreakHandler breakHandler;
    public Instant timer;
    public Duration runningDuration = Duration.ZERO;
    private static final Set<Integer> MiningAnimationIDs = ImmutableSet.of(AnimationID.MINING_BRONZE_PICKAXE, AnimationID.MINING_IRON_PICKAXE, AnimationID.MINING_STEEL_PICKAXE, AnimationID.MINING_BLACK_PICKAXE, AnimationID.MINING_MITHRIL_PICKAXE, AnimationID.MINING_ADAMANT_PICKAXE, AnimationID.MINING_RUNE_PICKAXE, AnimationID.MINING_GILDED_PICKAXE, AnimationID.MINING_DRAGON_PICKAXE, AnimationID.MINING_DRAGON_PICKAXE_UPGRADED, AnimationID.MINING_DRAGON_PICKAXE_OR, AnimationID.MINING_DRAGON_PICKAXE_OR_TRAILBLAZER, AnimationID.MINING_INFERNAL_PICKAXE, AnimationID.MINING_3A_PICKAXE, AnimationID.MINING_CRYSTAL_PICKAXE, AnimationID.MINING_TRAILBLAZER_PICKAXE, AnimationID.MINING_TRAILBLAZER_PICKAXE_2, AnimationID.MINING_TRAILBLAZER_PICKAXE_3);
    private final List<Integer> RunePouchList = ImmutableList.of(ItemID.RUNE_POUCH, ItemID.DIVINE_RUNE_POUCH, ItemID.RUNE_POUCH_L, ItemID.DIVINE_RUNE_POUCH_L);
    private final List<Integer> RuneList = ImmutableList.of(
            ItemID.AIR_RUNE,
            ItemID.WATER_RUNE,
            ItemID.EARTH_RUNE,
            ItemID.FIRE_RUNE,
            ItemID.MIND_RUNE,
            ItemID.CHAOS_RUNE,
            ItemID.DEATH_RUNE,
            ItemID.BLOOD_RUNE,
            ItemID.COSMIC_RUNE,
            ItemID.NATURE_RUNE,
            ItemID.LAW_RUNE,
            ItemID.BODY_RUNE,
            ItemID.SOUL_RUNE,
            ItemID.ASTRAL_RUNE,
            ItemID.MIST_RUNE,
            ItemID.MUD_RUNE,
            ItemID.DUST_RUNE,
            ItemID.LAVA_RUNE,
            ItemID.STEAM_RUNE,
            ItemID.SMOKE_RUNE,
            ItemID.WRATH_RUNE);

    private final List<Integer> PickaxeIDs = ImmutableList.of(ItemID.BRONZE_PICKAXE,
            ItemID.IRON_PICKAXE,
            ItemID.STEEL_PICKAXE,
            ItemID.BLACK_PICKAXE,
            ItemID.MITHRIL_PICKAXE,
            ItemID.ADAMANT_PICKAXE,
            ItemID.RUNE_PICKAXE,
            ItemID.GILDED_PICKAXE,
            ItemID.DRAGON_PICKAXE,
            ItemID.DRAGON_PICKAXE_OR,
            ItemID.DRAGON_PICKAXE_OR_25376,
            ItemID.DRAGON_PICKAXE_12797,
            ItemID.INFERNAL_PICKAXE,
            ItemID._3RD_AGE_PICKAXE,
            ItemID.CRYSTAL_PICKAXE,
            ItemID.CRYSTAL_PICKAXE_INACTIVE,
            ItemID.INFERNAL_PICKAXE_OR);
    private final List<Integer> PoweredCellList = ImmutableList.of(ItemID.WEAK_CELL, ItemID.OVERCHARGED_CELL, ItemID.STRONG_CELL, ItemID.MEDIUM_CELL);
    public boolean started = false;
    public int timeout = 0;
    private boolean startingRun = false;
    private boolean needsMoreStartingFragments = false;

    private boolean canEnterBarrier = false;

    @Provides
    private AutoRiftsConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoRiftsConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        keyManager.registerKeyListener(toggle);
        overlayManager.add(overlay);
        breakHandler.registerPlugin(this);
        timeout = 0;
        timer = Instant.now();
        pouchManager.register();
        riftState.register();
    }

    @Override
    protected void shutDown() throws Exception {
        keyManager.unregisterKeyListener(toggle);
        overlayManager.remove(overlay);
        breakHandler.stopPlugin(this);
        breakHandler.unregisterPlugin(this);
        pouchManager.deregister();
        riftState.deregister();
        timeout = 0;
        toggle();
        timer = Instant.now();
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (timeout > 0) {
            timeout--;
            return;
        }
        if (client.getGameState() != GameState.LOGGED_IN || !started || breakHandler.isBreakActive(this)) {
            return;
        }

        if (Inventory.search().idInList(PickaxeIDs).first().isEmpty() && Equipment.search().idInList(PickaxeIDs).first().isEmpty()) {
            addMessage("No pickaxe found");
            setStarted(false);
            return;
        }

        if (config.usePouches() && !hasDegradePrevention() && (!hasNPCContactRunes() || !isOnLunar())) {
            addMessage("Must have a rune pouch with NPC contact runes or some form of prevention to use essence pouches");
            setStarted(false);
            return;
        }

        runningDuration = runningDuration.plus(Duration.between(timer, Instant.now()));
        timer = Instant.now();

        if (Inventory.full()
                && config.usePouches()
                && pouchManager.hasEmptyPouches()
                && Inventory.getItemAmount(ItemID.GUARDIAN_ESSENCE) > 0
                && !riftState.isInAltar()) {
            pouchManager.fillPouches();
            if (riftState.isInHugeMine()) {
                mineHugeGuardians();
            } else {
                craftEssence();
            }
            return;
        }

        AutoRiftsState state = getState();
        overlay.overlayState = state.toString();

        if (EthanApiPlugin.isMoving()) {
            //Attempt to put runes in pouch where possible
            getInventoryRunes().ifPresent(widget -> {
                Optional<Widget> optPouch = getRunePouch();
                if (optPouch.isEmpty()) {
                    return;
                }

                if (!canDepositRune(widget.getItemId())) {
                    return;
                }

                Widget pouch = optPouch.get();
                MousePackets.queueClickPacket();
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetOnWidget(widget, pouch);
            });

            dropRunes();

            //Handle moving to the altar differently (allow for re-tasking)
            if (state != AutoRiftsState.ENTER_ALTAR) {
                return;
            }
        }

        switch (state) {
            case WAITING_FOR_GAME:
                waitForGame();
                break;
            case MOVE_TO_EAST_MINE:
            case LEAVE_EAST_MINE:
                climbLargeMine();
                break;
            case ENTER_PORTAL:
                enterPortal();
                break;
            case LEAVE_ALTAR:
                exitAltar();
                break;
            case GET_CELLS:
                getCells();
                break;
            case CRAFT_ESSENCE:
                craftEssence();
                break;
            case REPAIR_POUCH:
                repairPouch();
                break;
            case POWER_GUARDIAN:
                powerGuardian();
                break;
            case MINE_HUGE_GUARDIANS:
                mineHugeGuardians();
                break;
            case MINE_LARGE_GUARDIANS:
                mineLargeGuardians();
                break;
            case MINE_REGULAR_GUARDIANS:
                mineGameGuardians();
                break;
            case CRAFTING_ESSENCE:
                break;
            case DEPOSIT_RUNES:
                depositRunes();
                break;
            case USE_CELL:
                usePowerCell();
                break;
            case CRAFT_RUNES:
                craftRunes();
                break;
            case ENTER_ALTAR:
                enterRift();
                break;
            case ENTER_GAME:
                enterGame();
                break;
            case GAME_BUSY:
                gameBusy();
                break;
            case BREAK:
                breakHandler.startBreak(this);
                break;
        }
    }

    private AutoRiftsState getState() {
        if (config.startingFrags() > 0 && getFragmentCount() > config.startingFrags()) {
            needsMoreStartingFragments = false;
        } else if (config.startingFrags() == 0) {
            needsMoreStartingFragments = true;
        }

        if (!riftState.isGameStarted() && !riftState.isInAltar() && breakHandler.shouldBreak(this)) {
            return AutoRiftsState.BREAK;
        }

        if (riftState.isGameBusy()) {
            return AutoRiftsState.GAME_BUSY;
        }

        if (riftState.isOutsideBarrier()) {
            return AutoRiftsState.ENTER_GAME;
        } else {
            canEnterBarrier = false;
        }

        if (pouchManager.hasDegradedPouches()) {
            return AutoRiftsState.REPAIR_POUCH;
        }

        if (!riftState.isGameStarted()) {
            return getPregameState();
        }

        if (!riftState.hasFirstPortalSpawned && needsMoreStartingFragments) {
            return getPreFirstPortalState();
        }

        if (riftState.isGameEnding) {
            return getGameEndingState();
        }

        //After the first portal has spawned, we should always be leaving the east mine
        if (riftState.isInLargeMine()) {
            return AutoRiftsState.LEAVE_EAST_MINE;
        }

        //If we're in the huge mine, we should mine until inventory is full and then leave
        if (riftState.isInHugeMine()) {
            if (Inventory.full()) {
                return AutoRiftsState.ENTER_PORTAL;
            }

            if (isMining()) {
                return AutoRiftsState.MINING;
            }

            return AutoRiftsState.MINE_HUGE_GUARDIANS;
        }

        if (riftState.isInAltar()) {
            startingRun = false;
            if (Inventory.getItemAmount(ItemID.GUARDIAN_ESSENCE) > 0 || pouchManager.hasFullPouch()) {
                return AutoRiftsState.CRAFT_RUNES;
            }

            return AutoRiftsState.LEAVE_ALTAR;
        }

        //If we're here, were in the game area
        if (riftState.isPortalSpawned() && (pouchManager.hasEmptyPouches() || Inventory.getEmptySlots() > 15)) {
            return AutoRiftsState.ENTER_PORTAL;
        }

        if (Inventory.full() || startingRun) {
            startingRun = true;
            if (hasPowerEssence()) {
                return AutoRiftsState.POWER_GUARDIAN;
            }

            if (hasPowerCell()) {
                return AutoRiftsState.USE_CELL;
            }

            return AutoRiftsState.ENTER_ALTAR;
        }

        if (client.getLocalPlayer().getAnimation() == 9365) {
            return AutoRiftsState.CRAFTING_ESSENCE;
        }

        if (getCellCount() == 0) {
            return AutoRiftsState.GET_CELLS;
        }

        if (getInventoryRunes().isPresent() && !config.dropRunes() && getDroppableRunes().isEmpty()) {
            return AutoRiftsState.DEPOSIT_RUNES;
        }

        if (getFragmentCount() < neededFrags()) {
            if (isMining()) {
                return AutoRiftsState.MINING;
            }
            return AutoRiftsState.MINE_REGULAR_GUARDIANS;
        }

        return AutoRiftsState.CRAFT_ESSENCE;
    }

    private AutoRiftsState getGameEndingState() {
        if (riftState.isInAltar()) {
            if (pouchManager.getEssenceInPouches() > 0 || Inventory.getItemAmount(ItemID.GUARDIAN_ESSENCE) > 0) {
                return AutoRiftsState.CRAFT_RUNES;
            }

            return AutoRiftsState.LEAVE_ALTAR;
        }

        if (riftState.isInHugeMine()) {
            return AutoRiftsState.ENTER_PORTAL;
        }

        if (hasPowerEssence()) {
            return AutoRiftsState.POWER_GUARDIAN;
        }

        if (hasPowerCell()) {
            return AutoRiftsState.USE_CELL;
        }

        int essence = pouchManager.getEssenceInPouches() + Inventory.getItemAmount(ItemID.GUARDIAN_ESSENCE);

        if (essence > 20) {
            return AutoRiftsState.ENTER_ALTAR;
        }

        if (getInventoryRunes().isPresent()) {
            return AutoRiftsState.DEPOSIT_RUNES;
        }

        return AutoRiftsState.CRAFT_ESSENCE;
    }


    private AutoRiftsState getPreFirstPortalState() {
        //Safety
        if (riftState.isInAltar()) {
            return AutoRiftsState.LEAVE_ALTAR;
        }
        //We're starting the game in the portal area
        if (riftState.isInHugeMine()) {
            if (Inventory.full()) {
                return AutoRiftsState.ENTER_PORTAL;
            }

            if (isMining()) {
                return AutoRiftsState.MINING;
            }

            return AutoRiftsState.MINE_HUGE_GUARDIANS;
        }

        if (riftState.isInLargeMine()) {
            if (hasPowerCell()) {
                Inventory.search().idInList(PoweredCellList).first().ifPresent(widget -> {
                    InventoryInteraction.useItem(widget, "Drop");
                });
            }
            if (isMining()) {
                return AutoRiftsState.MINING;
            }

            return AutoRiftsState.MINE_LARGE_GUARDIANS;
        }

        //If we get here, we're probably walking to east from the portal.
        //Make a quick stop at cells if we need too
        if (getCellCount() < 10) {
            return AutoRiftsState.GET_CELLS;
        }

        return AutoRiftsState.MOVE_TO_EAST_MINE;
    }

    private AutoRiftsState getPregameState() {
        if (config.startingFrags() > 0) {
            needsMoreStartingFragments = true;
        }

        if (riftState.isInHugeMine()) {
            return AutoRiftsState.ENTER_PORTAL;
        }
        if (riftState.isInLargeMine()) {
            return AutoRiftsState.WAITING_FOR_GAME;
        }

        if (riftState.isInAltar()) {
            if (pouchManager.getEssenceInPouches() > 0 || Inventory.getItemAmount(ItemID.GUARDIAN_ESSENCE) > 0) {
                return AutoRiftsState.CRAFT_RUNES;
            }
            return AutoRiftsState.LEAVE_ALTAR;
        }

        if (getInventoryRunes().isPresent()) {
            return AutoRiftsState.DEPOSIT_RUNES;
        }

        if (getCellCount() < 10) {
            return AutoRiftsState.GET_CELLS;
        }

        return AutoRiftsState.MOVE_TO_EAST_MINE;
    }

    private int getCellCount() {
        return Inventory.search().withId(ItemID.UNCHARGED_CELL).first().map(Widget::getItemQuantity).orElse(0);
    }

    private void waitForGame() {
        if (client.getLocalPlayer().getWorldLocation().getX() == Constants.LARGE_MINE_X) {
            if (tickDelay() % 2 == 0) {
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(3639, 9500, false);
            } else {
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(3640, 9500, false);
            }
        }
    }

    @Subscribe
    private void onChatMessage(ChatMessage event) {
        if (client.getGameState() != GameState.LOGGED_IN || !started) {
            return;
        }

        if (event.getType() != ChatMessageType.SPAM && event.getType() != ChatMessageType.GAMEMESSAGE) return;

        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        if (event.getMessage().contains("5 seconds.")) {
            int attempt = RandomUtils.nextInt(0, 10);
            if (attempt > 7) {
                activatePickSpecial();
            }
        }

        if (event.getMessage().contains("3..")) {
            int attempt = RandomUtils.nextInt(0, 5);
            if (attempt > 2) {
                activatePickSpecial();
            }
        }

        if (event.getMessage().contains("2..")) {
            activatePickSpecial();
        }

        if (event.getMessage().contains("already enough adventurers")) {
            timeout = 3;
        }
    }

    private void activatePickSpecial() {
        if (client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT) == 1000) {
            if (!Equipment.search().matchesWildCardNoCase("*Dragon pickaxe*").empty() || !Equipment.search().matchesWildCardNoCase("*infernal pickaxe*").empty()) {
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetActionPacket(1, 38862885, -1, -1);
            }
        }
    }

    private void climbLargeMine() {
        if (EthanApiPlugin.isMoving()) {
            return;
        }
        Optional<TileObject> tileObject = TileObjects.search().withAction("Climb").nearestToPlayer();
        if (tileObject.isEmpty()) {
            return;
        }

        TileObject rubble = tileObject.get();
        TileObjectInteraction.interact(rubble, "Climb");
        timeout = tickDelay();
    }

    private void getCells() {
        if (Inventory.full() && Inventory.getItemAmount(ItemID.UNCHARGED_CELL) == 0) {
            Inventory.search().withId(ItemID.GUARDIAN_ESSENCE).first().ifPresent(widget -> {
                InventoryInteraction.useItem(widget, "Drop");
            });
        }

        if (EthanApiPlugin.isMoving()) {
            return;
        }

        TileObjects.search().withName("Uncharged cells").first().ifPresent(tileObject -> {
            TileObjectInteraction.interact(tileObject, "Take-10");
            timeout = tickDelay();
        });
    }

    private void craftEssence() {
        if (riftState.isGameStarted()) {
            Optional<TileObject> tileObject = TileObjects.search().nameContains(Constants.WORKBENCH).nearestToPlayer();
            if (tileObject.isEmpty()) {
                return;
            }
            TileObject workbench = tileObject.get();
            TileObjectInteraction.interact(workbench, "Work-at");
            timeout = tickDelay();
        }
    }

    private void mineHugeGuardians() {
        Optional<TileObject> tileObject = TileObjects.search().nameContains(Constants.HUGE_REMAINS).nearestToPlayer();
        if (tileObject.isEmpty()) {
            return;
        }
        TileObject remains = tileObject.get();
        TileObjectInteraction.interact(remains, "Mine");
        timeout = tickDelay();
    }

    private void mineLargeGuardians() {
        if (Inventory.full() && Inventory.getItemAmount(ItemID.GUARDIAN_FRAGMENTS) == 0) {
            Inventory.search().withId(ItemID.GUARDIAN_ESSENCE).first().ifPresent(widget -> {
                InventoryInteraction.useItem(widget, "Drop");
            });
        }

        Optional<TileObject> tileObject = TileObjects.search().nameContains(Constants.LARGE_REMAINS).nearestToPlayer();
        if (tileObject.isEmpty()) {
            return;
        }
        if (client.getLocalPlayer().getAnimation() == -1) {
            TileObject remains = tileObject.get();
            TileObjectInteraction.interact(remains, "Mine");
            timeout = tickDelay();
        }
    }

    private Optional<Widget> getInventoryRunes() {
        return Inventory.search().idInList(RuneList).first();
    }

    private void mineGameGuardians() {
        Optional<TileObject> tileObject = nameContainsNoCase(Constants.GAME_PARTS).nearestToPlayer();
        if (tileObject.isEmpty()) {
            return;
        }
        if (client.getLocalPlayer().getAnimation() == -1) {
            TileObject remains = tileObject.get();
            TileObjectInteraction.interact(remains, "Mine");
            timeout = tickDelay();
        }
    }

    private void gameBusy() {
        if (EthanApiPlugin.isMoving()) {
            return;
        }

        if (riftState.getElementalPoints() < 0 && riftState.getCatalyticPoints() < 0) {
            TileObjects.search().withId(43695).first().ifPresent(tileObject -> {
                TileObjectInteraction.interact(tileObject, "Check");
            });
            timeout = tickDelay();
            return;
        }

        if (RandomUtils.nextInt(0, 100) == 30) {
            TileObjects.search().withId(Constants.BARRIER_BUSY_ID).first().ifPresent(tileObject -> {
                TileObjectInteraction.interact(tileObject, "Peek");
            });
        }
    }

    private void enterGame() {
        if (canEnterBarrier) {
            TileObjects.search().withName("Barrier").first().ifPresent(tileObject -> TileObjectInteraction.interact(tileObject, "Quick-pass"));
            timeout = 1;
            return;
        }

        Optional<Widget> dialog = Widgets.search().withId(15007475).hiddenState(false).first();
        if (dialog.isEmpty()) {
            TileObjects.search().withName("Barrier").first().ifPresent(tileObject -> TileObjectInteraction.interact(tileObject, "Quick-pass"));
            return;
        }

        String dialogText = dialog.get().getText();
        if (dialogText.contains("the rift is already being guarded")) {
            timeout = 10;
            clickContinue();
            return;
        }

        if (dialogText.contains("the adventurers within are just finishing up.")) {
            canEnterBarrier = true;
            timeout = 1;
        }
    }

    private void clickContinue() {
        Widgets.search().withTextContains("Click here to continue").first().ifPresent(widget -> {
            MousePackets.queueClickPacket();
            WidgetPackets.queueResumePause(widget.getId(), -1);
        });
    }

    private void setStarted(boolean started) {
        this.started = started;
        riftState.setStarted(started);
        pouchManager.setStarted(started);
        if (started) {
            timer = Instant.now();
            if (config.usePouches()) {
                clientThread.invokeLater(() -> pouchManager.refreshPouches());
            }

            breakHandler.startPlugin(this);
        } else {
            breakHandler.stopPlugin(this);
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
        setStarted(!started);
    }

    private int tickDelay() {
        return config.tickDelay() ? ThreadLocalRandom.current().nextInt(config.tickDelayMin(), config.tickDelayMax()) : 0;
    }

    private boolean isMining() {
        return MiningAnimationIDs.contains(client.getLocalPlayer().getAnimation());
    }

    private void addMessage(String message) {
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", message, null);
    }

    private boolean hasRuneAmount(int runeId, int amount) {
        return (client.getVarbitValue(Varbits.RUNE_POUCH_RUNE1) == runeId
                && client.getVarbitValue(Varbits.RUNE_POUCH_AMOUNT1) >= amount)
                || (client.getVarbitValue(Varbits.RUNE_POUCH_RUNE2) == runeId
                && client.getVarbitValue(Varbits.RUNE_POUCH_AMOUNT2) >= amount)
                || (client.getVarbitValue(Varbits.RUNE_POUCH_RUNE3) == runeId
                && client.getVarbitValue(Varbits.RUNE_POUCH_AMOUNT3) >= amount)
                || (client.getVarbitValue(Varbits.RUNE_POUCH_RUNE4) == runeId
                && client.getVarbitValue(Varbits.RUNE_POUCH_AMOUNT4) >= amount);
    }

    private boolean canDepositRune(int runeId) {
        return (client.getVarbitValue(Varbits.RUNE_POUCH_RUNE1) == runeId
                && client.getVarbitValue(Varbits.RUNE_POUCH_AMOUNT1) < 16000)
                || (client.getVarbitValue(Varbits.RUNE_POUCH_RUNE2) == runeId
                && client.getVarbitValue(Varbits.RUNE_POUCH_AMOUNT2) < 16000)
                || (client.getVarbitValue(Varbits.RUNE_POUCH_RUNE3) == runeId
                && client.getVarbitValue(Varbits.RUNE_POUCH_AMOUNT3) < 16000)
                || (client.getVarbitValue(Varbits.RUNE_POUCH_RUNE4) == runeId
                && client.getVarbitValue(Varbits.RUNE_POUCH_AMOUNT4) < 16000);
    }

    private boolean isOnLunar() {
        return client.getVarbitValue(4070) == 2;
    }

    private boolean hasNPCContactRunes() {
        return hasRuneAmount(1, 2) && hasRuneAmount(14, 1) && hasRuneAmount(9, 1);
    }

    private boolean hasPowerEssence() {
        return Inventory.getItemAmount(ItemID.CATALYTIC_GUARDIAN_STONE) > 0 || Inventory.getItemAmount(ItemID.ELEMENTAL_GUARDIAN_STONE) > 0;
    }

    private boolean hasPowerCell() {
        return Inventory.search().idInList(PoweredCellList).first().isPresent();
    }

    private void enterPortal() {
        if (EthanApiPlugin.isMoving()) {
            return;
        }
        Optional<TileObject> tileObject = nameContainsNoCase(Constants.PORTAL).filter(to -> to.getWorldLocation().getY() >= Constants.OUTSIDE_BARRIER_Y).nearestToPlayer();
        if (tileObject.isEmpty()) {
            MousePackets.queueClickPacket();
            MovementPackets.queueMovement(new WorldPoint(3615, 9499, 0));
            return;
        }

        TileObject portal = tileObject.get();
        TileObjectInteraction.interact(portal, "Enter", "Exit", "Use");
        timeout = tickDelay();
    }

    private void exitAltar() {
        lastAltar = null;
        if (EthanApiPlugin.isMoving()) {
            return;
        }
        Optional<TileObject> tileObject = TileObjects.search().nameContains(Constants.PORTAL).nearestToPlayer();
        if (tileObject.isEmpty()) {
            return;
        }

        TileObject portal = tileObject.get();
        TileObjectInteraction.interact(portal, "Use");
        timeout = tickDelay();
    }

    private void powerGuardian() {
        Optional<NPC> npc = NPCs.search().nameContains(Constants.GREAT_GUARDIAN).nearestToPlayer();
        if (npc.isEmpty()) {
            return;
        }

        NPC guardian = npc.get();
        NPCInteraction.interact(guardian, "Power-up");
        timeout = tickDelay();
    }

    private void repairPouch() {
        if (!Widgets.search().withTextContains("What do you want?").hiddenState(false).empty() || !Widgets.search().withTextContains("Can you repair").hiddenState(false).empty()) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueResumePause(15138821, -1);
            MousePackets.queueClickPacket();
            WidgetPackets.queueResumePause(14352385, config.hasBook() ? 1 : 2);
            MousePackets.queueClickPacket();
            WidgetPackets.queueResumePause(14221317, -1);
            MousePackets.queueClickPacket();
            EthanApiPlugin.invoke(-1, -1, 26, -1, -1, -1, "", "", -1, -1);
            timeout = 0;
            if (config.usePouches()) {
                clientThread.invokeLater(() -> pouchManager.refreshPouches());
            }
        } else {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(2, WidgetInfoExtended.SPELL_NPC_CONTACT.getPackedId(),
                    -1, -1);
            timeout = 15;
        }
    }

    private Optional<Widget> getRunePouch() {
        return Inventory.search().idInList(RunePouchList).first();
    }

    private void depositRunes() {
        Optional<TileObject> tileObject = TileObjects.search().withAction("Deposit-runes").nearestToPlayer();
        if (tileObject.isEmpty()) {
            return;
        }

        TileObject runeDeposit = tileObject.get();
        TileObjectInteraction.interact(runeDeposit, "Deposit-runes");
        timeout = tickDelay();
    }

    private int neededFrags() {
        return Inventory.getEmptySlots() + pouchManager.getAvailableSpace() + 5;
    }

    private void usePowerCell() {
        if (EthanApiPlugin.isMoving()) {
            return;
        }

        Optional<Widget> optCell = Inventory.search().idInList(PoweredCellList).first();
        if (optCell.isEmpty()) {
            return;
        }

        Widget cell = optCell.get();
        TileObject nextAltar = riftState.getNextAltar();
        int cellTier = CellMapper.GetCellTier(cell.getItemId());
        List<TileObject> shieldCells = TileObjects.search().nameContains("cell tile").result();
        //Prioritize upgrading shield cells
        for (TileObject c : shieldCells) {
            if (CellMapper.GetShieldTier(c.getId()) < cellTier) {
                log.info("Upgrading power cell at " + c.getWorldLocation());
                TileObjectInteraction.interact(c, "Place-cell");
                timeout = tickDelay();
                return;
            }
        }

        if (nextAltar == null) {
            Optional<NPC> bestBarrier = NPCs.search()
                    .filter(x -> x.getId() <= 11425 && x.getId() >= 11418)
                    .result().stream().min(Comparator.comparingDouble(this::getBarrierHealth));

            if (bestBarrier.isPresent()) {
                Optional<TileObject> tile = TileObjects.search().nameContains("cell tile").nearestToPoint(bestBarrier.get().getWorldLocation());
                if (tile.isPresent()) {
                    tile.ifPresent(tileObject -> TileObjectInteraction.interact(tileObject, "Place-cell"));
                    log.info("Placing power cell at " + tile.get().getWorldLocation() + " with no altar present");
                    timeout = tickDelay();
                }
            }
        } else {
            Optional<NPC> damagedBarrier = NPCs.search().filter(x -> x.getId() <= 11425 && x.getId() >= 11418 && getBarrierHealth(x) <= 50).first();
            if (damagedBarrier.isPresent()) {
                Optional<TileObject> tile = TileObjects.search().nameContains("cell tile").nearestToPoint(damagedBarrier.get().getWorldLocation());
                if (tile.isPresent()) {
                    log.info("Placing power cell at " + tile.get().getWorldLocation() + " to repair barrier");
                    TileObjectInteraction.interact(tile.get(), "Place-cell");
                    timeout = tickDelay();
                    return;
                }
            }

            Optional<NPC> bestBarrier = NPCs.search()
                    .filter(x -> x.getId() <= 11425 && x.getId() >= 11418)
                    .result().stream().min(Comparator.comparingInt(x -> x.getWorldLocation().distanceTo(nextAltar.getWorldLocation())));

            if (bestBarrier.isPresent()) {
                Optional<TileObject> tile = TileObjects.search().nameContains("cell tile").nearestToPoint(bestBarrier.get().getWorldLocation());
                if (tile.isPresent()) {
                    log.info("Placing power cell at " + tile.get().getWorldLocation() + " nearest to next altar");
                    TileObjectInteraction.interact(tile.get(), "Place-cell");
                    timeout = tickDelay();
                }
            }
        }
    }

    private List<Widget> getDroppableRunes() {
        List<Widget> runesToDrop = new ArrayList<>();
        String[] runeFilterConfig = config.dropRunesFilter().split(",");
        for (String rune : runeFilterConfig) {
            rune = rune.trim();
            Inventory.search().matchesWildCardNoCase(rune).first().ifPresent(runesToDrop::add);
        }
        return runesToDrop;
    }

    private void dropRunes() {
        if (!config.dropRunes() && config.dropRunesFilter().isEmpty()) {
            return;
        }

        if (config.dropRunes()) {
            Optional<Widget> itemWidget = InventoryUtil.nameContainsNoCase("rune").filter(item -> !item.getName().contains("pickaxe")).first();
            if (itemWidget.isEmpty()) {
                return;
            }

            Widget item = itemWidget.get();
            InventoryInteraction.useItem(item, "Drop");
        } else {
            for (Widget rune : getDroppableRunes()) {
                InventoryInteraction.useItem(rune, "Drop");
            }
        }
    }

    private double getBarrierHealth(NPC barrier) {
        if (barrier.getHealthScale() == -1) {
            return 100;
        }

        return (double) barrier.getHealthRatio() / (double) barrier.getHealthScale() * 100;
    }

    private void craftRunes() {
        Optional<TileObject> tileObject = TileObjects.search().withAction(Constants.CRAFT_RUNES).nearestToPlayer();
        if (tileObject.isEmpty()) {
            return;
        }
        if (pouchManager.getEssenceInPouches() > 0 && Inventory.getEmptySlots() > 0) {
            pouchManager.emptyPouches();
            TileObject altar = tileObject.get();
            TileObjectInteraction.interact(altar, Constants.CRAFT_RUNES);
            timeout = tickDelay();
            return;
        }
        if (Inventory.getItemAmount(ItemID.GUARDIAN_ESSENCE) > 0) {
            TileObject altar = tileObject.get();
            TileObjectInteraction.interact(altar, Constants.CRAFT_RUNES);
            timeout = tickDelay();
        }
    }

    private TileObject lastAltar = null;

    private void enterRift() {
        TileObject nextAltar = riftState.getNextAltar();
        if (nextAltar == null) {
            return;
        }
        //This allows us to redirect to a new altar if the state switches
        if (nextAltar == lastAltar && EthanApiPlugin.isMoving()) {
            return;
        }

        lastAltar = nextAltar;
        TileObjectInteraction.interact(nextAltar, "Enter");
        timeout = tickDelay();
    }

    public static TileObjectQuery nameContainsNoCase(String name) {
        return TileObjects.search().filter(tileObject -> {
            ObjectComposition comp = TileObjectQuery.getObjectComposition(tileObject);
            if (comp == null)
                return false;
            return comp.getName().toLowerCase().contains(name.toLowerCase());
        });
    }

    private boolean hasDegradePrevention() {
        return Equipment.search().withId(ItemID.ABYSSAL_LANTERN_REDWOOD_LOGS).first().isPresent() || Equipment.search().withId(ItemID.RUNECRAFT_CAPE).first().isPresent() || Equipment.search().withId(ItemID.RUNECRAFT_CAPET).first().isPresent();
    }

    private int getFragmentCount() {
        Optional<Widget> frags = Inventory.search().withId(ItemID.GUARDIAN_FRAGMENTS).first();
        return frags.map(Widget::getItemQuantity).orElse(0);
    }
}
