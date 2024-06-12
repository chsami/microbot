package net.runelite.client.plugins.hoseaplugins.lucidcombat;

import net.runelite.client.plugins.hoseaplugins.api.spells.Spells;
import lombok.Getter;
import net.runelite.client.config.*;

import java.util.Set;

@ConfigGroup("lucid-combat")
public interface LucidCombatConfig extends Config
{
    @ConfigSection(name = "Auto-Combat Settings", description = "Control settings for auto-combat", position = 0, closedByDefault = true)
    String autoCombatSection = "Auto-Combat Settings";

    @ConfigItem(name = "Show Overlay", description = "Shows an overlay with information about the current state of the auto-combat", position = 0, keyName = "autocombatOverlay", section = autoCombatSection)
    default boolean autocombatOverlay()
    {
        return true;
    }

    @ConfigItem(name = "Highlight Starting Tile", description = "Shows an tile overlay over your starting tile", position = 1, keyName = "highlightStartTile", section = autoCombatSection)
    default boolean highlightStartTile()
    {
        return false;
    }

    @ConfigItem(name = "Highlight Max Range Tiles", description = "Shows an tile overlay around the circumfurence of your max range", position = 2, keyName = "highlightMaxRangeTiles", section = autoCombatSection)
    default boolean highlightMaxRangeTiles()
    {
        return false;
    }

