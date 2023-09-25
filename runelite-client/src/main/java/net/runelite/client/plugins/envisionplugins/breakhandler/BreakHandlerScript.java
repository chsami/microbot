package net.runelite.client.plugins.envisionplugins.breakhandler;

import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.math.Random;

import net.runelite.client.plugins.envisionplugins.breakhandler.ui.runtimer.RunTimerPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.breaktimer.BreakTimerPanel;

import javax.swing.*;
import java.util.concurrent.TimeUnit;

public class BreakHandlerScript extends Script {

    public static double version = 0.5;

    protected boolean shouldBreakTimerBeEnabled = false;
    protected boolean shouldRunTimeTimerBeEnabled = true;


    /* Run Time Duration Variables */
    protected static long minRunTimeDuration = -1;
    protected static long maxRunTimeDuration = -1;
    protected static long expectedRunTimeDuration = -1;

    /* Break Duration Variables */
    protected static long minBreakDuration = -1;
    protected static long maxBreakDuration = -1;
    protected static long expectedBreakDuration = -1;

    /* Timers */
    protected Timer runTimeTimer;
    protected Timer breakTimer;

    public boolean run(BreakHandlerConfig config) {
        runTimeTimer = new Timer("Run Time Timer", expectedRunTimeDuration);
        breakTimer = new Timer("Break Timer", expectedBreakDuration);

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (shouldRunTimeTimerBeEnabled) {
                runTimeTimer.run();
                SwingUtilities.invokeLater(() -> RunTimerPanel.setDurationTextField(runTimeTimer.getDisplayTime()));
            }

            if (shouldBreakTimerBeEnabled) {
                breakTimer.run();
                SwingUtilities.invokeLater(() -> BreakTimerPanel.setDurationTextField(breakTimer.getDisplayTime()));
            }
        }, 200, 200, TimeUnit.MILLISECONDS);

        return true;
    }

    public boolean shouldBreak() {
        return shouldBreakTimerBeEnabled;
    }

    public void setShouldBreak(boolean shouldBreak) {
        this.shouldBreakTimerBeEnabled = shouldBreak;
    }

    /* Run Time Duration Getters and Setters */
    public static void setMinRunTimeDuration(long minDuration) {
        minRunTimeDuration = minDuration;

        if (minRunTimeDuration != -1 && maxRunTimeDuration != -1) {
            calcExpectedRunTime();
        }
    }

    public static long getMinRunTimeDuration() { return minRunTimeDuration; }

    public static void setMaxRunTimeDuration(long maxDuration) {
        maxRunTimeDuration = maxDuration;

        if (minRunTimeDuration != -1 && maxRunTimeDuration != -1) {
            calcExpectedRunTime();
        }
    }

    public static long getMaxRunTimeDuration() { return maxRunTimeDuration; }


    /* Break Duration Getters and Setters */
    public static void setMinBreakDuration(long minDuration) {
        minBreakDuration = minDuration;

        if (minBreakDuration != -1 && maxBreakDuration != -1) {
            calcExpectedBreak();
        }
    }

    public static long getMinBreakDuration() { return minBreakDuration; }

    public static void setMaxBreakDuration(long maxDuration) {
        maxBreakDuration = maxDuration;

        if (minBreakDuration != -1 && maxBreakDuration != -1) {
            calcExpectedBreak();
        }
    }

    public static long getMaxBreakDuration() { return maxBreakDuration; }

    public static void calcExpectedRunTime() {
        expectedRunTimeDuration = Random.random((int) minRunTimeDuration, (int) maxRunTimeDuration);

        SwingUtilities.invokeLater(() -> RunTimerPanel.setDurationTextField(expectedRunTimeDuration));
    }

    public static void calcExpectedBreak() {
        expectedBreakDuration = Random.random((int) minBreakDuration, (int) maxBreakDuration);

        SwingUtilities.invokeLater(() -> BreakTimerPanel.setDurationTextField(expectedBreakDuration));
    }
}
