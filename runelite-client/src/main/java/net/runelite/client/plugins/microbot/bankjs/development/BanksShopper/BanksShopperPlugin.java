package net.runelite.client.plugins.microbot.bankjs.development.BanksShopper;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.shop.Rs2Shop;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

import static net.runelite.client.plugins.microbot.util.Global.sleep;

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
        if (overlayManager != null) {
            overlayManager.add(banksShopperOverlay);
        }
        banksShopperScript.run(config);
    }

    protected void shutDown() {
        banksShopperScript.shutdown();
        overlayManager.remove(banksShopperOverlay);
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        if (event.getType() == ChatMessageType.GAMEMESSAGE && !Microbot.pauseAllScripts) {
            if (event.getMessage().toLowerCase().contains("you don't have enough coins.")) {
                Microbot.status = "[Shutting down] - Reason: Not enough coins.";
                Microbot.showMessage(Microbot.status);
                banksShopperScript.shutdown();
                Rs2Shop.closeShop();
                if (config.logout()) {
                    Microbot.getClientThread().runOnSeperateThread(() -> {
                        sleep(1200);
                        Rs2Player.logout();
                        return true;
                    });
                }
            }
        }
    }
}
