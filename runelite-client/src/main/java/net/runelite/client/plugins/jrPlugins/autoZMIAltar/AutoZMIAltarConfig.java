package net.runelite.client.plugins.jrPlugins.autoZMIAltar;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("AutoZMIAltar")
public interface AutoZMIAltarConfig extends Config {

    @ConfigItem(
            keyName = "overlay",
            name = "Enable Overlay",
            description = "Enable Overlay?",
            position = 0
    )
    default boolean overlay() { return true; }

    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 1
    )
    default String GUIDE() {
        return "MUST BE ON LUNAR SPELL BOOK\n" +
                "MUST HAVE NPC CONTACT RUNES IN INVENTORY\n" +
                "MUST HAVE JEWELLERY BOX SET TO EDGEVILLE\n" +
                "MUST HAVE SETUP AUTO PAYMENT AT ZMI BANK\n" +
                "1. Setup Your Rs2Inventory Setup with it's name\n" +
                "2. Start next to your selected bank";
    }

    @ConfigItem(
            keyName = "inventory",
            name = "Rs2Inventory Setup Name",
            description = "Enter the name of the inventory setup you want to use",
            position = 2
    )
    default String INVENTORY() { return "AutoZMI"; }

    @ConfigItem(
            keyName = "teleport",
            name = "Teleport",
            description = "Choose your teleport",
            position = 3
    )
    default TELEPORT TELEPORT() {
        return TELEPORT.CONSTRUCT_CAPE_T;
    }

    @ConfigItem(
            keyName = "bank",
            name = "Bank",
            description = "Where to bank?",
            position = 4
    )
    default BANK BANK() {
        return BANK.ZMIBANK;
    }

    @ConfigItem(
            keyName = "stamina",
            name = "Stamina",
            description = "Choose your stamina",
            position = 5
    )
    default STAMINA STAMINA() {
        return STAMINA.ORNATEPOOL;
    }

    @ConfigItem(
            keyName = "fixPouches",
            name = "Fix Pouches",
            description = "How many runs until fix pouches?",
            position = 6
    )
    default int fixPouchesAt() {
        return 9;
    }
}

