package net.runelite.client.plugins.hoseaplugins.AutoCombatv2;


import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.EthanApiPlugin;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.*;
import com.google.inject.Inject;
import com.google.inject.Provides;
import net.runelite.client.plugins.hoseaplugins.AutoCombatv2.tasks.CheckCombatStatus;
import net.runelite.client.plugins.hoseaplugins.AutoCombatv2.tasks.LootItems;
import net.runelite.client.plugins.hoseaplugins.AutoCombatv2.tasks.attackNPC;
import net.runelite.client.plugins.hoseaplugins.AutoCombatv2.tasks.checkStats;
import net.runelite.client.plugins.hoseaplugins.PiggyUtils.API.PlayerUtil;
import net.runelite.client.plugins.hoseaplugins.PiggyUtils.strategy.AbstractTask;
import net.runelite.client.plugins.hoseaplugins.PiggyUtils.strategy.TaskManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemSpawned;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

@PluginDescriptor(
        name = "<html><font color=\"#ff4d00\">[BS]</font> Auto Combat v2</html>",
        description = "",
        enabledByDefault = false,
        tags = {"BS", "piggy", "PP", "plugin"}
)
@Slf4j
public class AutoCombatv2Plugin extends Plugin {
    @Inject
    @Getter
    private Client client;
    @Inject
    private AutoCombatv2Config config;
    @Inject
    private AutoCombatv2Overlay overlay;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    @Getter
    private ItemManager itemManager;
    @Inject
    @Getter
    private ClientThread clientThread;
    public boolean started = false;
    public int timeout = 0;
    public TaskManager taskManager = new TaskManager();
    public boolean inCombat;
    public int idleTicks = 0;
    @Inject
    PlayerUtil playerUtil;
    @Getter
    private Set<String> lootItems = new HashSet<>();
    @Getter
    private Queue<Pair<TileItem, Tile>> lootQueue = new ConcurrentLinkedQueue<>();

    @Provides
    private AutoCombatv2Config getConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoCombatv2Config.class);
    }

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
        timeout = 0;
        inCombat = false;
        keyManager.registerKeyListener(toggle);
    }

    @Override
    protected void shutDown() throws Exception {
        inCombat = false;
        timeout = 0;
        idleTicks = 0;
        started = false;
        lootQueue.clear();
        keyManager.unregisterKeyListener(toggle);
        overlayManager.remove(overlay);
    }


    @Subscribe
    private void onGameTick(GameTick event) {
        if (client.getGameState() != GameState.LOGGED_IN || !started) {
            return;
        }

        if (playerUtil.isInteracting() || client.getLocalPlayer().getAnimation() == -1) {
            idleTicks++;
        } else {
            idleTicks = 0;
        }

        if (timeout > 0) {
            timeout--;
            log.info("Timeout: {}", timeout);
            return;
        }

        log.info("Game tick observed. Queue size before any operation: {}", lootQueue.size());
        // Existing logic here
        checkRunEnergy();
        if (taskManager.hasTasks()) {
            for (AbstractTask t : taskManager.getTasks()) {
                if (t.validate()) {
                    t.execute();
                    break;
                }
            }
        }
        log.info("Game tick processing completed. Queue size after operations: {}", lootQueue.size());
    }

    @Subscribe
    private void onItemSpawned(ItemSpawned event) {
        TileItem tileItem = event.getItem();
        Tile tile = event.getTile(); // This is how you get the Tile from the event

        if (tileItem != null) {
            ItemComposition composition = itemManager.getItemComposition(tileItem.getId());
            if (isLootable(composition.getName())) {
                lootQueue.add(Pair.of(tileItem, tile)); // Store both the TileItem and the Tile
                log.info("Loot added: {} at {}", composition.getName(), tile.getWorldLocation());
            }
        }
    }

    private boolean isLootable(String itemName) {
        return config.loot().contains(itemName);
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
        if (started) {
            taskManager.addTask(new attackNPC(this, config));
            taskManager.addTask(new LootItems(this, config));
            taskManager.addTask(new CheckCombatStatus(this, config));
            taskManager.addTask(new checkStats(this, config));

        } else {
            taskManager.clearTasks();
        }
    }
}