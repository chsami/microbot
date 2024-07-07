package net.runelite.client.plugins.microbot.magetrainingarena;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.magetrainingarena.enums.*;
import net.runelite.client.plugins.microbot.magetrainingarena.enums.staves.AirStaves;
import net.runelite.client.plugins.microbot.magetrainingarena.enums.staves.EarthStaves;
import net.runelite.client.plugins.microbot.magetrainingarena.enums.staves.FireStaves;
import net.runelite.client.plugins.microbot.magetrainingarena.enums.staves.WaterStaves;

@ConfigGroup("mta")
public interface MageTrainingArenaConfig extends Config {
    @ConfigSection(
            name = "General",
            description = "General",
            position = 0,
            closedByDefault = false
    )
    String generalSection = "general";

    @ConfigSection(
            name = "Rewards",
            description = "Rewards",
            position = 1,
            closedByDefault = false
    )
    String rewardsSection = "rewards";

    @ConfigSection(
            name = "Staves",
            description = "Staves",
            position = 1,
            closedByDefault = true
    )
    String stavesSection = "staves";

    @ConfigItem(
            keyName = "GUIDE",
            name = "GUIDE",
            description = "GUIDE",
            position = 0,
            section = generalSection
    )
    default String GUIDE() {
        return "";
    }

    @ConfigItem(
            keyName = "Buy rewards",
            name = "Buy rewards",
            description = "Buy rewards",
            position = 1,
            section = rewardsSection
    )
    default boolean buyRewards() {
        return false;
    }

    @ConfigItem(
            keyName = "Reward",
            name = "Reward",
            description = "The reward to buy",
            position = 2,
            section = rewardsSection
    )
    default Rewards reward() {
        return Rewards.BONES_TO_PEACHES;
    }

    @ConfigItem(
            keyName = "Air staff",
            name = "Air staff",
            description = "Air staff",
            position = 3,
            section = stavesSection
    )
    default AirStaves airStaff() {
        return AirStaves.STAFF_OF_AIR;
    }

    @ConfigItem(
            keyName = "Water staff",
            name = "Water staff",
            description = "Water staff",
            position = 4,
            section = stavesSection
    )
    default WaterStaves waterStaff() {
        return WaterStaves.MUD_BATTLESTAFF;
    }

    @ConfigItem(
            keyName = "Earth staff",
            name = "Earth staff",
            description = "Earth staff",
            position = 5,
            section = stavesSection
    )
    default EarthStaves earthStaff() {
        return EarthStaves.MUD_BATTLESTAFF;
    }

    @ConfigItem(
            keyName = "Fire staff",
            name = "Fire staff",
            description = "Fire staff",
            position = 6,
            section = stavesSection
    )
    default FireStaves fireStaff() {
        return FireStaves.LAVA_BATTLESTAFF;
    }
}
