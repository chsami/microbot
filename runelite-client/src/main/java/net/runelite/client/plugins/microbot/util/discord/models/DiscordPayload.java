package net.runelite.client.plugins.microbot.util.discord.models;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class DiscordPayload {
    private final String content;
    private final List<DiscordEmbed> embeds;

    public DiscordPayload(String content) {
        this.content = content;
        this.embeds = Collections.emptyList();
    }

    public DiscordPayload(String content, List<DiscordEmbed> embeds) {
        this.content = content;
        this.embeds = embeds;
    }
}
