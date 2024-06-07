package net.runelite.client.plugins.microbot.blackjack.enums;

import lombok.AllArgsConstructor;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

@AllArgsConstructor
public enum Thugs {
    MENAPHITE("Menaphite Thug", new WorldPoint(3344, 2955, 0),
            new WorldArea(3342, 2954, 2, 2, 0),
            new WorldPoint(3345, 2955, 0));

    public final String displayName;
    //public final List<String> names;// we don't have a contain method atm, so just use a list
    public final WorldPoint location;
    public final WorldArea thugArea;
    public final WorldPoint door;
    @Override
    public String toString() {
        return super.toString();
    }

}
