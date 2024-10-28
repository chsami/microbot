package net.runelite.client.plugins.microbot.util.discord;

import com.google.gson.Gson;
import net.runelite.client.config.ConfigProfile;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.discord.models.DiscordEmbed;
import net.runelite.client.plugins.microbot.util.discord.models.DiscordPayload;
import net.runelite.client.plugins.microbot.util.security.Login;
import okhttp3.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Rs2Discord {

    private static final OkHttpClient httpClient = new OkHttpClient();
    private static final Gson GSON = new Gson();

    /**
     * Sends a message to the configured Webhook URL with optional embeds and files as attachments.
     *
     * @param bodyMessage The message content
     * @param embeds      The list of Discord embeds
     * @param files       The list of file paths to upload as attachments
     * @return boolean
     */
    public static boolean sendWebhookMessage(String bodyMessage, List<DiscordEmbed> embeds, List<String> files) {
        // Retrieve and validate the Discord Webhook URL
        String webHookUrl = Optional.ofNullable(getDiscordWebhookUrl())
                .filter(url -> !url.isEmpty())
                .orElseGet(() -> {
                    Microbot.log("The webhook URL is not configured in the RuneLite profile. Please check the configuration.");
                    return null;
                });

        if (webHookUrl == null) return false;

        // Create the payload
        DiscordPayload payload = new DiscordPayload(bodyMessage, embeds);
        String jsonPayload = GSON.toJson(payload);

        // Build the multipart request body
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("payload_json", jsonPayload);

        // Add files to the request
        files.stream()
                .map(File::new)
                .filter(File::exists)
                .forEach(file -> {
                    try {
                        String mimeType = Optional.ofNullable(Files.probeContentType(file.toPath()))
                                .orElse("application/octet-stream");
                        builder.addFormDataPart(
                                "file",
                                file.getName(),
                                RequestBody.create(MediaType.parse(mimeType), file)
                        );
                    } catch (IOException e) {
                        Microbot.log("Failed to determine MIME type for file: " + file.getPath() + " - " + e.getMessage());
                    }
                });

        RequestBody requestBody = builder.build();

        // Build and execute the request
        Request request = new Request.Builder()
                .url(webHookUrl)
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                Microbot.log("Failed to send Discord notification. Error Code: " + response.code());
            }
            return response.isSuccessful();
        } catch (IOException e) {
            Microbot.log("Error while sending Discord notification: " + e.getMessage());
            return false;
        }
    }

    /**
     * Overloaded method to send a message with optional embeds.
     *
     * @param bodyMessage The message content
     * @param embeds      The list of Discord embeds
     * @return boolean
     */
    public static boolean sendWebhookMessage(String bodyMessage, List<DiscordEmbed> embeds) {
        return sendWebhookMessage(bodyMessage, embeds, Collections.emptyList());
    }

    /**
     * Overloaded method to send a plain message.
     *
     * @param bodyMessage The message content
     * @return boolean
     */
    public static boolean sendWebhookMessage(String bodyMessage) {
        return sendWebhookMessage(bodyMessage, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * Gets the Discord Webhook URL from the active RuneLite profile.
     *
     * @return String
     */
    private static String getDiscordWebhookUrl() {
        return Optional.ofNullable(Login.activeProfile)
                .map(ConfigProfile::getDiscordWebhookUrl)
                .orElse(null);
    }

    /**
     * Converts a hex color code to an integer representation compatible with embed.setColor().
     * The input can be in the format "#RRGGBB" or "RRGGBB".
     *
     * @param hexCode the hex color code as a String, e.g., "#FF5733" or "FF5733"
     * @return an integer representation of the color, e.g., 0xFF5733
     * @throws NumberFormatException if the hexCode is not a valid hex color
     */
    public static int convertHexToInt(String hexCode) {
        if (hexCode.startsWith("#")) {
            hexCode = hexCode.substring(1);
        }

        return Integer.parseInt(hexCode, 16);
    }

    /**
     * Converts a java.awt.Color object to an integer representation compatible with embed.setColor().
     *
     * @param color the Color object to convert
     * @return an integer representation of the color in the format 0xRRGGBB
     */
    public static int convertColorToInt(Color color) {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        // Shift the red, green, and blue components to create the integer color representation
        return (red << 16) | (green << 8) | blue;
    }
}
