package net.runelite.client.plugins.microbot.flipperschaser;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("flipperschaser")
public interface FlippersChaserConfig extends Config {

    @ConfigItem(
        keyName = "useDiscordWebhook",
        name = "Use Discord Webhook",
        description = "Enable or disable Discord webhook notifications"
    )
    default boolean useDiscordWebhook() {
        return false;
    }

    @ConfigItem(
        keyName = "discordWebhookUrl",
        name = "Discord Webhook URL",
        description = "URL of the Discord webhook"
    )
    default String discordWebhookUrl() {
        return "";
    }
}
