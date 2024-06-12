package net.runelite.client.plugins.hoseaplugins.strategyexample;


import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Inventory;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.EthanApiPlugin;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.*;
import com.google.inject.Inject;
import com.google.inject.Provides;
import net.runelite.client.plugins.hoseaplugins.PiggyUtils.API.InventoryUtil;
import net.runelite.client.plugins.hoseaplugins.PiggyUtils.API.PlayerUtil;
import net.runelite.client.plugins.hoseaplugins.PiggyUtils.strategy.AbstractTask;
import net.runelite.client.plugins.hoseaplugins.PiggyUtils.strategy.TaskManager;
import net.runelite.client.plugins.hoseaplugins.strategyexample.tasks.Banking;
import net.runelite.client.plugins.hoseaplugins.strategyexample.tasks.DoSmithing;
import net.runelite.client.plugins.hoseaplugins.strategyexample.tasks.OpenAnvil;
import net.runelite.client.plugins.hoseaplugins.strategyexample.tasks.OpenBank;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
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

@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> StrategySmith</html>",
        description = "",
        enabledByDefault = false,
        tags = {"piggy", "plugin"}
)
@Slf4j
public class StrategySmithPlugin extends Plugin {
    @Inject
    @Getter
    private Client client;
    @Inject
    private StrategySmithConfig config;
    @Inject
    private StrategySmithOverlay overlay;
    @Inject
    private KeyManager keyManager;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    @Getter
    private ClientThread clientThread;
    public boolean started = false;
    public int timeout = 0;
    public TaskManager taskManager = new TaskManager();
    public boolean isSmithing;
    public int idleTicks = 0;
    @Inject
    PlayerUtil playerUtil;

    @Provides
    private StrategySmithConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(StrategySmithConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
        timeout = 0;
        isSmithing = false;
        keyManager.registerKeyListener(toggle);
        log.info(config.bar().getName() + " - " + config.item().toString());
    }

    @Override
    protected void shutDown() throws Exception {
        isSmithing = false;
        timeout = 0;
        idleTicks = 0;
        started = false;
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
            if (idleTicks > 10 || !hasEnoughBars()) {
                timeout = 0;
                isSmithing = false;
            }
            return;
        }


        if (isSmithing) {
            if (!hasEnoughBars()) {
                isSmithing = false;
            }
            if (hasEnoughBars())
                return;
        }

        checkRunEnergy();
        if (taskManager.hasTasks()) {
            for (AbstractTask t : taskManager.getTasks()) {
                if (t.validate()) {
                    t.execute();
                    return;
                }
            }
        }

    }

    public boolean hasHammer() {
        return !Inventory.search().nameContains("Hammer").empty();
    }

    public boolean hasBarsButNotEnough() {
        return InventoryUtil.hasItem(config.bar().getName()) && !hasEnoughBars();
    }

    public boolean hasEnoughBars() {
        return (Inventory.getItemAmount(config.bar().getName()) >= config.item().getBarsRequired());
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
            taskManager.addTask(new OpenBank(this, config));
            taskManager.addTask(new Banking(this, config));
            taskManager.addTask(new OpenAnvil(this, config));
            taskManager.addTask(new DoSmithing(this, config));
        } else {
            taskManager.clearTasks();
        }
    }
}
//Strategy Abstract tasks written by poly j