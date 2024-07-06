package net.runelite.client.plugins.microbot.magetrainingarena;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("example")
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
    default Reward reward() {
        return Reward.BONES_TO_PEACHES;
    }
}
