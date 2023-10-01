package net.runelite.client.plugins.nateplugins.combat.nateteleporter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.nateplugins.combat.nateteleporter.enums.Teleports;

@ConfigGroup("Magic")
public interface TeleporterConfig extends Config {
    @ConfigSection(
            name = "General",
            description = "General",
            position = 0
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "Teleport",
            name = "Teleport",
            description = "Choose the Teleport",
            position = 0,
            section = generalSection
    )

    default Teleports SPELL()
    {
        return Teleports.FALADOR;
    }
    @ConfigItem(
            keyName = "High Alchemy",
            name = "High Alchemy",
            description = "High alchemy",
            position = 1,
            section = generalSection
    )
    default boolean highAlchemy()
    {
        return false;
    }
    @ConfigItem(
            keyName = "High Alchemy item",
            name = "High Alchemy item",
            description = "Name of the item to highalch",
            position = 2,
            section = generalSection
    )
    default String highAlchemyItem()
    {
        return "";
    }
}
