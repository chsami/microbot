package net.runelite.client.plugins.hoseaplugins.api.utils;

import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.client.RuneLite;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;

import java.awt.*;

public class MessageUtils
{
    static Client client = RuneLite.getInjector().getInstance(Client.class);
    static final ChatMessageManager chatMessageManager = RuneLite.getInjector().getInstance(ChatMessageManager.class);
    public static void addMessage(String message, Color color)
    {
        final String chatMessage = new ChatMessageBuilder()
                .append(color, message)
                .build();

        chatMessageManager.queue(
                QueuedMessage.builder()
                        .type(ChatMessageType.ENGINE)
                        .runeLiteFormattedMessage(chatMessage)
                        .build());
    }
}
