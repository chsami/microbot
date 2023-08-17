package net.runelite.client.plugins.ogPlugins.ogblastfurnace;

import net.runelite.client.config.*;

import java.awt.event.KeyEvent;

@ConfigGroup("ogBlastFurnace")
public interface ogBlastFurnaceConfig extends Config {
    @ConfigSection(
            name = "Keybinds",
            description = "Input your keybinds for the given",
            position = 1
    )
    String keybindSection = "keybinds";
    @ConfigItem(
            position = 11,
            keyName = "Inventory",
            name = "Inventory",
            description = "The key which will replace {F5}.",
            section = keybindSection
    )
    default ModifierlessKeybind inventoryKey()
    {
        return new ModifierlessKeybind(KeyEvent.VK_S, 0);
    }
}
