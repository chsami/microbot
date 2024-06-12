package net.runelite.client.plugins.hoseaplugins.CalvarionHelper;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("Calvarion")
public interface CalvarionHelperConfig extends Config {
    @ConfigItem(
            keyName = "lightning",
            name = "lightning colour",
            description = ""
    )
    default Color lightning() {
        return Color.RED;
    }

    @ConfigItem(
            keyName = "swing",
            name = "swing colour",
            description = ""
    )
    default Color swing() {
        return Color.BLUE;
    }

    @ConfigItem(
            keyName = "lightningFill",
            name = "lightning fill colour",
            description = ""
    )
    @Alpha
    default Color lightningFill() {
        return new Color(0, 0, 0, 50);
    }

    @Alpha
    @ConfigItem(
            keyName = "swingFill",
            name = "swing fill colour",
            description = ""
    )
    default Color swingFill() {
        return new Color(0, 0, 0, 50);
    }
}
