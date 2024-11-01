package net.runelite.client.plugins.microbot.zerozero.varrockcleaner;

import com.google.inject.Provides;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;

import javax.inject.Inject;

@PluginDescriptor(
        name = PluginDescriptor.zerozero + "Museum Cleaner",
        description = "Varrock Museum Cleaner",
        tags = {"varrock", "museum", "cleaner"},
        enabledByDefault = false
)
public class VarrockCleanerPlugin extends Plugin {
    static final String CONFIG = "varrockmuseum";

    @Inject
    private VarrockCleanerScript script;

    @Inject
    private VarrockCleanerConfig config;

    @Override
    protected void startUp() {
        if (config.startPlugin()) {
            script.run(config);
        }
    }

    @Override
    protected void shutDown() {
        script.stop();
    }

    @Provides
    VarrockCleanerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(VarrockCleanerConfig.class);
    }
}
