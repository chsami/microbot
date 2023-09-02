package net.runelite.client.plugins.jrPlugins.autoZMIAltar;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("AutoZMIAltar")
public interface AutoZMIAltarConfig extends Config {

    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 0
    )
    default String GUIDE() {
        return "1. \n2. ";
    }

    @ConfigItem(
            keyName = "overlay",
            name = "Enable Overlay",
            description = "Enable Overlay?",
            position = 1
    )
    default boolean overlay() { return true; }

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

