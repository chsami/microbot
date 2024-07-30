package net.runelite.client.plugins.microbot.magetrainingarena.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

@Getter
@AllArgsConstructor
public enum Rooms {
    TELEKINETIC("Telekinetic", 23673, null, null, ItemID.LAW_RUNE, Points.TELEKINETIC),
    ALCHEMIST("Alchemist", 23675, new WorldArea(3345, 9616, 38, 38, 2), new WorldPoint(3364, 9623, 2), ItemID.NATURE_RUNE, Points.ALCHEMIST),
    ENCHANTMENT("Enchantment", 23674, new WorldArea(3339, 9617, 50, 46, 0), new WorldPoint(3363, 9640, 0), ItemID.COSMIC_RUNE, Points.ENCHANTMENT),
    GRAVEYARD("Graveyard", 23676, new WorldArea(3336, 9614, 54, 51, 1), new WorldPoint(3363, 9640, 1), ItemID.NATURE_RUNE, Points.GRAVEYARD);

    private final String name;
    private final int teleporter;
    private final WorldArea area;
    private final WorldPoint exit;
    private final int runesId;
    private final Points points;

    @Override
    public String toString() {
        String name = name();
        return name.charAt(0) + name.substring(1).toLowerCase();
    }
}
