package net.runelite.client.plugins.microbot.playerassist.combat;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.concurrent.TimeUnit;

public class SafeSpot extends Script {

    public WorldPoint currentSafeSpot = null;

    public boolean run(PlayerAssistConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;

                if (currentSafeSpot == null)
                    currentSafeSpot = Microbot.getClient().getLocalPlayer().getWorldLocation();

                if (currentSafeSpot != null && currentSafeSpot.distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) > 2) {
                    Rs2Walker.walkMiniMap(currentSafeSpot);
                }


            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        currentSafeSpot = null;
    }
}
