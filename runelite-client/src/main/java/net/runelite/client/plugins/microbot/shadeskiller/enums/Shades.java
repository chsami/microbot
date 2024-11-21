package net.runelite.client.plugins.microbot.shadeskiller.enums;

import lombok.AllArgsConstructor;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public enum Shades {
    FIYR("Fiyr Shades", Arrays.asList("Fiyr Shade", "Fiyr Shadow"), new WorldPoint(3466, 9708, 0),
            new WorldArea(3459, 9700, 8, 83, 0));

    public final String displayName;
    public final List<String> names;// we don't have a contain method atm, so just use a list
    public final WorldPoint location;
    public final WorldArea shadeArea;
    @Override
    public String toString() {
        return super.toString();
    }

}
