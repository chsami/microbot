package net.runelite.client.plugins.griffinplugins.griffinmining;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.griffinplugins.griffintrainer.GriffinMiningPlugin;

@ConfigGroup(GriffinMiningPlugin.CONFIG_GROUP)
public interface GriffinMiningConfig extends Config {
    @ConfigSection(
            name = "Skill Levels",
            description = "Skill Levels",
            position = 0,
            closedByDefault = false
    )
    String skillsSection = "skills";

    @ConfigItem(
            keyName = "miningLevel",
            name = "Mining",
            description = " Mining Level",
            position = 0,
            section = skillsSection
    )
    default int miningLevel() {
        return 0;
    }

    @ConfigSection(
            name = "Mining Settings",
            description = "Mining Settings",
            position = 1,
            closedByDefault = false
    )
    String miningSettingsSection = "miningSettings";

    @ConfigItem(
            keyName = "keepOre",
            name = "Keep Ore",
            description = "Keep Ore",
            position = 0,
            section = miningSettingsSection
    )
    default boolean keepOre() {
        return true;
    }


}
