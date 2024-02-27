package net.runelite.client.plugins.microbot.thieving.summergarden;


import lombok.Getter;
import lombok.Setter;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;

import javax.inject.Singleton;
import java.util.Arrays;

@Singleton
public class ElementalCollisionDetector {
    private static final WorldPoint[] HOMES = {
            new WorldPoint(2907, 5488, 0),
            new WorldPoint(2907, 5490, 0),
            new WorldPoint(2910, 5487, 0),
            new WorldPoint(2912, 5485, 0),
            new WorldPoint(2921, 5486, 0),
            new WorldPoint(2921, 5495, 0),
    };
    private static final int[] CYCLE_LENGTHS = {10, 10, 12, 20, 12, 12};

    @Setter
    private static boolean gateStart;

    private static final int[] tickBasis = {-1, -1, -1, -1, -1, -1};

    @Getter
    public static int ticksUntilStart = -1;

    static final boolean[][] VALID_TICKS_NORMAL_START = {
            {false, false, false, false, false, false, false, true, false, false},
            {false, false, false, true, false, false, false, false, true, false},
            {true, false, false, false, false, true, true, true, false, false, false, false},
            {true, true, true, true, false, false, false, true, true, true, true, false, false, true, false, false, false, false, false, true},
            {false, false, true, false, false, false, false, false, true, false, false, false},
            {true, false, false, false, true, false, true, true, true, true, true, true}
    };
    static final boolean[][] VALID_TICKS_GATE_START = {
            {false, false, false, false, false, false, true, false, false, false},
            {true, true, true, false, false, false, false, true, false, false},
            {false, false, true, true, true, true, false, false, false, false, true, true},
            {true, true, true, false, false, false, true, true, true, true, true, true, false, false, false, false, false, true, true, true},
            {true, false, true, false, false, false, true, false, false, false, false, true},
            {false, false, false, true, false, false, true, true, true, true, true, false}
    };

    /**
     * The second index is the number of ticks since it was last at its home spot.
     * The value in the array is whether you can run past that elemental without getting caught.
     */
    private static boolean[] getValidTicksForElemental(int elementalIndex)
    {
        return gateStart ? VALID_TICKS_GATE_START[elementalIndex] : VALID_TICKS_NORMAL_START[elementalIndex];
    }

    public static boolean isSummerElemental(NPC npc)
    {
        return npc.getId() >= 1801 && npc.getId() <= 1806;
    }

    public static void updatePosition(NPC npc, int tc)
    {
        if (!isSummerElemental(npc))
        {
            return;
        }

        int eId = npc.getId() - 1801;
        if (npc.getWorldLocation().equals(HOMES[eId]))
        {
            tickBasis[eId] = tc;
        }
    }

    public static void updateCountdownTimer(int tc)
    {
        // This is less than 60 * 6 * 20 = 7200 operations, shouldn't lag anyone to run it every game tick.
        ticksUntilStart = moduloPositive(getBestStartPointForLowestTotalParityScore() - tc, 60);
    }

    /**
     * only returns one best point, even if there are multiple with the same parity.
     */
    static int getBestStartPointForLowestTotalParityScore()
    {
        int smallestParity = Integer.MAX_VALUE;
        int smallestParityIndex = -1;
        for (int i = 0; i < 60; i++)
        {
            int paritySum = getParitySum(i);
            if (paritySum < smallestParity)
            {
                smallestParity = paritySum;
                smallestParityIndex = i;
            }
        }
        if (smallestParityIndex == -1)
        {
            throw new IllegalStateException("Every elemental should be passable on at least one tick.");
        }
        return smallestParityIndex;
    }

    static int getParitySum(int startCycle)
    {
        int paritySum = 0;
        for (int elementalIndex = 0; elementalIndex < 6; elementalIndex++)
        {
            paritySum += getParityForStartCycle(startCycle, elementalIndex);
        }
        return paritySum;
    }

    private static int getParityForStartCycle(int startCycle, int elementalIndex)
    {
        if (tickBasis[elementalIndex] == -1)
        {
            return -1;
        }

        int basis = tickBasis[elementalIndex];

        boolean[] validTicksForElemental = getValidTicksForElemental(elementalIndex);
        for (int parity = 0; parity < validTicksForElemental.length; parity++)
        {
            int cycleLength = CYCLE_LENGTHS[elementalIndex];
            int indexWithParity = moduloPositive(startCycle - basis - parity, cycleLength);
            if (validTicksForElemental[indexWithParity])
            {
                return parity;
            }
        }
        throw new IllegalStateException("Every elemental should be passable on at least one tick.");
    }

    public static  int getParity(int elementalId)
    {
        boolean seenAllElementalBasis = !Arrays.stream(tickBasis).filter(i -> i == -1).findAny().isPresent();
        return !seenAllElementalBasis ? -1 : getParityForStartCycle(getBestStartPointForLowestTotalParityScore(), elementalId - 1801);
    }

    public static boolean isLaunchCycle()
    {
        return ticksUntilStart < 8;
    }

    private static int moduloPositive(int base, int mod)
    {
        return ((base % mod) + mod) % mod;
    }
}
