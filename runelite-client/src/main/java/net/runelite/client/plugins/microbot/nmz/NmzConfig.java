package net.runelite.client.plugins.microbot.nmz;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("nmz")
public interface NmzConfig extends Config {
    String GROUP = "Nmz";

    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 0,
            section = generalSection
    )
    default String GUIDE() {
        return "Start outside NMZ with gp in Dominic's coffer and make sure bankpin has been unlocked\n" +
                "Make sure you have already set up your hard dream once!\n" +
                "Make sure to turn on AUTO RETALIATE!";
    }

    @ConfigSection(
            name = "General",
            description = "General",
            position = 0,
            closedByDefault = false
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "Use special attack",
            name = "Use special attack",
            description = "Use special attack",
            position = 2,
            section = generalSection
    )
    default boolean useSpecialAttack()
    {
        return false;
    }

    @ConfigItem(
            keyName = "How many overload potions to use",
            name = "How many overload potions to use",
            description = "How many overload potions to use",
            position = 3,
            section = generalSection
    )
    default int overloadPotionAmount()
    {
        return 8;
    }

    @ConfigItem(
            keyName = "How many absorption potions to use",
            name = "How many absorption potions to use",
            description = "How many absorption potions to use",
            position = 4,
            section = generalSection
    )
    default int absorptionPotionAmount()
    {
        return 20;
    }

    @ConfigItem(
            keyName = "Stop after death",
            name = "Stop after death",
            description = "Stop after death",
            position = 4,
            section = generalSection
    )
    default boolean stopAfterDeath()
    {
        return true;
    }

    @ConfigItem(
            keyName = "Use Zapper",
            name = "Use Zapper",
            description = "Use Zapper to increase nightmare zone points",
            position = 4,
            section = generalSection
    )
    default boolean useZapper()
    {
        return true;
    }

    @ConfigItem(
            keyName = "Use Reccurent damage",
            name = "Use Reccurent damage",
            description = "Use reccurent damage to increase nightmare zone points",
            position = 4,
            section = generalSection
    )
    default boolean useReccurentDamage()
    {
        return true;
    }
}
