package net.runelite.client.plugins.caleblite.construction;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.plugins.caleblite.construction.enums.Buildables;

@ConfigGroup("clconstruction")
public interface CLConstructionConfig extends Config {
    @ConfigItem(
            keyName = "buildable",
            name = "Buildable",
            description = "Select the furniture to build",
            position = 1
    )
    default Buildables buildable() {
        return Buildables.OAK_LARDER;
    }

    @ConfigItem(
            keyName = "enablePlugin",
            name = "Enable Plugin",
            description = "Enable or disable the Construction plugin",
            position = 0
    )
    default boolean enablePlugin() {
        return false;
    }

    @ConfigItem(
            keyName = "useButler",
            name = "Use Butler",
            description = "Use butler to fetch planks",
            position = 2
    )
    default boolean useButler() {
        return true;
    }

    @ConfigItem(
            keyName = "butlerType",
            name = "Butler Type",
            description = "Select the type of butler to use",
            position = 3
    )
    default ButlerType butlerType() {
        return ButlerType.DEMON_BUTLER;
    }

    enum ButlerType {
        BUTLER,
        DEMON_BUTLER
    }
}