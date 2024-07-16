package net.runelite.client.plugins.microbot.crafting.enums;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Glass {
    NONE(" ", "", '0', 0),
    PROGRESSIVE("Progressive Mode","None", '0', 1),
    BEER_GLASS("Beer Glass", "Beer Glass", '1', 1),
    CANDLE_LANTERN("Empty Candle Lantern", "Empty Candle Lantern", '2', 4),
    OIL_LAMP("Empty Oil Lamp", "Empty Oil Lamp", '3', 12),
    VIAL("Vial", "Vial", '4', 33),
    FISHBOWL("Empty Fishbowl", "Empty Fishbowl", '5', 42),
    UNPOWERED_ORB("Unpowered Orb", "Unpowered Orb", '6', 46),
    LANTERN_LENS("Lantern Lens", "Lantern Lens", '7', 49),
    LIGHT_ORB("Empty Light Orb", "Empty Light Orb", '8', 87);

    private final String label;
    private final String itemName;
    private final char menuEntry;
    private final int levelRequired;

    @Override
    public String toString()
    {
        return label;
    }
}