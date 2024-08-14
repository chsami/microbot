package net.runelite.client.plugins.microbot.CrashedStar;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("crashedStar")
public interface CrashedStarConfig extends Config {
    @ConfigSection(
            name = "Guide",
            description = "Guide",
            position = 1
    )
    String guideSection = "Guide";

    @ConfigItem(
            keyName = "Guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 1,
            section = guideSection
    )
    default String GUIDE() {
        return "Start next to a Crashed Star\n" +
                "Plugin will automatically mine best tier\n" +
                "If Dragon Pick is equipped, it will use the special\n";
    }
}
