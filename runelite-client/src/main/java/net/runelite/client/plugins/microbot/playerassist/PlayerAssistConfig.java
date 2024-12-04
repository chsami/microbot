package net.runelite.client.plugins.microbot.playerassist;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.config.*;
import net.runelite.client.plugins.microbot.inventorysetups.InventorySetup;
import net.runelite.client.plugins.microbot.playerassist.enums.PlayStyle;
import net.runelite.client.plugins.microbot.playerassist.enums.PrayerStyle;

@ConfigGroup(net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig.GROUP)
@ConfigInformation("1. Make sure to place the cannon first before starting the plugin. <br />" +
        "2. Use food also supports Guthan's healing, the shield weapon is default set to Dragon Defender. <br />" +
        "3. Prayer, Combat, Ranging & AntiPoison potions are supported. <br />" +
        "4. Items to loot based your requirements. <br />" +
        "5. You can turn auto attack NPC off if you have a cannon. <br />" +
        "6. PrayFlick in different styles. <br />" +
        "7. SafeSpot you can Shift Right-click the ground to select the tile. <br />" +
        "8. Right-click NPCs to add them to the attack list. <br />")
public interface PlayerAssistConfig extends Config {

    String GROUP = "PlayerAssistant";

    @ConfigSection(
            name = "Combat",
            description = "Combat settings",
            position = 1,
            closedByDefault = false
    )
    String combatSection = "Combat";

    @ConfigSection(
            name = "Food & Potions",
            description = "Food and potion settings",
            position = 2,
            closedByDefault = false
    )
    String foodAndPotionsSection = "Food & Potions";

    @ConfigSection(
            name = "Loot",
            description = "Looting settings",
            position = 3,
            closedByDefault = false
    )
    String lootSection = "Loot";

    @ConfigSection(
            name = "Prayer",
            description = "Prayer settings",
            position = 4,
            closedByDefault = false
    )
    String prayerSection = "Prayer";

    @ConfigSection(
            name = "Combat skilling",
            description = "Combat skill training settings",
            position = 5,
            closedByDefault = false
    )
    String skillingSection = "Combat Skilling";

    @ConfigSection(
            name = "Gear",
            description = "Equipment settings",
            position = 6,
            closedByDefault = true
    )
    String gearSection = "Gear";

    @ConfigSection(
            name = "Banking",
            description = "Banking settings",
            position = 7,
            closedByDefault = false
    )
    String banking = "Banking";


    @ConfigItem(
            keyName = "Combat",
            name = "Auto attack NPC",
            description = "Automatically attack NPCs",
            position = 0,
            section = combatSection
    )
    default boolean toggleCombat() {
        return false;
    }

    @ConfigItem(
            keyName = "monster",
            name = "Attackable NPCs",
            description = "Comma-separated list of NPCs to attack",
            position = 1,
            section = combatSection
    )
    default String attackableNpcs() {
        return "";
    }

    @ConfigItem(
            keyName = "Attack Radius",
            name = "Attack radius",
            description = "Maximum radius to attack NPCs",
            position = 2,
            section = combatSection
    )
    default int attackRadius() {
        return 10;
    }

    @ConfigItem(
            keyName = "Center Tile",
            name = "Manual center tile",
            description = "Shift right-click the ground to select the center tile",
            position = 3,
            section = combatSection
    )
    default boolean toggleCenterTile() {
        return false;
    }

    @ConfigItem(
            keyName = "Safe Spot",
            name = "Safe spot",
            description = "Shift right-click the ground to select the safe spot tile",
            position = 4,
            section = combatSection
    )
    default boolean toggleSafeSpot() {
        return false;
    }

    @ConfigItem(
            keyName = "PlayStyle",
            name = "Play style",
            description = "Select combat play style",
            position = 5,
            section = combatSection
    )
    default PlayStyle playStyle() {
        return PlayStyle.AGGRESSIVE;
    }

    @ConfigItem(
            keyName = "Use special attack",
            name = "Use special attack",
            description = "Enable special attack usage",
            position = 6,
            section = combatSection
    )
    default boolean useSpecialAttack() {
        return false;
    }

    @ConfigItem(
            keyName = "Cannon",
            name = "Auto reload cannon",
            description = "Automatically reload cannon",
            position = 7,
            section = combatSection
    )
    default boolean toggleCannon() {
        return false;
    }

