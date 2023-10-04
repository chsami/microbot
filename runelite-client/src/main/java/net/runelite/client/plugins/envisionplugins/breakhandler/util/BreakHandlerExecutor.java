package net.runelite.client.plugins.envisionplugins.breakhandler.util;

import net.runelite.client.plugins.envisionplugins.breakhandler.BreakHandlerScript;

import static net.runelite.client.plugins.microbot.util.Global.sleepUntil;

public class BreakHandlerExecutor {
    private boolean preNotification = false;
    private boolean postNotification = false;

    private String[] skillExperienceGainedPre;
    private String[] resourcesGainedPre;
    private String gpGainedPre;

    private String[] skillExperienceGainedPost;
    private String[] resourcesGainedPost;
    private String gpGainedPost;

    public interface Executor {
        void execute();
    }

    public void sendDiscordNotificationBeforeBreak(String[] skillExperienceGained, String[] resourcesGained, String gpGained) {
        skillExperienceGainedPre = skillExperienceGained;
        resourcesGainedPre = resourcesGained;
        gpGainedPre = gpGained;

        preNotification = true;
    }

    public void sendDiscordNotificationAfterBreak(String[] skillExperienceGained, String[] resourcesGained, String gpGained) {
        skillExperienceGainedPost = skillExperienceGained;
        resourcesGainedPost = resourcesGained;
        gpGainedPost = gpGained;

        postNotification = true;
    }

    private void sendDiscordNotifications(boolean isPost) {
        if (isPost) {
            BreakHandlerScript.setSkillExperienceGained(skillExperienceGainedPost);
            BreakHandlerScript.setResourcesGained(resourcesGainedPost);
            BreakHandlerScript.setGpGained(gpGainedPost);
        } else {
            BreakHandlerScript.setSkillExperienceGained(skillExperienceGainedPre);
            BreakHandlerScript.setResourcesGained(resourcesGainedPre);
            BreakHandlerScript.setGpGained(gpGainedPre);
        }

    }

    public void breakOrExecute(Executor executor) {
        if (BreakHandlerScript.getHasRunTimeTimerFinished()) {
            executeBreak();
        } else {
            executor.execute();
        }
    }

    public void breakOrExecute(Executor executor, Boolean skipBreakCheck) {
        if (BreakHandlerScript.getHasRunTimeTimerFinished() && !skipBreakCheck) {
            executeBreak();
        } else {
            executor.execute();
        }
    }

    private void executeBreak() {
        if (preNotification) sendDiscordNotifications(false);

        BreakHandlerScript.setLetBreakHandlerStartBreak(true);
        sleepUntil(BreakHandlerScript::getIsBreakOver);
        BreakHandlerScript.setLetBreakHandlerStartBreak(false);

        if (postNotification) sendDiscordNotifications(true);
    }
}
