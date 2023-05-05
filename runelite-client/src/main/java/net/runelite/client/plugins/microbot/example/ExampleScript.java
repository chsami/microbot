package net.runelite.client.plugins.microbot.example;

import net.runelite.client.plugins.microbot.Script;

import java.util.concurrent.TimeUnit;

public class ExampleScript extends Script {

    public static double version = 1.0;

    public boolean run(ExampleConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                //code here
                //Rs2Bank.openBank();
                //Rs2Bank.closeBank();
                //Microbot.getWalker().walkTo(new WorldPoint(3275, 3192, 0));
                //Microbot.getWalker().walkTo(new WorldPoint(3141, 9915, 0));
                //Microbot.getWalker().walkTo(new WorldPoint(3093, 3488, 0));
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }
}
