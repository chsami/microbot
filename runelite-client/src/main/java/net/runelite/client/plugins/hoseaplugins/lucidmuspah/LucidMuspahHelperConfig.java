package net.runelite.client.plugins.hoseaplugins.lucidmuspah;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("lucid-gear-swapper")
public interface LucidMuspahHelperConfig extends Config
{
    @ConfigSection(
            name = "General",
            description = "General settings",
            position = 0
    )
    String generalSection = "General";
    @ConfigItem(
            name = "Auto-pray",
            description = "Swaps prayers when muspah changes forms + does magic attack",
            position = 0,
            keyName = "autoPray",
            section = generalSection
    )
    default boolean autoPray()
    {
        return false;
    }
    @ConfigItem(
            name = "Melee step-back tick overlay",
            description = "Overlays ticks until muspah does melee attack for step-back method",
            position = 1,
            keyName = "stepBackOverlay",
            section = generalSection
    )
    default boolean stepBackOverlay()
    {
        return false;
    }
}
