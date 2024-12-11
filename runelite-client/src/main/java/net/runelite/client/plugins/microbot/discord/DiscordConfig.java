package net.runelite.client.plugins.microbot.discord;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("discordnotifier")
public interface DiscordConfig extends Config {
    @ConfigItem(
            keyName = "enableNotifications",
            name = "Enable Notifications",
            description = "Enable Discord notifications",
            position = 0
    )
    default boolean enableNotifications() {
        return true;
    }

    @ConfigItem(
            keyName = "proximityRadius",
            name = "Proximity Alert Radius",
            description = "Send alert when players are within this many tiles",
            position = 1
    )
    default int proximityRadius() {
        return 5;
    }

    @ConfigItem(
            keyName = "enableProximityAlerts",
            name = "Enable Proximity Alerts",
            description = "Send Discord alerts when players are nearby",
            position = 2
    )
    default boolean enableProximityAlerts() {
        return false;
    }

    @ConfigItem(
            keyName = "onlyTrackNewPlayers",
            name = "Only Track New Players",
            description = "Only send alerts for players entering radius for the first time",
            position = 3
    )
    default boolean onlyTrackNewPlayers() {
        return true;
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