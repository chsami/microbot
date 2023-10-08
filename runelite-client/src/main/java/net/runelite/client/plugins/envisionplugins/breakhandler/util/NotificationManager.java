package net.runelite.client.plugins.envisionplugins.breakhandler.util;

import net.runelite.client.config.Config;
import net.runelite.client.plugins.envisionplugins.breakhandler.enums.BreakHandlerStates;

public class NotificationManager {
    private final DiscordWebhook discordWebhook;

    private final boolean logToConsole;
    private final boolean sendDiscordNotifications;
    private int messageLimit;
    private int discordNotificationLimit;
    private final String clientName;

    public NotificationManager(String discordWebhookEndpoint, boolean verboseLogging, boolean discordNotifications, int logMessageLimit, int discordMessageLimit, String clientName) {
        discordWebhook = new DiscordWebhook(discordWebhookEndpoint);

        logToConsole = verboseLogging;
        sendDiscordNotifications = discordNotifications;

        messageLimit = logMessageLimit;
        discordNotificationLimit = discordMessageLimit;
        this.clientName = clientName;
    }

    public void log(String message) {
        if (logToConsole && messageLimit == 0) {
            System.out.println(message);
            messageLimit++;
        }
    }

    public void logState(BreakHandlerStates state) {
        if (logToConsole && messageLimit == 0) {
            System.out.println("STATE: " + state);
            messageLimit++;
        }
    }

    public void notifyDiscord(boolean sendDetailedReport, String parentPluginName, String[] skillExperienceGained,
                              String[] resourcesGained, String gpGained, String message) {
        if (sendDiscordNotifications && discordNotificationLimit == 0) {
            discordNotificationLimit++;

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

    public void notifyDiscordSimple(String parentPluginName, String message) {
        if (sendDiscordNotifications && discordNotificationLimit == 0) {
            discordNotificationLimit++;

            discordWebhook.sendClientStatus(clientName, parentPluginName, message);
        }
    }

    public void resetVerboseMessageCount() {
        messageLimit = 0;
    }

    public void resetDiscordNotificationCount() {
        discordNotificationLimit = 0;
    }
}
