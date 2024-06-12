package net.runelite.client.plugins.hoseaplugins.spines;


import net.runelite.client.config.*;

@ConfigGroup("ScurriusSpinesConfig")
public interface ScurriusSpinesConfig extends Config {
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
            keyName = "spineSkill",
            name = "Spine Skill",
            description = "",
            position = 1
    )
    default SpineSkill spineSkill() {
        return SpineSkill.ATTACK;
    }
    //int stop at spines redeemed

    @ConfigItem(
            keyName = "stopAtSpinesRedeemed",
            name = "Stop at # redeemed",
            description = "0 to redeem all spines",
            position = 2
    )
    default int stopAtSpinesRedeemed() {
        return 0;
    }
}

