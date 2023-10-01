package net.runelite.client.plugins.envisionplugins.breakhandler.enums;

import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerScript;

public enum BreakHandlerStates {
    STARTUP,
    START_BREAK,
    AFK_BREAK,
    LOGOUT_BREAK,
    RUN,
    RESET_BREAK_TIMER,
    RESET_RUN_TIMER,
    RESET_BOTH_TIMERS,
    POST_BREAK_AFK,
    POST_BREAK_LOGIN;

    /**
     * Check if we should shift state to STARTUP
     */
    public static void startupCheck(BreakHandlerScript breakHandlerScript) {
        if (!BreakHandlerScript.isIsParentPluginRunning() && !breakHandlerScript.getIsAtAccountScreens()) {
            BreakHandlerScript.myState = BreakHandlerStates.STARTUP;
        }
    }

    /**
     * Check if we should shift state to START_BREAK
     */
    public static void breakCheck(BreakHandlerScript breakHandlerScript) {
        if (BreakHandlerScript.letBreakHandlerStartBreak) {
            // We should be on break
            if (BreakHandlerScript.myState == BreakHandlerStates.RUN && (BreakHandlerScript.runTimeManager.timeHasPast() && !BreakHandlerScript.breakTimeManager.timeHasPast())) {
                breakHandlerScript.discordNotificationCount = 0;
                BreakHandlerScript.myState = BreakHandlerStates.START_BREAK;
                breakHandlerScript.debugCount = 0;
            }
        }
    }

    /**
     * Check if we should shift state to POST_BREAK_AFK
     */
    public static void afkBreakCheck(BreakHandlerScript breakHandlerScript) {
        if (BreakHandlerScript.myState == BreakHandlerStates.AFK_BREAK && (BreakHandlerScript.breakTimeManager.timeHasPast() && BreakHandlerScript.runTimeManager.timeHasPast())) {
            breakHandlerScript.discordNotificationCount = 0;
            BreakHandlerScript.myState = BreakHandlerStates.POST_BREAK_AFK;
            breakHandlerScript.debugCount = 0;
        }

    }

    /**
     * Check if we should shift state to POST_BREAK_LOGIN
     */
    public static void logoutBreakCheck(BreakHandlerScript breakHandlerScript) {
        if (BreakHandlerScript.myState == BreakHandlerStates.LOGOUT_BREAK && (BreakHandlerScript.breakTimeManager.timeHasPast() && BreakHandlerScript.runTimeManager.timeHasPast())) {
            breakHandlerScript.discordNotificationCount = 0;
            BreakHandlerScript.myState = BreakHandlerStates.POST_BREAK_LOGIN;
            breakHandlerScript.debugCount = 0;
        }
    }
}
