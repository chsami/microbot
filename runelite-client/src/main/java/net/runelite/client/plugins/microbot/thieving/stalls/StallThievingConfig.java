package net.runelite.client.plugins.microbot.thieving.stalls;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.plugins.microbot.thieving.stalls.constants.ThievingSpot;

@ConfigGroup("stallthieving")
public interface StallThievingConfig extends Config {
    @ConfigItem(
            keyName = "instructions",
            name = "Instructions",
            description = "How to use plugin",
            position = 0
    )
    default String INSTRUCTIONS() {
        return "Go to desired thieving location, start script.\n " +
                "Ardy Silk thieving does not lure the guards, use at your own risk.\n " +
                "Add item ids that you do not care to bank to always drop item ids list";
    }

    @ConfigItem(
            keyName = "thievingSpot",
            name = "Thieving Spot",
            description = "Stall to thieve from",
            position = 1
    )
    default ThievingSpot THIEVING_SPOT() {
        return ThievingSpot.ARDY_BAKER;
    }

    @ConfigItem(
            keyName = "shouldBank",
            name = "Bank When Possible",
            description = "Banks items thieved if possible for thieving location",
            position = 2
    )
    default boolean shouldBankWhenPossible() {
        return false;
    }

    @ConfigItem(
            keyName = "alwaysDrop",
            name = "Always Drop Item ids",
            description = "Item ids to always drop even when banking",
            position = 3
    )
    default String alwaysDrop() {
        return "2309,1901";
    }
}
