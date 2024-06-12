package net.runelite.client.plugins.hoseaplugins.lucidtobprayers;

import lombok.Getter;
import net.runelite.api.Prayer;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("lucid-tob-prayers")
public interface LucidToBPrayersConfig extends Config
{
    @ConfigSection(
            name = "Maiden",
            description = "",
            position = 0
    )
    String maidenSection = "Maiden";

    @ConfigSection(
            name = "Bloat",
            description = "",
            position = 1
    )
    String bloatSection = "Bloat";

    @ConfigSection(
            name = "Nylo",
            description = "",
            position = 2
    )
    String nyloSection = "Nylo";

    @ConfigSection(
            name = "Sote",
            description = "",
            position = 3
    )
    String soteSection = "Sote";

    @ConfigSection(
            name = "Xarpus",
            description = "",
            position = 4
    )
    String xarpusSection = "Xarpus";

    @ConfigSection(
            name = "Verzik",
            description = "",
            position = 5
    )
    String verzikSection = "Verzik";


    // Maiden
    @ConfigItem(
            name = "Maiden Defense",
            description = "",
            position = 0,
            keyName = "maidenDefense",
            section = maidenSection
    )
    default boolean maidenDefense()
    {
        return false;
    }

    @ConfigItem(
            name = "Maiden Offense",
            description = "",
            position = 1,
            keyName = "maidenOffense",
            section = maidenSection
    )
    default OffensivePrayer maidenOffense()
    {
        return OffensivePrayer.NONE;
    }

    // Bloat
    @ConfigItem(
            name = "Bloat Defense",
            description = "",
            position = 0,
            keyName = "bloatDefense",
            section = bloatSection
    )
    default boolean bloatDefense()
    {
        return false;
    }

    @ConfigItem(
            name = "Bloat Offense",
            description = "",
            position = 1,
            keyName = "bloatOffense",
            section = bloatSection
    )
    default OffensivePrayer bloatOffense()
    {
        return OffensivePrayer.NONE;
    }

    // Nylo
    @ConfigItem(
            name = "Nylo Boss Defense",
            description = "",
            position = 0,
            keyName = "nyloBossDefense",
            section = nyloSection
    )
    default boolean nyloBossDefense()
    {
        return false;
    }

    @ConfigItem(
            name = "Nylo Boss Offense",
            description = "",
            position = 1,
            keyName = "nyloBossOffense",
            section = nyloSection
    )
    default boolean nyloBossOffense()
    {
        return false;
    }

    // Sote
    @ConfigItem(
            name = "Sote Defense",
            description = "",
            position = 0,
            keyName = "soteDefense",
            section = soteSection
    )
    default boolean soteDefense()
    {
        return false;
    }

    @ConfigItem(
            name = "Sote Offense",
            description = "",
            position = 1,
            keyName = "soteOffense",
            section = soteSection
    )
    default OffensivePrayer soteOffense()
    {
        return OffensivePrayer.NONE;
    }

    @ConfigItem(
            name = "Mage Ball Tick Eat",
            description = "",
            position = 2,
            keyName = "mageBallTickEat",
            section = soteSection
    )
    default boolean mageBallTickEat()
    {
        return false;
    }

    @ConfigItem(
            name = "Food Config",
            description = "",
            position = 3,
            keyName = "tickEatFood",
            section = soteSection
    )
    default String tickEatFood()
    {
        return "Drink:Saradomin brew";
    }

    // Xarpus
    @ConfigItem(
            name = "Xarpus Defense",
            description = "",
            position = 0,
            keyName = "xarpusDefense",
            section = xarpusSection
    )
    default boolean xarpusDefense()
    {
        return false;
    }

    @ConfigItem(
            name = "Xarpus Offense",
            description = "",
            position = 1,
            keyName = "xarpusOffense",
            section = xarpusSection
    )
    default OffensivePrayer xarpusOffense()
    {
        return OffensivePrayer.NONE;
    }

    // Verzik
    @ConfigItem(
            name = "Verzik P1 Defense",
            description = "",
            position = 0,
            keyName = "verzikDefense1",
            section = verzikSection
    )
    default boolean verzikDefense1()
    {
        return false;
    }

    @ConfigItem(
            name = "Verzik P1 Offense",
            description = "",
            position = 1,
            keyName = "verzikOffense1",
            section = verzikSection
    )
    default OffensivePrayer verzikOffense1()
    {
        return OffensivePrayer.NONE;
    }

    @ConfigItem(
            name = "Verzik P2 Defense",
            description = "",
            position = 2,
            keyName = "verzikDefense2",
            section = verzikSection
    )
    default boolean verzikDefense2()
    {
        return false;
    }

    @ConfigItem(
            name = "Verzik P2 Offense",
            description = "",
            position = 3,
            keyName = "verzikOffense2",
            section = verzikSection
    )
    default OffensivePrayer verzikOffense2()
    {
        return OffensivePrayer.NONE;
    }

    @ConfigItem(
            name = "Verzik P3 Defense",
            description = "",
            position = 4,
            keyName = "verzikDefense3",
            section = verzikSection
    )
    default boolean verzikDefense3()
    {
        return false;
    }

    enum OffensivePrayer
    {
        NONE(null), RIGOUR(Prayer.RIGOUR), AUGURY(Prayer.AUGURY), PIETY(Prayer.PIETY);

        @Getter
        Prayer prayer;
        OffensivePrayer(Prayer prayer)
        {
            this.prayer = prayer;
        }
    }

}

