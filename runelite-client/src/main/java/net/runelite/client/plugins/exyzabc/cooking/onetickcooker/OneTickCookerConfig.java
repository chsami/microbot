package net.runelite.client.plugins.exyzabc.cooking.onetickcooker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.plugins.exyzabc.cooking.onetickcooker.enums.OneTickCookerLocation;

@ConfigGroup("oneTickCooker")
public interface OneTickCookerConfig extends Config {
    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 0
    )
    default String GUIDE() {
        return "Steps to use the plugin:\n" +
                "\n1. Go to your chosen cooking location"+
                "\n2. Make sure you have uncooked Karambwans in your bank" +
                "\n3. Enable Plugin\n" +
                "\nNotes:\n" +
                "\nMake sure to have the RuneLite plugin \"Stretched Mode\" turned off to avoid potential issues!\n"+
                "\nMake sure to have your camera zoomed out so the bank and cooking range in your chosen location are visible!\n" +
                "\nRequired Items\n" +
                "\nUncooked karambwans (In bank)\n" +
                "\nRecommended Items\n" +
                "\nNone\n" +
                "\nLocation:\n" +
                "\nAny location from the ones available below\n" +
                "\nSkill requirements:\n" +
                "\nLevel 30 Cooking\n" +
                "\nFor a more detailed guide (Including Screenshots and Videos) go to the Discord forum thread (Under exyzabc-plugins)!";
    }

    @ConfigItem(
            keyName = "Location",
            name = "Location",
            description = "Choose the location to cook at",
            position = 0
    )
    default OneTickCookerLocation LOCATION() {
        return OneTickCookerLocation.HOSIDIUS_KITCHEN;
    }

    @ConfigItem(
            keyName = "overlay",
            name = "Enable Overlay",
            description = "Enable Overlay?",
            position = 1
    )
    default boolean overlay() { return true; }
}
