package net.runelite.client.plugins.microbot.gabplugs.goldrush;

import net.runelite.client.config.*;

@ConfigGroup("GabulhasGoldRush")
@ConfigInformation("<ol><li>Make sure to have gold ore in your bank</li><li>Start inside the Blast Furnace room</li></ol>")
public interface GabulhasGoldRushConfig extends Config {


    @ConfigSection(
            name="Starting State",
            description = "Starting State",
            position = 1
    )
    String startingStateSection = "startingStateSection";
    @ConfigItem(
            keyName = "startingState",
            name = "Starting State",
            description = "Starting State",
            position = 1,
            section = startingStateSection
    )
    default GabulhasGoldRushInfo.states STARTINGSTATE(){
        return GabulhasGoldRushInfo.states.GETTING_BARS;
    }

}


