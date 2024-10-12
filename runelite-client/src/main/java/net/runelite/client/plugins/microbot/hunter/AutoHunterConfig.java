package net.runelite.client.plugins.microbot.hunter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigInformation;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("AutoHunter")
@ConfigInformation("1. This script only supports box catching.<br/> 2. Place your box traps down <br/> 3. Enable Plugin")
public interface AutoHunterConfig extends Config {
    @ConfigItem(
            position = 1,
            keyName = "MinSleepAfterCatch",
            name = "Min. Sleep After Catch",
            description = "Min sleep after catch"
    )
    default int minSleepAfterCatch() {
        return 8300;
    }
    @ConfigItem(
            position = 2,
            keyName = "MaxSleepAfterCatch",
            name = "Max. Sleep After Catch",
            description = "Max sleep after catch"
    )
    default int maxSleepAfterCatch() {
        return 8400;
    }
    @ConfigItem(
            position = 3,
            keyName = "MinSleepAfterLay",
            name = "Min. Sleep After Lay",
            description = "Min sleep after lay"
    )
    default int minSleepAfterLay() {
        return 5500;
    }
    @ConfigItem(
            position = 4,
            keyName = "MaxSleepAfterLay",
            name = "Max. Sleep After Lay",
            description = "Max sleep after lay"
    )
    default int maxSleepAfterLay() {
        return 5700;
    }
}
