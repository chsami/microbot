package net.runelite.client.plugins.microbot.playerassist.cannon;

import net.runelite.api.VarPlayer;
import net.runelite.api.coords.WorldArea;
import net.runelite.client.plugins.cannon.CannonPlugin;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.menu.Rs2Menu;

import java.util.concurrent.TimeUnit;

public class CannonScript extends Script {
    public boolean run(PlayerAssistConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            if (!config.toggleCannon()) return;
            net.runelite.api.GameObject brokenCannon = Rs2GameObject.findObjectById(14916);
            if (brokenCannon != null) {
                Rs2Menu.doAction("Repair", brokenCannon.getCanvasTilePoly(), new String[] {"Broken multicannon"});
                return;
            }
            int cannonBallsLeft = Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getVarpValue(VarPlayer.CANNON_AMMO));
            if (cannonBallsLeft < Random.random(10, 15)) {
                net.runelite.api.GameObject cannon = Rs2GameObject.findObjectById(6);
                if (cannon == null) return;
                WorldArea cannonLocation = new WorldArea(cannon.getWorldLocation().getX() - 1, cannon.getWorldLocation().getY() - 1, 3, 3, cannon.getWorldLocation().getPlane());
                if (!cannonLocation.toWorldPoint().equals(CannonPlugin.getCannonPosition().toWorldPoint())) return;
                Rs2Menu.doAction("Fire", cannon.getCanvasTilePoly(), new String[] {"Dwarf multicannon"});
                sleep(2000, 4000);
            }
        }, 0, 2000, TimeUnit.MILLISECONDS);
        return true;
    }
}
