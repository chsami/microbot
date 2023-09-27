package net.runelite.client.plugins.envisionplugins.breakhandler;

import net.runelite.client.plugins.envisionplugins.breakhandler.enums.BreakHandlerStates;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.currenttimes.CurrentTimesBreakPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.currenttimes.CurrentTimesRunPanel;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.math.Random;


import javax.swing.*;
import java.util.concurrent.TimeUnit;

public class BreakHandlerScript extends Script {

    public static double version = 0.8;

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

    protected int debugCount = 0;

    public boolean run(BreakHandlerConfig config) {
        runTimeTimer = new Timer("Run Time Timer", expectedRunTimeDuration);
        breakTimer = new Timer("Break Timer", expectedBreakDuration);

        if (isBreakHandlerCompatible) {
            mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
                switch (myState) {
                    case RUN:
                        if (config.VERBOSE_LOGGING() && debugCount == 0) {
                            System.out.println("STATE: " + myState);
                            debugCount++;
                        }

                        runTimeTimer.run();
                        SwingUtilities.invokeLater(() -> CurrentTimesRunPanel.setDurationTextField(runTimeTimer.getDisplayTime()));
                        break;

                    case START_BREAK:
                        if (config.VERBOSE_LOGGING() && debugCount == 0) {
                            System.out.println("STATE: " + myState + " with break style: " + breakMethod);
                            debugCount++;
                        }

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
                        if (config.VERBOSE_LOGGING() && debugCount == 0) {
                            System.out.println("STATE: " + myState);
                            debugCount++;
                        }

                        breakTimer.run();
                        SwingUtilities.invokeLater(() -> CurrentTimesBreakPanel.setDurationTextField(breakTimer.getDisplayTime()));
                        break;

                    case LOGOUT_BREAK:
                        // TODO: Fully implement
                        if (config.VERBOSE_LOGGING() && debugCount == 0) {
                            System.out.println("STATE: " + myState);
                            debugCount++;
                        }

                        breakTimer.run();
                        SwingUtilities.invokeLater(() -> CurrentTimesBreakPanel.setDurationTextField(breakTimer.getDisplayTime()));
                        break;

                    case RESET_RUN_TIMER:
                        if (config.VERBOSE_LOGGING() && debugCount == 0) {
                            System.out.println("STATE: " + myState);
                            debugCount++;
                        }

                        calcExpectedRunTime();
                        break;

                    case RESET_BREAK_TIMER:
                        if (config.VERBOSE_LOGGING() && debugCount == 0) {
                            System.out.println("STATE: " + myState);
                            debugCount++;
                        }

                        calcExpectedBreak();
                        break;

                    case RESET_BOTH_TIMERS:
                        if (config.VERBOSE_LOGGING() && debugCount == 0) {
                            System.out.println("STATE: " + myState);
                            debugCount++;
                        }

                        isBreakOver = false;

                        regenerateExpectedRunTime();
                        runTimeTimer.setDuration(expectedRunTimeDuration);
                        SwingUtilities.invokeLater(() -> CurrentTimesRunPanel.setDurationTextField(runTimeTimer.getDisplayTime()));

                        regenerateExpectedBreakTime();
                        breakTimer.setDuration(expectedBreakDuration);
                        SwingUtilities.invokeLater(() -> CurrentTimesBreakPanel.setDurationTextField(breakTimer.getDisplayTime()));

                        debugCount = 0;
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
                        if (config.VERBOSE_LOGGING() && debugCount == 0) {
                            System.out.println("STATE: " + myState);
                            debugCount++;
                        }

                        isBreakOver = true;
                        break;

                    case POST_BREAK_LOGIN:
                        // TODO: Fully implement
                        if (config.VERBOSE_LOGGING() && debugCount == 0) {
                            System.out.println("STATE: " + myState);
                            debugCount++;
                        }

                        isBreakOver = true;
                        break;

                    default:
                        System.err.println("Bad Break Handler State...");
                }


                // We should be on break
                if (myState == BreakHandlerStates.RUN && (runTimeTimer.getDisplayTime() == 0 && breakTimer.getDisplayTime() > 0)) {
                    myState = BreakHandlerStates.START_BREAK;
                    debugCount = 0;
                }

                // We just finished a afk break - lets move to post_afk_break
                if (myState == BreakHandlerStates.AFK_BREAK && !isBreakOver && (breakTimer.getDisplayTime() == 0 && runTimeTimer.getDisplayTime() == 0)) {
                    myState = BreakHandlerStates.POST_BREAK_AFK;
                    debugCount = 0;
                }

                // We are both POST break and run, lets regenerate new timers
                if (myState == BreakHandlerStates.POST_BREAK_AFK && isBreakOver && (breakTimer.getDisplayTime() == 0 && runTimeTimer.getDisplayTime() == 0)) {
                    myState = BreakHandlerStates.RESET_BOTH_TIMERS;
                    debugCount = 0;
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

        SwingUtilities.invokeLater(() -> CurrentTimesRunPanel.setDurationTextField(expectedRunTimeDuration));
    }

    private void regenerateExpectedRunTime() {
        expectedRunTimeDuration = Random.random((int) minRunTimeDuration, (int) maxRunTimeDuration);
    }

    public static void calcExpectedBreak() {
        expectedBreakDuration = Random.random((int) minBreakDuration, (int) maxBreakDuration);

        SwingUtilities.invokeLater(() -> CurrentTimesBreakPanel.setDurationTextField(expectedBreakDuration));
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
