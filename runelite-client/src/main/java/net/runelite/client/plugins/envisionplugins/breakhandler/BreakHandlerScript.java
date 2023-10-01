package net.runelite.client.plugins.envisionplugins.breakhandler;

import net.runelite.api.GameState;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.envisionplugins.breakhandler.enums.BreakHandlerStates;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.currenttimes.CurrentTimesBreakPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.ui.currenttimes.CurrentTimesRunPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.util.DiscordWebhook;
import net.runelite.client.plugins.envisionplugins.breakhandler.util.TimeManager;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.security.Login;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.http.api.worlds.WorldRegion;


import javax.swing.*;
import java.util.concurrent.TimeUnit;

public class BreakHandlerScript extends Script {

    public static double version = 0.16;

    /* Variables for other script's references */
    // TODO set this to false for production
    private static boolean isBreakHandlerCompatible = true;     // Use setter method in your Plugin's Run Method
    public static boolean letBreakHandlerStartBreak = false;   // Use setter method in your Plugin's Run Method
    //    private static boolean isBreakOver = false;                 // Use this variable to progress have the break has completed
    private static boolean isParentPluginRunning = false;       // Use setter method in your Plugin's Run Method
    private static String parentPluginName = "Default Plugin";
    private static boolean detailedReportNotification = false;
    private static String[] skillExperienceGained = {"Default1", "Default2"};
    private static String[] resourcesGained = {"Default1", "Default2"};
    private static String gpGained = "ZERO";
//    private static boolean hasRunTimeTimerFinished = false;
    /* End variables for other script's references */

    /* Discord Stuff */
    private DiscordWebhook discordWebhook;

    protected static boolean shouldBreakTimerBeEnabled = false;
    protected static boolean shouldRunTimeTimerBeEnabled = true;

    /* Run Time Duration Variables */
    protected static long minRunTimeDuration = -1;
    protected static long maxRunTimeDuration = -1;
    //    protected static long expectedRunTimeDuration = -1;
    public static TimeManager runTimeManager = new TimeManager();
    public static TimeManager breakTimeManager = new TimeManager();
//    protected static Instant expectedBreakTimeInstant;

    /* Break Duration Variables */
    protected static long minBreakDuration = -1;
    protected static long maxBreakDuration = -1;
//    protected static long expectedBreakDuration = -1;

    /* Timers */
//    protected static Timer runTimeTimer;
//    protected static Timer breakTimer;

    public static BreakHandlerStates myState;

    protected static String breakMethod;
    protected static boolean enableWorldHoppingPostBreak = false;
    protected static boolean useMemberWorldsToHop = false;
    protected static WorldRegion worldRegionToHopTo;

    public int debugCount = 0;
    public int discordNotificationCount = 0;

    public static void resetRunTimeManager() {
        runTimeManager = new TimeManager();
    }

