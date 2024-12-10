package net.runelite.client.plugins.microbot.runecrafting.arceuus;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigInformation;

@ConfigGroup("arceuusRc")
@ConfigInformation("<div style='font-family: Arial, sans-serif; line-height: 1.6;'>"
        + "<h2 style='color: #4CAF50;'>S-1D Arceuus Runecrafting</h2>"
        + "<p>Start the plugin near the <strong>Dense runestone pillars</strong>.</p><br />"
        + "<p>You only need a <strong>pickaxe</strong> and a <strong>chisel</strong> in your inventory.</p> <br />"
        + "<p style='color: #FF5722;'>The plugin currently only supports <strong>blood runes</strong>, but <strong>soul runes</strong> will be added in the future.</p>"
        + "</div>")
public interface ArceuusRcConfig extends Config {

}
