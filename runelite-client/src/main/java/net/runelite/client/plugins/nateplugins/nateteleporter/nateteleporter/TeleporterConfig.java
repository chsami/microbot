package net.runelite.client.plugins.nateplugins.nateteleporter.nateteleporter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.nateplugins.nateteleporter.nateteleporter.enums.Teleports;

@ConfigGroup("Magic")
public interface TeleporterConfig extends Config {
    @ConfigSection(
            name = "General",
            description = "General",
            position = 0,
            closedByDefault = false
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

}
