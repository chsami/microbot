package net.runelite.client.plugins.microbot.deserttreasure2.bosses.leviathan;

import net.runelite.client.plugins.microbot.Script;

import java.util.concurrent.TimeUnit;

public class LeviathanScript extends Script {
    public boolean run(LeviathanConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;

            try {
                //write your code here
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }
}
