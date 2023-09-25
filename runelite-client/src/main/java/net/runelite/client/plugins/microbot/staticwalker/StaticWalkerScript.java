package net.runelite.client.plugins.microbot.staticwalker;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;

import java.util.concurrent.TimeUnit;

public class StaticWalkerScript extends Script {

    public static double version = 1.0;
    private final int maxFails = 3;
    private int failCount = 0;

    public boolean run(StaticWalkerConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {

                WorldPoint destination;

                if (!config.locations().getName().equals("None")) {
                    destination = config.locations().getWorldPoint();

                } else {
                    int x = Integer.parseInt(config.xCoordinate());
                    int y = Integer.parseInt(config.yCoordinate());
                    int z = Integer.parseInt(config.zCoordinate());

                    destination = new WorldPoint(x, y, z);

                }

                if (!Microbot.getWalker().staticWalkTo(destination)) {
                    failCount++;
                } else {
                    shutdown();
                }

                if (failCount >= maxFails) {
                    shutdown();
                }

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }
}
