package net.runelite.client.plugins.microbot.bankjs.development.BanksShopper;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Bank + "Bank's Shopper",
        description = "Bank's Auto Shopper",
        tags = {"shopper", "bank.js"},
        enabledByDefault = false
)
@Slf4j
public class BanksShopperPlugin extends Plugin {
    @Inject
    private BanksShopperConfig config;

    @Provides
    BanksShopperConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BanksShopperConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BanksShopperOverlay banksShopperOverlay;

    @Inject
    BanksShopperScript banksShopperScript;


    @Override
    protected void startUp() throws AWTException {
        banksShopperScript.run(config);
    }

    protected void shutDown() {
        banksShopperScript.shutdown();
        overlayManager.remove(banksShopperOverlay);
    }
}
