package net.runelite.client.plugins.microbot.fishing.aerial;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigInformation;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(AerialFishingConfig.GROUP)
@ConfigInformation("<h2>\uD83E\uDD86 Aerial Fisher</h2>" +
        "<h3>Version: " + AerialFishingScript.version + "</h3>" +
        "<p>Welcome to the Aerial Fishing Plugin! This plugin assists with the Aerial Fishing activity on Molch Island.</p>" +
        "<h3>Requirements:</h3>" +
        "<ul>" +
        "<li><strong>Location:</strong> Molch Island</li>" +
        "<li><strong>Equipment:</strong> Bird must be equipped</li>" +
        "<li><strong>Items:</strong> Knife and bait (either \"Fish chunks\" or \"King worm\")</li>" +
        "</ul>" +
        "<p>Ensure that you have met all the requirements before starting the plugin for a seamless experience.</p>")
public interface AerialFishingConfig extends Config {
    String GROUP = "AerialFishing";

    @ConfigSection(
            name = "General",
            description = "General",
            position = 0,
            closedByDefault = false
    )
    String generalSection = "general";


}
