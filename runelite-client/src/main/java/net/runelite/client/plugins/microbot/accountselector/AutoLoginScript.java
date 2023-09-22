package net.runelite.client.plugins.microbot.accountselector;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.security.Login;

import java.util.concurrent.TimeUnit;

public class AutoLoginScript extends Script {

    public boolean run(AutoLoginConfig autoLoginConfig) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            super.run();
            try {
                if (!Microbot.isLoggedIn()) {
                    if (autoLoginConfig.world() == -1) {
                        new Login(383);
                    } else {
                        new Login(autoLoginConfig.world());
                    }
                    sleep(5000);
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }
}
