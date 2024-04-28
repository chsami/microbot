package net.runelite.client.plugins.microbot.playerassist;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig.GROUP)
public interface PlayerAssistConfig extends Config {

    String GROUP = "PlayerAssistant";

    @ConfigSection(
            name = "General",
            description = "General",
            position = 0,
            closedByDefault = false
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "GUIDE",
            name = "GUIDE",
            description = "GUIDE",
            position = 0,
            section = generalSection
    )
    default String GUIDE()
    {
        return "This plugin allows for semi afk.\n 1.Make sure to place cannon first before starting the plugin \n" +
                "\n 2. Use food also supports guthan healing, the shield weapon is default set to dragon defender \n" +
                " 3. Use antiPosion supports any potion with 'poison' in the name\n 4. Items to loot are comma seperated strings \n 5. You can turn auto attack npc off if you have a cannon \n " +
                " 6. PrayFlick does not work at the moment \n 7. SafeSpot & auto loot arrows might act funny and is not tested throughly. Use @ own risk!";
    }

    @ConfigSection(
            name = "Combat",
            description = "Combat",
            position = 1,
            closedByDefault = false
    )
    String combatSection = "Combat";

    @ConfigItem(
            keyName = "Combat",
            name = "Auto attack npc",
            description = "Attacks npc",
            position = 0,
            section = combatSection
    )
    default boolean toggleCombat()
    {
        return false;
    }

    @ConfigItem(
            keyName = "Attackable Npcs",
            name = "Attackable npcs",
            description = "List of attackable npcs",
            position = 1,
            section = combatSection
    )
    default String attackableNpcs()
    {
        return "";
    }

    @ConfigItem(
            keyName = "Attack Radius",
            name = "Attack Radius",
            description = "The max radius to attack npcs",
            position = 2,
            section = combatSection
    )
    default int attackRadius()
    {
        return 10;
    }
    @ConfigItem(
            keyName = "Use special attack",
            name = "Use special attack",
            description = "Use special attack",
            position = 3,
            section = combatSection
    )
    default boolean useSpecialAttack()
    {
        return false;
    }

    @ConfigItem(
            keyName = "Cannon",
            name = "Auto reload cannon",
            description = "Automatically reloads cannon",
            position = 4,
            section = combatSection
    )
    default boolean toggleCannon()
    {
        return false;
    }

    @ConfigSection(
            name = "Food & Potions",
            description = "Food & Potions",
            position = 2,
            closedByDefault = false
    )
    String foodAndPotionsSection = "Food & Potions";

    @ConfigItem(
            keyName = "Food",
            name = "Auto eat food",
            description = "Automatically eats food",
            position = 0,
            section = foodAndPotionsSection
    )
    default boolean toggleFood()
    {
        return false;
    }
    @ConfigItem(
            keyName = "Auto Prayer Potion",
            name = "Auto drink prayer potion",
            description = "Automatically drinks prayer potions",
            position = 1,
            section = foodAndPotionsSection
    )
    default boolean togglePrayerPotions()
    {
        return false;
    }
    @ConfigItem(
            keyName = "Combat potion",
            name = "Auto drink super combat potion",
            description = "Automatically drinks combat potions",
            position = 2,
            section = foodAndPotionsSection
    )
    default boolean toggleCombatPotion()
    {
        return false;
    }
    @ConfigItem(
            keyName = "Ranging/Bastion potion",
            name = "Auto drink Ranging/Bastion potion",
            description = "Automatically drinks Ranging/Bastion potions",
            position = 3,
            section = foodAndPotionsSection
    )
    default boolean toggleRangingPotion()
    {
        return false;
    }
    @ConfigItem(
            keyName = "Use AntiPoison",
            name = "Use AntiPoison",
            description = "Use AntiPoison",
            position = 4,
            section = foodAndPotionsSection
    )
    default boolean useAntiPoison()
    {
        return false;
    }

    @ConfigSection(
            name = "Loot",
            description = "Loot",
            position = 3,
            closedByDefault = false
    )
    String lootSection = "Loot";

    @ConfigItem(
            keyName = "Loot items",
            name = "Auto loot items",
            description = "Enable/disable loot items",
            position = 0,
            section = lootSection
    )
    default boolean toggleLootItems()
    {
        return true;
    }
    @ConfigItem(
            keyName = "Price of items to loot",
            name = "Price of items to loot",
            description = "Price of items to loot comma seperated",
            position = 1,
            section = lootSection
    )
    default int priceOfItemsToLoot()
    {
        return 10000;
    }
    @ConfigItem(
            keyName = "Loot arrows",
            name = "Auto loot arrows",
            description = "Enable/disable loot arrows",
            position = 2,
            section = lootSection
    )
    default boolean toggleLootArrows()
    {
        return true;
    }
}

