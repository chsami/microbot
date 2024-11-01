package net.runelite.client.plugins.microbot.zerozero.bluedragons;

import com.google.inject.Provides;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;

@PluginDescriptor(
        name = PluginDescriptor.zerozero + "Blue Dragons",
        description = "Blue dragon farmer for bones",
        tags = {"blue", "dragons", "prayer"},
        enabledByDefault = false
)
public class BlueDragonsPlugin extends Plugin {
    static final String CONFIG = "bluedragons";

    @Inject
    private BlueDragonsScript script;

    @Inject
    private BlueDragonsConfig config;

    @Override
    protected void startUp() {
        if (config.startPlugin()) {
            script.run(config);
        }
    }

    @Override
    protected void shutDown() {
        Microbot.log("Stopping Blue Dragons plugin...");
        script.stop();
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        if (event.getGroup().equals("bluedragons")) {

            switch (event.getKey()) {
                case "startPlugin":
                    if (config.startPlugin()) {
                        Microbot.log("Starting Blue Dragon plugin...");
                        script.run(config);
                    } else {
                        Microbot.log("Stopping Blue Dragon plugin!");
                        script.stop();
                    }
                    break;

                case "lootDragonhide":
                case "foodType":
                case "foodAmount":
                case "eatAtHealthPercent":
                case "lootEnsouledHead":
                    Microbot.log("Configuration changed. Updating script settings.");
                    if (config.startPlugin()) {
                        script.updateConfig(config);
                    }
                    break;

                default:
                    break;
            }
        }
    }


    @Provides
    BlueDragonsConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BlueDragonsConfig.class);
    }
}
