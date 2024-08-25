package net.runelite.client.plugins.microbot.qualityoflife;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("QoL")
public interface QoLConfig extends Config {
    // boolean to render Max Hit Overlay
    @ConfigItem(
            keyName = "renderMaxHitOverlay",
            name = "Render Max Hit Overlay",
            description = "Render Max Hit Overlay",
            position = 0
    )
    default boolean renderMaxHitOverlay() {
        return true;
    }

    // boolean to use Withdraw-Last from bank
    @ConfigItem(
            keyName = "useWithdrawLast",
            name = "Use Withdraw-Last",
            description = "Use Withdraw-Last",
            position = 1
    )
    default boolean useWithdrawLast() {
        return true;
    }
}
