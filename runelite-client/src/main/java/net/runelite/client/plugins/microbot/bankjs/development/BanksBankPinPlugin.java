package net.runelite.client.plugins.microbot.bankjs.development;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Bank + "Bank's BankPin",
        description = "Auto completes bank pin",
        tags = {"bank pin", "bank.js"},
        enabledByDefault = true
)
@Slf4j
public class BanksBankPinPlugin extends Plugin {
    @Inject
    private BanksBankPinConfig config;

    @Provides
    BanksBankPinConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BanksBankPinConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BanksBankPinOverlay banksBankPinOverlay;

    @Inject
    BanksBankPinScript banksBankPinScript;


    @Override
    protected void startUp() throws AWTException {
        banksBankPinScript.run(config);
    }

    protected void shutDown() {
        banksBankPinScript.shutdown();
        overlayManager.remove(banksBankPinOverlay);
    }
}
