package net.runelite.client.plugins.microbot.AgilityPyramid;

import net.runelite.client.plugins.microbot.AgilityPyramid.tasks.BankHandler;
import net.runelite.client.plugins.microbot.AgilityPyramid.tasks.ClaimReward;
import net.runelite.client.plugins.microbot.AgilityPyramid.tasks.CompleteCourse;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

public class AgilitypyramidScript extends Script {
    public static String state = "CompleteCourse";

    @Inject
    private PyramidConfig config;

    @Override
    public boolean run() {
        Microbot.enableAutoRunOn = false;

        // Ensure config is not null
        if (config == null) {
            System.err.println("PyramidConfig is null. Ensure proper dependency injection.");
            return false;
        }

        // Debug configuration values

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;

                // Run script here based on state
                if (state.equals("CompleteCourse")) {
                    CompleteCourse.main(config);
                } else if (state.equals("TurnInPyramids")) {
                    ClaimReward.HandleSimon(config);
                } else if (state.equals("BankHandler")) {
                    BankHandler.HandleBank(config);
                } else {
                    System.err.println("Unknown state: " + state);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);

        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
