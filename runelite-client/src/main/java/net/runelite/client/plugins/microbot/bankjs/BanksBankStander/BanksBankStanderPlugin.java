package net.runelite.client.plugins.microbot.bankjs.BanksBankStander;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Bank + "Bank's BankStander",
        description = "For Skilling at the Bank",
        tags = {"bankstander", "bank.js"},
        enabledByDefault = false
)
@Slf4j
public class BanksBankStanderPlugin extends Plugin {
    @Inject
    private BanksBankStanderConfig config;

    @Provides
    BanksBankStanderConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BanksBankStanderConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BanksBankStanderOverlay banksBankStanderOverlay;

    @Inject
    BanksBankStanderScript banksBankStanderScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(banksBankStanderOverlay);
        }
        banksBankStanderScript.run(config);
    }

    protected void shutDown() {
        banksBankStanderScript.shutdown();
        overlayManager.remove(banksBankStanderOverlay);
    }
}
