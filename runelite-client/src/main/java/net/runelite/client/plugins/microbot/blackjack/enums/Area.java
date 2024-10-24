package net.runelite.client.plugins.microbot.blackjack.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Area {
MenaphiteHut(3340, 2953, 3344,2955),
BeardedBanditHut(3363, 3000, 3364,3002),
ShopsArea(3355, 2984, 3363,2992);

    public final int ax;
    public final int ay;
    public final int bx;
    public final int by;
@Override
    public String toString() {
        return super.toString();
    }
}
