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
        return "Bird snare";  // Default to Bird Snare
    }

    @ConfigItem(
            keyName = "showAreaOverlay",
            name = "Area Overlay",
            description = "Toggle to show or hide the bird hunting area overlay",
            position = 4
    )
    default boolean showAreaOverlay() {
        return true;  // Default to show the area overlay
    }

    @ConfigItem(
            keyName = "startScript",
            name = "Start/Stop Script",
            description = "Toggle to start or stop the Bird Hunter script",
            position = 5
    )
    default boolean startScript() {
        return false; // Default is off
    }
}
