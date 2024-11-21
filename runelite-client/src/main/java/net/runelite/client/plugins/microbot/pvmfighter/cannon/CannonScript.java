package net.runelite.client.plugins.microbot.pvmfighter.cannon;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2Cannon;
import net.runelite.client.plugins.microbot.pvmfighter.PvmFighterConfig;

import java.util.concurrent.TimeUnit;

public class CannonScript extends Script {
    public boolean run(PvmFighterConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run() || !config.toggleCannon()) return;
               if (Rs2Cannon.repair())
                   return;
               Rs2Cannon.refill();
            } catch(Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 2000, TimeUnit.MILLISECONDS);
        return true;
    }
}
