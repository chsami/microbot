package net.runelite.client.plugins.microbot.playerassist.combat;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.concurrent.TimeUnit;

public class SafeSpot extends Script {

    public WorldPoint currentSafeSpot = null;
    private boolean messageShown = false;

public boolean run(PlayerAssistConfig config) {
    mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
        try {
            if (!Microbot.isLoggedIn() || !super.run() || !config.toggleSafeSpot() || Microbot.isMoving()) return;

            currentSafeSpot = config.safeSpot();
            if(isDefaultSafeSpot(currentSafeSpot)){

                if(!messageShown){
                    Microbot.showMessage("Please set a center location");
                    messageShown = true;
                }
                return;
            }
            if (isDefaultSafeSpot(currentSafeSpot) || isPlayerAtSafeSpot(currentSafeSpot)) return;
            messageShown = false;

            if(Rs2Walker.walkMiniMap(currentSafeSpot)) {
                Microbot.pauseAllScripts = true;
                sleepUntil(() -> isPlayerAtSafeSpot(currentSafeSpot));
                Microbot.pauseAllScripts = false;
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }, 0, 600, TimeUnit.MILLISECONDS);
    return true;
}

private boolean isDefaultSafeSpot(WorldPoint safeSpot) {
    return safeSpot.getX() == 0 && safeSpot.getY() == 0;
}

private boolean isPlayerAtSafeSpot(WorldPoint safeSpot) {
    return safeSpot.distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) <= 0;
}

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
