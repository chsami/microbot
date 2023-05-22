package net.runelite.client.plugins.microbot.crafting;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.crafting.enums.Gems;
import net.runelite.client.plugins.microbot.util.mouse.VirtualMouse;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = "Micro Crafting",
        description = "Microbot crafting plugin",
        tags = {"skilling", "microbot", "crafting"},
        enabledByDefault = false
)
@Slf4j
public class CraftingPlugin extends Plugin {

    @Inject
    private CraftingConfig config;

    @Provides
    CraftingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(CraftingConfig.class);
    }

    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private Notifier notifier;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private CraftingOverlay constructionOverlay;

    private CraftingScript craftingScript = new CraftingScript();
    private GemCraftingScript gemCraftingScript = new GemCraftingScript();

    @Override
    protected void startUp() throws AWTException {
        Microbot.pauseAllScripts = false;
        Microbot.setClient(client);
        Microbot.setClientThread(clientThread);
        Microbot.setNotifier(notifier);
        Microbot.setMouse(new VirtualMouse());
        if (overlayManager != null) {
            overlayManager.add(constructionOverlay);
        }
        if (config.gemType() != Gems.NONE) {
            gemCraftingScript.run(config);
        } else { //add config for hides
            craftingScript.run(config);
        }
    }

    protected void shutDown() {
        gemCraftingScript.shutdown();
        craftingScript.shutdown();
        overlayManager.remove(constructionOverlay);
    }
}
