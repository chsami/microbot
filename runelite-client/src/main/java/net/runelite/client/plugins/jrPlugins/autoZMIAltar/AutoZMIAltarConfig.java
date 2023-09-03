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
                "1. Setup Your Inventory Setup with it's name\n" +
                "2. Start at Edgeville bank";
    }

    @ConfigItem(
            keyName = "inventory",
            name = "Inventory Setup Name",
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
            keyName = "stamina",
            name = "Stamina",
            description = "Choose your stamina",
            position = 4
    )
    default STAMINA STAMINA() {
        return STAMINA.ORNATEPOOL;
    }
}

