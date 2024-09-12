package net.runelite.client.plugins.microbot.util.antiban;

import java.util.Random;

public class MouseFatigue {


    private final Random random = new Random();
    public double increaseRate = 0.005;
    public double noiseAmplitude = 5.0;

    // Method to calculate the base time with noise
    public int calculateBaseTimeWithNoise(int initialBaseTimeMs, int maxBaseTimeMs) {
        double noise = random.nextGaussian() * noiseAmplitude;
        int newBaseTimeMs = (int) (initialBaseTimeMs + AntibanPlugin.ticksSinceLogin * increaseRate + noise);
        return Math.min(newBaseTimeMs, maxBaseTimeMs);
    }


}
