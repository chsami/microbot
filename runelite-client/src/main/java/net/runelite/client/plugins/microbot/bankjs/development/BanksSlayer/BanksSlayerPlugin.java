package net.runelite.client.plugins.microbot.bankjs.development.BanksSlayer;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;


@PluginDescriptor(
        name = PluginDescriptor.Bank + "Bank's AIO Slayer",
        description = "Bank's AIO Slayer",
        tags = {"slayer", "bank.js"},
        enabledByDefault = false
)
@Slf4j
public class BanksSlayerPlugin extends Plugin {
    @Inject
    private BanksSlayerConfig config;

    @Provides
    BanksSlayerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BanksSlayerConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BanksSlayerOverlay banksSlayerOverlay;

    @Inject
    BanksSlayerScript banksSlayerScript;

    @Inject
    private ClientThread clientThread;

    @Inject
    Client client;


    @Override
    protected void startUp() throws AWTException {
        banksSlayerScript.run(config);
    }

    protected void shutDown() {
        banksSlayerScript.shutdown();
        overlayManager.remove(banksSlayerOverlay);
    }

        
}
