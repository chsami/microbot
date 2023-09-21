package net.runelite.client.plugins.envisionplugins.breakhandler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
abstract class Clock {
    protected String name;

    // last updated time (as seconds since epoch)
    protected long lastUpdate;

    protected boolean active;

    Clock(String name) {
        this.name = name;
        this.lastUpdate = Instant.now().getEpochSecond();
        this.active = false;
    }

    abstract long getDisplayTime();

    abstract void setDuration(long duration);

    abstract boolean start();

    abstract boolean pause();

    abstract void reset();
}
