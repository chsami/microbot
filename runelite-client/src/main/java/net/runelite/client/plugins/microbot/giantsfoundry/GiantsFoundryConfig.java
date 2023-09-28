package net.runelite.client.plugins.microbot.giantsfoundry;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(GiantsFoundryConfig.GROUP)
public interface GiantsFoundryConfig extends Config {

    String GROUP = "GiantsFoundry";

    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 1
    )
    default String GUIDE() {
        return "Start at the giants foundry minigame\n" +
                "Make sure to have mithril bars and steel bars in your bank\n" +
                "Make sure to babysit the script!!\n" +
                "Zoom out a lot as seen in this video: https://www.youtube.com/watch?v=OCSHhRXoH1U&t=3s";
    }
}