    @ConfigItem(
            keyName = "ReachableNpcs",
            name = "Only attack reachable NPCs",
            description = "Only attack NPCs that can be reached with melee",
            position = 8,
            section = combatSection
    )
    default boolean attackReachableNpcs() {
        return true;
    }

    @ConfigItem(
            keyName = "Food",
            name = "Auto eat food",
            description = "Automatically eat food",
            position = 0,
            section = foodAndPotionsSection
    )
    default boolean toggleFood() {
        return false;
    }

    @ConfigItem(
            keyName = "Auto Prayer Potion",
            name = "Auto drink prayer potion",
            description = "Automatically drink prayer potions",
            position = 1,
            section = foodAndPotionsSection
    )
    default boolean togglePrayerPotions() {
        return false;
    }

    @ConfigItem(
            keyName = "Combat potion",
            name = "Auto drink combat potion",
            description = "Automatically drink combat potions",
            position = 2,
            section = foodAndPotionsSection
    )
    default boolean toggleCombatPotion() {
        return false;
    }

    @ConfigItem(
            keyName = "Ranging/Bastion potion",
            name = "Auto drink ranging potion",
            description = "Automatically drink ranging/bastion potions",
            position = 3,
            section = foodAndPotionsSection
    )
    default boolean toggleRangingPotion() {
        return false;
    }

    @ConfigItem(
            keyName = "Use AntiPoison",
            name = "Use antipoison",
            description = "Automatically use antipoison",
            position = 4,
            section = foodAndPotionsSection
    )
    default boolean useAntiPoison() {
        return false;
    }

    @ConfigItem(
            keyName = "Loot items",
            name = "Auto loot items",
            description = "Enable/disable auto looting",
            position = 0,
            section = lootSection
    )
    default boolean toggleLootItems() {
        return true;
    }

    @ConfigItem(
            keyName = "listItemsToLoot",
            name = "Items to loot",
            description = "Comma-separated list of items to loot (e.g., 'bread, coins' or 'bread,coins')",
            position = 1,
            section = lootSection
    )
    default String listItemsToLoot() {
        return "";
    }

    @ConfigItem(
            keyName = "Min Price of items to loot",
            name = "Min price to loot",
            description = "Minimum price of items to loot",
            position = 2,
            section = lootSection
    )
    default int minPriceOfItemsToLoot() {
        return 5000;
    }

    @ConfigItem(
            keyName = "Max Price of items to loot",
            name = "Max price to loot",
            description = "Maximum price of items to loot (default: 10M)",
            position = 3,
            section = lootSection
    )
    default int maxPriceOfItemsToLoot() {
        return 10000000;
    }

    @ConfigItem(
            keyName = "Loot arrows",
            name = "Loot arrows",
            description = "Enable arrow looting",
            position = 4,
            section = lootSection
    )
    default boolean toggleLootArrows() {
        return false;
    }

    @ConfigItem(
            keyName = "Loot runes",
            name = "Loot runes",
            description = "Enable rune looting",
            position = 5,
            section = lootSection
    )
    default boolean toggleLootRunes() {
        return false;
    }

    @ConfigItem(
            keyName = "Loot coins",
            name = "Loot coins",
            description = "Enable coin looting",
            position = 6,
            section = lootSection
    )
    default boolean toggleLootCoins() {
        return false;
    }

    @ConfigItem(
            keyName = "Loot untradables",
            name = "Loot untradables",
            description = "Enable untradable item looting",
            position = 7,
            section = lootSection
    )
    default boolean toggleLootUntradables() {
        return false;
    }

    @ConfigItem(
            keyName = "Bury Bones",
            name = "Bury bones",
            description = "Pick up and bury bones",
            position = 8,
            section = lootSection
    )
    default boolean toggleBuryBones() {
        return false;
    }

    @ConfigItem(
            keyName = "Scatter",
            name = "Scatter ashes",
            description = "Pick up and scatter ashes",
            position = 9,
            section = lootSection
    )
    default boolean toggleScatter() {
        return false;
    }

    @ConfigItem(
            keyName = "delayedLooting",
            name = "Delayed looting",
            description = "Let loot stay on ground before picking up",
            position = 10,
            section = lootSection
    )
    default boolean toggleDelayedLooting() {
        return false;
    }

