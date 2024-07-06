package net.runelite.client.plugins.microbot.magetrainingarena;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

@Getter
@AllArgsConstructor
public enum Room {
    TELEKINETIC("Telekinetic", 23673, null, null, ItemID.LAW_RUNE),
    ALCHEMIST("Alchemist", 23675, new WorldArea(3345, 9616, 38, 38, 2), new WorldPoint(3364, 9623, 2), ItemID.NATURE_RUNE),
    ENCHANTMENT("Enchantment", 23674, new WorldArea(3339, 9617, 50, 46, 0), new WorldPoint(3363, 9640, 0), ItemID.COSMIC_RUNE),
    GRAVEYARD("Graveyard", 23676, new WorldArea(3336, 9614, 54, 51, 1), new WorldPoint(3363, 9640, 1), ItemID.NATURE_RUNE);

    private final String name;
    private final int teleporter;
    private final WorldArea area;
    private final WorldPoint exit;
    private final int runesId;
}
