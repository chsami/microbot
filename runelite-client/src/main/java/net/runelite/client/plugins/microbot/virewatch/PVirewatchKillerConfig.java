package net.runelite.client.plugins.microbot.virewatch;


import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.virewatch.models.PRAY_STYLE;

@ConfigGroup("PVireKillerConfig")
public interface PVirewatchKillerConfig extends Config {

    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 0
    )
    default String GUIDE() {
        return "Read the description of the settings for more information. Start with inventory and gear setup near the prayer statue and configure the settings. \n HAVE AUTO RETALIATE ON!" ;
    }

    @ConfigItem(
            keyName = "killRadius",
            name = "Tile Radius",
            description = "Radius to kill/roam in",
            position = 1,
            section = combatSection
    )
    default int radius() {
        return 10;
    }

    @ConfigItem(
            keyName = "prayStyle",
            name = "Prayer style",
            description = "Normal prayers are using the interface quick-prayers use the quick prayer orb to turn on prayer",
            position = 2,
            section = combatSection
    )
    default PRAY_STYLE prayStyle() {
        return PRAY_STYLE.NORMAL;
    }


    @ConfigItem(
            keyName = "piety",
            name = "Use piety",
            description = "Use the piety prayer",
            position = 3,
            section = combatSection
    )
    default boolean piety() {
        return false;
    }

    @ConfigItem(
            keyName = "Hitpoints",
            name = "Hitpoints treshhold",
            description = "Use food at certain hitpoint treshhold. If there's no food in the inventory, the script stops. Set to 0 in order to disable.",
            position = 4,
            section = combatSection
    )
    default int hitpoints()
    {
        return 20;
    }

    @ConfigItem(
            keyName = "prayAt",
            name = "Recharge prayer when points below",
            description = "At what interval to recharge ur prayers at the altar",
            position = 5,
            section = combatSection
    )
    default int prayAt() {
        return 15;
    }


    @ConfigSection(
            name = "Combat",
            description = "Combat",
            position = 1,
            closedByDefault = false
    )
    String combatSection = "Combat";

    @ConfigSection(
            name = "Loot",
            description = "Loot",
            position = 2,
            closedByDefault = false
    )
    String lootSection = "Loot";

    @ConfigSection(
            name = "Money",
            description = "Money",
            position = 3,
            closedByDefault = false
    )
    String moneySection = "Money";

    @ConfigSection(
            name = "Ticks",
            description = "Ticks",
            position = 4,
            closedByDefault = false
    )
    String tickSection = "Ticks";

    @ConfigSection(
            name = "Performance",
            description = "Performance",
            position = 5,
            closedByDefault = true
    )
    String performanceSection = "Performance";

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
            position = 2,
            section = lootSection
    )
    default int maxPriceOfItemsToLoot() {
        return 15000000;
    }

    // delayed looting
    @ConfigItem(
            keyName = "delayedLooting",
            name = "Delayed Looting",
            description = "Lets the loot stay on the ground for a while before picking it up",
            position = 3,
            section = lootSection
    )
    default boolean toggleDelayedLooting() {
        return false;
    }

    @ConfigItem(
            keyName = "onlyLootMyItems",
            name = "Only Loot My Items",
            description = "Only loot items that are dropped by you",
            position = 4,
            section = lootSection
    )

    default boolean toggleOnlyLootMyItems() {
        return false;
    }

    @ConfigItem(
            keyName = "lootRunes",
            name = "Loot runes",
            description = "Loots the Death/Nature/Blood ignoring min value",
            position = 5,
            section = lootSection
    )

    default boolean lootRunes() {
        return false;
    }

    @ConfigItem(
            keyName = "lootCoins",
            name = "Loot coins",
            description = "Loots the coins dropped",
            position = 6,
            section = lootSection
    )

    default boolean lootCoins() {
        return false;
    }

    @ConfigItem(
            keyName = "alchItems",
            name = "High Alch items",
            description = "High alch items from their drop table not the rare drop table!",
            position = 1,
            section = moneySection
    )
    default boolean alchItems() {
        return false;
    }


    @ConfigItem(
            keyName = "outOfAreaTicks",
            name = "Return after x ticks out of area",
            description = "Return back to the starting location if x ticks out of the area",
            position = 1,
            section = tickSection
    )
    default int tickToReturn() {
        return 25;
    }

    @ConfigItem(
            keyName = "outOfAreaCombatTicks",
            name = "Return after x out of combat ticks",
            description = "Return back to the starting location if x ticks out of combat",
            position = 2,
            section = tickSection
    )
    default int tickToReturnCombat() {
        return 25;
    }

    @ConfigItem(
            keyName = "disableAreaRender",
            name = "Disable fight area render",
            description = "Disables the fight are tile drawing",
            position = 1,
            section = performanceSection
    )
    default boolean disableFightArea() {
        return false;
    }

    @ConfigItem(
            keyName = "disableStatueOutline",
            name = "Disable statue outline",
            description = "Disables the statue drawing outline!",
            position = 2,
            section = performanceSection
    )
    default boolean disableStatueOutline() {
        return false;
    }

    @ConfigItem(
            keyName = "disableNpcOutline",
            name = "Disable NPC outline",
            description = "Disables the NPC drawing outline!",
            position = 3,
            section = performanceSection
    )
    default boolean disableNPCOutline() {
        return false;
    }

}
