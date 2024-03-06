package net.runelite.client.plugins.microbot.dashboard;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("Dashboard")
public interface DashboardConfig extends Config {

    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 0
    )
    default String GUIDE() {
        return "Visit https://microbot-dashboard.vercel.app and generate a token and fill it in here\n" +
                "Make sure to fill in the name, this will be displayed on the website dashboard!";
    }

    @ConfigItem(
            keyName = "Bot Name",
            name = "Bot Name",
            description = "The bot name that will be displayed on the dashboard",
            position = 1
    )
    default String botName()
    {
        return "BOT #1";
    }

    @ConfigItem(
            keyName = "Token",
            name = "Token",
            description = "A secured token that is generatead on https://microbot-dashboard.vercel.app",
            position = 1
    )
    default String token()
    {
        return "";
    }
}
