package net.runelite.client.plugins.microbot.accountselector;

import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.util.concurrent.TimeUnit;

public class AccountSelectorScript extends Script {

    public boolean run() {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (Rs2Widget.getWidget(24772680) != null) {
                    Rs2Widget.clickWidget(24772680);
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }
}
