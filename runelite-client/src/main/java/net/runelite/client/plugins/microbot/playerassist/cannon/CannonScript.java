package net.runelite.client.plugins.microbot.playerassist.cannon;

import net.runelite.api.TileObject;
import net.runelite.api.VarPlayer;
import net.runelite.api.coords.WorldArea;
import net.runelite.client.plugins.cannon.CannonPlugin;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.math.Random;

import java.util.concurrent.TimeUnit;

public class CannonScript extends Script {
    public boolean run(PlayerAssistConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run()) return;
                if (!config.toggleCannon()) return;
                TileObject brokenCannon = Rs2GameObject.findObjectById(14916);
                if (brokenCannon != null) {
                    Rs2GameObject.interact(brokenCannon, "Repair");
                    return;
                }
                int cannonBallsLeft = Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getVarpValue(VarPlayer.CANNON_AMMO));
                if (cannonBallsLeft < Random.random(10, 15)) {
                    TileObject cannon = Rs2GameObject.findObjectById(6);
                    if (cannon == null) return;
                    WorldArea cannonLocation = new WorldArea(cannon.getWorldLocation().getX() - 1, cannon.getWorldLocation().getY() - 1, 3, 3, cannon.getWorldLocation().getPlane());
                    if (!cannonLocation.toWorldPoint().equals(CannonPlugin.getCannonPosition().toWorldPoint())) return;
                    Rs2GameObject.interact(cannon, "Fire");
                    sleep(1200);
                    Rs2GameObject.interact(cannon, "Fire");
                    sleepUntil(() -> Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getVarpValue(VarPlayer.CANNON_AMMO)) > Random.random(10, 15));
                }
            } catch(Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 2000, TimeUnit.MILLISECONDS);
        return true;
    }
}
