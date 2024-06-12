package net.runelite.client.plugins.hoseaplugins.luciddiscordlogger;

import com.google.common.base.Strings;
import com.google.inject.Provides;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;
import okhttp3.*;
import javax.inject.Inject;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.logging.Logger;

import static net.runelite.http.api.RuneLiteAPI.GSON;

@PluginDescriptor(
        name = PluginDescriptor.Lucid + "Discord Logger</html>",
        description = "A plugin that sends various messages to a specified Discord webhook URL",
        enabledByDefault = false,
        tags = {"discord", "webhook", "chat"}
)
public class LucidDiscordLoggerPlugin extends Plugin
{
    @Inject
    private OkHttpClient okHttpClient;

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private LucidDiscordLoggerConfig config;

    private Logger log = Logger.getLogger(getName());

    private long nextMessageAttempt = -1;

    private int remainingMessages = 4;

    private final String SPECIAL_CHAR_REGEX = "<.*?>";

    private final String ALPHANUMERIC_REGEX = "[^0-9a-zA-Z ]+";

    @Provides
    LucidDiscordLoggerConfig getConfig(final ConfigManager configManager)
    {
        return configManager.getConfig(LucidDiscordLoggerConfig.class);
    }

    @Override
    protected void startUp()
    {
        log.info(getName() + " Started");
    }

    @Override
    protected void shutDown()
    {
        log.info(getName() + " Stopped");
    }

    @Subscribe
    public void onChatMessage(final ChatMessage chatMessage)
    {
        String clanName = "";
        String sender = "";
        String outputMessage = Text.removeTags(chatMessage.getMessage());

        if (!Strings.isNullOrEmpty(chatMessage.getName()))
        {
            sender = chatMessage.getName().replaceAll(SPECIAL_CHAR_REGEX, "").replaceAll(ALPHANUMERIC_REGEX, " ");
            sender = Text.removeTags(sender);
        }
        else
        {
            sender = "SERVER";
        }

        if (!Strings.isNullOrEmpty(chatMessage.getSender()))
        {
            clanName = chatMessage.getSender().replaceAll(SPECIAL_CHAR_REGEX, "").replaceAll(ALPHANUMERIC_REGEX, " ");
        }

        processMessage(clanName, sender, outputMessage, chatMessage.getType());
    }

    private void processMessage(String clanName, String sender, String message, ChatMessageType messageType)
    {
        if (!canSendMessage(messageType))
        {
            return;
        }

        if (messageType == ChatMessageType.CLAN_MESSAGE &&  message.contains("To talk in your"))
        {
            return;
        }

        StringBuilder messageBuilder = new StringBuilder();

        if (config.prependTimestamp())
        {
            String currentTimestamp = getTimestamp();
            messageBuilder.append(currentTimestamp).append(" ");
        }

        if (!Strings.isNullOrEmpty(clanName))
        {
            messageBuilder.append("**[").append(clanName).append("]** ");
        }

        if (!Strings.isNullOrEmpty(sender))
        {
            messageBuilder.append("**").append(sender).append("**: ");
        }

        if (!Strings.isNullOrEmpty(message))
        {
            messageBuilder.append(message);
        }

        WebhookBody webhookBody = new WebhookBody();
        webhookBody.setContent(messageBuilder.toString());

        String webhookURL = getWebhookURL(messageType);

        if (Strings.isNullOrEmpty(webhookURL))
        {
            return;
        }

        sendWebhook(webhookURL, webhookBody);
    }

    private boolean canSendMessage(ChatMessageType chatMessageType)
    {
        switch (chatMessageType)
        {
            case CLAN_CHAT:
                return config.sendClanChat();
            case CLAN_MESSAGE:
                return config.sendClanMessage();
            case CLAN_GIM_CHAT:
                return config.sendGroupChat();
            case CLAN_GIM_MESSAGE:
                return config.sendGroupMessage();
            case PRIVATECHAT:
            case PRIVATECHATOUT:
                return config.sendPrivateChat();
            case PUBLICCHAT:
                return config.sendPublicChat();
            case GAMEMESSAGE:
                return config.sendGameMessages();
            case SPAM:
                return config.sendGameMessages() && config.includeSpam();
        }

        return false;
    }

    private String getWebhookURL(ChatMessageType chatMessageType)
    {
        String webhookURL = "";

        switch (chatMessageType)
        {
            case CLAN_CHAT:
                webhookURL = config.clanChatOverrideURL();
                break;
            case CLAN_MESSAGE:
                webhookURL = config.clanServerOverrideURL();
                break;
            case CLAN_GIM_CHAT:
                webhookURL = config.groupChatOverrideURL();
                break;
            case CLAN_GIM_MESSAGE:
                webhookURL = config.groupServerOverrideURL();
                break;
            case PRIVATECHAT:
            case PRIVATECHATOUT:
                webhookURL = config.privateChatOverrideURL();
                break;
            case PUBLICCHAT:
                webhookURL = config.publicChatOverrideURL();
                break;
            case SPAM:
            case GAMEMESSAGE:
                webhookURL = config.serverMessageOverrideURL();
                break;
        }

        if (!Strings.isNullOrEmpty(webhookURL))
        {
            return webhookURL;
        }

        return config.mainWebhookURL();
    }

    private void sendWebhook(String webhookURL, WebhookBody webhookBody)
    {
        if (nextMessageAttempt > System.currentTimeMillis() || Strings.isNullOrEmpty(webhookURL))
        {
            return;
        }

        if (remainingMessages == 1)
        {
            nextMessageAttempt = System.currentTimeMillis() + 1_000;
        }

        HttpUrl url = HttpUrl.parse(webhookURL);
        MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("payload_json", GSON.toJson(webhookBody));
        buildRequestAndSend(url, requestBodyBuilder);
    }

    private void buildRequestAndSend(HttpUrl url, MultipartBody.Builder requestBodyBuilder)
    {
        RequestBody requestBody = requestBodyBuilder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        sendRequest(request);
    }

    private void sendRequest(Request request)
    {
        okHttpClient.newCall(request).enqueue(new Callback()
        {

            @Override
            public void onFailure(Call call, IOException e)
            {
                log.info(e.getMessage());
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                assert response.body() != null;

                if (response.body().string().contains("rate limited"))
                {
                    float retry = Float.parseFloat(Objects.requireNonNull(response.header("retry_after")));
                    nextMessageAttempt = System.currentTimeMillis() + (long) (retry * 1000);
                }
                else
                {
                    remainingMessages = Integer.parseInt(Objects.requireNonNull(response.header("X-RateLimit-Remaining")));
                }

                response.body().close();
            }
        });
    }

    private String getTimestamp()
    {
        final Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);

        final int hour = config.use12HourFormat() ? 12 + (calendar.get(Calendar.HOUR_OF_DAY) % 12) : calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        final int second = calendar.get(Calendar.SECOND);

        return config.includeSeconds() ? String.format("[%02d:%02d:%02d]", hour, minute, second) : String.format("[%d:%02d]", hour, minute);
    }
}