package net.runelite.client.plugins.envisionplugins.breakhandler;

import net.runelite.client.plugins.microbot.Script;

import java.util.concurrent.TimeUnit;

public class BreakHandlerScript extends Script {

    public static double version = 0.1;

    public boolean run(BreakHandlerConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {

        }, 0, 600, TimeUnit.MILLISECONDS);

        return true;
    }
}
