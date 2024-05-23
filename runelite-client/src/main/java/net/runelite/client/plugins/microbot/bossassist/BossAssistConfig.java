package net.runelite.client.plugins.microbot.bossassist;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.bossassist.models.DAMAGE_PRAYERS;
import net.runelite.client.plugins.microbot.bossassist.models.INSTANCES;
import net.runelite.client.plugins.microbot.bossassist.models.PRAY_MODE;
import net.runelite.client.plugins.microbot.bossassist.models.SPEC_WEAPON;

@ConfigGroup("Boss Assist")
public interface BossAssistConfig extends Config {

    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 0
    )
    default String GUIDE() {
        return "This plugin will help u with defeating bosses this is not a Bot script but more like a legit helper";
    }

    @ConfigSection(
            name = "General",
            description = "General",
            position = 1,
            closedByDefault = false
    )
    String generalSection = "general";


    @ConfigSection(
            name = "Scurrius",
            description = "Settings for the Scurrius assist",
            position = 2,
            closedByDefault = true
    )
    String scurriousSection = "scurrious";

    @ConfigSection(
            name = "Deranged Archaeologist",
            description = "Settings for the Deranged Archaeologist assist",
            position = 3,
            closedByDefault = true
    )
    String derangedarchaeologist = "deranged archaeologist";

    @ConfigSection(
            name = "Obor",
            description = "Settings for the Obor assist",
            position = 4,
            closedByDefault = true
    )
    String obor = "obor";


    @ConfigItem(
            keyName = "Scurrius",
            name = "Enabled",
            description = "Enable this if you want to assist againt scurry",
            position = 1,
            section = scurriousSection
    )
    default boolean isScurriousOn()
    {
        return false;
    }

    @ConfigItem(
            keyName = "instancescurry",
            name = "Instance type",
            description = "Choose your instance",
            position = 2,
            section = scurriousSection
    )
    default INSTANCES GET_INSTANCE() {
        return INSTANCES.PRIVATE;
    }

    @ConfigItem(
            keyName = "specscurry",
            name = "Special weapon",
            description = "Choose your spec weapon",
            position = 3,
            section = scurriousSection
    )
    default SPEC_WEAPON SPEC_WEAPON() {
        return SPEC_WEAPON.NONE;
    }


    @ConfigItem(
            keyName = "DerangedArcheo",
            name = "Enabled",
            description = "Enable this if you want to assist against Deranged Archaeologist",
            position = 1,
            section = derangedarchaeologist
    )
    default boolean isArcheoOn()
    {
        return false;
    }

    @ConfigItem(
            keyName = "Obor",
            name = "Enabled",
            description = "Enable this if you want to assist against Obor",
            position = 1,
            section = obor
    )
    default boolean isOborOn()
    {
        return false;
    }

    @ConfigItem(
            keyName = "prayermode",
            name = "Prayer mode",
            description = "Choose your prayer mode",
            position = 1,
            section = generalSection
    )
    default PRAY_MODE PRAYER_MODE() {
        return PRAY_MODE.AUTO;
    }

    @ConfigItem(
            keyName = "damageprayer",
            name = "Damage prayer",
            description = "Choose your damage prayer",
            position = 2,
            section = generalSection
    )
    default DAMAGE_PRAYERS DAMAGE_PRAYER() {
        return DAMAGE_PRAYERS.NONE;
    }

}

