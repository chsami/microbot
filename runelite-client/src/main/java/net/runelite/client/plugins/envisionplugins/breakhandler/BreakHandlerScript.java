package net.runelite.client.plugins.envisionplugins.breakhandler;

import net.runelite.client.plugins.microbot.Script;

import java.util.concurrent.TimeUnit;

public class BreakHandlerScript extends Script {

    public static double version = 0.2;
    protected boolean shouldBreak = false;

    public boolean run(BreakHandlerConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {

        }, 0, 600, TimeUnit.MILLISECONDS);

        return true;
    }

    public boolean shouldBreak() {
        return shouldBreak;
    }

    public void setShouldBreak(boolean shouldBreak) {
        this.shouldBreak = shouldBreak;
    }
}
