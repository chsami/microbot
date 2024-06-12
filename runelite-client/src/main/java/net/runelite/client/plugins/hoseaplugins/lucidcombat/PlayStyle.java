package net.runelite.client.plugins.hoseaplugins.lucidcombat;

import lombok.Getter;

public enum PlayStyle
{
    ROBOTIC(0, 0), ATTENTIVE(2, 5), NORMAL(4, 7), LAZY(6, 12), STONER(6, 16), AFK(6, 23);

    @Getter
    final int lowestDelay;

    @Getter
    final int highestDelay;

    PlayStyle(int lowestDelay, int highestDelay)
    {
        this.lowestDelay = lowestDelay;
        this.highestDelay = highestDelay;
    }
}