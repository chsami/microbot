package net.runelite.client.plugins.microbot.vorkath;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.jrPlugins.autoVorkath.enums.CROSSBOW;

@ConfigGroup("Vorkath Config")
public interface VorkathConfig extends Config {
    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 0
    )
    default String GUIDE() {
        return "Use the equipment inventory plugin to setup your plugin and give it a name\n" +
                "Make sure to have a bank and all the logs in your bank";
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
            position = 2,
            closedByDefault = false
    )
    String teleportSection = "Teleports";

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
            keyName = "slayersStaff",
            name = "Slayers Staff",
            description = "Choose your slayers staff",
            position = 0,
            section = equipmentSection
    )
    default STAFF SLAYERSTAFF() {
        return STAFF.SLAYER_STAFF;
    }
    @ConfigItem(
            keyName = "crossbow",
            name = "Crossbow",
            description = "Choose your crossbow",
            position = 1,
            section = equipmentSection
    )
    default net.runelite.client.plugins.jrPlugins.autoVorkath.enums.CROSSBOW CROSSBOW() {
        return CROSSBOW.DRAGON_HUNTER_CROSSBOW;
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

}
