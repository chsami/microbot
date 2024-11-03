package net.runelite.client.plugins.microbot.util.discord.models;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Discord Embed, which is a rich content format used in Discord messages.
 * This class models the structure of an embed, including fields like title, description,
 * color, author, footer, and more. It also supports adding multiple fields, thumbnails,
 * and images, allowing for detailed and formatted messages.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * DiscordEmbed embed = new DiscordEmbed();
 * embed.setTitle("Example Embed");
 * embed.setDescription("This is a description.");
 * embed.setColor(0xFF5733); // RGB color for the embed
 *
 * DiscordEmbed.Author author = new DiscordEmbed.Author("Author Name", "https://example.com", "https://example.com/icon.png");
 * embed.setAuthor(author);
 *
 * DiscordEmbed.Field field = new DiscordEmbed.Field("Field Name", "Field Value", true);
 * embed.addField(field);
 * }</pre>
 */
@Data
public class DiscordEmbed {
    private String title;
    private String description;
    private String url;
    private int color; // Discord color in RGB
    private Author author;
    private Footer footer;
    private Thumbnail thumbnail;
    private Image image;
    private List<Field> fields = new ArrayList<>();

    // Nested classes for different embed components
    @Data
    public static class Author {
        private String name;
        private String url;
        private String iconUrl;

        public Author(String name, String url, String iconUrl) {
            this.name = name;
            this.url = url;
            this.iconUrl = iconUrl;
        }
    }

    @Data
    public static class Footer {
        private String text;
        private String iconUrl;

        public Footer(String text, String iconUrl) {
            this.text = text;
            this.iconUrl = iconUrl;
        }
    }

    @Data
    public static class Thumbnail {
        private String url;

        public Thumbnail(String url) {
            this.url = url;
        }
    }

    @Data
    public static class Image {
        private String url;

        public Image(String url) {
            this.url = url;
        }
    }

    @Data
    public static class Field {
        private String name;
        private String value;
        private boolean inline;

        public Field(String name, String value, boolean inline) {
            this.name = name;
            this.value = value;
            this.inline = inline;
        }
    }

    // Add a method to add fields to the list
    public void addField(Field field) {
        this.fields.add(field);
    }
}