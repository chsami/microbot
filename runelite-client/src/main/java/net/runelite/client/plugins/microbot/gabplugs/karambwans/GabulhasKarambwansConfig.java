package net.runelite.client.plugins.microbot.gabplugs.karambwans;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("GabulhasKarambwans")
public interface GabulhasKarambwansConfig extends Config {
    @ConfigSection(
            name = "Guide",
            description = "Guide",
            position = 1
    )
    String guideSection = "Guide";

    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 1,
            section = guideSection
    )
    default String GUIDE() {
        return "";
    }

    @ConfigSection(
            name="Starting State",
            description = "Starting State",
            position = 2
    )
    String startingStateSection = "startingStateSection";
    @ConfigItem(
            keyName = "startingState",
            name = "Starting State",
            description = "Starting State",
            position = 2,
            section = startingStateSection
    )
    default GabulhasKarambwansInfo.states STARTINGSTATE(){
        return GabulhasKarambwansInfo.states.FISHING;
    }

}