    @ConfigItem(
            keyName = "onlyLootMyItems",
            name = "Only loot my items",
            description = "Only loot items that you dropped or were dropped for you",
            position = 11,
            section = lootSection
    )
    default boolean toggleOnlyLootMyItems() {
        return false;
    }

    @ConfigItem(
            keyName = "forceLoot",
            name = "Force loot",
            description = "Force looting even while in combat",
            position = 12,
            section = lootSection
    )
    default boolean toggleForceLoot() {
        return false;
    }

    @ConfigItem(
            keyName = "prayerStyleGuide",
            name = "Prayer style guide",
            description = "Prayer style explanations",
            position = 0,
            section = prayerSection
    )
    default String prayerStyleGuide() {
        return "Lazy Flick: Flicks tick before hit\n" +
                "Perfect Lazy Flick: Flicks on hit\n" +
                "Continuous: Quick prayer is on when in combat\n" +
                "Always On: Quick prayer is always on";
    }

    @ConfigItem(
            keyName = "Use prayer",
            name = "Use prayer",
            description = "Enable prayer usage",
            position = 1,
            section = prayerSection
    )
    default boolean togglePrayer() {
        return false;
    }

    @ConfigItem(
            keyName = "quickPrayer",
            name = "Quick prayer",
            description = "Use quick prayers",
            position = 2,
            section = prayerSection
    )
    default boolean toggleQuickPray() {
        return false;
    }

    @ConfigItem(
            keyName = "prayerStyle",
            name = "Prayer style",
            description = "Select prayer activation style",
            position = 3,
            section = prayerSection
    )
    default PrayerStyle prayerStyle() {
        return PrayerStyle.LAZY_FLICK;
    }

    @ConfigItem(
            keyName = "enableSkilling",
            name = "Enable skilling",
            description = "Enable combat skill training",
            position = 0,
            section = skillingSection
    )
    default boolean toggleEnableSkilling() {
        return false;
    }

    @ConfigItem(
            keyName = "balanceCombatSkills",
            name = "Balance combat skills",
            description = "Balance combat skill training",
            position = 1,
            section = skillingSection
    )
    default boolean toggleBalanceCombatSkills() {
        return false;
    }

    @ConfigItem(
            keyName = "avoidControlled",
            name = "No controlled attack",
            description = "Avoid controlled attack style",
            position = 2,
            section = skillingSection
    )
    default boolean toggleAvoidControlled() {
        return true;
    }

    @ConfigItem(
            keyName = "attackStyleChangeDelay",
            name = "Change delay",
            description = "Attack style change delay in seconds",
            position = 3,
            section = skillingSection
    )
    default int attackStyleChangeDelay() {
        return 60 * 15;
    }

    @ConfigItem(
            keyName = "disableOnMaxCombat",
            name = "Disable on max combat",
            description = "Disable when combat skills are maxed",
            position = 4,
            section = skillingSection
    )
    default boolean toggleDisableOnMaxCombat() {
        return true;
    }

    @ConfigItem(
            keyName = "attackSkillTarget",
            name = "Attack level target",
            description = "Target level for Attack",
            position = 5,
            section = skillingSection
    )
    default int attackSkillTarget() {
        return 99;
    }

    @ConfigItem(
            keyName = "strengthSkillTarget",
            name = "Strength level target",
            description = "Target level for Strength",
            position = 6,
            section = skillingSection
    )
    default int strengthSkillTarget() {
        return 99;
    }

    @ConfigItem(
            keyName = "defenceSkillTarget",
            name = "Defence level target",
            description = "Target level for Defence",
            position = 7,
            section = skillingSection
    )
    default int defenceSkillTarget() {
        return 99;
    }

    @ConfigItem(
            keyName = "useInventorySetup",
            name = "Use inventory setup",
            description = "Use inventory setup from Inventory Setup plugin",
            position = 0,
            section = gearSection
    )
    default boolean useInventorySetup() {
        return false;
    }

    @ConfigItem(
            keyName = "InventorySetupName",
            name = "Setup name",
            description = "Name of the inventory setup to use",
            position = 1,
            section = gearSection
    )
    default String inventorySetup() {
        return "";
    }

    @ConfigItem(
            keyName = "bank",
            name = "Enable banking",
            description = "Bank items when inventory is full",
            position = 0,
            section = banking
    )
    default boolean bank() {
        return false;
    }

