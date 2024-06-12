package net.runelite.client.plugins.hoseaplugins.strategyexample;


import net.runelite.client.config.*;

@ConfigGroup("StrategySmithConfig")
public interface StrategySmithConfig extends Config {
    @ConfigItem(
            keyName = "Toggle",
            name = "Toggle",
            description = "",
            position = 0
    )
    default Keybind toggle() {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "bar",
            name = "Bar",
            description = "Which bar you will use",
            position = 1
    )
    default Bar bar() {
        return Bar.MITHRIL;
    }

    @ConfigItem(
            keyName = "item",
            name = "Item",
            description = "Which item you will make",
            position = 2
    )
    default SmithingItem item() {
        return SmithingItem.PLATE_BODY;
    }

    @ConfigItem(
            keyName = "tickDelay",
            name = "Tick Delay",
            description = "Slow down certain actions",
            position = 3
    )
    default int tickDelay() {
        return 0;
    }
}