    public boolean run(BreakHandlerConfig config, BreakHandlerPanel breakHandlerPanel) {
        discordWebhook = new DiscordWebhook(config.DISCORD_WEBHOOK());

        if (isBreakHandlerCompatible) {
            mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
                try {
                    switch (myState) {
                        case RUN:
                            if (config.VERBOSE_LOGGING() && debugCount == 0) {
                                System.out.println("STATE: " + myState);
                                debugCount++;
                            }
                            calcExpectedBreak();
                            if (!getIsAtAccountScreens()) {    // We are not on the login screens
                                SwingUtilities.invokeLater(() -> CurrentTimesRunPanel.setDurationTextField(runTimeManager.getSecondsUntil()));
                                SwingUtilities.invokeLater(() -> CurrentTimesBreakPanel.setDurationTextFieldIdleMessage("Waiting..."));
                            }

                            break;

                        case START_BREAK:
                            if (config.VERBOSE_LOGGING() && debugCount == 0) {
                                System.out.println("STATE: " + myState + " with break style: " + breakMethod);
                                debugCount++;
                            }

                            SwingUtilities.invokeLater(() -> CurrentTimesRunPanel.setDurationTextFieldIdleMessage("Waiting..."));
                            calcExpectedBreak();
                            SwingUtilities.invokeLater(() -> CurrentTimesBreakPanel.setDurationTextField(breakTimeManager.getSecondsUntil()));

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

                            sendPostRunDiscordMessage(config, breakMethod);

                            SwingUtilities.invokeLater(() -> CurrentTimesBreakPanel.setDurationTextField(breakTimeManager.getSecondsUntil()));
                            Microbot.status = "AFK breaking for " + breakTimeManager.getSecondsUntil();
                            break;

                        case LOGOUT_BREAK:
                            if (config.VERBOSE_LOGGING() && debugCount == 0) {
                                System.out.println("STATE: " + myState);
                                debugCount++;
                            }

                            sendPostRunDiscordMessage(config, breakMethod);

                            if (Microbot.isLoggedIn()) {
                                logout();
                            }

                            SwingUtilities.invokeLater(() -> CurrentTimesBreakPanel.setDurationTextField(breakTimeManager.getSecondsUntil()));
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

                            regenerateExpectedRunTime(false);
                            SwingUtilities.invokeLater(() -> CurrentTimesRunPanel.setDurationTextField(runTimeManager.getSecondsUntil()));

                            regenerateExpectedBreakTime();
                            SwingUtilities.invokeLater(() -> CurrentTimesBreakPanel.setDurationTextField(breakTimeManager.getSecondsUntil()));

                            debugCount = 0;
                            myState = BreakHandlerStates.RUN;
                            break;

                        case STARTUP:
                            SwingUtilities.invokeLater(() -> CurrentTimesRunPanel.setDurationTextFieldIdleMessage("Waiting..."));
                            SwingUtilities.invokeLater(() -> CurrentTimesBreakPanel.setDurationTextFieldIdleMessage("Waiting..."));
                            if (isParentPluginRunning) {
                                System.out.println("Break Handler has started successfully");
                                if (!getIsAtAccountScreens()) {    // We are not on the login screens
                                    myState = BreakHandlerStates.RUN;
                                    calcExpectedRunTime();

                                    SwingUtilities.invokeLater(() -> CurrentTimesRunPanel.setDurationTextField(runTimeManager.getSecondsUntil()));
                                }

                            }

                            break;

                        case POST_BREAK_AFK:
                            if (config.VERBOSE_LOGGING() && debugCount == 0) {
                                System.out.println("STATE: " + myState);
                                debugCount++;
                            }

                            if (config.ENABLE_DISCORD_WEBHOOK() && discordNotificationCount == 0) {
                                discordNotificationCount++;
                                discordWebhook.sendClientStatus(
                                        config.DISCORD_CLIENT_NAME(),
                                        parentPluginName,
                                        "Break Over, Resuming Plugin."
                                );
                            }

                            break;

                        case POST_BREAK_LOGIN:
                            System.out.println("yummy yummy");
                            if (config.VERBOSE_LOGGING() && debugCount == 0) {
                                System.out.println("STATE: " + myState);
                                debugCount++;
                            }

                            if (config.ENABLE_DISCORD_WEBHOOK() && discordNotificationCount == 0) {
                                discordNotificationCount++;
                                discordWebhook.sendClientStatus(
                                        config.DISCORD_CLIENT_NAME(),
                                        parentPluginName,
                                        "Break Over, Resuming Plugin."
                                );
                            }

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

            }, 0, 300, TimeUnit.MILLISECONDS);
        } else {
            System.err.println("Current active script is not compatible with Micro Break Handler...");
        }

        return true;
    }

    private void sendPostRunDiscordMessage(BreakHandlerConfig config, String breakMethod) {
        if (config.ENABLE_DISCORD_WEBHOOK() && discordNotificationCount == 0) {
            discordNotificationCount++;

            if (detailedReportNotification) {
                discordWebhook.sendClientStatusWithGains(
                        config.DISCORD_CLIENT_NAME(),
                        parentPluginName,
                        "Starting break via " + breakMethod + ".",
                        skillExperienceGained,
                        resourcesGained,
                        gpGained
                );
            } else {
                discordWebhook.sendClientStatus(
                        config.DISCORD_CLIENT_NAME(),
                        parentPluginName,
                        "Starting break via " + breakMethod + "."
                );
            }
        }
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
    }