    @ConfigItem(name = "Toggle Hotkey", description = "This hotkey will toggle the auto-combat on/off", position = 3, keyName = "autocombatHotkey", section = autoCombatSection)
    default Keybind autocombatHotkey()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "NPC(s) To Fight", description = "The names of the monsters to fight, separated by commas. E.g: Cow, Abyssal demon, Zulrah", position = 4, keyName = "npcToFight", section = autoCombatSection)
    default String npcToFight()
    {
        return "";
    }

    @ConfigItem(name = "NPC ID Blacklist", description = "The IDs of NPCs to avoid, separated by commas. E.g: 122, 6233, 352", position = 5, keyName = "idBlacklist", section = autoCombatSection)
    default String idBlacklist()
    {
        return "";
    }

    @ConfigItem(name = "Right Click Entry", description = "Shows a menu entry on NPC's to be able to start the plugin that way.", position = 6, keyName = "rightClickMenu", section = autoCombatSection)
    default boolean rightClickMenu()
    {
        return false;
    }

    @ConfigItem(name = "Max Range From Start", description = "The plugin will ignore any monsters AND loot that are more than x tiles away from starting tile", position = 7, keyName = "maxRange", section = autoCombatSection)
    default int maxRange()
    {
        return 5;
    }

    @ConfigItem(name = "Anti-Lure Protection", description = "Will loot not loot any items outside of the max range + 3", position = 8, keyName = "antilureProtection", section = autoCombatSection)
    default boolean antilureProtection()
    {
        return false;
    }

    @ConfigItem(name = "Auto-Combat Play-Style", description = "The plugin will imitate a certain type of playstyle to suit your needs. Normal by default.", position = 9, keyName = "autocombatStyle", section = autoCombatSection)
    default PlayStyle autocombatStyle()
    {
        return PlayStyle.NORMAL;
    }

    @ConfigItem(name = "Reaction Anti-Pattern", description = "The plugin will make random micro-adjustments to reaction times over time in an attempt to create anti-patterns. Will make the reaction times change over time.", position = 10, keyName = "reactionAntiPattern", section = autoCombatSection)
    default boolean reactionAntiPattern()
    {
        return false;
    }

    @ConfigItem(name = "Use Safepot", description = "Uses the starting tile as your safespot and will return to it if not looting", position = 11, keyName = "useSafespot", section = autoCombatSection)
    default boolean useSafespot()
    {
        return false;
    }

    @ConfigItem(name = "Allow Unreachable NPCs", description = "Allows the plugin to target NPCs whose tile can't be reached", position = 12, keyName = "allowUnreachable", section = autoCombatSection)
    default boolean allowUnreachable()
    {
        return false;
    }

    @ConfigItem(name = "Multiple Targets", description = "The plugin will tag as many eligible targets instead of focusing on a single one", position = 13, keyName = "multipleTargets", section = autoCombatSection)
    default boolean multipleTargets()
    {
        return false;
    }

    @ConfigItem(name = "Ticks Until Idle", description = "How many ticks until the plugin considers you idle and stops your boosts and thralls upkeep and deactivates prayers. (100 ticks in 1 minute) HP and Pray upkeep will still work regardless of idleness.", position = 14, keyName = "inactiveTicks", section = autoCombatSection)
    default int inactiveTicks()
    {
        return 200;
    }

    @ConfigItem(name = "Deactivate all prayers on idle", description = "Deactiates any active prayers if you go idle", position = 15, keyName = "deactivatePrayersOnIdle", section = autoCombatSection)
    default boolean deactivatePrayersOnIdle()
    {
        return false;
    }

    @ConfigItem(name = "Reset Target Range Hotkey", description = "This hotkey will reset the starting position, used for targeting range.", position = 16, keyName = "resetTargetRangeHotkey", section = autoCombatSection)
    default Keybind resetTargetRangeHotkey()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Reset Safespot Hotkey", description = "This hotkey reset the safespot location to the players current location.", position = 17, keyName = "resetSafeSpotHotkey", section = autoCombatSection)
    default Keybind resetSafeSpotHotkey()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(name = "Allow External Setup", description = "Allows the state of auto-combat to be controlled by config changes. This is to allow Lucid Hotkeys 2 to work with the setAutoCombat action.", position = 18, keyName = "allowExternalSetup", section = autoCombatSection)
    default boolean allowExternalSetup()
    {
        return false;
    }

    @ConfigSection(name = "Loot Settings", description = "Control loot settings for auto-combat", position = 1, closedByDefault = true)
    String lootSection = "Loot Settings";

    @ConfigItem(name = "Enable Looting", description = "Will loot nearby items during auto-combat", position = 0, keyName = "enableLooting", section = lootSection)
    default boolean enableLooting()
    {
        return false;
    }

    @ConfigItem(name = "Loot Names", description = "Names of items to loot, separated by commas", position = 1, keyName = "lootNames", section = lootSection)
    default String lootNames()
    {
        return "";
    }

    @ConfigItem(name = "Blacklist Items", description = "Names of items to to avoid specifically (for excluding unwanted items from partial name matching)", position = 2, keyName = "lootBlacklist", section = lootSection)
    default String lootBlacklist()
    {
        return "";
    }

    @ConfigItem(name = "Loot Above Price", description = "Loot items above this price", position = 3, keyName = "lootAbovePrice", section = lootSection)
    default int lootAbovePrice()
    {
        return 10000;
    }

    @ConfigItem(name = "Max Range From Player", description = "How far away from the player can an item be for us to pick it up? Plugin still respects max range.", position = 4, keyName = "lootRange", section = lootSection)
    default int lootRange()
    {
        return 5;
    }

    @ConfigItem(name = "Stackable Only", description = "Will only loot the item if it is stackable in your inventory <br>"
            + "(excludes bones/ashes which can still be picked up normally)", position = 5, keyName = "stackableOnly", section = lootSection)
    default boolean stackableOnly()
    {
        return false;
    }

    @ConfigItem(name = "Bury Bones/Scatter Ashes", description = "Will auto-bury or scatter any bones or ashes that get picked up", position = 6, keyName = "buryScatter", section = lootSection)
    default boolean buryScatter()
    {
        return false;
    }

    @ConfigItem(name = "Only Loot No/Dead Target", description = "Will only grab loot if you don't have a target or they are dead or it's been more than max loot ticks since the last time you looted something", position = 7, keyName = "onlyLootWithNoTarget", section = lootSection)
    default boolean onlyLootWithNoTarget()
    {
        return false;
    }

    @ConfigItem(name = "Max Ticks Between Loot Attempts", description = "If you are only looting with no/dead target it will attempt to loot if its been x amount of ticks since you last looted", position = 8, keyName = "maxTicksBetweenLooting", section = lootSection)
    default int maxTicksBetweenLooting()
    {
        return 115;
    }

    @ConfigItem(name = "Loot Goblin", description = "Will grab any valid loot, even if it's not yours", position = 9, keyName = "lootGoblin", section = lootSection)
    default boolean lootGoblin()
    {
        return false;
    }

    // Slayer Settings
    @ConfigSection(name = "Slayer Settings", description = "Control settings for slayer features", position = 2, closedByDefault = true)
    String slayerSection = "Slayer Settings";

    @ConfigItem(name = "Stop Fighting On Task Complete", description = "Will turn off the auto-combat once your slayer task is finished", position = 0, keyName = "stopOnTaskCompletion", section = slayerSection)
    default boolean stopOnTaskCompletion()
    {
        return false;
    }

    @ConfigItem(name = "Pause Upkeep On Task Complete", description = "Forces the plugin to go idle upon slayer task completion to prevent upkeep from continuing (will re-activate if you take damage)", position = 1, keyName = "stopUpkeepOnTaskCompletion", section = slayerSection)
    default boolean stopUpkeepOnTaskCompletion()
    {
        return false;
    }

    @ConfigItem(name = "Use Item On Task Complete", description = "Will use a custom item available in your inventory when the slayer task is done", position = 2, keyName = "useItemOnCompletion", section = slayerSection)
    default boolean useItemOnCompletion()
    {
        return false;
    }

    @ConfigItem(name = "Item Name(s) To Use", description = "Will use any of the items available in your inventory when the slayer task is done. Separate multiple values with commas. Case sensitive.", position = 3, keyName = "itemNames", section = slayerSection)
    default String itemNames()
    {
        return "Teleport to house";
    }

    @ConfigItem(name = "Item Action(s) To Use", description = "Will use this action on the chosen item. Each item must have only 1 action paired with it.", position = 4, keyName = "itemActions", section = slayerSection)
    default String itemActions()
    {
        return "Break";
    }

    @ConfigItem(name = "Take Cannon On Task Complete", description = "Will attempt to take the closest cannon on task completion", position = 5, keyName = "cannonOnCompletion", section = slayerSection)
    default boolean cannonOnCompletion()
    {
        return false;
    }

    @ConfigItem(name = "Slayer NPC Finishing Item", description = "Will auto use the defined item on your target when they get below % health to finish them off. Works regardless of auto-combat state if enabled", position = 6, keyName = "autoSlayerFinisher", section = slayerSection)
    default boolean autoSlayerFinisher()
    {
        return false;
    }

    @ConfigItem(name = "Finishing Item", description = "This is the item you need to use to full-kill your slayer target.", position = 7, keyName = "slayerFinisherItem", section = slayerSection)
    default SlayerFinisher slayerFinisherItem()
    {
        return SlayerFinisher.NONE;
    }

    @ConfigItem(name = "Finish Below HP %", description = "Will auto-use the slayer finisher when your target is less than this % of health remaining", position = 8, keyName = "slayerFinisherHpPercent", section = slayerSection)
    default int slayerFinisherHpPercent()
    {
        return 10;
    }

    @ConfigItem(name = "Equip Slaughter Bracelets", description = "Will equip any Bracelets of Slaughter from your inventory when the current one disintegrates.", position = 9, keyName = "equipSlaughterBracelet", section = slayerSection)
    default boolean equipSlaughterBracelet()
    {
        return false;
    }

    @ConfigItem(name = "Equip Expeditious Bracelets", description = "Will equip any Expeditious Bracelets from your inventory when the current one disintegrates.", position = 10, keyName = "equipExpeditiousBracelet", section = slayerSection)
    default boolean equipExpeditiousBracelet()
    {
        return false;
    }

    // Prayer upkeep
    @ConfigSection(name = "Prayer Upkeep", description = "Control settings for prayer upkeep", position = 3, closedByDefault = true)
    String prayerUpkeepSection = "Prayer Upkeep";

    @ConfigItem(name = "Enable Prayer Restore", description = "Enables auto prayer upkeep. Auto-detects prayer restore items in inventory", position = 0, keyName = "enablePrayerRestore", section = prayerUpkeepSection)
    default boolean enablePrayerRestore()
    {
        return false;
    }

    @ConfigItem(name = "Prayer Points Minimum", description = "Will drink once prayer points goes below this level", position = 1, keyName = "prayerPointsMin", section = prayerUpkeepSection)
    default int prayerPointsMin()
    {
        return 30;
    }

    @ConfigItem(name = "Min Restore Buffer", description = "Will add this random buffer range onto the minimum prayer points needed before restore to make the restoration a bit more random", position = 2, keyName = "prayerRestoreBuffer", section = prayerUpkeepSection)
    default int prayerRestoreBuffer()
    {
        return 0;
    }

    @ConfigItem(name = "Restore To Max", description = "When it restores, it will keep sipping until max prayer points minus the buffer amount", position = 3, keyName = "restorePrayerToMax", section = prayerUpkeepSection)
    default boolean restorePrayerToMax()
    {
        return false;
    }

    @ConfigItem(name = "Max Buffer", description = "Adds a buffer to check if your Prayer is within range of max minus this amount. E.g. Your Prayer is 99 and the buffer is 5, it will consider 94+ Prayer 'max'", position = 4, keyName = "maxPrayerBuffer", section = prayerUpkeepSection)
    default int maxPrayerBuffer()
    {
        return 0;
    }

    // HP upkeep
    @ConfigSection(name = "HP Upkeep", description = "Control settings for HP upkeep", position = 4, closedByDefault = true)
    String hpUpkeepSection = "HP Upkeep";

    @ConfigItem(name = "Enable HP Restore", description = "Enables auto HP upkeep. Auto-detects any food not on the blacklist", position = 0, keyName = "enableHpRestore", section = hpUpkeepSection)
    default boolean enableHpRestore()
    {
        return false;
    }

    @ConfigItem(name = "Stop If Out Of Food", description = "Stops the auto-combat if you run out of food", position = 1, keyName = "stopIfNoFood", section = hpUpkeepSection)
    default boolean stopIfNoFood()
    {
        return false;
    }

    @ConfigItem(name = "Use Item When Out Of Food", description = "Will use an item if out of food. Uses the item names/actions from the 'use item on task complete' config", position = 2, keyName = "useItemIfOutOfFood", section = hpUpkeepSection)
    default boolean useItemIfOutOfFood()
    {
        return false;
    }

    @ConfigItem(name = "Food Blacklist", description = "Will not attempt to eat any of these item named when looking for food. Multiple values should be separated by commas. Uses names only, no IDs", position = 3, keyName = "foodBlacklist", section = hpUpkeepSection)
    default String foodBlacklist()
    {
        return "";
    }

    @ConfigItem(name = "Enable Double Eat", description = "Enables 1-tick double-eating with main food item + karambwan if applicable", position = 4, keyName = "enableDoubleEat", section = hpUpkeepSection)
    default boolean enableDoubleEat()
    {
        return false;
    }

    @ConfigItem(name = "Enable Triple Eat", description = "Enables 1-tick triple-eating with main food item + brew + karambwan if applicable", position = 5, keyName = "enableTripleEat", section = hpUpkeepSection)
    default boolean enableTripleEat()
    {
        return false;
    }

    @ConfigItem(name = "Minimum HP", description = "Will eat once your HP goes below this level", position = 6, keyName = "minHp", section = hpUpkeepSection)
    default int minHp()
    {
        return 30;
    }

    @ConfigItem(name = "Min Restore Buffer", description = "Will add this random buffer range onto the minimum HP needed before restore to make the restoration a bit more random", position = 7, keyName = "minHpBuffer", section = hpUpkeepSection)
    default int minHpBuffer()
    {
        return 4;
    }

    @ConfigItem(name = "Restore To Max", description = "Will keep eating until Max HP minus the buffer amount", position = 8, keyName = "restoreHpToMax", section = hpUpkeepSection)
    default boolean restoreHpToMax()
    {
        return false;
    }

    @ConfigItem(name = "Max Buffer", description = "Adds a buffer to check if your HP is within range of max minus this amount. E.g. Your HP is 99 and the buffer is 5, it will consider 94+ HP 'max'", position = 9, keyName = "maxHpBuffer", section = hpUpkeepSection)
    default int maxHpBuffer()
    {
        return 0;
    }

    // Boost Upkeep
    @ConfigSection(name = "Boost Upkeep", description = "Control settings for HP upkeep", position = 5, closedByDefault = true)
    String boostUpkeepSection = "Boost Upkeep";

    @ConfigItem(name = "Melee Boost Upkeep", description = "Enables auto Melee boost upkeep. Auto-detects potions", position = 0, keyName = "enableMeleeUpkeep", section = boostUpkeepSection)
    default boolean enableMeleeUpkeep()
    {
        return false;
    }

    @ConfigItem(name = "Min Melee Level Boost", description = "Re-boosts once below this amount of boost above normal Melee level", position = 1, keyName = "minMeleeBoost", section = boostUpkeepSection)
    default int minMeleeBoost()
    {
        return 5;
    }

    @ConfigItem(name = "Ranged Boost Upkeep", description = "Enables auto Ranged boost upkeep. Auto-detects potions", position = 2, keyName = "enableRangedUpkeep", section = boostUpkeepSection)
    default boolean enableRangedUpkeep()
    {
        return false;
    }

    @ConfigItem(name = "Min Ranged Level Boost", description = "Re-boosts once below this amount of boost above normal Ranged level", position = 3, keyName = "minRangedBoost", section = boostUpkeepSection)
    default int minRangedBoost()
    {
        return 5;
    }

    @ConfigItem(name = "Magic Boost Upkeep", description = "Enables auto Magic boost upkeep. Auto-detects potions + imbued/saturated heart", position = 4, keyName = "enableMagicUpkeep", section = boostUpkeepSection)
    default boolean enableMagicUpkeep()
    {
        return false;
    }

    @ConfigItem(name = "Min Magic Level Boost", description = "Re-boosts once below this amount of boost above normal Magic level", position = 5, keyName = "minMagicBoost", section = boostUpkeepSection)
    default int minMagicBoost()
    {
        return 5;
    }

    @ConfigItem(name = "Upkeep Anti-fire protection", description = "Re-applies antifire protection when you run out", position = 6, keyName = "enableAntiFireUpkeep", section = boostUpkeepSection)
    default boolean enableAntiFireUpkeep()
    {
        return false;
    }

    @ConfigItem(name = "Upkeep Anti-poison/venom protection", description = "Re-applies antipoison protection when you run out", position = 7, keyName = "enablePoisonUpkeep", section = boostUpkeepSection)
    default boolean enablePoisonUpkeep()
    {
        return false;
    }

    @ConfigItem(name = "Only Remove Poison/Venom", description = "Will only apply anti-poison/venom protection to remove active poison/venom but will not upkeep protection beyond that.", position = 8, keyName = "onlyRemovePoison", section = boostUpkeepSection)
    default boolean onlyRemovePoison()
    {
        return false;
    }


    // Auto-spec
    @ConfigSection(name = "Auto-Spec Settings", description = "Control settings for Auto-Spec", position = 6, closedByDefault = true)
    String autoSpecSection = "Auto-Spec Settings";

    @ConfigItem(name = "Enable Auto-Spec", description = "Enables auto-spec", position = 0, keyName = "enableAutoSpec", section = autoSpecSection)
    default boolean enableAutoSpec()
    {
        return false;
    }

    @ConfigItem(name = "Spec Weapon", description = "Name of your spec weapon. Partial name matches WILL work.", position = 1, keyName = "specWeapon", section = autoSpecSection)
    default String specWeapon()
    {
        return "";
    }

    @ConfigItem(name = "Spec % Needed", description = "How much spec % the special attack uses.", position = 2, keyName = "specNeeded", section = autoSpecSection)
    default int specNeeded()
    {
        return 0;
    }

    @ConfigItem(name = "Min % Before Spec", description = "How much spec % you need before the plugin will start using the special attack.", position = 3, keyName = "minSpec", section = autoSpecSection)
    default int minSpec()
    {
        return 0;
    }

    @ConfigItem(name = "Only Spec If Equipped", description = "Only toggles spec when you manually equip your spec weapon", position = 4, keyName = "specIfEquipped", section = autoSpecSection)
    default boolean specIfEquipped()
    {
        return false;
    }

    @ConfigItem(name = "Only Spec If Autocombat", description = "Only specs when autocombat is running", position = 5, keyName = "specIfAutocombat", section = autoSpecSection)
    default boolean specIfAutocombat()
    {
        return false;
    }

    // Alching
    @ConfigSection(name = "Alchemy Settings", description = "Control settings for Low/High Alchemy", position = 7, closedByDefault = true)
    String alchemySection = "Alchemy Settings";

    @ConfigItem(name = "Enable Alchemy", description = "Looks at your magic level to see which level alchemy to use and uses it on the named items", position = 0, keyName = "alchStuff", section = alchemySection)
    default boolean alchStuff()
    {
        return false;
    }

    @ConfigItem(name = "Alch Names", description = "Names of items to alch, separated by commas. partial matches DO work. case sensitive.", position = 1, keyName = "alchNames", section = alchemySection)
    default String alchNames()
    {
        return "";
    }

    @ConfigItem(name = "Blacklist Alchs", description = "Names of items to to explicitly NOT alch (for excluding unwanted items from partial name matching)", position = 2, keyName = "alchBlacklist", section = alchemySection)
    default String alchBlacklist()
    {
        return "";
    }

    @ConfigItem(name = "Don't Alch Untradables", description = "Will not attempt to alch any untradable items", position = 3, keyName = "ignoreUntradables", section = alchemySection)
    default boolean ignoreUntradables()
    {
        return false;
    }

    // Auto-thralls
    @ConfigSection(name = "Thrall Settings", description = "Control settings for Thrall Summoning", position = 8, closedByDefault = true)
    String thrallSection = "Thrall Settings";

    @ConfigItem(name = "Enable Auto-Thralls", description = "Will auto-summon thralls as long as you've recently animated and have the runes for it", position = 0, keyName = "enableThralls", section = thrallSection)
    default boolean enableThralls()
    {
        return false;
    }

    @ConfigItem(name = "Thrall Type", description = "Select which type of thrall to summon", position = 1, keyName = "thrallType", section = thrallSection)
    default ThrallType thrallType()
    {
        return ThrallType.GREATER_GHOST;
    }

    enum ThrallType
    {
        LESSER_GHOST(Spells.NecromancySpell.RESURRECT_LESSER_GHOST),
        LESSER_SKELETON(Spells.NecromancySpell.RESURRECT_LESSER_SKELETON),
        LESSER_ZOMBIE(Spells.NecromancySpell.RESURRECT_LESSER_ZOMBIE),
        SUPERIOR_GHOST(Spells.NecromancySpell.RESURRECT_SUPERIOR_GHOST),
        SUPERIOR_SKELETON(Spells.NecromancySpell.RESURRECT_SUPERIOR_SKELETON),
        SUPERIOR_ZOMBIE(Spells.NecromancySpell.RESURRECT_SUPERIOR_ZOMBIE),
        GREATER_GHOST(Spells.NecromancySpell.RESURRECT_GREATER_GHOST),
        GREATER_SKELETON(Spells.NecromancySpell.RESURRECT_GREATER_SKELETON),
        GREATER_ZOMBIE(Spells.NecromancySpell.RESURRECT_GREATER_ZOMBIE);

        @Getter
        final Spells.NecromancySpell thrallSpell;
        ThrallType(Spells.NecromancySpell thrallSpell)
        {
            this.thrallSpell = thrallSpell;
        }
    }

    enum SlayerFinisher
    {
        BAG_OF_SALT("Bag of salt", 1568, "Rockslug"), ICE_COOLER("Ice cooler", 2778, "Lizard"), FUNGICIDE_SPRAY("Fungicide spray", 3327, "Zygomite"), ROCK_HAMMER("Rock hammer", 1520, "Gargoyle"), ROCK_THROWNHAMMER("Rock thrownhammer", 1520, "Gargoyle"), NONE("n/a", -1, "n/a");

        @Getter
        final String itemName;

        @Getter
        final int deathAnimation;

        @Getter
        final String monsterName;

        SlayerFinisher(String itemName, int deathAnimation, String monsterName)
        {
            this.itemName = itemName;
            this.deathAnimation = deathAnimation;
            this.monsterName = monsterName;
        }

    }
}