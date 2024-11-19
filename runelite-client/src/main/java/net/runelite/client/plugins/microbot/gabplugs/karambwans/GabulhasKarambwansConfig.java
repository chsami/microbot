package net.runelite.client.plugins.microbot.gabplugs.karambwans;

import net.runelite.client.config.*;

@ConfigGroup("GabulhasKarambwans")
@ConfigInformation(
        "<ol><li>Configure the fairy rings to DKP at least once</li><li>Make sure to have karambwan vessel and raw karambwanji</li><li>Start the script next to the karambwan fishing spot</li></ol>"
)
public interface GabulhasKarambwansConfig extends Config {

    @ConfigSection(
            name="Starting State",
            description = "Starting State",
            position = 1
    )
    String startingStateSection = "startingStateSection";
    @ConfigItem(
            keyName = "startingState",
            name = "(Debug) Starting State",
            description = "Starting State. Only used for development.",
            position = 1,
            section = startingStateSection
    )
    default GabulhasKarambwansInfo.states STARTINGSTATE(){
        return GabulhasKarambwansInfo.states.FISHING;
    }

}


