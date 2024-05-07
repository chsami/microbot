package net.runelite.client.plugins.natepainthelper;

import java.util.concurrent.TimeUnit;

public class PaintFormat {
    public static String ft(long duration) {
        long days = TimeUnit.MILLISECONDS.toDays(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60;

        if (days == 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d:%02d:%02d", days, hours, minutes, seconds);
        }
    }
}
