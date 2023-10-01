package net.runelite.client.plugins.envisionplugins.breakhandler.enums;

import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerScript;

public enum BreakHandlerStates {
    STARTUP,
    RUN,
    START_BREAK,
    AFK_BREAK,
    LOGOUT_BREAK,
    POST_BREAK_AFK,
    POST_BREAK_LOGIN,
    RESET_BOTH_TIMERS;

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
            if (BreakHandlerScript.myState == BreakHandlerStates.RUN && (BreakHandlerScript.runTimeManager.timeHasPast() && BreakHandlerScript.breakTimeManager.isEmpty())) {
                breakHandlerScript.getNotificationManager().resetDiscordNotificationCount();
                BreakHandlerScript.myState = BreakHandlerStates.START_BREAK;
                breakHandlerScript.getNotificationManager().resetVerboseMessageCount();
            }
        }
    }

    /**
     * Check if we should shift state to POST_BREAK_AFK
     */
    public static void afkBreakCheck(BreakHandlerScript breakHandlerScript) {
        if (BreakHandlerScript.myState == BreakHandlerStates.AFK_BREAK && (BreakHandlerScript.breakTimeManager.orElseThrow().timeHasPast() && BreakHandlerScript.runTimeManager.timeHasPast())) {
            breakHandlerScript.getNotificationManager().resetDiscordNotificationCount();
            BreakHandlerScript.myState = BreakHandlerStates.POST_BREAK_AFK;
            breakHandlerScript.getNotificationManager().resetVerboseMessageCount();
        }

    }

    /**
     * Check if we should shift state to POST_BREAK_LOGIN
     */
    public static void logoutBreakCheck(BreakHandlerScript breakHandlerScript) {
        if (BreakHandlerScript.myState == BreakHandlerStates.LOGOUT_BREAK && (BreakHandlerScript.breakTimeManager.orElseThrow().timeHasPast() && BreakHandlerScript.runTimeManager.timeHasPast())) {
            breakHandlerScript.getNotificationManager().resetDiscordNotificationCount();
            BreakHandlerScript.myState = BreakHandlerStates.POST_BREAK_LOGIN;
            breakHandlerScript.getNotificationManager().resetVerboseMessageCount();
        }
    }
}
