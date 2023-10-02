package net.runelite.client.plugins.envisionplugins.breakhandler.enums;

import net.runelite.api.GameState;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerScript;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

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
    public static void failureCheck(BreakHandlerScript breakHandlerScript) {
        Widget clickHereToPlayButton = Rs2Widget.getWidget(24772680); //on login screen

        if (!BreakHandlerScript.isIsParentPluginRunning() &&
            (Microbot.getClient().getGameState() != GameState.LOGIN_SCREEN &&
                Microbot.getClient().getGameState() != GameState.LOADING &&
                Microbot.getClient().getGameState() != GameState.LOGGING_IN &&
                clickHereToPlayButton != null
            ) &&
            Microbot.isLoggedIn()
        ) {
            BreakHandlerScript.myState = FAILURE;
            resetCounts(breakHandlerScript);
        }
    }

    private static void resetCounts(BreakHandlerScript breakHandlerScript) {
        breakHandlerScript.getNotificationManager().resetDiscordNotificationCount();
        breakHandlerScript.getNotificationManager().resetVerboseMessageCount();
    }
}
