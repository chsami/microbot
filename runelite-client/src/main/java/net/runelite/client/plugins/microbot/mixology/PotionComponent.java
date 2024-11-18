package net.runelite.client.plugins.microbot.mixology;


public enum PotionComponent {
    AGA('A', "00e676"),
    LYE('L', "e91e63"),
    MOX('M', "03a9f4");

    private final char character;
    private final String color;

    private PotionComponent(char character, String color) {
        this.character = character;
        this.color = color;
    }

    public char character() {
        return this.character;
    }

    public String color() {
        return this.color;
    }
}

