package net.runelite.client.plugins.microbot.zerozero.birdhunter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.zerozero.enums.hunter.Birds;

@ConfigGroup("Hunter")
public interface BirdHunterConfig extends Config {
    @ConfigSection(
            name = "General",
            description = "General",
            position = 0
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "Bird",
            name = "Bird",
            description = "Choose the bird type",
            position = 0,
            section = generalSection
    )
    default Birds BIRD()
    {
        return Birds.TROPICAL;
    }

    @ConfigItem(
            keyName = "BuryBones",
            name = "Bury Bones",
            description = "Bury Bones?",
            position = 0,
            section = generalSection
    )
    default boolean buryBones()
    {
        return false;
    }

    @ConfigItem(
            keyName = "DistanceToStray",
            name = "Distance to Stray",
            description = "Set how far you can travel from your initial position in tiles",
            position = 2,
            section = generalSection
    )
    default int distanceToStray()
    {
        return 10;
    }

}