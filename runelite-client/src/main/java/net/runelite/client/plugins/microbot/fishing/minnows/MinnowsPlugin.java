package net.runelite.client.plugins.microbot.fishing.minnows;

import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.See1Duck + "Auto Minnows",
        description = "Microbot minnows plugin",
        tags = {"minnows", "microbot"},
        enabledByDefault = false
)
public class MinnowsPlugin extends Plugin {
/*    @Inject
    private MinnowsConfig config;
    @Provides
    MinnowsConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(MinnowsConfig.class);
    }*/


    @Inject
    MinnowsScript minnowsScript;


    @Override
    protected void startUp() throws AWTException {
        minnowsScript.run();
    }

    protected void shutDown() {
        minnowsScript.shutdown();
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        minnowsScript.onGameTick();

    }

}