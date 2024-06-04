package net.runelite.client.plugins.microbot.playerassist;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.config.*;
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
    default String GUIDE() {
        return "This plugin allows for semi afk.\n" +
                "1. Make sure to place the cannon first before starting the plugin.\n" +
                "2. Use food also supports Guthan's healing, the shield weapon is default set to Dragon Defender.\n" +
                "3. Use antiPoison supports any potion with 'poison' in the name.\n" +
                "4. Items to loot are comma-separated strings.\n" +
                "5. You can turn auto attack NPC off if you have a cannon.\n" +
                "6. PrayFlick should work at the moment.\n" +
                "7. SafeSpot & auto loot arrows might act funny and are not tested thoroughly. Use at your own risk!\n" +
                "8. Shift Right-click the ground to select the center tile.\n" +
                "9. Shift Right-click NPCs to add them to the attack list.";
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
            keyName = "monster",
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
    //safe spot
    @ConfigItem(
            keyName = "Safe Spot",
            name = "Safe Spot",
            description = "Right-click the ground to select the safe spot tile",
            position = 4,
            section = combatSection
    )
    default boolean toggleSafeSpot()
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
            keyName = "Min Price of items to loot",
            name = "Min. Price of items to loot",
            description = "Min. Price of items to loot",
            position = 1,
            section = lootSection
    )
    default int minPriceOfItemsToLoot()
    {
        return 5000;
    }
    @ConfigItem(
            keyName = "Max Price of items to loot",
            name = "Max. Price of items to loot",
            description = "Max. Price of items to loot default is set to 10M",
            position = 1,
            section = lootSection
    )
    default int maxPriceOfItemsToLoot()
    {
        return 10000000;
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

    @ConfigItem(
            keyName = "Bury Bones",
            name = "Bury Bones",
            description = "Bury Bones",
            position = 3,
            section = lootSection
    )
    default boolean toggleBuryBones() { return false; }
    // toggle scatter

    @ConfigItem(
            keyName = "Scatter",
            name = "Scatter",
            description = "Scatter ashes",
            position = 4,
            section = lootSection
    )
    default boolean toggleScatter() { return false; }




    //Prayer section
    @ConfigSection(
            name = "Prayer",
            description = "Prayer",
            position = 4,
            closedByDefault = false
    )
    String prayerSection = "Prayer";

    //Use quick prayer
    @ConfigItem(
            keyName = "Use prayer",
            name = "Use prayer",
            description = "Use prayer",
            position = 0,
            section = prayerSection
    )
    default boolean togglePrayer()
    { return false; }

    //Flick quick prayer
    @ConfigItem(
            keyName = "Pray flick",
            name = "Flick quick prayer",
            description = "Flick quick prayer, works with lazy flick",
            position = 1,
            section = prayerSection
    )
    default boolean toggleQuickPrayFlick()
    {
        return false;
    }
    //Lazy flick
    @ConfigItem(
            keyName = "Lazy flick",
            name = "Lazy flick",
            description = "Will flick correct prayer when npc is about to attack you",
            position = 2,
            section = prayerSection
    )
    default boolean toggleLazyFlick()
    {
        return false;
    }

    //hidden config item for center location
    @ConfigItem(
            keyName = "centerLocation",
            name = "Center Location",
            description = "Center Location",
            hidden = true
    )
    default WorldPoint centerLocation()
    {
        return new WorldPoint(0, 0, 0);
    }

    //hidden config item for safe spot location
    @ConfigItem(
            keyName = "safeSpotLocation",
            name = "Safe Spot Location",
            description = "Safe Spot Location",
            hidden = true
    )
    default WorldPoint safeSpot()
    {
        return new WorldPoint(0, 0, 0);
    }

}


