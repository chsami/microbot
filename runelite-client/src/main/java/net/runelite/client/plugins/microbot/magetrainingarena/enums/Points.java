package net.runelite.client.plugins.microbot.magetrainingarena.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Points {
    TELEKINETIC(553, 10, 198, 6),
    ALCHEMIST(553, 11, 194, 6),
    ENCHANTMENT(553, 12, 195, 6),
    GRAVEYARD(553, 13, 196, 6);

    private int widgetId;
    private int childId;
    private int roomWidgetId;
    private int roomChildId;

    @Override
    public String toString() {
        String name = name();
        return name.charAt(0) + name.substring(1).toLowerCase();
    }
}
