package net.runelite.client.plugins.microbot.holidayevent;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("holiday")
public interface HolidayConfig extends Config {
    String GROUP = "holiday";

    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "Notes on the plugin",
            position = 0
    )

    default String GUIDE() {
        return "Start the plugin anywhere on the map. Activate QoL Auto Dialogue option to help with the dialogues. Turning off plugin resets chain of events.  ";
    }

    @ConfigItem(
            keyName = "showCutscenePopup",
            name = "Show Cutscene Popup",
            description = "Toggle whether the popup to cancel cutscenes should appear."
    )
    default boolean showCutscenePopup() {
        return true; // Default to true
    }

    @ConfigItem(
            keyName = "collectSnow",
            name = "Collect Snow",
                description = "Only use if 1 tile next to snow. Will interact with snow on the client thread when near logout."
    )
    default boolean collectSnow() {
        return true; // Default to true
    }



}
