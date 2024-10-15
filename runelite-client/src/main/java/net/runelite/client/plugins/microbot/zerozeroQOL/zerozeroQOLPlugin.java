package net.runelite.client.plugins.microbot.zerozeroQOL;

import com.google.inject.Provides;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@PluginDescriptor(
        name = "ZeroZero QOL",
        description = "Quality of Life improvements, like walking to the nearest bank or Varrock tree.",
        tags = {"zerozero", "qol"}
)
public class zerozeroQOLPlugin extends Plugin {

    @Inject
    private zerozeroQOLConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private zerozeroQOLOverlay overlay;

    @Inject
    private ChatMessageManager chatMessageManager;

    private zerozeroQOLScript script;

    @Provides
    zerozeroQOLConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(zerozeroQOLConfig.class);
    }

    @Override
    protected void startUp() throws Exception {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() throws Exception {
        overlayManager.remove(overlay);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("zerozeroQOL")) {
            WalkLocation location = determineLocation();
            if (location != null) {
                walkTo(location);
            } else {
                // Handle case when no valid option is selected, or both are off
                // You can show a message or simply do nothing
            }
        }
    }

    // Method to determine the location based on the config options
    private WalkLocation determineLocation() {
        if (config.walkToBank()) {
            return WalkLocation.BANK;
        } else if (config.walkToVarrockTree()) {
            return WalkLocation.VARROCK_TREE;
        } else {
            return null;  // No valid location selected
        }
    }

    // Method to pass the WalkLocation enum to the script
    private void walkTo(WalkLocation location) {
        script = new zerozeroQOLScript(config, chatMessageManager, location);
        script.run();  // Run the script to walk to the location based on the enum
    }
}
