package net.runelite.client.plugins.microbot.vorkath;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("Vorkath Config")
public interface VorkathConfig extends Config {
    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 0
    )
    default String GUIDE() {
        return "1.Use the equipment inventory plugin to setup your plugin and give it the name 'vorkath'\n" +
                "More information about inventory plugin: https://github.com/dillydill123/inventory-setups?tab=readme-ov-file#creating-a-new-setup" +
                "2.Make sure to start at a bank";
    }

    @ConfigSection(
            name = "Potions",
            description = "Potions",
            position = 1,
            closedByDefault = false
    )
    String potionSection = "potions";


    @ConfigSection(
            name = "Equipment",
            description = "Equipment",
            position = 2,
            closedByDefault = false
    )
    String equipmentSection = "Equipment";

    @ConfigSection(
            name = "Loot",
            description = "Loot",
            position = 3,
            closedByDefault = false
    )
    String lootSection = "Loot";

    @ConfigSection(
            name = "Teleports",
            description = "Teleports",
            position = 4,
            closedByDefault = false
    )
    String teleportSection = "Teleports";

    @ConfigSection(
            name = "Prayers",
            description = "Prayers",
            position = 5,
            closedByDefault = false
    )
    String prayerSection = "Prayers";

    @ConfigItem(
            keyName = "Teleport",
            name = "Teleport",
            description = "Choose your mode of Teleport",
            position = 0,
            section = teleportSection
    )
    default Teleport teleportMode()
    {
        return Teleport.VARROCK_TAB;
    }
    @ConfigItem(
            keyName = "rangePotion",
            name = "Ranging Potion",
            description = "What Ranging potion to use?",
            position = 1,
            section = potionSection
    )
    default RANGE_POTION rangePotion() { return RANGE_POTION.DIVINE_RANGING_POTION; }
    @ConfigItem(
            keyName = "prayerPotion",
            name = "Prayer Potion",
            description = "What Prayer potion to use?",
            position = 2,
            section = potionSection
    )
    default PRAYER_POTION prayerPotion() { return PRAYER_POTION.PRAYER; }
    @ConfigItem(
            keyName = "crossbow",
            name = "Crossbow",
            description = "Choose your crossbow",
            position = 1,
            section = equipmentSection
    )
    default CROSSBOW CROSSBOW() {
        return CROSSBOW.DRAGON_HUNTER_CROSSBOW;
    }
    @ConfigItem(
            keyName = "Secondary bolt",
            name = "Secondary Bolts",
            description = "Secondary Bolts to Equip when vorkath is lower health",
            position = 2,
            section = equipmentSection
    )
    default String secondaryBolts()
    {
        return "diamond dragon bolts (e)";
    }

    @ConfigItem(
            keyName = "Price of items to loot",
            name = "Price of items to loot",
            description = "Price of items to loot comma seperated",
            position = 0,
            section = lootSection
    )
    default int priceOfItemsToLoot()
    {
        return 5000;
    }

    @ConfigItem(
            keyName = "Rigour",
            name = "Rigour",
            description = "Activate Rigour? (Make sure you have it unlocked and have 74 prayer!)",
            position = 4,
            section = prayerSection
    )
    default boolean activateRigour() {
        return false;
    }
}
