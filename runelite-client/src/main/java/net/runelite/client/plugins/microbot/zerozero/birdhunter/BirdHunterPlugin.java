package net.runelite.client.plugins.microbot.zerozero.birdhunter;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;

@PluginDescriptor(
        name = PluginDescriptor.zerozero + "Bird Hunter",
        description = "Hunts birds",
        tags = {"hunting", "00", "bird", "skilling"},
        enabledByDefault = false
)
@Slf4j
public class BirdHunterPlugin extends Plugin {

    @Inject
    private BirdHunterConfig config;

    @Inject
    private BirdHunterScript birdHunterScript;

    @Provides
    BirdHunterConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BirdHunterConfig.class);
    }

    @Override
    protected void startUp() {
        if (config.startScript()) {
            birdHunterScript.run(config);
        }
    }

    @Override
    protected void shutDown() {
        birdHunterScript.shutdown();
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("birdhunter") && event.getKey().equals("startScript")) {
            if (config.startScript()) {
                birdHunterScript.run(config);
            } else {
                birdHunterScript.shutdown();
            }
        }
        if (event.getKey().equals("radius")) {
            birdHunterScript.updateHuntingArea(config);
        }
    }
}
