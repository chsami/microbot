package net.runelite.client.plugins.microbot.util.misc;

import java.time.Duration;
import java.time.Instant;

public class TimeUtils {
    /**
     * Get formatted duration between two instants
     *
     * @param start
     * @param finish
     * @return duration as string formatted to d:mm:ss
     */
    public static String getFormattedDurationBetween(Instant start, Instant finish) {
        Duration duration = Duration.between(start, finish);
        return String.format("%d:%02d:%02d",
                duration.toHours(),
                duration.toMinutesPart(),
                duration.toSecondsPart());
    }
}