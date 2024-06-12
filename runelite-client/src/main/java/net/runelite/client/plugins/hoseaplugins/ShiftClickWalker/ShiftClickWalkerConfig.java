package net.runelite.client.plugins.hoseaplugins.ShiftClickWalker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

@ConfigGroup("shiftclickwalker")
public interface ShiftClickWalkerConfig
        extends Config {
    @ConfigItem(keyName="hotkey", name="Hotkey", description="Sets the hotkey you want to hold to walk under", position=1)
    default public Keybind hotkey() {
        return Keybind.SHIFT;
    }
}