package net.runelite.client.plugins.microbot.playerassist;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.config.*;
import net.runelite.client.plugins.inventorysetups.InventorySetup;
import net.runelite.client.plugins.microbot.playerassist.enums.PlayStyle;

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
    @ConfigSection(
            name = "Combat",
            description = "Combat",
            position = 1,
            closedByDefault = false
    )
    String combatSection = "Combat";
    @ConfigSection(
            name = "Banking",
            description = "Banking settings",
            position = 992,
            closedByDefault = true
    )
    String banking = "Banking";
    //Gear section
    @ConfigSection(
            name = "Gear",
            description = "Gear",
            position = 55,
            closedByDefault = true
    )
    String gearSection = "Gear";
    @ConfigSection(
            name = "Food & Potions",
            description = "Food & Potions",
            position = 2,
            closedByDefault = false
    )
    String foodAndPotionsSection = "Food & Potions";
    @ConfigSection(
            name = "Loot",
            description = "Loot",
            position = 3,
            closedByDefault = false
    )
    String lootSection = "Loot";
    //Prayer section
    @ConfigSection(
            name = "Prayer",
            description = "Prayer",
            position = 4,
            closedByDefault = false
    )
    String prayerSection = "Prayer";
    //Skilling section
    @ConfigSection(
            name = "Skilling",
            description = "Skilling",
            position = 5,
            closedByDefault = false
    )
    String skillingSection = "Combat Skilling";

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
                "9. Right-click NPCs to add them to the attack list.";
    }

    @ConfigItem(
            keyName = "Combat",
            name = "Auto attack npc",
            description = "Attacks npc",
            position = 0,
            section = combatSection
    )
    default boolean toggleCombat() {
        return false;
    }

    @ConfigItem(
            keyName = "monster",
            name = "Attackable npcs",
            description = "List of attackable npcs",
            position = 1,
            section = combatSection
    )
    default String attackableNpcs() {
        return "";
    }

    @ConfigItem(
            keyName = "Attack Radius",
            name = "Attack Radius",
            description = "The max radius to attack npcs",
            position = 2,
            section = combatSection
    )
    default int attackRadius() {
        return 10;
    }

    @ConfigItem(
            keyName = "Use special attack",
            name = "Use special attack",
            description = "Use special attack",
            position = 3,
            section = combatSection
    )
    default boolean useSpecialAttack() {
        return false;
    }

    @ConfigItem(
            keyName = "Cannon",
            name = "Auto reload cannon",
            description = "Automatically reloads cannon",
            position = 4,
            section = combatSection
    )
    default boolean toggleCannon() {
        return false;
    }

    //safe spot
    @ConfigItem(
            keyName = "Safe Spot",
            name = "Safe Spot",
            description = "Right-click the ground to select the safe spot tile",
            position = 5,
            section = combatSection
    )
    default boolean toggleSafeSpot() {
        return false;
    }

    //PlayStyle
    @ConfigItem(
            keyName = "PlayStyle",
            name = "Play Style",
            description = "Play Style",
            position = 6,
            section = combatSection
    )
    default PlayStyle playStyle() {
        return PlayStyle.AGGRESSIVE;
    }

    @ConfigItem(
            keyName = "Food",
            name = "Auto eat food",
            description = "Automatically eats food",
            position = 0,
            section = foodAndPotionsSection
    )
    default boolean toggleFood() {
        return false;
    }

    @ConfigItem(
            keyName = "Auto Prayer Potion",
            name = "Auto drink prayer potion",
            description = "Automatically drinks prayer potions",
            position = 1,
            section = foodAndPotionsSection
    )
    default boolean togglePrayerPotions() {
        return false;
    }

    @ConfigItem(
            keyName = "Combat potion",
            name = "Auto drink super combat potion",
            description = "Automatically drinks combat potions",
            position = 2,
            section = foodAndPotionsSection
    )
    default boolean toggleCombatPotion() {
        return false;
    }

    @ConfigItem(
            keyName = "Ranging/Bastion potion",
            name = "Auto drink Ranging/Bastion potion",
            description = "Automatically drinks Ranging/Bastion potions",
            position = 3,
            section = foodAndPotionsSection
    )
    default boolean toggleRangingPotion() {
        return false;
    }

    @ConfigItem(
            keyName = "Use AntiPoison",
            name = "Use AntiPoison",
            description = "Use AntiPoison",
            position = 4,
            section = foodAndPotionsSection
    )


    default boolean useAntiPoison() {
        return false;
    }

    @ConfigItem(
            keyName = "Loot items",
            name = "Auto loot items",
            description = "Enable/disable loot items",
            position = 0,
            section = lootSection
    )
    default boolean toggleLootItems() {
        return true;
    }

    @ConfigItem(
            keyName = "Min Price of items to loot",
            name = "Min. Price of items to loot",
            description = "Min. Price of items to loot",
            position = 1,
            section = lootSection
    )
    default int minPriceOfItemsToLoot() {
        return 5000;
    }

    @ConfigItem(
            keyName = "Max Price of items to loot",
            name = "Max. Price of items to loot",
            description = "Max. Price of items to loot default is set to 10M",
            position = 1,
            section = lootSection
    )
    default int maxPriceOfItemsToLoot() {
        return 10000000;
    }
    // toggle scatter

    @ConfigItem(
            keyName = "Loot arrows",
            name = "Auto loot arrows",
            description = "Enable/disable loot arrows",
            position = 2,
            section = lootSection
    )
    default boolean toggleLootArrows() {
        return true;
    }

    @ConfigItem(
            keyName = "Bury Bones",
            name = "Bury Bones",
            description = "Bury Bones",
            position = 3,
            section = lootSection
    )
    default boolean toggleBuryBones() {
        return false;
    }

    @ConfigItem(
            keyName = "Scatter",
            name = "Scatter",
            description = "Scatter ashes",
            position = 4,
            section = lootSection
    )
    default boolean toggleScatter() {
        return false;
    }

    // delayed looting
    @ConfigItem(
            keyName = "delayedLooting",
            name = "Delayed Looting",
            description = "Lets the loot stay on the ground for a while before picking it up",
            position = 5,
            section = lootSection
    )
    default boolean toggleDelayedLooting() {
        return false;
    }

    //set center tile manually
    @ConfigItem(
            keyName = "Center Tile",
            name = "Manual Center Tile",
            description = "Right-click the ground to select the center tile",
            position = 6,
            section = combatSection
    )
    default boolean toggleCenterTile() {
        return false;
    }

    //Use quick prayer
    @ConfigItem(
            keyName = "Use prayer",
            name = "Use prayer",
            description = "Use prayer",
            position = 0,
            section = prayerSection
    )
    default boolean togglePrayer() {
        return false;
    }

    //Flick quick prayer
    @ConfigItem(
            keyName = "Pray flick",
            name = "Flick quick prayer",
            description = "Flick quick prayer, works with lazy flick",
            position = 1,
            section = prayerSection
    )
    default boolean toggleQuickPrayFlick() {
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
    default boolean toggleLazyFlick() {
        return false;
    }

    //Balance combat skills
    @ConfigItem(
            keyName = "balanceCombatSkills",
            name = "Balance combat skills",
            description = "Balance combat skills",
            position = 0,
            section = skillingSection
    )
    default boolean toggleBalanceCombatSkills() {
        return false;
    }

    //Avoid Controlled attack style
    @ConfigItem(
            keyName = "avoidControlled",
            name = "No Controlled Attack",
            description = "Avoid Controlled attack style so you won't accidentally train unwanted combat skills",
            position = 1,
            section = skillingSection
    )
    default boolean toggleAvoidControlled() {
        return true;
    }


    //Attack style change delay (Seconds)
    @ConfigItem(
            keyName = "attackStyleChangeDelay",
            name = "Change Delay",
            description = "Attack Style Change Delay In Seconds",
            position = 2,
            section = skillingSection
    )
    default int attackStyleChangeDelay() {
        return 60 * 15;
    }
    // Disable on Max combat
    @ConfigItem(
            keyName = "disableOnMaxCombat",
            name = "Disable on Max Combat",
            description = "Disable on Max Combat",
            position = 3,
            section = skillingSection
    )
    default boolean toggleDisableOnMaxCombat() {
        return true;
    }
    //Attack skill target
    @ConfigItem(
            keyName = "attackSkillTarget",
            name = "Attack Level Target",
            description = "Attack level target",
            position = 97,
            section = skillingSection
    )
    default int attackSkillTarget() {
        return 99;
    }

    //Strength skill target
    @ConfigItem(
            keyName = "strengthSkillTarget",
            name = "Strength Level Target",
            description = "Strength level target",
            position = 98,
            section = skillingSection
    )
    default int strengthSkillTarget() {
        return 99;
    }

    //Defence skill target
    @ConfigItem(
            keyName = "defenceSkillTarget",
            name = "Defence Level Target",
            description = "Defence level target",
            position = 99,
            section = skillingSection
    )
    default int defenceSkillTarget() {
        return 99;
    }

    // only loot my items
    @ConfigItem(
            keyName = "onlyLootMyItems",
            name = "Only Loot My Items",
            description = "Only loot items that are dropped by you",
            position = 6,
            section = lootSection
    )
    default boolean toggleOnlyLootMyItems() {
        return false;
    }

    // Use Inventory Setup
    @ConfigItem(
            keyName = "useInventorySetup",
            name = "Use Inventory Setup",
            description = "Use Inventory Setup, make sure to select consumables used in the bank section",
            position = 1,
            section = gearSection
    )
    default boolean useInventorySetup() {
        return false;
    }

    // Inventory setup selection TODO: Add inventory setup selection
    @ConfigItem(
            keyName = "InventorySetupName",
            name = "Inventory setup name",
            description = "Create an inventory setup in the inventory setup plugin and enter the name here",
            position = 99,
            section = gearSection
    )
    default String inventorySetup() {
        return "";
    }

    @ConfigItem(
            keyName = "bank",
            name = "Bank",
            description = "If enabled, will bank items when inventory is full. If disabled, will just stop looting",
            position = 0,
            section = banking
    )
    default boolean bank() {
        return true;
    }

    //Minimum free inventory slots to bank
    @Range(max = 28)
    @ConfigItem(
            keyName = "minFreeSlots",
            name = "Min. free slots",
            description = "Minimum free inventory slots to bank, if less than this, will bank items",
            position = 1,
            section = banking
    )
    default int minFreeSlots() {
        return 5;
    }

    // checkbox to use stamina potions when banking
    @ConfigItem(
            keyName = "useStamina",
            name = "Use stamina potions",
            description = "Use stamina potions when banking",
            position = 2,
            section = banking
    )
    default boolean useStamina() {
        return true;
    }

    @ConfigItem(
            keyName = "staminaValue",
            name = "Stamina Potions",
            description = "Amount of stamina potions to withdraw",
            position = 2,
            section = banking
    )
    default int staminaValue() {
        return 0;
    }

    // checkbox to use food when banking
    @ConfigItem(
            keyName = "useFood",
            name = "Use food",
            description = "Use food when banking",
            position = 3,
            section = banking
    )
    default boolean useFood() {
        return true;
    }

    @ConfigItem(
            keyName = "foodValue",
            name = "Food",
            description = "Amount of food to withdraw",
            position = 3,
            section = banking
    )
    default int foodValue() {
        return 0;
    }

    // checkbox to use restore potions when banking
    @ConfigItem(
            keyName = "useRestore",
            name = "Use restore potions",
            description = "Use restore potions when banking",
            position = 4,
            section = banking
    )
    default boolean useRestore() {
        return true;
    }

    @ConfigItem(
            keyName = "restoreValue",
            name = "Restore Potions",
            description = "Amount of restore potions to withdraw",
            position = 4,
            section = banking
    )
    default int restoreValue() {
        return 0;
    }

    // checkbox to use prayer potions when banking
    @ConfigItem(
            keyName = "usePrayer",
            name = "Use prayer potions",
            description = "Use prayer potions when banking",
            position = 5,
            section = banking
    )
    default boolean usePrayer() {
        return true;
    }

    @ConfigItem(
            keyName = "prayerValue",
            name = "Prayer Potions",
            description = "Amount of prayer potions to withdraw",
            position = 5,
            section = banking
    )
    default int prayerValue() {
        return 0;
    }

    // checkbox to use antipoison potions when banking
    @ConfigItem(
            keyName = "useAntipoison",
            name = "Use antipoison potions",
            description = "Use antipoison potions when banking",
            position = 6,
            section = banking
    )
    default boolean useAntipoison() {
        return true;
    }

    @ConfigItem(
            keyName = "antipoisonValue",
            name = "Antipoison Potions",
            description = "Amount of antipoison potions to withdraw",
            position = 6,
            section = banking
    )
    default int antipoisonValue() {
        return 0;
    }

    // checkbox to use antifire potions when banking
    @ConfigItem(
            keyName = "useAntifire",
            name = "Use antifire potions",
            description = "Use antifire potions when banking",
            position = 7,
            section = banking
    )
    default boolean useAntifire() {
        return true;
    }

    @ConfigItem(
            keyName = "antifireValue",
            name = "Antifire Potions",
            description = "Amount of antifire potions to withdraw",
            position = 7,
            section = banking
    )
    default int antifireValue() {
        return 0;
    }

    // checkbox to use combat potions when banking
    @ConfigItem(
            keyName = "useCombat",
            name = "Use combat potions",
            description = "Use combat potions when banking",
            position = 8,
            section = banking
    )
    default boolean useCombat() {
        return true;
    }

    @ConfigItem(
            keyName = "combatValue",
            name = "Combat Potions",
            description = "Amount of combat potions to withdraw",
            position = 8,
            section = banking
    )
    default int combatValue() {
        return 0;
    }


    // checkbox to use teleportation items when banking
    @ConfigItem(
            keyName = "ignoreTeleport",
            name = "Ignore Teleport Items",
            description = "ignore teleport items when banking",
            position = 9,
            section = banking
    )
    default boolean ignoreTeleport() {
        return true;
    }


    // Hidden config item for inventory setup
    @ConfigItem(
            keyName = "inventorySetupHidden",
            name = "inventorySetupHidden",
            description = "inventorySetupHidden",
            hidden = true
    )
    default InventorySetup inventorySetupHidden() {
        return null;
    }

    //hidden config item for center location
    @ConfigItem(
            keyName = "centerLocation",
            name = "Center Location",
            description = "Center Location",
            hidden = true
    )
    default WorldPoint centerLocation() {
        return new WorldPoint(0, 0, 0);
    }

    //hidden config item for safe spot location
    @ConfigItem(
            keyName = "safeSpotLocation",
            name = "Safe Spot Location",
            description = "Safe Spot Location",
            hidden = true
    )
    default WorldPoint safeSpot() {
        return new WorldPoint(0, 0, 0);
    }

}


