package net.runelite.client.plugins.microbot.tithefarm;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.plugins.microbot.tithefarm.enums.TitheFarmLanes;

@ConfigGroup(TitheFarmingConfig.GROUP)
public interface TitheFarmingConfig extends Config {

    String GROUP = "Farming";

    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 1
    )
    default String GUIDE() {
        return "Start at the entrance near the table to get seeds\n" +
                "Make sure to have 16 x watering can(8) or Gricoller's can, seed dibber, spade in inventory\n" +
                "Stamina potions are supported!\n" +
                "Make sure to have the tithe farm plugin from runelite enabled\n" +
                "Happy botting!";
    }

    @ConfigItem(
            keyName = "Enable Debug",
            name = "Enable Debug",
            description = "Enable debugger",
            position = 1
    )
    default boolean enableDebugging() {
        return false;
    }

    @ConfigItem(
            keyName = "Enable Overlay",
            name = "Enable Overlay",
            description = "Enable Overlay",
            position = 1
    )
    default boolean enableOverlay() {
        return false;
    }

    @ConfigItem(
            keyName = "Lanes",
            name = "Tithe farm lanes",
            description = "Choose a lane starting from the entrance",
            position = 1
    )
    default TitheFarmLanes Lanes() {
        return TitheFarmLanes.LANE_2_3;
    }
    @ConfigItem(
            keyName = "Sleep after planting seed",
            name = "Sleep after planting seed",
            description = "Sleep after planting seed - changing this value might result in unexpected behavior",
            position = 1
    )
    default int sleepAfterPlantingSeed() {
        return 2000;
    }
    @ConfigItem(
            keyName = "Sleep after watering seed",
            name = "Sleep after watering seed",
            description = "Sleep after watering seed - changing this value might result in unexpected behavior",
            position = 1
    )
    default int sleepAfterWateringSeed() {
        return 2000;
    }
    @ConfigItem(
            keyName = "Sleep after harvesting seed",
            name = "Sleep after harvesting seed",
            description = "Sleep after harvesting seed - changing this value might result in unexpected behavior",
            position = 1
    )
    default int sleepAfterHarvestingSeed() {
        return 2000;
    }
    @ConfigItem(
            keyName = "Gricoller's can refill treshhold",
            name = "Gricoller's can refill treshhold",
            description = "Percentage before refilling the gricoller's can",
            position = 1
    )
    default int gricollerCanRefillTreshhold() {
        return 30;
    }
}

