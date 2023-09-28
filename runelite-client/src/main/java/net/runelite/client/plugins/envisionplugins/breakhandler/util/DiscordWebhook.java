package net.runelite.client.plugins.envisionplugins.breakhandler.util;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DiscordWebhook {

    private final String url;
    Date now;

    public DiscordWebhook(String url) {
        this.url = url;
        now = new Date();
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    public void sendClientStatus(String clientMsgValue, String statusMsgValue) {
        DiscordWebhookBuilder discordWebhookBuilder = new DiscordWebhookBuilder(url);
        discordWebhookBuilder.addEmbed(new DiscordWebhookBuilder.EmbedObject()
                .setColor(new Color(88, 185, 225))
                .addField("Client:", clientMsgValue, true)
                .addField("Status:", statusMsgValue, true)
                .setAuthor("Microbot Break Handler", "https://chsami.github.io/microbot/", "https://i.ibb.co/Fm4L8XF/breakhandler-watch.png")
                .setFooter(now.toString(), "https://i.ibb.co/1Q4TNgK/watch.png")
        );
        try {
            discordWebhookBuilder.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void sendClientStatusWithGains(String clientMsgValue, String statusMsgValue, String[] skillExperienceGained, String[] resourcesGained, String gp) {
        DiscordWebhookBuilder discordWebhookBuilder = new DiscordWebhookBuilder(url);

        discordWebhookBuilder.addEmbed(new DiscordWebhookBuilder.EmbedObject()
                .setColor(new Color(88, 185, 225))
                .addField("Client:", clientMsgValue, true)
                .addField("Status:", statusMsgValue, true)
                .addField("Experience:", String.join("\\n", skillExperienceGained), false)
                .addField("Resources:", String.join("\\n", resourcesGained), false)
                .addField("GP:", gp, false)
                .setAuthor("Microbot Break Handler", "https://chsami.github.io/microbot/", "https://i.ibb.co/Fm4L8XF/breakhandler-watch.png")
                .setFooter(now.toString(), "https://i.ibb.co/1Q4TNgK/watch.png")
        );
        try {
            discordWebhookBuilder.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
