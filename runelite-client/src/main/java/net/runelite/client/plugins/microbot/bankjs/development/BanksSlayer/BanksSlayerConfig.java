package net.runelite.client.plugins.microbot.bankjs.development.BanksSlayer;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.bankjs.development.BanksSlayer.enums.SlayerMasters;

@ConfigGroup("banksSlayer")
public interface BanksSlayerConfig extends Config {

    @ConfigSection(
            name = "Slayer Master Settings",
            description = "Slayer Master Settings",
            position = 0,
            closedByDefault = false
    )
    String slayerMasterSection = "slayerMasterSection";

    // Object or NPC to trade with
    @ConfigItem(
            keyName = "Slayer Master Name",
            name = "Slayer Master Name",
            description = "Sets Slayer Master to Use.",
            position = 0,
            section = slayerMasterSection
    )

    default SlayerMasters slayerMaster() {
        return SlayerMasters.TURAEL;
    }

    // Turael Point Skipping
    @ConfigItem(
            keyName = "Point Skipping",
            name = "Point Skipping",
            description = "Enable Turael Point Skipping",
            position = 1,
            section = slayerMasterSection
    )

    default boolean isPointSkippingEnabled() {
        return false;
    }

    // NPC Contact
    @ConfigItem(
            keyName = "NPC Contact",
            name = "NPC Contact",
            description = "Enable NPC Contact",
            position = 2,
            section = slayerMasterSection
    )

    default boolean isNpcContactEnabled() {
        return false;
    }

    @ConfigSection(
            name = "Utility Settings",
            description = "Utility Settings",
            position = 0,
            closedByDefault = false
    )
    String utilitySection = "utilitySection";

    // Prayer Flicking
    @ConfigItem(
            keyName = "Prayer Flicking",
            name = "Prayer Flicking",
            description = "Enable Prayer Flicking",
            position = 0,
            section = utilitySection
    )

    default boolean prayerFlicking() {
        return false;
    }

    // Emergency/Reset TP
    @ConfigItem(
            keyName = "Emergency/Reset Teleport",
            name = "Emergency/Reset Teleport",
            description = "Set Emergency/Reset Teleport",
            position = 1,
            section = utilitySection
    )

    default String emergencyTeleport() {
        return "Varrock Teleport";
    }

    // Cannon Tasks
    @ConfigItem(
            keyName = "Player Threshold",
            name = "Player Threshold",
            description = "Amount of players needed to trigger world hopping.",
            position = 2,
            section = utilitySection
    )

    default int playerThreshold() {
        return 1;
    }

    // Cannon Tasks
    @ConfigItem(
            keyName = "Cannon Tasks",
            name = "Cannon Tasks",
            description = "Tasks that you would like to use the cannon on",
            position = 3,
            section = utilitySection
    )

    default String cannonTasks() {
        return "Kalphite, Dagannoth, Fire Giants, Hellhounds, Bloodveld";
    }

    @ConfigSection(
            name = "Loot",
            description = "Loot",
            position = 3,
            closedByDefault = false
    )
    String lootSection = "Loot";

    @ConfigItem(
            keyName = "Min Price of items to loot",
            name = "Min Price of items to loot",
            description = "Min. Price of items to loot",
            position = 0,
            section = lootSection
    )
    default int priceOfItemsToLoot() {
        return 5000;
    }

    @ConfigItem(
            keyName = "Prioritise looting over Combat",
            name = "Prioritise looting over Combat",
            description = "Prioritise looting over Combat",
            position = 1,
            section = lootSection
    )
    default boolean prioritiseLoot() {
        return true;
    }

    @ConfigItem(
            keyName = "Important Loot Items",
            name = "Important Loot Items",
            description = "Important Loot Items",
            position = 2,
            section = lootSection
    )
    default String importantLootItems() {
        return "Imbued heart, Dust battlestaff, Mist battlestaff, Eternal Gem, Abyssal Whip, Abyssal Tentacle, Dragon Boots, Dark Bow, Draconic Visage, Dragon Harpoon, Basilisk Jaw, Dragon Knife, Dragon Platelegs, Dragon Plateskirt, Leaf-bladed sword, Leaf-bladed battleaxe";
    }

    @ConfigItem(
            keyName = "Ignore Loot Items",
            name = "Ignore Loot Items",
            description = "Ignore Loot Items",
            position = 3,
            section = lootSection
    )
    default String ignoreLootItems() {
        return "*Bones, *ashes";
    }
}
