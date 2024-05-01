package net.runelite.client.plugins.nateplugins.combat.nateteleporter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.nateplugins.combat.nateteleporter.enums.SPELLS;

@ConfigGroup("Magic")
public interface MagicConfig extends Config {
    @ConfigSection(
            name = "General",
            description = "General",
            position = 0
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "Mode",
            name = "Mode",
            description = "Choose the Mode",
            position = 0,
            section = generalSection
    )

    default SPELLS MODE()
    {
        return SPELLS.NONE;
    }
    @ConfigItem(
            keyName = "Name Npc",
            name = "Name Npc",
            description = "name the npc",
            position = 1,
            section = generalSection
    )

    default String NPC()
    {
        return "";
    }
    @ConfigItem(
            keyName = "Alchemy",
            name = "High/Low Alchemy",
            description = "High/Low alchemy based on your magic level",
            position = 2,
            section = generalSection
    )
    default boolean alchemy()
    {
        return false;
    }
    @ConfigItem(
            keyName = "Alch item",
            name = "Alch item",
            description = "Name of the item to alch",
            position = 3,
            section = generalSection
    )
    default String AlchItem()
    {
        return "";
    }
    @ConfigItem(
            keyName = "Refill Cannon",
            name = "Refill cannon",
            description = "Fill the cannon with cannonballs and move to a safe location, afterwards the script will refill the cannon and walk back to the original location",
            position = 4,
            section = generalSection
    )
    default boolean cannon()
    {
        return false;
    }
}
