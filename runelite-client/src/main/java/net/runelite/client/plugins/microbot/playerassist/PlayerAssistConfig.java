package net.runelite.client.plugins.microbot.playerassist;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig.GROUP)
public interface PlayerAssistConfig extends Config {

    String GROUP = "PlayerAssistant";

    @ConfigItem(
            keyName = "GUIDE",
            name = "GUIDE",
            description = "GUIDE",
            position = 0
    )
    default String GUIDE()
    {
        return "This plugin allows for semi afk.\n 1.Make sure to place cannon first before starting the plugin \n" +
                "\n 2. Use food also supports guthan healing, the shield weapon is default set to dragon defender \n" +
                " 3. Use antiPosion supports any potion with 'poison' in the name\n 4. Items to loot are comma seperated strings \n 5. You can turn auto attack npc off if you have a cannon \n " +
                " 6. PrayFlick does not work at the moment \n 7. SafeSpot & auto loot arrows might act funny and is not tested throughly. Use @ own risk!";
    }

    @ConfigItem(
            keyName = "Cannon",
            name = "Auto reload cannon",
            description = "Automatically reloads cannon",
            position = 1
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
            keyName = "Price of items to loot",
            name = "Price of items to loot",
            description = "Price of items to loot comma seperated",
            position = 5
    )
    default int priceOfItemsToLoot()
    {
        return 10000;
    }
    @ConfigItem(
            keyName = "Loot arrows",
            name = "auto loot arrows",
            description = "Enable/disable loot arrows",
            position = 5
    )
    default boolean toggleLootArrows()
    {
        return true;
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
            keyName = "PrayFlick (DEVS ONLY)",
            name = "PrayFlick (DEVS ONLY)",
            description = "PrayFlick (DEVS ONLY)",
            position = 6
    )
    default boolean prayFlick()
    {
        return false;
    }
    @ConfigItem(
            keyName = "Attack Radius",
            name = "Attack Radius",
            description = "The max radius to attack npcs",
            position = 6
    )
    default int attackRadius()
    {
        return 10;
    }
    @ConfigItem(
            keyName = "Use special attack",
            name = "Use special attack",
            description = "Use special attack",
            position = 7
    )
    default boolean useSpecialAttack()
    {
        return false;
    }
    @ConfigItem(
            keyName = "Use AntiPoison",
            name = "Use AntiPoison",
            description = "Use AntiPoison",
            position = 7
    )
    default boolean useAntiPoison()
    {
        return false;
    }
}

