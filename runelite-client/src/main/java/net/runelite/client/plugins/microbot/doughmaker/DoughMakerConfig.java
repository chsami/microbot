package net.runelite.client.plugins.microbot.doughmaker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.doughmaker.enums.CookingActivity;
import net.runelite.client.plugins.microbot.doughmaker.enums.DoughItem;
import net.runelite.client.plugins.microbot.doughmaker.enums.HumidifyItem;
import net.runelite.client.plugins.microbot.util.inventory.InteractOrder;

@ConfigGroup("autocooking")
public interface DoughMakerConfig extends Config {

    @ConfigSection(
            name = "General",
            description = "General Cooking Settings",
            position = 0
    )
    String generalSection = "general";

    @ConfigSection(
            name = "Cooking",
            description = "Cooking",
            position = 1
    )
    String cookingSection = "cooking";

    @ConfigItem(
            name = "Guide",
            keyName = "guide",
            position = 0,
            description = "",
            section = generalSection
    )
    default String guide() {
        return "This plugin allows for semi-AFK cooking, start the script with an empty inventory\n" +
                "1. Ensure to prepare your bank with ingredients\n" +
                "2. Use nearest cooking location will override the configured cooking location & choose the nearest from the configured locations\n\n" +
                "At the moment, only cooked fish are supported. Other cooking activities will be added in the future";
    }

    @ConfigItem(
            name = "Cooking Activity",
            keyName = "cookingActivity",
            position = 1,
            description = "Choose AutoCooking Activity",
            section = generalSection
    )
    default CookingActivity cookingActivity() {
        return CookingActivity.COOKING;
    }

    @ConfigItem(
            name = "Dough to generate",
            keyName = "doughItem",
            position = 0,
            description = "Select which dough paste you want to create",
            section = cookingSection
    )
    default DoughItem doughItem() {
        return DoughItem.BREAD_DOUGH;
    }

    @ConfigItem(
            name = "Humidify Item",
            keyName = "humidifyItem",
            position = 1,
            description = "The item you wish to use to make to humidify the dough",
            section = cookingSection
    )
    default HumidifyItem humidifyItem() {
        return HumidifyItem.JUG;
    }

    @ConfigItem(
            name = "Use Nearest Cooking Location",
            keyName = "useNearestCookingLocation",
            position = 3,
            description = "Use Nearest Cooking location (this overrides the specified cooking location)",
            section = cookingSection
    )
    default boolean useNearestCookingLocation() {
        return false;
    }

    @ConfigItem(
            name = "DropOrder",
            keyName = "dropOrder",
            position = 4,
            description = "The order in which to drop items",
            section = cookingSection
    )
    default InteractOrder getDropOrder() {
        return InteractOrder.STANDARD;
    }
}
