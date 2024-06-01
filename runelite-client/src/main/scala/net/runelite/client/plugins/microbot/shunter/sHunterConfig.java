package net.runelite.client.plugins.microbot.shunter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.fletching.enums.FletchingMode;
import net.runelite.client.plugins.microbot.shunter.enums.hunterMode;
import net.runelite.client.plugins.microbot.shunter.enums.salamanderMode;

@ConfigGroup("hunter")
public interface sHunterConfig extends Config {
    @ConfigSection(
            name = "General",
            description = "General",
            position = 0
    )
    String generalSection = "general";
    @ConfigItem(
            keyName = "Mode",
            name = "Mode",
            description = "Choose your mode of hunter",
            position = 0,
            section = generalSection
    )
    default hunterMode hunterMode() {return hunterMode.BUTTERFLY;}
    @ConfigItem(
            keyName = "Salamanders",
            name = "Salamanders",
            description = "Which salamander to hunt?",
            position = 1,
            section = generalSection
    )
    default salamanderMode salamanderMode() { return salamanderMode.NONE; }
}
