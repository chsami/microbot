package net.runelite.client.plugins.exyzabc.woodcutting.threetickteaks;

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
        return "Steps to use the plugin:\n" +
                "\n1. Have your inventory setup for 3T Teaks" +
                "\n2. Enable Plugin\n" +
                "\nNotes:\n" +
                "\nMake sure to have the RuneLite plugin \"Stretched Mode\" turned off to avoid potential issues!\n"+
                "\nRequired Items\n" +
                "\nAny axe (equipped)" +
                "\nGuam leaf (Or any leaf that you can use with your level)" +
                "\nSwamp tar" +
                "\nPestle and mortar\n" +
                "\nRecommended Items\n" +
                "\nGrimy guam leaf (Or any grimy leaf that you can use with your level in case the bot accidentally processes the clean leaf)\n" +
                "\nLocation:\n" +
                "\nAny location where you can stand between 2 teak trees\n" +
                "\nSkill requirements:\n" +
                "\nLevel 35 Woodcutting\n" +
                "\nFor a more detailed guide (Including Screenshots and Videos) go to the Discord forum thread (Under exyzabc-plugins)!";
    }

    @ConfigItem(
            keyName = "overlay",
            name = "Enable Overlay",
            description = "Enable Overlay?",
            position = 1
    )
    default boolean overlay() { return true; }
}
