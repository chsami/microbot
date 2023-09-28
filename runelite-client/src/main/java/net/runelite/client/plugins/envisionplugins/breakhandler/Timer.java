package net.runelite.client.plugins.envisionplugins.breakhandler;

import java.time.Instant;

public class Timer extends Clock{
    private long duration;

    private long remaining;

    Timer(String name, long duration) {
        super(name);
        this.duration = duration;
        this.remaining = duration;
    }

    @Override
    long getDisplayTime() {
        if (!active) {
            return remaining;
        }

        return Math.max(0, remaining - (Instant.now().getEpochSecond() - lastUpdate));
    }

    @Override
    public void setDuration(long duration) {
        this.duration = duration;
        reset();
    }

    @Override
    boolean run() {
        if (!active && duration > 0)
        {
            if (remaining <= 0)
            {
                remaining = duration;
            }
            lastUpdate = Instant.now().getEpochSecond();
            active = true;
            return true;
        }

        return false;
    }

    @Override
    boolean pause() {
        if (active)
        {
            active = false;
            remaining = Math.max(0, remaining - (Instant.now().getEpochSecond() - lastUpdate));
            lastUpdate = Instant.now().getEpochSecond();
            return true;
        }

        return false;
    }

    @Override
    void reset() {
        active = false;
        remaining = duration;
        lastUpdate = Instant.now().getEpochSecond();
    }

    public long getRemaining() {
        return remaining;
    }

    public long getDuration() {
        return duration;
    }
}
