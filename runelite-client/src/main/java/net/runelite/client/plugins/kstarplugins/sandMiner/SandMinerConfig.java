package net.runelite.client.plugins.kstarplugins.sandMiner;


import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("SandMiner")
public interface SandMinerConfig extends Config {

    @ConfigItem(
            keyName = "clues",
            name = "Drop clue geodes?",
            description = "Enable Overlay?",
            position = 1
    )
    default boolean dropClues() { return true; }
}
