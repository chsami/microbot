package net.runelite.client.plugins.hoseaplugins.AutoCombatv2;


import net.runelite.client.config.*;

@ConfigGroup("AutoCombatv2Config")
public interface AutoCombatv2Config extends Config {
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
            keyName = "npcTarget",
            name = "npcTarget",
            description = "NPCs you want to kill",
            position = 1
    )
    default String npcTarget() {
        return "Hill giant";
    }

    @ConfigItem(
            keyName = "loot",
            name = "Items to loot",
            description = "Write a list of item names (Case sensitive) separated by commas.",
            position = 2
    )
    default String loot() {
        return "Coins,Big bones";
    }

    @ConfigItem(
            keyName = "specEnabled?",
            name = "Use Wep Spec?",
            description = "Use your weapons special?",
            position = 1
    )
    default boolean specEnabled() {
        return true;
    }

    @ConfigItem(
            keyName = "foodToEat",
            name = "Food: ",
            description = "What food will you use to heal?",
            position = 1
    )
    default String foodToEat() {
        return "Shark";
    }

    @Range(
            min = 1,
            max = 99
    )
    @ConfigItem(
            keyName = "EatAt",
            name = "Eat at",
            description = "",
            position = 3
    )
    default int EatAt() {
        return 50;
    }

    @ConfigItem(
            keyName = "tickDelay",
            name = "Tick Delay",
            description = "Slow down certain actions",
            position = 3
    )
    default int tickDelay() {
        return 0;
    }
}