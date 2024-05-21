package net.runelite.client.plugins.microbot.derangedarchaeologist;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.vorkath.CROSSBOW;
import net.runelite.client.plugins.microbot.vorkath.PRAYER_POTION;
import net.runelite.client.plugins.microbot.vorkath.RANGE_POTION;
import net.runelite.client.plugins.microbot.vorkath.Teleport;

@ConfigGroup("Deranged Archaeologist Config")
public interface DerangedaAchaeologistConfig extends Config {
    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 0
    )
    default String GUIDE() {
        return "1.Use the equipment inventory plugin to setup your plugin and give it the name 'archaeologist'\n" +
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


//    @ConfigSection(
//            name = "Equipment",
//            description = "Equipment",
//            position = 2,
//            closedByDefault = false
//    )
//    String equipmentSection = "Equipment";

    @ConfigSection(
            name = "Loot",
            description = "Loot",
            position = 2,
            closedByDefault = false
    )
    String lootSection = "Loot";

    @ConfigSection(
            name = "Teleports",
            description = "Teleports",
            position = 3,
            closedByDefault = false
    )
    String teleportSection = "Teleports";

    @ConfigSection(
            name = "Prayers",
            description = "Prayers",
            position = 4,
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
//    @ConfigItem(
//            keyName = "rangePotion",
//            name = "Ranging Potion",
//            description = "What Ranging potion to use?",
//            position = 1,
//            section = potionSection
//    )
//    default RANGE_POTION rangePotion() { return RANGE_POTION.DIVINE_RANGING_POTION; }
    @ConfigItem(
            keyName = "prayerPotion",
            name = "Prayer Potion",
            description = "What Prayer potion to use?",
            position = 2,
            section = potionSection
    )
    default PRAYER_POTION prayerPotion() { return PRAYER_POTION.PRAYER; }
//    @ConfigItem(
//            keyName = "staff",
//            name = "Staff",
//            description = "Choose your staff",
//            position = 1,
//            section = equipmentSection
//    )
//    default STAFFS STAFF() {
//        return STAFFS.IBANS;
//    }

    @ConfigItem(
            keyName = "Price of items to loot",
            name = "Price of items to loot",
            description = "Price of items to loot comma seperated",
            position = 0,
            section = lootSection
    )
    default int priceOfItemsToLoot()
    {
        return 2500;
    }

    @ConfigItem(
            keyName = "Augury",
            name = "Augury",
            description = "Activate Augury? (Make sure you have it unlocked and have 77 prayer!)",
            position = 4,
            section = prayerSection
    )
    default boolean activateAugyury() {
        return false;
    }

    @ConfigItem(
            keyName = "prayermode",
            name = "Prayer mode",
            description = "Choose your prayer mode",
            position = 2,
            section = prayerSection
    )
    default PRAY_MODE PRAYER_MODE() {
        return PRAY_MODE.AUTO;
    }
}
