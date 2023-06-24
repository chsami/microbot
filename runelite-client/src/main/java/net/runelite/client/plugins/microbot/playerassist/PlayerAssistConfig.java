package net.runelite.client.plugins.microbot.playerassist;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig.GROUP)
public interface PlayerAssistConfig extends Config {

    String GROUP = "PlayerAssistant";

    @ConfigItem(
            keyName = "Cannon",
            name = "Auto reload cannon",
            description = "Automatically reloads cannon",
            position = 0
    )
    default boolean toggleCannon()
    {
        return false;
    }
    @ConfigItem(
            keyName = "Combat",
            name = "Auto attack npc",
            description = "Attacks npc",
            position = 1
    )
    default boolean toggleCombat()
    {
        return false;
    }
    @ConfigItem(
            keyName = "Attackable Npcs",
            name = "Attackable npcs",
            description = "List of attackable npcs",
            position = 2
    )
    default String attackableNpcs()
    {
        return "";
    }
    @ConfigItem(
            keyName = "Food",
            name = "Auto eat food",
            description = "Automatically eats food",
            position = 3
    )
    default boolean toggleFood()
    {
        return false;
    }
    @ConfigItem(
            keyName = "Auto Prayer Potion",
            name = "Auto drink prayer potion",
            description = "Automatically drinks prayer potions",
            position = 4
    )
    default boolean togglePrayerPotions()
    {
        return false;
    }
    @ConfigItem(
            keyName = "Combat potion",
            name = "Auto drink super combat potion",
            description = "Automatically drinks combat potions",
            position = 5
    )
    default boolean toggleCombatPotion()
    {
        return false;
    }
    @ConfigItem(
            keyName = "Loot items",
            name = "auto loot items",
            description = "Enable/disable loot items",
            position = 4
    )
    default boolean toggleLootItems()
    {
        return true;
    }
    @ConfigItem(
            keyName = "Items to loot",
            name = "Items to loot",
            description = "Items to loot comma seperated",
            position = 5
    )
    default String itemsToLoot()
    {
        return "";
    }

    @ConfigItem(
            keyName = "SafeSpot",
            name = "SafeSpot",
            description = "Start at your safespot",
            position = 5
    )
    default boolean safeSpot()
    {
        return false;
    }
    @ConfigItem(
            keyName = "PrayFlick",
            name = "PrayFlick",
            description = "PrayFlick",
            position = 6
    )
    default boolean prayFlick()
    {
        return false;
    }
}

