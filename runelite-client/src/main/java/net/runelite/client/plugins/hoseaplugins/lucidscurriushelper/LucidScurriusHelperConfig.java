package net.runelite.client.plugins.hoseaplugins.lucidscurriushelper;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("lucid-scurrius-helper")
public interface LucidScurriusHelperConfig extends Config
{
    @ConfigItem(
            name = "Dodge within melee range",
            description = "Dodges only to tiles within melee range of Scurrius (for meleeing)",
            position = 0,
            keyName = "stayMelee"
    )
    default boolean stayMelee()
    {
        return false;
    }

    @ConfigItem(
            name = "Attack after dodging",
            description = "Attacks Scurrius after dodging the falling ceiling attack",
            position = 1,
            keyName = "attackAfterDodge"
    )
    default boolean attackAfterDodge()
    {
        return false;
    }

    @ConfigItem(
            name = "Attack On Spawn",
            description = "Attacks Scurrius when he spawns",
            position = 2,
            keyName = "attackOnSpawn"
    )
    default boolean attackOnSpawn()
    {
        return false;
    }

    @ConfigItem(
            name = "Attack Giant Rats",
            description = "Attacks Giant Rats When Scurrius is dead",
            position = 3,
            keyName = "attackRats"
    )
    default boolean attackRats()
    {
        return false;
    }

    @ConfigItem(
            name = "Prioritize Giant Rats over Scurrius",
            description = "Attacks Giant Rats Over Scurrius to kill them first",
            position = 4,
            keyName = "prioritizeRats"
    )
    default boolean prioritizeRats()
    {
        return false;
    }

    @ConfigItem(
            name = "Auto Pray",
            description = "Auto pray against attacks",
            position = 5,
            keyName = "autoPray"
    )
    default boolean autoPray()
    {
        return false;
    }
}