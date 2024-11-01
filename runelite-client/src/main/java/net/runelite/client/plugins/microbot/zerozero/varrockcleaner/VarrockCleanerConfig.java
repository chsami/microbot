package net.runelite.client.plugins.microbot.zerozero.varrockcleaner;

import net.runelite.client.config.*;
import net.runelite.client.plugins.microbot.util.misc.Rs2Food;

@ConfigGroup(VarrockCleanerPlugin.CONFIG)

public interface VarrockCleanerConfig extends Config {

    @ConfigItem(
            keyName = "startPlugin",
            name = "Start/Stop the Plugin",
            description = "This is start or stop the plugin on a toggle"
    )
    default boolean startPlugin() {
        return true;
    }
}
