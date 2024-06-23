package net.runelite.client.plugins.microbot.chompy;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.plugins.microbot.fletching.FletchingConfig;


@ConfigGroup("chompy")
public interface ChompyConfig extends Config {
    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 1
    )
    default String GUIDE() {
        return  "Chompy Hunt plugin - start near some toads with bow and arrows equipped. You might want to babysit this one.";
    }
}
