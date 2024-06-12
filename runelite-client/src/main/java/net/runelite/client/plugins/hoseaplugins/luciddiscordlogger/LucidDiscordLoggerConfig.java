package net.runelite.client.plugins.hoseaplugins.luciddiscordlogger;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("lucid-discord-logger")
public interface LucidDiscordLoggerConfig extends Config
{
    @ConfigSection(
            name = "General",
            description = "General settings",
            position = 0
    )
    String generalSection = "General";

    @ConfigSection(
            name = "Webhook Overrides",
            description = "Custom override URLs for specific messages",
            position = 1
    )
    String overridesSection = "Webhook Overrides";

    @ConfigSection(
            name = "Chat Logging Toggles",
            description = "Choose which chat types you want to log.",
            position = 2
    )
    String toggleSection = "Clan Chat";


    // General Settings
    @ConfigItem(
            name = "Main Webhook URL:",
            description = "Main Discord Webhook URL to send messages to",
            position = 0,
            keyName = "mainWebhookURL",
            section = generalSection
    )
    default String mainWebhookURL()
    {
        return "";
    }

    @ConfigItem(
            name = "Prepend Timestamp To Messages",
            description = "Prepends current time in [HH:mm] format to chat messages",
            position = 1,
            keyName = "prependTimestamp",
            section = generalSection
    )
    default boolean prependTimestamp()
    {
        return false;
    }

    @ConfigItem(
            name = "12-Hour Format Timestamp",
            description = "Uses 12-Hour Format for timestamp instead of 24-Hour Format",
            position = 2,
            keyName = "use12HourFormat",
            section = generalSection
    )
    default boolean use12HourFormat()
    {
        return false;
    }

    @ConfigItem(
            name = "Include Seconds In Timestamp",
            description = "Includes seconds to the timestamp.",
            position = 3,
            keyName = "includeSeconds",
            section = generalSection
    )
    default boolean includeSeconds()
    {
        return false;
    }

    @ConfigItem(
            name = "Include SPAM Server Msg Type",
            description = "Includes messages with SPAM message type to be included along with server messages"
                    + "<br>This can cause you to hit the rate limit quickly if you are doing spam-intensive activities."
                    + "<br>A 1-hour timeout from excessive API usage will occur if you hit the limit frequently enough."
                    + "<br>Although there are built-in protections against this, there is no guarantee that you won't be limited,"
                    + "<br>as this is entirely within Discord's control.",
            position = 4,
            keyName = "includeSpam",
            section = generalSection
    )
    default boolean includeSpam()
    {
        return false;
    }

    // Webhook Overrides

    @ConfigItem(
            name = "Clan Chat Override",
            description = "Overrides the main webhook URL to use this URL for Clan Chats",
            position = 0,
            keyName = "clanChatOverrideURL",
            section = overridesSection
    )
    default String clanChatOverrideURL()
    {
        return "";
    }
    @ConfigItem(
            name = "Clan Message Override",
            description = "Overrides the main webhook URL to use this URL for Clan Messages",
            position = 1,
            keyName = "clanServerOverrideURL",
            section = overridesSection
    )
    default String clanServerOverrideURL()
    {
        return "";
    }

    @ConfigItem(
            name = "Group Chat Override",
            description = "Overrides the main webhook URL to use this URL for Group Chats",
            position = 2,
            keyName = "groupChatOverrideURL",
            section = overridesSection
    )
    default String groupChatOverrideURL()
    {
        return "";
    }

    @ConfigItem(
            name = "Group Message Override",
            description = "Overrides the main webhook URL to use this URL for Group Messages",
            position = 3,
            keyName = "groupServerOverrideURL",
            section = overridesSection
    )
    default String groupServerOverrideURL()
    {
        return "";
    }

    @ConfigItem(
            name = "Private Chat Override",
            description = "Overrides the main webhook URL for Private Chat",
            position = 4,
            keyName = "privateChatOverrideURL",
            section = overridesSection
    )
    default String privateChatOverrideURL()
    {
        return "";
    }

    @ConfigItem(
            name = "Public Chat Override",
            description = "Overrides the main webhook URL for Public Chat",
            position = 5,
            keyName = "publicChatOverrideURL",
            section = overridesSection
    )
    default String publicChatOverrideURL()
    {
        return "";
    }

    @ConfigItem(
            name = "Game Message Override",
            description = "Overrides the main webhook URL for Game Messages",
            position = 6,
            keyName = "serverMessageOverrideURL",
            section = overridesSection
    )
    default String serverMessageOverrideURL()
    {
        return "";
    }

    // Logging Toggles
    @ConfigItem(
            name = "Send Clan Chat",
            description = "Sends all clan chat messages (excludes server messages)",
            position = 0,
            keyName = "sendClanChat",
            section = toggleSection
    )
    default boolean sendClanChat()
    {
        return false;
    }

    @ConfigItem(
            name = "Send Clan Messages",
            description = "Sends all clan server messages (excludes player messages)",
            position = 1,
            keyName = "sendClanMessage",
            section = toggleSection
    )
    default boolean sendClanMessage()
    {
        return false;
    }

    @ConfigItem(
            name = "Send Group Chat",
            description = "Sends all group chat messages (excludes server messages)",
            position = 2,
            keyName = "sendGroupChat",
            section = toggleSection
    )
    default boolean sendGroupChat()
    {
        return false;
    }

    @ConfigItem(
            name = "Send Group Messages",
            description = "Sends all group server messages (excludes player messages)",
            position = 3,
            keyName = "sendGroupMessage",
            section = toggleSection
    )
    default boolean sendGroupMessage()
    {
        return false;
    }

    @ConfigItem(
            name = "Send Private Chat",
            description = "Sends all private chat messages (includes yourself)",
            position = 4,
            keyName = "sendPrivateChat",
            section = toggleSection
    )
    default boolean sendPrivateChat()
    {
        return false;
    }

    @ConfigItem(
            name = "Send Public Chat",
            description = "Sends all public chat messages"
            + "<br>This can cause you to hit the rate limit quickly in busy areas."
            + "<br>A 1-hour timeout from excessive API usage will occur if you hit the limit frequently enough."
            + "<br>Although there are built-in protections against this, there is no guarantee that you won't be limited,"
            + "<br>as this is entirely within Discord's control.",
            position = 5,
            keyName = "sendPublicChat",
            section = toggleSection
    )
    default boolean sendPublicChat()
    {
        return false;
    }

    @ConfigItem(
            name = "Send Game Messages",
            description = "Sends all game server messages to main URL"
                    + "<br>or the override URL if it's not empty.",
            position = 6,
            keyName = "sendGameMessages",
            section = toggleSection
    )
    default boolean sendGameMessages()
    {
        return false;
    }
}
