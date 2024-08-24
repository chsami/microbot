package net.runelite.client.plugins.microbot.autoeat;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import javax.inject.Inject;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

@PluginDescriptor(
        name = PluginDescriptor.xsvl + "Auto Eat",
        description = "Automatically eats food when health is below a certain threshold.",
        tags = {"auto", "eat", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class AutoEatPlugin extends Plugin {
    @Inject
    private AutoEatConfig config;

    @Provides
    AutoEatConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(AutoEatConfig.class);
    }

    @Override
    protected void startUp() throws AWTException {
        log.info("Auto Eat Plugin started.");
    }

    @Override
    protected void shutDown() {
        log.info("Auto Eat Plugin stopped.");
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        // Get the allowed foods from the config
        String allowedFoodConfig = config.allowedFood();
        List<String> allowedFoodList = Arrays.asList(allowedFoodConfig.split(",\\s*"));

        // Use eatAt to consume food when health is below the threshold, considering allowed foods
        Rs2Player.eatAt(config.healthThreshold(), allowedFoodList);
    }
}
