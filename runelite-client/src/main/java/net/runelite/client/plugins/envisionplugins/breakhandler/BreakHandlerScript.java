package net.runelite.client.plugins.envisionplugins.breakhandler;

import net.runelite.client.plugins.envisionplugins.breakhandler.enums.BreakHandlerStates;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.math.Random;

import net.runelite.client.plugins.envisionplugins.breakhandler.ui.runtimer.RunTimerPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.breaktimer.BreakTimerPanel;

import javax.swing.*;
import java.util.concurrent.TimeUnit;

public class BreakHandlerScript extends Script {

    public static double version = 0.6;

    /* Variables for other script's references */
    // TODO set this to false for production
    private static boolean isBreakHandlerCompatible = true;     // Use setter method in your Plugin's StartUp Method
    private static boolean letBreakHandlerStartBreak = false;   // Use setter method in your Plugin's StartUp Method
    private static boolean isBreakOver = false;                 // Use this variable to progress have the break has completed
    /* End variables for other script's references */

    protected static boolean shouldBreakTimerBeEnabled = false;
    protected static boolean shouldRunTimeTimerBeEnabled = true;


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

    protected static BreakHandlerStates myState;

    protected static String breakMethod;

    public boolean run(BreakHandlerConfig config) {
        runTimeTimer = new Timer("Run Time Timer", expectedRunTimeDuration);
        breakTimer = new Timer("Break Timer", expectedBreakDuration);

        if (isBreakHandlerCompatible) {
            mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
                switch (myState) {
                    case RUN:
                        runTimeTimer.run();
                        SwingUtilities.invokeLater(() -> RunTimerPanel.setDurationTextField(runTimeTimer.getDisplayTime()));
                        break;

                    case START_BREAK:
                        isBreakOver = false;

                        if (breakMethod.equals("AFK")) {
                            myState = BreakHandlerStates.AFK_BREAK;
                        } else if (breakMethod.equals("LOGOUT")) {
                            myState = BreakHandlerStates.LOGOUT_BREAK;
                        } else {
                            System.err.println("Bad break method...");
                        }
                        break;

                    case AFK_BREAK:
                        breakTimer.run();
                        SwingUtilities.invokeLater(() -> BreakTimerPanel.setDurationTextField(breakTimer.getDisplayTime()));
                        break;

                    case LOGOUT_BREAK:
                        // TODO: Fully implement
                        System.out.println("STATE: " + myState);

                        breakTimer.run();
                        SwingUtilities.invokeLater(() -> BreakTimerPanel.setDurationTextField(breakTimer.getDisplayTime()));
                        break;

                    case RESET_RUN_TIMER:
                        System.out.println("STATE: " + myState);
                        calcExpectedRunTime();
                        break;

                    case RESET_BREAK_TIMER:
                        System.out.println("STATE: " + myState);

                        calcExpectedBreak();
                        break;

                    case RESET_BOTH_TIMERS:
                        isBreakOver = false;

                        regenerateExpectedRunTime();
                        runTimeTimer.setDuration(expectedRunTimeDuration);
                        SwingUtilities.invokeLater(() -> RunTimerPanel.setDurationTextField(runTimeTimer.getDisplayTime()));

                        regenerateExpectedBreakTime();
                        breakTimer.setDuration(expectedBreakDuration);
                        SwingUtilities.invokeLater(() -> BreakTimerPanel.setDurationTextField(breakTimer.getDisplayTime()));

                        myState = BreakHandlerStates.RUN;
                        break;

                    case STARTUP:
                        // TODO: Temporary until I put in place working with external scripts

                        System.out.println("STARTUP: Wait");
                        sleep(10000);
                        System.out.println("STARTUP: Post Wait -> change state to run");
                        myState = BreakHandlerStates.RUN;
                        break;

                    case POST_BREAK_AFK:
                        isBreakOver = true;
                        break;

                    case POST_BREAK_LOGIN:
                        // TODO: Fully implement

                        System.out.println("STATE: " + myState);

                        isBreakOver = true;
                        break;

                    default:
                        System.err.println("Bad Break Handler State...");
                }


                // We should be on break
                if (myState == BreakHandlerStates.RUN && (runTimeTimer.getDisplayTime() == 0 && breakTimer.getDisplayTime() > 0)) {
                    myState = BreakHandlerStates.START_BREAK;
                }

                // We just finished a afk break - lets move to post_afk_break
                if (myState == BreakHandlerStates.AFK_BREAK && !isBreakOver && (breakTimer.getDisplayTime() == 0 && runTimeTimer.getDisplayTime() == 0)) {
                    myState = BreakHandlerStates.POST_BREAK_AFK;
                }

                // We are both POST break and run, lets regenerate new timers
                if (myState == BreakHandlerStates.POST_BREAK_AFK && isBreakOver && (breakTimer.getDisplayTime() == 0 && runTimeTimer.getDisplayTime() == 0)) {
                    myState = BreakHandlerStates.RESET_BOTH_TIMERS;
                }


            }, 200, 200, TimeUnit.MILLISECONDS);    //TODO: Is this delay good?
        } else {
            //TODO: Ask Mocrosoft if there is a good way to disable the handler completely if this case hits
            System.err.println("Current active script is not compatible with Micro Break Handler...");
        }

        return true;
    }

    public static void setBreakHandlerState(BreakHandlerStates state) {
        myState = state;
    }

    public boolean shouldBreak() {
        return shouldBreakTimerBeEnabled;
    }

    public static void setShouldBreak(boolean shouldBreak) {
        shouldBreakTimerBeEnabled = shouldBreak;
    }

    public static boolean isShouldRunTimeTimerBeEnabled() {
        return shouldRunTimeTimerBeEnabled;
    }

    public static void setShouldEnableRunTimeTimer(boolean enableRunTimeTimer) {
        shouldRunTimeTimerBeEnabled = enableRunTimeTimer;
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

    private void regenerateExpectedRunTime() {
        expectedRunTimeDuration = Random.random((int) minRunTimeDuration, (int) maxRunTimeDuration);
    }

    public static void calcExpectedBreak() {
        expectedBreakDuration = Random.random((int) minBreakDuration, (int) maxBreakDuration);

        SwingUtilities.invokeLater(() -> BreakTimerPanel.setDurationTextField(expectedBreakDuration));
    }

    private void regenerateExpectedBreakTime() {
        expectedBreakDuration = Random.random((int) minBreakDuration, (int) maxBreakDuration);
    }

    public static void setBreakMethod(String method) {
        breakMethod = method;
    }

    public static void setIsBreakHandlerCompatible(boolean compatible) {
        isBreakHandlerCompatible = compatible;
    }

    public static boolean getIsBreakHandlerCompatible() {
        return isBreakHandlerCompatible;
    }

    public static void setLetBreakHandlerStartBreak(boolean startBreak) {
        letBreakHandlerStartBreak = startBreak;
    }

    public static boolean getLetBreakHandlerStartBreak() {
        return letBreakHandlerStartBreak;
    }

    public static boolean getIsBreakOver() {
        return isBreakOver;
    }
}
