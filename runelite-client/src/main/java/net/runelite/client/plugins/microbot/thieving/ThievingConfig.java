package net.runelite.client.plugins.microbot.thieving;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.thieving.enums.ThievingNpc;

@ConfigGroup("Thieving")
public interface ThievingConfig extends Config {
    @ConfigSection(
            name = "General",
            description = "General",
            position = 0,
            closedByDefault = false
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "Npc",
            name = "Npc",
            description = "Choose the npc to start thieving from",
            position = 0,
            section = generalSection
    )
    default ThievingNpc THIEVING_NPC()
    {
        return ThievingNpc.MEN;
    }

    @ConfigItem(
            keyName = "Hitpoints",
            name = "Hitpoints treshhold",
            description = "Use food at certain hitpoint treshhold",
            position = 2,
            section = generalSection
    )
    default int hitpoints()
    {
        return 20;
    }
}
