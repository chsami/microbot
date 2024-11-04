package net.runelite.client.plugins.microbot.gabplugs.sandminer;

import net.runelite.client.config.*;

@ConfigGroup("GabulhasSandMiner")
@ConfigInformation(
    "Mines sandstone and deposits it in the Quarry. Just start next to the grinder with a pickaxe. Optionally you can use waterskins + humidify runes (Lunar Spells)"
)
public interface GabulhasSandMinerConfig extends Config {

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
    default GabulhasSandMinerInfo.states STARTINGSTATE(){
        return GabulhasSandMinerInfo.states.Mining;
    }

}


