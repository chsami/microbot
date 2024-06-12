package net.runelite.client.plugins.hoseaplugins.SpeedDartMaker;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Inventory;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.MousePackets;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.WidgetPackets;
import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.HotkeyListener;

@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> Speed Dart Maker</font>",
        description = "Fletches up to 10 darts per tick",
        enabledByDefault = false,
        tags = {"ethan", "piggy"}
)
@Slf4j
public class SpeedDartMakerPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private SpeedDartMakerConfig config;
    @Inject
    private KeyManager keyManager;

    private boolean started = false;

    private String tipSearch = "dart tip";

    @Provides
    private SpeedDartMakerConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(SpeedDartMakerConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        keyManager.registerKeyListener(toggle);
    }

    @Override
    protected void shutDown() throws Exception {
        keyManager.unregisterKeyListener(toggle);
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        tipSearch = config.broadBolts() ? "Unfinished broad bolt" : "dart tip";
        if (client.getGameState() != GameState.LOGGED_IN
                || !started
                || !hasDarts()
                || !hasFeather()) {
            return;
        }
        fletchDarts();
    }

    private void fletchDarts() {
        Widget feather = Inventory.search().nameContains("Feather").first().get();

        Inventory.search().nameContains(tipSearch).first().ifPresent(item -> {
            for (int i = 0; i < config.perTick(); i++) {
                MousePackets.queueClickPacket();
                MousePackets.queueClickPacket();
                WidgetPackets.queueWidgetOnWidget(item, feather);
            }
        });


    }

    private boolean hasDarts() {
        return Inventory.search().nameContains(tipSearch).first().isPresent();
    }

    private boolean hasFeather() {
        return Inventory.search().nameContains("Feather").first().isPresent();
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
