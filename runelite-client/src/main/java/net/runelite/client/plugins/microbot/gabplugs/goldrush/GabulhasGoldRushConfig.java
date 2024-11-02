package net.runelite.client.plugins.microbot.gabplugs.goldrush;

import net.runelite.client.config.*;

@ConfigGroup("GabulhasGoldRush")
@ConfigInformation("<ol><li>Make sure to have gold ore in your bank</li><li>Start inside the Blast Furnace room</li></ol>")
public interface GabulhasGoldRushConfig extends Config {
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
        return
                "1. make sure to have gold ore in your bank\n" +
                "2. start inside the Blast Furnace room"
                ;
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
    default GabulhasGoldRushInfo.states STARTINGSTATE(){
        return GabulhasGoldRushInfo.states.GETTING_BARS;
    }

}


