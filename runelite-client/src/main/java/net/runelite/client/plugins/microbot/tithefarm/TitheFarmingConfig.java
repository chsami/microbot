package net.runelite.client.plugins.microbot.tithefarm;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.tithefarm.enums.TitheFarmLanes;

@ConfigGroup(TitheFarmingConfig.GROUP)
public interface TitheFarmingConfig extends Config {

    String GROUP = "Farming";

    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 0
    )
    default String GUIDE() {
        return "Start at the entrance near the table to get seeds\n" +
                "Make sure to have 8 x watering can(8) or Gricoller's can, seed dibber, spade in inventory\n" +
                "Stamina potions are supported!\n" +
                "Make sure to have the tithe farm plugin from runelite enabled\n" +
                "Happy botting!";
    }

    @ConfigSection(
            name = "Script Settings",
            description = "General",
            position = 1,
            closedByDefault = false
    )
    String scriptSettings = "Script Settings";
    @ConfigItem(
            keyName = "storing",
            name = "Store fruit treshhold",
            description = "Amount of fruits to have in your inventory before storing them in the sack",
            position = 0,
            section = scriptSettings
    )
    default int storeFruitTreshhold() {
        return 100;
    }
    @ConfigItem(
            keyName = "Lanes",
            name = "Tithe farm lanes",
            description = "Choose a lane starting from the entrance",
            position = 0,
            section = scriptSettings
    )
    default TitheFarmLanes Lanes() {
        return TitheFarmLanes.LANE_2_3;
    }
    @ConfigItem(
            keyName = "Gricoller's can refill treshhold",
            name = "Gricoller's can refill treshhold",
            description = "Percentage before refilling the gricoller's can",
            position = 1,
            section = scriptSettings
    )
    default int gricollerCanRefillTreshhold() {
        return 30;
    }
    @ConfigItem(
            keyName = "Sleep after planting seed",
            name = "Sleep after planting seed",
            description = "Sleep after planting seed - changing this value might result in unexpected behavior",
            position = 2,
            section = scriptSettings
    )
    default int sleepAfterPlantingSeed() {
        return 2000;
    }
    @ConfigItem(
            keyName = "Sleep after watering seed",
            name = "Sleep after watering seed",
            description = "Sleep after watering seed - changing this value might result in unexpected behavior",
            position = 3,
            section = scriptSettings
    )
    default int sleepAfterWateringSeed() {
        return 2000;
    }
    @ConfigItem(
            keyName = "Sleep after harvesting seed",
            name = "Sleep after harvesting seed",
            description = "Sleep after harvesting seed - changing this value might result in unexpected behavior",
            position = 4,
            section = scriptSettings
    )
    default int sleepAfterHarvestingSeed() {
        return 2000;
    }


    @ConfigSection(
            name = "Debug Settings",
            description = "General",
            position = 2,
            closedByDefault = false
    )
    String debugSettings = "Debug Settings";

    @ConfigItem(
            keyName = "Enable Debug",
            name = "Enable Debug",
            description = "Enable debugger",
            position = 1,
            section = debugSettings
    )
    default boolean enableDebugging() {
        return false;
    }

    @ConfigItem(
            keyName = "Enable Overlay",
            name = "Enable Overlay",
            description = "Enable Overlay",
            position = 1,
            section = debugSettings
    )
    default boolean enableOverlay() {
        return false;
    }
}

