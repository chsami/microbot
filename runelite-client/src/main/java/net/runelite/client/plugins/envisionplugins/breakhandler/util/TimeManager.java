package net.runelite.client.plugins.envisionplugins.breakhandler.util;

import net.runelite.client.plugins.microbot.util.math.Random;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class TimeManager {
    private Instant time;

    public TimeManager() {
        time = Instant.now();
    }

    public void calculateTime(int min, int max) {
        time = Instant.now()
                .plus(
                        Random.random(min, max),
                        ChronoUnit.SECONDS)
                .plus(
                        Random.random(1, 999),
                        ChronoUnit.MILLIS);

    }

    public long getSeconds() {
        return time.getEpochSecond();
    }

    public long getSecondsUntil() {
        return Instant.now().until(time, ChronoUnit.SECONDS);
    }

    public boolean timeHasPast() {
        return getSecondsUntil() <= 0;
    }
}

