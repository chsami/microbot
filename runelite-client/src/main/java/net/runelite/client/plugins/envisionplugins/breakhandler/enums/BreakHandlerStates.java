package net.runelite.client.plugins.envisionplugins.breakhandler.enums;

import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerPanel;
import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerScript;

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

        if (BreakHandlerScript.myState == FAILURE && BreakHandlerScript.isIsParentPluginRunning()) {
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
    public static void failureCheck(BreakHandlerScript breakHandlerScript, BreakHandlerPanel breakHandlerPanel) throws Exception {

        if (!BreakHandlerScript.isIsParentPluginRunning()) {
            BreakHandlerScript.myState = FAILURE;
            breakHandlerScript.getNotificationManager().logState(FAILURE);
        }

        // Notify the user they are missing login creds before logout break starts
        if (BreakHandlerScript.myState != STARTUP) {
            if (breakHandlerScript.getBreakMethod().equals("LOGOUT") &&
                    (breakHandlerPanel.getUsername().getText() == null || breakHandlerPanel.getUsername().getText().trim().isEmpty()) ||
                    (breakHandlerPanel.getPasswordEncryptedValue().length() == 0)
            ){
                BreakHandlerScript.myState = FAILURE;
                breakHandlerScript.getNotificationManager().logState(FAILURE);

                breakHandlerScript.getNotificationManager().simpleNotifyDiscord(
                        breakHandlerScript.getParentPluginName(),
                        "Missing one or more required fields for account credentials, please set them in the Account Tab!"
                );
            }
        }
    }

    private static void resetCounts(BreakHandlerScript breakHandlerScript) {
        breakHandlerScript.getNotificationManager().resetDiscordNotificationCount();
        breakHandlerScript.getNotificationManager().resetVerboseMessageCount();
    }
}
