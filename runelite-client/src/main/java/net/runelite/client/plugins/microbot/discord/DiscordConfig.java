package net.runelite.client.plugins.microbot.discord;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("discordnotifier")
public interface DiscordConfig extends Config {
    @ConfigItem(
            keyName = "enableNotifications",
            name = "Enable Notifications",
            description = "Enable sending notifications to Discord",
            hidden = true
    )
    default boolean enableNotifications() {
        return false;
    }

    @ConfigItem(
            keyName = "notifyLoginLogout",
            name = "Login/Logout",
            description = "Send notifications for login/logout events",
            hidden = true
    )
    default boolean notifyLoginLogout() {
        return true;
    }

    @ConfigItem(
            keyName = "notifyDeath",
            name = "Player Death",
            description = "Send notifications when the player dies",
            hidden = true
    )
    default boolean notifyDeath() {
        return true;
    }

    @ConfigItem(
            keyName = "notifyLevelUp",
            name = "Level Up",
            description = "Send notifications when you gain a level",
            hidden = true
    )
    default boolean notifyLevelUp() {
        return true;
    }

    @ConfigItem(
            keyName = "testWebhook",
            name = "Test Webhook",
            description = "Click to send a test message to Discord",
            hidden = true
    )
    default boolean testWebhook() {
        return false;
    }

    @ConfigItem(
            keyName = "testWebhook",
            name = "",
            description = "",
            hidden = true
    )
    void setTestWebhook(boolean test);

    @ConfigItem(
            keyName = "valuableItemThreshold",
            name = "Valuable Item Threshold",
            description = "The minimum value in GP for an item to trigger a notification",
            hidden = true
    )
    default int valuableItemThreshold() {
        return 100000;
    }
} 