    @Range(max = 28)
    @ConfigItem(
            keyName = "minFreeSlots",
            name = "Min free slots",
            description = "Minimum free inventory slots before banking",
            position = 1,
            section = banking
    )
    default int minFreeSlots() {
        return 5;
    }

    @ConfigItem(
            keyName = "useStamina",
            name = "Use stamina potions",
            description = "Withdraw stamina potions when banking",
            position = 2,
            section = banking
    )
    default boolean useStamina() {
        return false;
    }

    @ConfigItem(
            keyName = "staminaValue",
            name = "Stamina amount",
            description = "Amount of stamina potions to withdraw",
            position = 3,
            section = banking
    )
    default int staminaValue() {
        return 0;
    }

    @ConfigItem(
            keyName = "useRestore",
            name = "Use restore potions",
            description = "Withdraw restore potions when banking",
            position = 4,
            section = banking
    )
    default boolean useRestore() {
        return false;
    }

    @ConfigItem(
            keyName = "restoreValue",
            name = "Restore amount",
            description = "Amount of restore potions to withdraw",
            position = 5,
            section = banking
    )
    default int restoreValue() {
        return 0;
    }

    @ConfigItem(
            keyName = "usePrayer",
            name = "Use prayer potions",
            description = "Withdraw prayer potions when banking",
            position = 6,
            section = banking
    )
    default boolean usePrayer() {
        return false;
    }

    @ConfigItem(
            keyName = "prayerValue",
            name = "Prayer amount",
            description = "Amount of prayer potions to withdraw",
            position = 7,
            section = banking
    )
    default int prayerValue() {
        return 0;
    }

    @ConfigItem(
            keyName = "useAntipoison",
            name = "Use antipoison",
            description = "Withdraw antipoison when banking",
            position = 8,
            section = banking
    )
    default boolean useAntipoison() {
        return false;
    }

    @ConfigItem(
            keyName = "antipoisonValue",
            name = "Antipoison amount",
            description = "Amount of antipoison to withdraw",
            position = 9,
            section = banking
    )
    default int antipoisonValue() {
        return 0;
    }

    @ConfigItem(
            keyName = "useAntifire",
            name = "Use antifire",
            description = "Withdraw antifire when banking",
            position = 10,
            section = banking
    )
    default boolean useAntifire() {
        return false;
    }

    @ConfigItem(
            keyName = "antifireValue",
            name = "Antifire amount",
            description = "Amount of antifire to withdraw",
            position = 11,
            section = banking
    )
    default int antifireValue() {
        return 0;
    }

    @ConfigItem(
            keyName = "useCombat",
            name = "Use combat potions",
            description = "Withdraw combat potions when banking",
            position = 12,
            section = banking
    )
    default boolean useCombat() {
        return false;
    }

    @ConfigItem(
            keyName = "combatValue",
            name = "Combat amount",
            description = "Amount of combat potions to withdraw",
            position = 13,
            section = banking
    )
    default int combatValue() {
        return 0;
    }

    @ConfigItem(
            keyName = "useFood",
            name = "Use food",
            description = "Withdraw food when banking",
            position = 14,
            section = banking
    )
    default boolean useFood() {
        return false;
    }

    @ConfigItem(
            keyName = "foodValue",
            name = "Food amount",
            description = "Amount of food to withdraw",
            position = 15,
            section = banking
    )
    default int foodValue() {
        return 0;
    }

    @ConfigItem(
            keyName = "ignoreTeleport",
            name = "Ignore teleport items",
            description = "Don't bank teleport items",
            position = 16,
            section = banking
    )
    default boolean ignoreTeleport() {
        return true;
    }

    // Hidden config items
    @ConfigItem(
            keyName = "inventorySetupHidden",
            name = "inventorySetupHidden",
            description = "inventorySetupHidden",
            hidden = true
    )
    default InventorySetup inventorySetupHidden() {
        return null;
    }

    @ConfigItem(
            keyName = "centerLocation",
            name = "Center location",
            description = "Center location",
            hidden = true
    )
    default WorldPoint centerLocation() {
        return new WorldPoint(0, 0, 0);
    }

    @ConfigItem(
            keyName = "safeSpotLocation",
            name = "Safe spot location",
            description = "Safe spot location",
            hidden = true
    )
    default WorldPoint safeSpot() {
        return new WorldPoint(0, 0, 0);
    }

}


