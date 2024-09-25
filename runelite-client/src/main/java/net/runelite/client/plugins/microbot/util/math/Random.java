package net.runelite.client.plugins.microbot.util.math;

import java.util.concurrent.ThreadLocalRandom;

public class Random {
    public static int random(final int min, final int max) {
        final int n = Math.abs(max - min);
        return Math.min(min, max) + (n == 0 ? 0 : new java.util.Random().nextInt(n));
    }
    public static double randomDouble(final int min, final int max) {
        return  min + (max - min) * new java.util.Random().nextDouble();
    }

    public static double clamp(double val) {
        return Math.max(1.0, Math.min(13000.0, val));
    }

    public static long randomDelay() {
        return (long)clamp(Math.round(new java.util.Random().nextGaussian() * 8000.0));
    }

    /**
     * A Gaussian distribution (bell curve) generates values clustered around the mean but with random variations.
     * This approach mimics more natural randomness than a uniform random value.
     * @param mean
     * @param stddev
     * @return
     */
    public static int randomGaussian(double mean, double stddev) {
        double u, v, s;
        do {
            u = 2.0 * ThreadLocalRandom.current().nextDouble() - 1.0;
            v = 2.0 * ThreadLocalRandom.current().nextDouble() - 1.0;
            s = u * u + v * v;
        } while (s >= 1 || s == 0);
        double multiplier = Math.sqrt(-2.0 * Math.log(s) / s);
        return (int) (mean + stddev * u * multiplier);
    }

    public static int randomWithNoise(int base, int noise) {
        return base + ThreadLocalRandom.current().nextInt(-noise / 2, noise / 2 + 1);
    }

    public static int randomExponential(int base, double factor) {
        return (int) (base * Math.pow(2, ThreadLocalRandom.current().nextDouble() * factor));
    }
}