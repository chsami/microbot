package net.runelite.client.plugins.hoseaplugins.lucidgauntlet.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.NPC;

@RequiredArgsConstructor
public class Tornado
{
    private static final int TICK_DURATION = 21;

    @Getter
    private int timeLeft = TICK_DURATION;

    @Getter
    private final NPC npc;

    public void updateTimeLeft()
    {
        if (timeLeft >= 0)
        {
            timeLeft--;
        }
    }
}
