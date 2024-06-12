package net.runelite.client.plugins.hoseaplugins.AutoTele;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("AutoTele")
public interface AutoTeleConfig extends Config {
    @ConfigItem(
            keyName = "combatrange",
            name = "combat range only",
            description = "When checked you will only teleport from players able to attack you"
    )
    default boolean combatrange() {
        return true;
    }

    @ConfigItem(
            keyName = "notify",
            name = "chat notification on enter wilderness?",
            description = "When enabled the plugin will tell you if you brought a teleport item or not when entering " +
                    "the wilderness"
    )
    default boolean alert() {
        return true;
    }
    //	@ConfigItem(
    //			keyName = "mage bonus failsafe",
    //			name = "override weapon filter for enemies with positive mage bonus",
    //			description = "When enabled the plugin will still tele from people with weaponfilter weapons if they have" +
    //					" positive mage bonus"
    //	)
    //	default boolean mageFilter()
    //	{
    //		return false;
    //	}
    //	@ConfigItem(
    //			keyName = "weaponFilter",
    //			name = "dont tele from enemies with these weapons",
    //			description = "Values are a comma seperated list of ids or names, including wildcards, e.g 513,rune " + "pickaxe,dragon*"
    //	)
    //	default String weaponFilter()
    //	{
    //		return "";
    //	}
}
