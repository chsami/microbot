package net.runelite.client.plugins.microbot.cooking;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.cooking.enums.*;
import net.runelite.client.plugins.microbot.util.inventory.DropOrder;

@ConfigGroup("autocooking")
public interface AutoCookingConfig extends Config {

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
            name = "Item to Cook",
            keyName = "itemToCook",
            position = 0,
            description = "Item to cook",
            section = cookingSection
    )
    default CookingItem cookingItem() {
        return CookingItem.RAW_SHRIMP;
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
            name = "Location",
            keyName = "cookingLocation",
            position = 2,
            description = "Location to cook",
            section = cookingSection
    )
    default CookingLocation cookingLocation() {
        return CookingLocation.COOKS_KITCHEN;
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
    default DropOrder getDropOrder() {
        return DropOrder.STANDARD;
    }
}
