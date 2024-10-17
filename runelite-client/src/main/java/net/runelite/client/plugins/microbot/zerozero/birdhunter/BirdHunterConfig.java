package net.runelite.client.plugins.microbot.zerozero.birdhunter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.plugins.microbot.zerozero.enums.hunter.Birds;

@ConfigGroup("birdhunter")
public interface BirdHunterConfig extends Config {

    @ConfigItem(
            keyName = "bird",
            name = "Bird Type",
            description = "Select the type of bird to hunt",
            position = 0
    )
    default Birds BIRD() {
        return Birds.CRIMSON;
    }

    @ConfigItem(
            keyName = "buryBones",
            name = "Bury Bones",
            description = "Select whether to bury bones during hunting",
            position = 2
    )
    default boolean buryBones() {
        return true;
    }

    @ConfigItem(
            keyName = "keepItemNames",
            name = "Keep Item Names",
            description = "Comma-separated list of item names that should not be dropped",
            position = 3
    )
    default String keepItemNames() {
        return "Bird snare";
    }
}