    public static long getMinRunTimeDuration() {
        return minRunTimeDuration;
    }

    public static void setMaxRunTimeDuration(long maxDuration) {
        maxRunTimeDuration = maxDuration;
    }

    public static long getMaxRunTimeDuration() {
        return maxRunTimeDuration;
    }


    /* Break Duration Getters and Setters */
    public static void setMinBreakDuration(long minDuration) {
        minBreakDuration = minDuration;
    }

    public static long getMinBreakDuration() {
        return minBreakDuration;
    }

    public static void setMaxBreakDuration(long maxDuration) {
        maxBreakDuration = maxDuration;
    }

    public static long getMaxBreakDuration() {
        return maxBreakDuration;
    }

    public static void calcExpectedRunTime() {
        runTimeManager.calculateTime((int) minRunTimeDuration, (int) maxRunTimeDuration);
        SwingUtilities.invokeLater(() -> CurrentTimesRunPanel.setDurationTextField(runTimeManager.getSeconds()));
    }

    public static void regenerateExpectedRunTime(boolean fromButtonRequest) {
        if (fromButtonRequest && runTimeManager.timeHasPast()) return;
        calcExpectedRunTime();
    }

    public static void calcExpectedBreak() {
        breakTimeManager.calculateTime((int) minBreakDuration, (int) maxBreakDuration);
        SwingUtilities.invokeLater(() -> CurrentTimesBreakPanel.setDurationTextField(breakTimeManager.getSeconds()));
    }

    public static void regenerateExpectedBreakTime() {
        calcExpectedBreak();
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
        return breakTimeManager.timeHasPast() && runTimeManager.timeHasPast();
    }

    public static void setIsParentPluginRunning(boolean isRunning) {
        isParentPluginRunning = isRunning;
    }

    public static boolean isIsParentPluginRunning() {
        return isParentPluginRunning;
    }

    // Notify the parent script that the Run Time Timer has finished running
    //      and they can start a break when it is convenient
    public static boolean getHasRunTimeTimerFinished() {
        return runTimeManager.timeHasPast();
    }

    public static void setParentPluginName(String name) {
        parentPluginName = name;
    }

    public static String getParentPluginName() {
        return parentPluginName;
    }

    public static void setDetailedReportNotification(boolean flag) {
        detailedReportNotification = flag;
    }

    public static boolean getDetailedReportNotification() {
        return detailedReportNotification;
    }

    public static void setSkillExperienceGained(String[] experienceGained) {
        skillExperienceGained = experienceGained;
    }

    public static String[] getSkillExperienceGained() {
        return skillExperienceGained;
    }

    public static void setResourcesGained(String[] resources) {
        resourcesGained = resources;
    }

    public static String[] getResourcesGained() {
        return resourcesGained;
    }

    public static void setGpGained(String gp) {
        gpGained = gp;
    }

    public static String getGpGained() {
        return gpGained;
    }

    public boolean getIsAtAccountScreens() {
        boolean atAccountScripts = false;

        if (Rs2Widget.getWidget(WidgetInfo.LOGIN_CLICK_TO_PLAY_SCREEN) != null) {
            atAccountScripts = true;
        }

        try {
            if (Microbot.getClient().getGameState() == GameState.LOGIN_SCREEN
                    || Microbot.getClient().getGameState() == GameState.LOGGING_IN) {
                atAccountScripts = true;
            }
        } catch (Exception ignored) {
            //Let's just ignore this, it means the client is still loading
            //Cannot invoke "net.runelite.client.callback.ClientThread.runOnClientThread(java.util.concurrent.Callable)" because the return value of "net.runelite.client.plugins.microbot.Microbot.getClientThread()" is null
        }


        return atAccountScripts;
    }
}
