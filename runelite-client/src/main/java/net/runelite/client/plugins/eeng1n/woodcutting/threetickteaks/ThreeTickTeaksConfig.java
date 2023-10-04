package net.runelite.client.plugins.eeng1n.woodcutting.threetickteaks;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("threeTickTeaks")
public interface ThreeTickTeaksConfig extends Config {
    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 0
    )
    default String GUIDE() {
        return "1. Have your inventory setup for 3T Teaks " +
                "\n2. Enable Plugin" +
                "\nNotes" +
                "\nItems:" +
                "\nRequired" +
                "\nAny axe (equipped)" +
                "\nGuam leaf" +
                "\nSwamp tar" +
                "\nPestle and mortar" +
                "\nRecommended:" +
                "\nGrimy Guam leaf in case the bot accidentally uses the Clean Guam leaf (For example when lagging)" +
                "\nLocation:" +
                "\nBetween 2 Teak trees (For example: Ape Atoll)" +
                "\nRequired Stats:" +
                "\nAtleast Level 35 Woodcutting";
    }

    @ConfigItem(
            keyName = "overlay",
            name = "Enable Overlay",
            description = "Enable Overlay?",
            position = 1
    )
    default boolean overlay() { return true; }
}
