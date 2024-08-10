package net.runelite.client.plugins.microbot.util.antiban.enums;

import java.time.Duration;
import java.time.LocalTime;

public enum PlaySchedule {
    SHORT_MORNING(LocalTime.of(8, 0), LocalTime.of(9, 0)),
    MEDIUM_MORNING(LocalTime.of(7, 0), LocalTime.of(10, 0)),
    LONG_MORNING(LocalTime.of(6, 0), LocalTime.of(12, 0)),

    SHORT_AFTERNOON(LocalTime.of(12, 0), LocalTime.of(13, 0)),
    MEDIUM_AFTERNOON(LocalTime.of(12, 0), LocalTime.of(15, 0)),
    LONG_AFTERNOON(LocalTime.of(12, 0), LocalTime.of(18, 0)),

    SHORT_EVENING(LocalTime.of(18, 0), LocalTime.of(19, 0)),
    MEDIUM_EVENING(LocalTime.of(17, 0), LocalTime.of(20, 0)),
    LONG_EVENING(LocalTime.of(17, 0), LocalTime.of(23, 0)),

    SHORT_DAY(LocalTime.of(9, 0), LocalTime.of(17, 0)),
    MEDIUM_DAY(LocalTime.of(8, 0), LocalTime.of(18, 0)),
    LONG_DAY(LocalTime.of(6, 0), LocalTime.of(22, 0));

    private final LocalTime startTime;
    private final LocalTime endTime;

    PlaySchedule(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public boolean isOutsideSchedule() {
        LocalTime currentTime = LocalTime.now();
        return currentTime.isBefore(startTime) || currentTime.isAfter(endTime);
    }

    public Duration timeUntilNextSchedule() {
        LocalTime currentTime = LocalTime.now();
        if (currentTime.isBefore(startTime)) {
            return Duration.between(currentTime, startTime);
        } else if (currentTime.isAfter(endTime)) {
            return Duration.between(currentTime, startTime).plusDays(1);
        } else {
            return Duration.ZERO;
        }
    }

}
