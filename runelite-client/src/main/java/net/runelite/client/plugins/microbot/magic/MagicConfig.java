package net.runelite.client.plugins.microbot.magic;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(MagicConfig.GROUP)
public interface MagicConfig extends Config {

    String GROUP = "Magic";

    @ConfigSection(
            name = "Bolt Enchanting",
            description = "Bolt Enchanting",
            position = 0,
            closedByDefault = true
    )
    String boltEnchantingSection = "Bolt enchanting";

    @ConfigSection(
            name = "High Alchemy",
            description = "High Alchemy",
            position = 1,
            closedByDefault = true
    )
    String highAlchemySection = "High Alchemy";

    @ConfigItem(
            keyName = "Bolt enchanting",
            name = "Bolt enchanting",
            description = "Bolt enchanting",
            position = 0,
            section = boltEnchantingSection
    )
    default boolean boltEnchanting()
    {
        return false;
    }

    @ConfigItem(
            keyName = "High Alchemy",
            name = "High Alchemy",
            description = "High alchemy",
            position = 0,
            section = highAlchemySection
    )
    default boolean highAlchemy()
    {
        return false;
    }
}

