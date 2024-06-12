package net.runelite.client.plugins.hoseaplugins.superglass;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("SuperGlassMaker")
public interface SuperGlassMakerPluginConfig extends Config {
    @ConfigItem(
            keyName = "Secondary",
            name = "Secondary",
            description = "Which secondary you will use"
    )
    default Secondary secondary() {
        return Secondary.GIANT_SEAWEED;
    }
}