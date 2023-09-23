package net.runelite.client.plugins.envisionplugins.breakhandler.ui.utility;

import java.time.Duration;
import java.time.format.DateTimeParseException;

public class PanelUtils {

    public static final String INPUT_HMS_REGEX = ".*[hms].*";
    public static final String WHITESPACE_REGEX = "\\s+";

    public static String getFormattedDuration(long duration)
    {
        long hours = duration / (60 * 60);
        long mins = (duration / 60) % 60;
        long seconds = duration % 60;

        return String.format("%02d:%02d:%02d", hours, mins, seconds);
    }

    public static long stringToSeconds(String time) throws NumberFormatException, DateTimeParseException
    {
        long duration = 0;

        if (time.matches(INPUT_HMS_REGEX))
        {
            String textWithoutWhitespaces = time.replaceAll(WHITESPACE_REGEX, "");
            //parse input using ISO-8601 Duration format (e.g. 'PT1h30m10s')
            duration = Duration.parse("PT" + textWithoutWhitespaces).toMillis() / 1000;
        }
        else
        {
            String[] parts = time.split(":");
            // parse from back to front, so as to accept hour:min:sec, min:sec, and sec formats
            for (int i = parts.length - 1, multiplier = 1; i >= 0 && multiplier <= 3600; i--, multiplier *= 60)
            {
                duration += (long) Integer.parseInt(parts[i].trim()) * multiplier;
            }
        }

        return duration;
    }
}
