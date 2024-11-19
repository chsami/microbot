package net.runelite.client.plugins.microbot.giantsfoundry;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.plugins.microbot.giantsfoundry.enums.SmithableBars;
import net.runelite.client.plugins.microbot.thieving.enums.ThievingNpc;

@ConfigGroup(GiantsFoundryConfig.GROUP)
public interface GiantsFoundryConfig extends Config {

    String GROUP = "GiantsFoundry";

    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 1
    )
    default String GUIDE() {
        return "Start at the giants foundry minigame\n" +
                "Please select the bars in your UI\n" +
                "Make sure to have ice gloves on\n" +
                "Make sure to have no weapon or shield on";
    }

    @ConfigItem(
            keyName = "FirstBar",
            name = "First Bar",
            description = "Choose the first type of bar",
            position = 0
    )
    default SmithableBars FirstBar()
    {
        return SmithableBars.STEEL_BAR;
    }

    @ConfigItem(
            keyName = "firstBarAmount",
            name = "First Bar Amount",
            description = "Choose the first type of bar",
            position = 1
    )
    default int firstBarAmount()
    {
        return 14;
    }

    @ConfigItem(
            keyName = "SecondBars",
            name = "Second Bar",
            description = "Choose the second type of bar",
            position = 2
    )
    default SmithableBars SecondBar()
    {
        return SmithableBars.MITHRIL_BAR;
    }
    
    @ConfigItem(
            keyName = "secondBarAmount",
            name = "Second Bar Amount",
            description = "Choose the second type of bar",
            position = 3
    )
    default int secondBarAmount()
    {
        return 14;
    }
}
