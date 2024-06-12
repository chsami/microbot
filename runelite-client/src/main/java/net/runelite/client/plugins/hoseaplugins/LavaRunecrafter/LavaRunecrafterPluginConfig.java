package net.runelite.client.plugins.hoseaplugins.LavaRunecrafter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("LavaRunecrafter")
public interface LavaRunecrafterPluginConfig extends Config {
    @ConfigItem(
            keyName = "TeleMethod",
            name = "Teleport Method",
            description = "Which method you will use to get to the altar"
    )
    default TeleportMethods TeleMethod() {
        return TeleportMethods.RING_OF_DUELING;
    }

    @ConfigItem(
            keyName = "VialSmasher",
            name = "Vial Smasher Enabled",
            description = "Does the account have vial smasher enabled?"
    )
    default boolean VialSmasher() {
        return false;
    }

    @ConfigItem(
            keyName = "EssenceType",
            name = "Essence Type",
            description = "Select the type of essence to use"
    )
    default EssenceType EssenceType() {
        return EssenceType.PURE_ESSENCE;
    }

    @ConfigItem(
            keyName = "MinTickDelay",
            name = "Minimum Tick Delay",
            description = "Minimum tick delay between actions"
    )
    default int MinTickDelay() {
        return 1;
    }

    @ConfigItem(
            keyName = "MaxTickDelay",
            name = "Maximum Tick Delay",
            description = "Maximum tick delay between actions"
    )
    default int MaxTickDelay() {
        return 3;
    }
}
