package net.runelite.client.plugins.microbot.AgilityPyramid;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("Pyramid")
public interface PyramidConfig extends Config {

    // Food Section
    @ConfigItem(
            keyName = "foodSection",
            name = "=== Food Settings ===",
            description = "",
            position = 0
    )
    default String foodSection() {
        return "";
    }

    @ConfigItem(
            keyName = "foodId",
            name = "Food ID",
            description = "The ID of the food item to use",
            position = 1
    )
    default int foodId() {
        return 379;
    }

    @ConfigItem(
            keyName = "minFood",
            name = "Minimum Food",
            description = "The minimum amount of food to carry",
            position = 2
    )
    default int minFood() {
        return 2;
    }

    @ConfigItem(
            keyName = "foodItemsToWithdraw",
            name = "Food Items to Withdraw",
            description = "The number of food items to withdraw",
            position = 3
    )
    default int foodItemsToWithdraw() {
        return 6;
    }

    // Waterskins Section
    @ConfigItem(
            keyName = "waterskinsSection",
            name = "=== Waterskin Settings ===",
            description = "",
            position = 4
    )
    default String waterskinsSection() {
        return "";
    }

    @ConfigItem(
            keyName = "minWaterskins",
            name = "Minimum Waterskins",
            description = "The minimum number of waterskins to carry",
            position = 5
    )
    default int minWaterskins() {
        return 2;
    }

    @ConfigItem(
            keyName = "waterskinsToWithdraw",
            name = "Waterskins to Withdraw",
            description = "The number of waterskins to withdraw",
            position = 6
    )
    default int waterskinsToWithdraw() {
        return 15;
    }

    // Pyramid Section
    @ConfigItem(
            keyName = "pyramidSection",
            name = "=== Pyramid Collection Settings ===",
            description = "",
            position = 7
    )
    default String pyramidSection() {
        return "";
    }

    @ConfigItem(
            keyName = "maxPyramids",
            name = "Maximum Pyramids",
            description = "The maximum number of pyramids to collect",
            position = 8
    )
    default int maxPyramids() {
        return 4;
    }
}
