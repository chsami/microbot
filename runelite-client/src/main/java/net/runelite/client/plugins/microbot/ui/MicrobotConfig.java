package net.runelite.client.plugins.microbot.ui;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(MicrobotConfig.GROUP_NAME)
public interface MicrobotConfig extends Config {
    String GROUP_NAME = "microbot";

    @ConfigSection(
            name = "Window Settings",
            description = "Settings relating to the client's window and frame",
            position = 0
    )
    String windowSettings = "windowSettings";
}
