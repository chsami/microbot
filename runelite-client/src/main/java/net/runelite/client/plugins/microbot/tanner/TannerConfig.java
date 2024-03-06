package net.runelite.client.plugins.microbot.tanner;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.tanner.enums.HideType;
import net.runelite.client.plugins.microbot.tanner.enums.Location;

@ConfigGroup("Tanner")
public interface TannerConfig extends Config {
    @ConfigSection(
            name = "General",
            description = "General",
            position = 0,
            closedByDefault = false
    )
    String generalSection = "general";
    @ConfigItem(
            keyName = "Location",
            name = "Location",
            description = "Specific location to tan hides",
            position = 0,
            section = generalSection
    )
    default Location LOCATION()
    {
        return Location.AL_KHARID;
    }
    @ConfigItem(
            keyName = "Hide",
            name = "Hide",
            description = "Hide",
            position = 1,
            section = generalSection
    )
    default HideType HIDE_TYPE()
    {
        return HideType.LEATHER;
    }

}
