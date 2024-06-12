package net.runelite.client.plugins.hoseaplugins.lucidvardorvishelper;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("lucid-vardorvis-helper")
public interface LucidVardorvisHelperConfig extends Config
{
    @ConfigItem(
            name = "Auto-blood captcha",
            description = "Automatically does the blood captcha attack",
            position = 0,
            keyName = "autoBlood"
    )
    default boolean autoBlood()
    {
        return false;
    }

    @ConfigItem(
            name = "Blood Splats Per Tick",
            description = "How many blood splats to Destroy each tick",
            position = 1,
            keyName = "splatsPerTick"
    )
    default int splatsPerTick()
    {
        return 3;
    }

    @ConfigItem(
            name = "Auto-pray against attacks",
            description = "Automatically activates prayers for projectiles and swaps to protect melee afterwards",
            position = 2,
            keyName = "autoPray"
    )
    default boolean autoPray()
    {
        return false;
    }

    @ConfigItem(
            name = "Auto-piety",
            description = "Automatically activates piety when first activating prayers and de-activates when vard dies",
            position = 3,
            keyName = "autoPiety"
    )
    default boolean autoPiety()
    {
        return false;
    }

    @ConfigItem(
            name = "Auto-dodge Axes",
            description = "Automatically dodges axes if you are on the correct tile",
            position = 4,
            keyName = "autoDodge"
    )
    default boolean autoDodge()
    {
        return false;
    }

    @ConfigItem(
            name = "Auto-attack after dodge",
            description = "Automatically attacks Vardorvis again after auto-dodging",
            position = 5,
            keyName = "autoAttack"
    )
    default boolean autoAttack()
    {
        return false;
    }
}
