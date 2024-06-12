package net.runelite.client.plugins.hoseaplugins.UpkeepPlugin;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("UpkeepPlugin")
public interface UpkeepPluginConfig extends Config {
    @ConfigItem(
            keyName = "HealthLowAmount",
            name = "Health Low Amount",
            description = "Health low actions will fire when health falls below this value"
    )
    default int HealthLowAmount() {
        return 0;
    }

    @ConfigItem(
            keyName = "HealthLowActions",
            name = "Health Low Actions",
            description = "List of item actions to use when health falls below value"
    )
    default String HealthActions() {
        return "";
    }

    @ConfigItem(
            keyName = "StaminaLowAmount",
            name = "Stamina Low Amount",
            description = "Health low actions will fire when stamina falls below this value"
    )
    default int StaminaLowAmount() {
        return 0;
    }

    @ConfigItem(
            keyName = "StaminaLowActions",
            name = "Stamina Low Actions",
            description = "List of item actions to use when stamina falls below value"
    )
    default String StaminaActions() {
        return "";
    }

    @ConfigItem(
            keyName = "RangeLowAmount",
            name = "Range Low Amount",
            description = "Health low actions will fire when range falls below this value"
    )
    default int RangeLowAmount() {
        return 0;
    }

    @ConfigItem(
            keyName = "RangeLowActions",
            name = "Range Low Actions",
            description = "List of item actions to use when range falls below value"
    )
    default String RangeActions() {
        return "";
    }

    @ConfigItem(
            keyName = "PrayerLowAmount",
            name = "Prayer Low Amount",
            description = "Health low actions will fire when prayer falls below this value"
    )
    default int PrayerLowAmount() {
        return 0;
    }

    @ConfigItem(
            keyName = "PrayerLowActions",
            name = "Prayer Low Actions",
            description = "List of item actions to use when prayer falls below value"
    )
    default String PrayerActions() {
        return "";
    }

    @ConfigItem(
            keyName = "MagicLowAmount",
            name = "Magic Low Amount",
            description = "Health low actions will fire when magic falls below this value"
    )
    default int MagicLowAmount() {
        return 0;
    }

    @ConfigItem(
            keyName = "MagicLowActions",
            name = "Magic Low Actions",
            description = "List of item actions to use when magic falls below value"
    )
    default String MagicActions() {
        return "";
    }

    @ConfigItem(
            keyName = "StrengthLowAmount",
            name = "Strength Low Amount",
            description = "Health low actions will fire when strength falls below this value"
    )
    default int StrengthLowAmount() {
        return 0;
    }

    @ConfigItem(
            keyName = "StrengthLowActions",
            name = "Strength Low Actions",
            description = "List of item actions to use when strength falls below value"
    )
    default String StrengthActions() {
        return "";
    }

    @ConfigItem(
            keyName = "AntiFireToggle",
            name = "AntiFire Toggle",
            description = "",
            position = 200
    )
    default boolean AntiFireToggle() {
        return false;
    }

    @ConfigItem(
            keyName = "AntiFireActions",
            name = "AntiFire Actions",
            description = "List of item actions to use when not anti-fired",
            position = 201
    )
    default String AntiFireActions() {
        return "";
    }

    @ConfigItem(
            keyName = "AntiVenomToggle",
            name = "AntiVenom Toggle",
            description = "",
            position = 202
    )
    default boolean AntiVenomToggle() {
        return false;
    }
    @ConfigItem(
            keyName = "AntiVenomActions",
            name = "AntiVenom Actions",
            description = "List of item actions to use when infliced with venom/poison",
            position = 203
    )
    default String AntiPoisonActions() {
        return "";
    }
}