package net.runelite.client.plugins.microbot.flipperschaser;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("flipperschaser")
public interface FlippersChaserConfig extends Config {

    @ConfigItem(
        keyName = "usePrayer",
        name = "Use Prayer",
        description = "Uses prayer protection melee when in combat only, + drinks prayer potion."
    )
    default boolean usePrayer() {
        return false;
    }

    @ConfigItem(
        keyName = "useFood",
        name = "Use Food",
        description = "Use food when needed"
    )
    default boolean useFood() {
        return false;
    }

    @ConfigItem(
        keyName = "foodType",
        name = "Food Type",
        description = "Select the type of food to use"
    )
    default String foodType() {
        return "Shark";
    }

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
