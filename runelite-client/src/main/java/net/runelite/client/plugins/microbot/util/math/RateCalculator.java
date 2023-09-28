package net.runelite.client.plugins.microbot.util.math;

public class RateCalculator {

    private static long startTime = -1;

    public static int getRatePerHour(int currentValue) {
        if (startTime == -1)
            startTime = System.currentTimeMillis();

         final double MILLIS_TO_HOURS = 1.0 / (1000 * 60 * 60);

        double timeDifferenceInHours = (System.currentTimeMillis() - startTime) * MILLIS_TO_HOURS;

        // Calculate and return the rate
        return (int) (currentValue / timeDifferenceInHours);
    }
}
