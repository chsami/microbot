package net.runelite.client.plugins.microbot.util.antiban.enums;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.Microbot;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public enum PlayStyle {
    EXTREME_AGGRESSIVE("Extreme Aggressive", 1, 3, 2, 0.00), // Almost no break between inputs
    AGGRESSIVE("Aggressive", 2, 5, 5, 0.01),                // Very short breaks
    MODERATE("Moderate", 3, 9, 10, 0.08),                    // Short breaks
    BALANCED("Balanced", 7, 15, 14, 0.12),                   // Moderate breaks
    CAREFUL("Careful", 12, 21, 14, 0.17),                    // Longer breaks
    CAUTIOUS("Cautious", 17, 30, 14, 0.23),                  // Very long breaks
    PASSIVE("Passive", 20, 35, 14, 0.4),                    // Minimal actions
    RANDOM("Random", 1, 35, 25, 0.00);                       // Random actions


    @Getter
    private final String name;
    private final int originalPrimaryTickInterval;
    private final int originalSecondaryTickInterval;
    private final int baseDuration; // Base duration for the attention span
    private final double refocusProbability; // Probability of refocusing
    @Getter
    @Setter
    public double frequency;
    @Getter
    @Setter
    public double amplitude;
    @Setter
    public Instant startTime; // Start time for the current playstyle
    @Getter
    private int primaryTickInterval;
    @Getter
    private int secondaryTickInterval;
    private double phase; // Phase angle for the sine function
    private int attentionSpan; // Attention span for the current playstyle

    PlayStyle(String name, int primaryTickInterval, int secondaryTickInterval, int baseDuration, double refocusProbability) {
        this.name = name;
        this.originalPrimaryTickInterval = primaryTickInterval;
        this.originalSecondaryTickInterval = secondaryTickInterval;
        this.primaryTickInterval = primaryTickInterval;
        this.secondaryTickInterval = secondaryTickInterval;
        this.refocusProbability = refocusProbability;
        this.phase = 0.0;
        this.frequency = 0.2;
        this.amplitude = 2.0;
        this.startTime = Instant.now();
        this.baseDuration = baseDuration;
        this.attentionSpan = generateAttentionSpan(baseDuration);
    }

    // Return a random PlayStyle
    public static PlayStyle random() {
        return values()[(int) (Math.random() * values().length)];
    }

    public int getRandomTickInterval() {
        return ThreadLocalRandom.current().nextInt(primaryTickInterval, secondaryTickInterval + 1);
    }

    // Method to evolve the play style over time using a sine function
    public void evolvePlayStyle() {
        phase += frequency;
        primaryTickInterval = adjustInterval(primaryTickInterval, amplitude);
        log.info("Primary tick interval: {}", primaryTickInterval);
        secondaryTickInterval = adjustInterval(secondaryTickInterval, amplitude);
        log.info("Secondary tick interval: {}", secondaryTickInterval);
        Microbot.log("Slightly adjusting playStyle intervals.");
    }

    // Helper method to adjust intervals using the sine of the phase
    private int adjustInterval(int interval, double amplitude) {
        int change = (int) (amplitude * Math.sin(phase));
        interval += change;
        // Ensure intervals remain within logical boundaries
        return Math.max(1, interval);
    }

    // Method to reset the intervals to their original values
    public void resetPlayStyle() {
        this.primaryTickInterval = originalPrimaryTickInterval;
        this.secondaryTickInterval = originalSecondaryTickInterval;
        this.phase = 0.0; // Reset the phase as well
        this.startTime = Instant.now(); // Reset start time
        this.attentionSpan = generateAttentionSpan(this.baseDuration); // Generate new attention span
    }

    // Switch profile to a new play style either one step up or down
    public PlayStyle switchProfile() {
        boolean up = Math.random() < 0.5;
        boolean refocus = Math.random() < refocusProbability;
        if (refocus) {
            PlayStyle newProfile = values()[0];
            Microbot.log("Refocusing, switching to: " + newProfile.getName());
            return newProfile;
        }
        int index = this.ordinal();
        if (up) {
            index++;
        } else {
            index--;
        }

        // Check if index is out of bounds and invert the direction if necessary
        if (index < 0 || index >= values().length - 1) {
            // Invert the direction
            up = !up;
            // Reset index to the current ordinal value
            index = this.ordinal();
            // Update the index again with the inverted direction
            if (up) {
                index = Math.min(values().length - 1, index + 1);
            } else {
                index = Math.max(0, index - 1);
            }
        }

        PlayStyle newProfile = values()[index];
        Microbot.log("Switched profile to: " + newProfile.getName());
        return newProfile;
    }

    // Switch profile based on simulated attention span
    public boolean shouldSwitchProfileBasedOnAttention() {
        Instant currentTime = Instant.now();
        long elapsedTime = java.time.Duration.between(this.startTime, currentTime).getSeconds(); // elapsed time in seconds

        return elapsedTime >= this.attentionSpan;
    }

    // Time left in the current play style
    public String getTimeLeftUntilNextSwitch() {
        Instant currentTime = Instant.now();
        long elapsedTime = Duration.between(this.startTime, currentTime).getSeconds(); // elapsed time in seconds
        long timeLeft = (this.attentionSpan) - elapsedTime;

        if (timeLeft <= 0) {
            return "00:00"; // switch should occur immediately
        }

        long minutes = timeLeft / 60;
        long seconds = timeLeft % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    // Generate attention span using normal and Poisson distributions
    private int generateAttentionSpan(int baseDuration) {
        // Define parameters for normal and Poisson distributions
        double normalMean = baseDuration * 60; // mean of the normal distribution
        double normalStdDev = normalMean * 0.2; // standard deviation of the normal distribution (20% of mean)
        double poissonLambda = normalMean * 0.1; // lambda parameter for the Poisson distribution (10% of mean)

        // Generate random values from normal and Poisson distributions
        Random random = new Random();
        double normalSample = random.nextGaussian() * normalStdDev + normalMean;
        double poissonSample = nextPoisson(poissonLambda);

        // Combine the values to get the attention span
        return (int) Math.max(1, normalSample + poissonSample);
    }

    // Helper method to generate a Poisson-distributed random value
    private int nextPoisson(double lambda) {
        Random random = new Random();
        double l = Math.exp(-lambda);
        int k = 0;
        double p = 1.0;
        do {
            k++;
            p *= random.nextDouble();
        } while (p > l);
        return k - 1;
    }
}
