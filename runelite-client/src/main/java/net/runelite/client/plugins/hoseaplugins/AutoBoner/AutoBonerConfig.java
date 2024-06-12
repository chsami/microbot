package net.runelite.client.plugins.hoseaplugins.AutoBoner;


import net.runelite.client.config.*;

@ConfigGroup("AutoBonerConfig")
public interface AutoBonerConfig extends Config {
    @ConfigItem(
            keyName = "Toggle",
            name = "Toggle",
            description = "",
            position = 0
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "boneName",
            name = "Bone name",
            description = "The name of the bone to use on the altar",
            position = 2
    )
    default String boneName() {
        return "Dragon bones";
    }

    @ConfigItem(
            keyName = "altarName",
            name = "Altar name",
            description = "The name of the altar to use",
            position = 3
    )
    default String altarName() {
        return "Chaos altar";
    }
}

