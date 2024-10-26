package net.runelite.client.plugins.microbot.shortestpath;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.walker.WalkerState;

import java.util.concurrent.TimeUnit;

public class ShortestPathScript extends Script {

    public boolean run(WorldPoint target) {
        Microbot.enableAutoRunOn = true;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;

                if (target != null) {
                    WalkerState state = Rs2Walker.walkWithState(target);
                    if (state == WalkerState.UNREACHABLE || state == WalkerState.ARRIVED) {
                        mainScheduledFuture.cancel(true);
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Microbot.log(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
