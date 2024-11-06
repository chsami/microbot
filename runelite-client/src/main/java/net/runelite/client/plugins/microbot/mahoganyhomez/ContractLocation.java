package net.runelite.client.plugins.microbot.mahoganyhomez;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@AllArgsConstructor
@Getter
public enum ContractLocation {
    MAHOGANY_HOMES_ARDOUGNE("Mahogany Homes", new WorldPoint(2634, 3295, 0)),
    MAHOGANY_HOMES_FALADOR("Mahogany Homes", new WorldPoint(2989, 3363, 0)),
    MAHOGANY_HOMES_HOSIDIUS("Mahogany Homes", new WorldPoint(1780, 3623, 0)),
    MAHOGANY_HOMES_VARROCK("Mahogany Homes", new WorldPoint(3240, 3471, 0));

    private final String name;
    private final WorldPoint location;
}
