package net.runelite.client.plugins.microbot.bankjs.development.other;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


@PluginDescriptor(
        name = PluginDescriptor.Bank + "Bank's Test",
        description = "Test",
        tags = {"slayer", "bank.js"},
        enabledByDefault = false
)
@Slf4j
public class BanksTestPlugin extends Plugin {
    @Inject
    private BanksTestConfig config;

    @Provides
    BanksTestConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BanksTestConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private BanksTestOverlay banksTestOverlay;

    @Inject
    private BanksTestScript banksTestScript;

    @Inject
    private ScheduledExecutorService scheduler;


    @Override
    protected void startUp() throws AWTException {
        banksTestScript.run(config);
        overlayManager.add(banksTestOverlay);
        scheduler = Executors.newScheduledThreadPool(1);

    }

    @Override
    protected void shutDown() {
        overlayManager.remove(banksTestOverlay);
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
    }





}
