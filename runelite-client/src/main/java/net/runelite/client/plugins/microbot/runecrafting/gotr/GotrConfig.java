package net.runelite.client.plugins.microbot.runecrafting.gotr;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigInformation;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.plugins.microbot.runecrafting.gotr.data.Mode;

@ConfigGroup("gotr")
@ConfigInformation("This plugin is in preview & only supports masses. <br /> The script will not create elemental guardians. <br /> Have fun and don't get banned!")
public interface GotrConfig extends Config {

    @ConfigItem(
            keyName = "Mode",
            name = "Mode",
            description = "Type of mode",
            position = 0
    )
    default Mode Mode() {
        return Mode.BALANCED;
    }

    @ConfigItem(
            keyName = "maxFragmentAmount",
            name = "Max. amount fragments",
            description = "Max amount fragments to collect",
            position = 1
    )
    default int maxFragmentAmount() {
        return 100;
    }

    @ConfigItem(
            keyName = "maxAmountEssence",
            name = "Max. amount essence before using portal",
            description = "If you have more than the threshold defined, the player will not use the portal",
            position = 2
    )
    default int maxAmountEssence() {
        return 20;
    }
}
