package net.runelite.client.plugins.microbot.blackjack.enums;

import lombok.AllArgsConstructor;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

@AllArgsConstructor
public enum Area {
ThugHut(3340, 2956, 3344,2953),
ShopsArea(3355, 2992, 3363,2984);

    public final int ax;
    public final int ay;
    public final int bx;
    public final int by;
@Override
    public String toString() {
        return super.toString();
    }
}
