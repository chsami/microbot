package net.runelite.client.plugins.microbot.crafting.jewelry;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.crafting.jewelry.enums.*;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
@PluginDescriptor(
        name = PluginDescriptor.GMason + "Jewelry Crafter",
        description = "All in one jewelry crafter",
        tags = {"crafting", "magic", "microbot", "skilling"},
        enabledByDefault = false
)
public class JewelryPlugin extends Plugin {
    
    @Inject
    private JewelryConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private JewelryOverlay jewelryOverlay;
    
    @Inject
    private JewelryScript jewelryScript;

    @Getter
    private Jewelry jewelry;
    @Getter
    private CraftingLocation craftingLocation;
    @Getter
    private CompletionAction completionAction;
    @Getter
    private Staff staff;
    @Getter
    private boolean useRunePouch;
    
    @Provides
    JewelryConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(JewelryConfig.class);
    }

    @Override
    protected void startUp() throws AWTException {
        jewelry = config.jewelry();
        craftingLocation = config.craftingLocation();
        completionAction = config.completionAction();
        staff = config.staff();
        useRunePouch = config.useRunePouch();
        
        jewelryScript.run();
        if (overlayManager != null) {
            overlayManager.add(jewelryOverlay);
        }
    }

    protected void shutDown() {
        overlayManager.remove(jewelryOverlay);
        jewelryScript.shutdown();
    }
    
    @Subscribe
    public void onConfigChanged(final ConfigChanged event){
        if (!(event.getGroup().equals(JewelryConfig.configGroup))) return;
        
        if (event.getKey().equals(JewelryConfig.jewelry)) {
            jewelry = config.jewelry();
        }
        
        if (event.getKey().equals(JewelryConfig.craftingLocation)) {
            craftingLocation = config.craftingLocation();
        }

        if (event.getKey().equals(JewelryConfig.completionAction)) {
            completionAction = config.completionAction();
        }

        if (event.getKey().equals(JewelryConfig.staff)) {
            staff = config.staff();
        }

        if (event.getKey().equals(JewelryConfig.useRunePouch)) {
            useRunePouch = config.useRunePouch();
        }
    }
}
