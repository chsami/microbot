package net.runelite.client.plugins.microbot.zerozero.varrockcleaner;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;

import java.util.concurrent.TimeUnit;

public class VarrockCleanerScript extends Script {

    public boolean run(VarrockCleanerConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;
                if (!config.startPlugin()) return;


            } catch (Exception ex) {
                Microbot.log(ex.getMessage());
            }
        }, 0, 100, TimeUnit.MILLISECONDS);

        return true;
    }

    public void stop() {
        Microbot.log("Plugin stopped.");
        if (mainScheduledFuture != null) {
            mainScheduledFuture.cancel(true);
            super.shutdown();
        }
    }
}