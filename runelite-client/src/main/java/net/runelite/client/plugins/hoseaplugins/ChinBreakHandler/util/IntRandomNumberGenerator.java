package net.runelite.client.plugins.ChinBreakHandler.util;

import java.util.PrimitiveIterator;
import java.util.Random;

public final class IntRandomNumberGenerator
{
    private final PrimitiveIterator.OfInt randomIterator;

    /**
     * Initialize a new random number generator that generates
     * random numbers in the range [min, max]
     *
     * @param min - the min value (inclusive)
     * @param max - the max value (inclusive)
     */
    public IntRandomNumberGenerator(int min, int max)
    {
        randomIterator = new Random().ints(min, max + 1).iterator();
    }

    /**
     * Returns a random number in the range (min, max)
     *
     * @return a random number in the range (min, max)
     */
    public int nextInt()
    {
        return randomIterator.nextInt();
    }
}