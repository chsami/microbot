

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.runelite.client.plugins.microbot.blastoisefurnace;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.blastoisefurnace.enums.Bars;

@ConfigGroup("blastoisefurnace")
public interface BlastoiseFurnaceConfig extends Config {
    @ConfigSection(
            name = "Blast Furnace Settings",
            description = "Blast Furnace Settings",
            position = 3,
            closedByDefault = false
    )
    String bFSettingsSection = "bFSettings";

    default boolean useStamina() {
        return true;
    }

    @ConfigItem(
            keyName = "BF Directions",
            name = "BlastoiseDirections",
            description = "Directions",
            position = 1,
            section = "bFSettings"
    )
    default String BlastoiseDirections() {
        return "must have Ice Gloves or smiths gloves (i) equiped\n+\ncoalbag, stamina potions and energy potions in bank\n\n| if doing gold bars; must have Goldsmiths Gauntlet and bank your coalbag |\n\n(makes 1.5m an hour minimum with steel and should cover expenses)\n\ncurrent version does not support foremen or coffer refill\n\nim working on that aswell as full native use of antiban\nEnjoy! :) ";
    }

    @ConfigItem(
            keyName = "Bars",
            name = "Bars",
            description = "Bars",
            position = 0,
            section = "bFSettings"
    )
    default Bars getBars() {
        return Bars.STEEL_BAR;
    }

    @ConfigItem(
            keyName = "Credits",
            name = "Credits",
            description = "Credits",
            position = 2,
            section = "bFSettings"
    )
    default String Credits() {
        return "Special thanks to:\n\nExioStorm, MrPecan, george\n\nfor building the backbone of this plugin!\nand teaching me how to java while doing so\ni will forever be gratefull.\nFishy ^_^";
    }
}