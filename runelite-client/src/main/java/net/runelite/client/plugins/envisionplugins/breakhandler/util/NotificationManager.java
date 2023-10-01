package net.runelite.client.plugins.envisionplugins.breakhandler.util;

import net.runelite.client.plugins.envisionplugins.breakhandler.enums.BreakHandlerStates;

public class NotificationManager {
    private final DiscordWebhook discordWebhook;

    private final boolean logToConsole;
    private final boolean sendDiscordNotifications;
    private final int messageLimit;
    private int messageCount;
    private final int discordNotificationLimit;
    private int discordNotificationCount;
    private final String clientName;



    public NotificationManager(String discordWebhookEndpoint, boolean verboseLogging, boolean discordNotifications, int logMessageLimit, int discordMessageLimit, String name) {
        discordWebhook = new DiscordWebhook(discordWebhookEndpoint);

        logToConsole = verboseLogging;
        sendDiscordNotifications = discordNotifications;

        messageLimit = logMessageLimit;
        messageCount = 0;
        discordNotificationLimit = discordMessageLimit;
        discordNotificationCount = 0;
        clientName = name;
    }

    public void log(String message) {
        if (logToConsole && messageCount < messageLimit) {
            System.out.println(message);
            messageCount++;
        }
    }

    public void err(String message) {
        if (logToConsole && messageCount < messageLimit) {
            System.err.println(message);
            messageCount++;
        }
    }

    public void logState(BreakHandlerStates state) {
        if (logToConsole && messageCount < messageLimit) {
            System.out.println("STATE: " + state);
            messageCount++;
        }
    }

    public void notifyDiscord(boolean sendDetailedReport, String clientName, String parentPluginName,
                              String[] skillExperienceGained, String[] resourcesGained, String gpGained, String message) {
        if (sendDiscordNotifications && discordNotificationCount < discordNotificationLimit) {
            discordNotificationCount++;

            if (sendDetailedReport) {
                discordWebhook.sendClientStatusWithGains(
                        clientName,
                        parentPluginName,
                        message,
                        skillExperienceGained,
                        resourcesGained,
                        String.valueOf(gpGained)
                );
            } else {
                discordWebhook.sendClientStatus(clientName, parentPluginName, message);
            }
        }
    }

    public void simpleNotifyDiscord(String parentPluginName, String message) {
        if (sendDiscordNotifications && discordNotificationCount < discordNotificationLimit) {
            discordNotificationCount++;

            discordWebhook.sendClientStatus(clientName, parentPluginName, message);
        }
    }

    public void resetVerboseMessageCount() {
        messageCount = 0;
    }

    public void resetDiscordNotificationCount() {
        discordNotificationCount = 0;
    }
}
