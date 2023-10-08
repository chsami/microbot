package net.runelite.client.plugins.envisionplugins.breakhandler.enums;

import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerScript;

import java.util.Objects;

public enum BreakHandlerStates {
    STARTUP,
    RUN,
    START_BREAK,
    AFK_BREAK,
    LOGOUT_BREAK,
    POST_BREAK_AFK,
    POST_BREAK_LOGIN,
    RESET,
    FAILURE;

    /**
     * Check if we should shift state to STARTUP
     */
    public static void startupCheck(BreakHandlerScript breakHandlerScript) {
        if (!BreakHandlerScript.isIsParentPluginRunning() && !breakHandlerScript.isAtAccountScreens()) {
            BreakHandlerScript.myState = STARTUP;
        }
    }

    /**
     * Check if we should shift state to START_BREAK
     */
    public static void breakCheck(BreakHandlerScript breakHandlerScript) {
        if (BreakHandlerScript.letBreakHandlerStartBreak) {
            if (BreakHandlerScript.myState == RUN && (BreakHandlerScript.runTimeManager.timeHasPast() && BreakHandlerScript.breakTimeManager.isEmpty())) {
                BreakHandlerScript.myState = START_BREAK;
                resetCounts(breakHandlerScript);
            }
        }
    }

    /**
     * Check if we should shift state to POST_BREAK_AFK
     */
    public static void afkBreakCheck(BreakHandlerScript breakHandlerScript) {
        if (BreakHandlerScript.myState == AFK_BREAK && (BreakHandlerScript.breakTimeManager.orElseThrow().timeHasPast() && BreakHandlerScript.runTimeManager.timeHasPast())) {
            BreakHandlerScript.myState = POST_BREAK_AFK;
            resetCounts(breakHandlerScript);
        }

    }

    /**
     * Check if we should shift state to POST_BREAK_LOGIN
     */
    public static void logoutBreakCheck(BreakHandlerScript breakHandlerScript) {
        if (BreakHandlerScript.myState == LOGOUT_BREAK && (BreakHandlerScript.breakTimeManager.orElseThrow().timeHasPast() && BreakHandlerScript.runTimeManager.timeHasPast())) {
            BreakHandlerScript.myState = POST_BREAK_LOGIN;
            resetCounts(breakHandlerScript);
        }
    }

    /**
     * Check if we should shift state to FAILURE
     */
    public static void failureCheck(BreakHandlerScript breakHandlerScript) throws Exception {

        if (!breakHandlerScript.breakHandlerPanel.pluginEnabledBoxChecked()) {
            breakHandlerScript.failureMessage = "If you wish to use the Break Handler, please enable it!";
            BreakHandlerScript.myState = FAILURE;
            return;
        }

        if (!BreakHandlerScript.isIsParentPluginRunning()) {
            breakHandlerScript.failureMessage = "No supported plugin is enabled!";
            BreakHandlerScript.myState = FAILURE;
            resetCounts(breakHandlerScript);
            return;
        }

        boolean isUsingLogoutMethod = breakHandlerScript.breakHandlerPanel.getBreakMethod().equals("LOGOUT");
        boolean isUsernameEmpty = breakHandlerScript.breakHandlerPanel.getUsername().getText().trim().length() == 0;
        boolean isPasswordValid = breakHandlerScript.breakHandlerPanel.isPasswordValid();

        if (isUsingLogoutMethod && (isUsernameEmpty || !isPasswordValid)) {
            breakHandlerScript.failureMessage = "Missing or invalid account credentials for login!";
            BreakHandlerScript.myState = FAILURE;
            return;
        }

        // THIS IF STATEMENT MUST BE CALLED LAST!!
        // DO NOT MOVE!!
        if (BreakHandlerScript.myState == FAILURE) {
            breakHandlerScript.breakHandlerPanel.redrawTimers();
            BreakHandlerScript.myState = STARTUP;
        }

    }

    private static void resetCounts(BreakHandlerScript breakHandlerScript) {
        breakHandlerScript.getNotificationManager().resetDiscordNotificationCount();
        breakHandlerScript.getNotificationManager().resetVerboseMessageCount();
    }
}
