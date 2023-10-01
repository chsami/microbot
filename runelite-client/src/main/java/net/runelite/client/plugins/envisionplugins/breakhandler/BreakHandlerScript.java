package net.runelite.client.plugins.envisionplugins.breakhandler;

import net.runelite.api.GameState;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.envisionplugins.breakhandler.enums.BreakHandlerStates;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.currenttimes.CurrentTimesBreakPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.currenttimes.CurrentTimesRunPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.util.NotificationManager;
import net.runelite.client.plugins.envisionplugins.breakhandler.util.TimeManager;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.security.Login;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.http.api.worlds.WorldRegion;


import javax.swing.*;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class BreakHandlerScript extends Script {

    public static double version = 0.19;

    /* Variables for other script's references */
    private static boolean isBreakHandlerCompatible = false;                    // Use setter method in your Plugin's Run Method
    public static boolean letBreakHandlerStartBreak = false;                    // Use setter method in your Plugin's Run Method
    private static boolean isParentPluginRunning = false;                       // Use setter method in your Plugin's Run Method

    /* Discord Notification Variable Section */                                 // Use setter methods in your Plugin to update these
    private static String parentPluginName = "Default Plugin";                  // The name of your plugin
    private static boolean detailedReportNotification = false;                  // Send simple discord notification for advanced
    private static String[] skillExperienceGained = {"Default1", "Default2"};   // Each String in the array is outputted with a new line separator
    private static String[] resourcesGained = {"Default1", "Default2"};         // Each String in the array is outputted with a new line separator
    private static String gpGained = "ZERO";                                    // MUST be converted to a String
    /* End variables for other script's references */

    /* Run Time Duration Variables */
    protected static long minRunTimeDuration = -1;
    protected static long maxRunTimeDuration = -1;
    public static TimeManager runTimeManager = new TimeManager();
    public static Optional<TimeManager> breakTimeManager = Optional.of(new TimeManager());

    /* Break Duration Variables */
    protected static long minBreakDuration = -1;
    protected static long maxBreakDuration = -1;

    public static BreakHandlerStates myState;

    protected static String breakMethod;

    /* Post Break Options */
    protected static boolean enableWorldHoppingPostBreak = false;
    protected static boolean useMemberWorldsToHop = false;
    protected static WorldRegion worldRegionToHopTo;

    /* Notification Management */
    protected static NotificationManager notificationManager;

    public static void resetRunTimeManager() {
        runTimeManager = new TimeManager();
    }

    public boolean run(BreakHandlerConfig config, BreakHandlerPanel breakHandlerPanel) {
        notificationManager = new NotificationManager(
                config.DISCORD_WEBHOOK(),
                config.VERBOSE_LOGGING(),
                config.ENABLE_DISCORD_WEBHOOK(),
                1, 1
        );

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (isBreakHandlerCompatible) {
                try {
                    switch (myState) {
                        case STARTUP:
                            SwingUtilities.invokeLater(() -> CurrentTimesRunPanel.setDurationTextFieldIdleMessage("Waiting..."));
                            SwingUtilities.invokeLater(() -> CurrentTimesBreakPanel.setDurationTextFieldIdleMessage("Waiting..."));

                            if (isParentPluginRunning) {
                                notificationManager.logState(myState);

                                // We are not on the login screens
                                if (!getIsAtAccountScreens()) {
                                    myState = BreakHandlerStates.RUN;
                                    calcExpectedRunTime();

                                    SwingUtilities.invokeLater(() -> CurrentTimesRunPanel.setDurationTextField(runTimeManager.getSecondsUntil()));
                                }

                            }

                            break;
                        case RUN:
                            notificationManager.logState(myState);

                            breakTimeManager = Optional.empty();

                            // We are not on the login screens
                            if (!getIsAtAccountScreens()) {
                                SwingUtilities.invokeLater(() -> CurrentTimesRunPanel.setDurationTextField(runTimeManager.getSecondsUntil()));
                                SwingUtilities.invokeLater(() -> CurrentTimesBreakPanel.setDurationTextFieldIdleMessage("Waiting..."));
                            }

                            break;

                        case START_BREAK:
                            notificationManager.logState(myState);

                            SwingUtilities.invokeLater(() -> CurrentTimesRunPanel.setDurationTextFieldIdleMessage("Waiting..."));

                            if (breakTimeManager.isEmpty()) breakTimeManager = Optional.of(new TimeManager());
                            calcExpectedBreak();
                            SwingUtilities.invokeLater(() -> CurrentTimesBreakPanel.setDurationTextField(breakTimeManager.get().getSecondsUntil()));

                            if (breakMethod.equals("AFK")) {
                                myState = BreakHandlerStates.AFK_BREAK;
                            } else if (breakMethod.equals("LOGOUT")) {
                                myState = BreakHandlerStates.LOGOUT_BREAK;
                            } else {
                                System.err.println("Bad break method...");
                            }
                            break;

                        case AFK_BREAK:
                            notificationManager.logState(myState);
                            notificationManager.notifyDiscord(
                                    detailedReportNotification,
                                    config.DISCORD_CLIENT_NAME(),
                                    parentPluginName,
                                    skillExperienceGained,
                                    resourcesGained,
                                    gpGained,
                                    "Starting break via " + breakMethod + "."
                            );

                            long secondsUntil = breakTimeManager.orElseThrow().getSecondsUntil();
                            SwingUtilities.invokeLater(() -> CurrentTimesBreakPanel.setDurationTextField(secondsUntil));
                            Microbot.status = "AFK breaking for " + secondsUntil;
                            break;

                        case LOGOUT_BREAK:
                            notificationManager.logState(myState);

                            notificationManager.notifyDiscord(
                                    detailedReportNotification,
                                    config.DISCORD_CLIENT_NAME(),
                                    parentPluginName,
                                    skillExperienceGained,
                                    resourcesGained,
                                    gpGained,
                                    "Starting break via " + breakMethod + "."
                            );

                            if (Microbot.isLoggedIn()) {
                                logout();
                            }

                            SwingUtilities.invokeLater(() -> CurrentTimesBreakPanel.setDurationTextField(breakTimeManager.orElseThrow().getSecondsUntil()));
                            break;


                        case POST_BREAK_AFK:
                            notificationManager.logState(myState);
                            notificationManager.notifyDiscord(
                                    false,
                                    config.DISCORD_CLIENT_NAME(),
                                    parentPluginName,
                                    skillExperienceGained,
                                    resourcesGained,
                                    gpGained,
                                    "Break Over, resuming plugin."
                            );
                            myState = BreakHandlerStates.RESET_BOTH_TIMERS;
                            break;

                        case POST_BREAK_LOGIN:
                            notificationManager.logState(myState);
                            notificationManager.notifyDiscord(
                                    false,
                                    config.DISCORD_CLIENT_NAME(),
                                    parentPluginName,
                                    skillExperienceGained,
                                    resourcesGained,
                                    gpGained,
                                    "Break Over, resuming plugin."
                            );

                            if (!Microbot.isLoggedIn()) {
                                if (enableWorldHoppingPostBreak) {
                                    new Login(breakHandlerPanel.getUsername().getText(),
                                            breakHandlerPanel.getPasswordEncryptedValue(),
                                            Login.getRandomWorld(useMemberWorldsToHop, worldRegionToHopTo));
                                } else {
                                    new Login(breakHandlerPanel.getUsername().getText(),
                                            breakHandlerPanel.getPasswordEncryptedValue(),
                                            Microbot.getClient().getWorld());
                                }
                            } else {
                                myState = BreakHandlerStates.RESET_BOTH_TIMERS;
                            }

                            break;

                        case RESET_BOTH_TIMERS:
                            notificationManager.logState(myState);

                            regenerateExpectedRunTime(false);
                            SwingUtilities.invokeLater(() -> CurrentTimesRunPanel.setDurationTextField(runTimeManager.getSecondsUntil()));

                            notificationManager.resetVerboseMessageCount();
                            myState = BreakHandlerStates.RUN;
                            break;

                        default:
                            System.err.println("Bad Break Handler State...");
                    }

                    BreakHandlerStates.startupCheck(this);
                    BreakHandlerStates.breakCheck(this);
                    BreakHandlerStates.afkBreakCheck(this);
                    BreakHandlerStates.logoutBreakCheck(this);

                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }

            } else {
                System.err.println("Current active script is not compatible with Micro Break Handler...");
            }

        }, 0, 300, TimeUnit.MILLISECONDS);

        return true;
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public static void setBreakHandlerState(BreakHandlerStates state) {
        myState = state;
    }

    /* Run Time Duration Getters and Setters */
    public static void setMinRunTimeDuration(long minDuration) {
        minRunTimeDuration = minDuration;
    }

    public static void setMaxRunTimeDuration(long maxDuration) {
        maxRunTimeDuration = maxDuration;
    }

    /* Break Duration Getters and Setters */
    public static void setMinBreakDuration(long minDuration) {
        minBreakDuration = minDuration;
    }

    public static void setMaxBreakDuration(long maxDuration) {
        maxBreakDuration = maxDuration;
    }

    public static void calcExpectedRunTime() {
        runTimeManager.calculateTime((int) minRunTimeDuration, (int) maxRunTimeDuration);
    }

    public static void regenerateExpectedRunTime(boolean fromButtonRequest) {
        if (fromButtonRequest && runTimeManager.timeHasPast()) return;
        calcExpectedRunTime();
    }

    public static void calcExpectedBreak() {
        if (breakTimeManager.isEmpty()) return;
        breakTimeManager.orElseThrow().calculateTime((int) minBreakDuration, (int) maxBreakDuration);
    }

    public static void setBreakMethod(String method) {
        breakMethod = method;
    }

    public static void setEnableWorldHoppingPostBreak(boolean enableHopping) {
        enableWorldHoppingPostBreak = enableHopping;
    }

    public static void setUseMemberWorldsToHop(boolean useMemWorld) {
        useMemberWorldsToHop = useMemWorld;
    }

    public static void setWorldRegionToHopTo(WorldRegion region) {
        worldRegionToHopTo = region;
    }

    public static void setIsBreakHandlerCompatible(boolean compatible) {
        isBreakHandlerCompatible = compatible;
    }

    public static void setLetBreakHandlerStartBreak(boolean startBreak) {
        letBreakHandlerStartBreak = startBreak;
    }

    public static boolean getIsBreakOver() {
        return breakTimeManager.orElseThrow().timeHasPast() && runTimeManager.timeHasPast();
    }

    public static void setIsParentPluginRunning(boolean isRunning) {
        isParentPluginRunning = isRunning;
    }

    public static boolean isIsParentPluginRunning() {
        return isParentPluginRunning;
    }

    // Notify the parent script that the Run Time Timer has finished running
    //      , and they can start a break when it is convenient
    public static boolean getHasRunTimeTimerFinished() {
        return runTimeManager.timeHasPast();
    }

    public static void setParentPluginName(String name) {
        parentPluginName = name;
    }

    public static void setDetailedReportNotification(boolean flag) {
        detailedReportNotification = flag;
    }

    public static void setSkillExperienceGained(String[] experienceGained) {
        skillExperienceGained = experienceGained;
    }

    public static void setResourcesGained(String[] resources) {
        resourcesGained = resources;
    }

    public static void setGpGained(String gp) {
        gpGained = gp;
    }

    public boolean getIsAtAccountScreens() {
        boolean atAccountScripts = Rs2Widget.getWidget(WidgetInfo.LOGIN_CLICK_TO_PLAY_SCREEN) != null;

        try {
            if (Microbot.getClient().getGameState() == GameState.LOGIN_SCREEN
                    || Microbot.getClient().getGameState() == GameState.LOGGING_IN) {
                atAccountScripts = true;
            }
        } catch (Exception ignored) {
            // Let's just ignore this, it means the client is still loading
            // Cannot invoke "net.runelite.client.callback.ClientThread.runOnClientThread(java.util.concurrent.Callable)" because the return value of "net.runelite.client.plugins.microbot.Microbot.getClientThread()" is null
        }


        return atAccountScripts;
    }
}
