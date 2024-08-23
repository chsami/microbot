package net.runelite.client.plugins.autoeat;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("autoeat")
public interface AutoEatConfig extends Config {

    @ConfigItem(
            keyName = "healthThreshold",
            name = "Health Threshold",
            description = "The percentage of health at which to eat food"
    )
    default int healthThreshold() {
        return 50;  // default value
    }

    @ConfigItem(
            keyName = "allowedFood",
            name = "Allowed Food",
            description = "Comma-separated list of allowed food names to eat"
    )
    default String allowedFood() {
        return "Shark,Lobster,Swordfish";  // default allowed foods
    }
}
