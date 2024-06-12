package net.runelite.client.plugins.hoseaplugins.KittenFeeder;


import net.runelite.client.config.*;

@ConfigGroup("KittenFeederConfig")
public interface KittenFeederConfig extends Config {
    @ConfigItem(
            keyName = "Food",
            name = "Food",
            description = "",
            position = 0
    )
    default String food() {
        return "Raw salmon";
    }

    @ConfigItem(
            keyName = "frequency",
            name = "Minutes",
            description = "How often to feed your kitten",
            position = 1
    )
    default int frequency() {
        return 20;
    }

    @ConfigItem(
            keyName = "stroke",
            name = "Stroke",
            description = "Stroke your kitten",
            position = 2
    )
    default boolean stroke() {
        return true;
    }

}

