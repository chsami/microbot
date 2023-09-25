package net.runelite.client.plugins.microbot.farming.tithefarm;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(net.runelite.client.plugins.microbot.farming.tithefarm.TitheFarmConfig.GROUP)
public interface TitheFarmConfig extends Config {

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
                "Make sure to have the tithe farm plugin from runelite enabled\n" +
                "Happy botting!";
    }

}

