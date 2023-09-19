package net.runelite.client.plugins.microbot.crafting.enums;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Glass {
    PROGRESSIVE("Progressive Mode","none", '0', 1),
    BEER_GLASS("Beer glass", "beer glass", '1', 1),
    CANDLE_LANTERN("Empty candle lantern", "empty candle lantern", '2', 4),
    OIL_LAMP("Empty oil lamp", "empty oil lamp", '3', 12),
    VIAL("Vial", "vial", '4', 33),
    FISHBOWL("Empty fishbowl", "empty fishbowl", '5', 42),
    UNPOWERED_ORB("Unpowered orb", "unpowered orb", '6', 46),
    LANTERN_LENS("Lantern lens", "lantern lens", '7', 49),
    LIGHT_ORB("Empty light orb", "empty light orb", '8', 87);

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
