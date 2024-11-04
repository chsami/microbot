package net.runelite.client.plugins.microbot.zerozero.birdhunter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("birdhunter")
public interface BirdHunterConfig extends Config {

    @ConfigItem(
            keyName = "radius",
            name = "Hunting Radius",
            description = "Set the radius for the hunting area",
            position = 1
    )
    default int radius() {
        return 4;
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


    @ConfigItem(
            keyName = "startScript",
            name = "Start/Stop Script",
            description = "Toggle to start or stop the Bird Hunter script",
            position = 5
    )
    default boolean startScript() {
        return false;
    }
}
