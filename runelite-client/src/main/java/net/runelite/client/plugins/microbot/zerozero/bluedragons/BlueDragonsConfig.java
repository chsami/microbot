package net.runelite.client.plugins.microbot.zerozero.bluedragons;

import net.runelite.client.config.*;
import net.runelite.client.plugins.microbot.util.misc.Rs2Food;

@ConfigGroup("bluedragons")
public interface BlueDragonsConfig extends Config {

    @ConfigItem(
            keyName = "How To",
            name = "How to use",
            description = "How to use the blue dragon plugin",
            position = 0
    )
    default String howTo()
    {
        return "Start with falador runes/teletab and correct number of food from config";
    }

    @ConfigItem(
            keyName = "startPlugin",
            name = "Start/Stop the Plugin",
            description = "This is start or stop the plugin on a toggle"
    )
    default boolean startPlugin() {
        return true;
    }

    @ConfigSection(
            name = "Loot Options",
            description = "Settings related to item looting",
            position = 1,
            closedByDefault = false
    )
    String lootSection = "lootSection";

    @ConfigItem(
            keyName = "lootDragonhide",
            name = "Loot Blue Dragonhide",
            description = "Loot blue dragonhide dropped by the dragon",
            section = lootSection
    )
    default boolean lootDragonhide() {
        return true;
    }

    @ConfigItem(
            keyName = "lootEnsouledHead",
            name = "Loot Ensouled heads",
            description = "LootEnsouled heads dropped by the dragon",
            section = lootSection
    )
    default boolean lootEnsouledHead() {
        return true;
    }


    @ConfigSection(
            name = "Food Options",
            description = "Settings for selecting food and health threshold",
            position = 2,
            closedByDefault = false
    )
    String foodSection = "foodSection";

    @ConfigItem(
            keyName = "foodType",
            name = " ",
            description = "Select the type of food to withdraw",
            section = foodSection
    )
    default Rs2Food foodType() {
        return Rs2Food.LOBSTER;
    }

    @ConfigItem(
            keyName = "foodAmount",
            name = "Amount of Food",
            description = "Specify the number of food items to withdraw",
            section = foodSection
    )
    default int foodAmount() {
        return 3;
    }

    @ConfigItem(
            keyName = "eatAtHealthPercent",
            name = "Eat at Health Percentage",
            description = "Eat food when health drops below this percentage",
            section = foodSection
    )
    default int eatAtHealthPercent() {
        return 50;
    }
}
