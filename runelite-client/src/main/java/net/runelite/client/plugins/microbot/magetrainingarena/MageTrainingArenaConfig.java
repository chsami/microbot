package net.runelite.client.plugins.microbot.magetrainingarena;

import net.runelite.client.config.*;
import net.runelite.client.plugins.microbot.magetrainingarena.enums.*;
import net.runelite.client.plugins.microbot.magetrainingarena.enums.staves.AirStaves;
import net.runelite.client.plugins.microbot.magetrainingarena.enums.staves.EarthStaves;
import net.runelite.client.plugins.microbot.magetrainingarena.enums.staves.FireStaves;
import net.runelite.client.plugins.microbot.magetrainingarena.enums.staves.WaterStaves;

@ConfigGroup("mta")
@ConfigInformation("- Make sure to enable the official RuneLite plugin 'Mage Training Arena'<br />" +
        "- You need to configure the staves to use and have the requirements to equip all of them <br />" +
        "- Your inventory should only contain all configured staves and Law/Cosmic/Nature runes <br />" +
        "- To use T6 enchants you need to have any lava staff configured as your fire staff <br />" +
        "- If set to buy rewards, the rooms are cycled though until the requirements are met and the reward will be stored in your bank <br />" +
        "- If not set to buy rewards, the rooms are cycled as if it would buy the rewards but just keep going the same way afterwards <br />" +
        "- The bot will stop if you are out of runes <br />" +
        "- Rune pouches are not supported yet")
public interface MageTrainingArenaConfig extends Config {
    @ConfigSection(
            name = "Rewards",
            description = "Rewards",
            position = 2
    )
    String rewardsSection = "rewards";

    @ConfigSection(
            name = "Staves",
            description = "Staves",
            position = 3,
            closedByDefault = true
    )
    String stavesSection = "staves";

    @ConfigSection(
            name = "Graveyard",
            description = "Graveyard",
            position = 4,
            closedByDefault = true
    )
    String graveyardSection = "graveyard";

    @ConfigItem(
            keyName = "Buy rewards",
            name = "Buy rewards",
            description = "Determines whether the bot should buy the selected reward.",
            position = 1,
            section = rewardsSection
    )
    default boolean buyRewards() {
        return true;
    }

    @ConfigItem(
            keyName = "Reward",
            name = "Reward",
            description = "The reward to aim for.",
            position = 2,
            section = rewardsSection
    )
    default Rewards reward() {
        return Rewards.BONES_TO_PEACHES;
    }

    @ConfigItem(
            keyName = "Air staff",
            name = "Air staff",
            description = "Air staff is used for teleknetic room",
            position = 3,
            section = stavesSection
    )
    default AirStaves airStaff() {
        return AirStaves.STAFF_OF_AIR;
    }

    @ConfigItem(
            keyName = "Water staff",
            name = "Water staff",
            description = "Water staff is used for graveyard room",
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

    @ConfigItem(
            keyName = "Healing threshold (min)",
            name = "Healing threshold (min)",
            description = "Each time the bot eats it chooses a random threshold (between min and max value) to eat at next time.",
            position = 7,
            section = graveyardSection
    )
    default int healingThresholdMin() {
        return 40;
    }

    @ConfigItem(
            keyName = "Healing threshold (max)",
            name = "Healing threshold (max)",
            description = "Each time the bot eats it chooses a random threshold (between min and max value) to eat at next time.",
            position = 8,
            section = graveyardSection
    )
    default int healingThresholdMax() {
        return 70;
    }
}
