package net.runelite.client.plugins.microbot.looter.scripts;

import net.runelite.api.GameObject;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.looter.AutoLooterConfig;
import net.runelite.client.plugins.microbot.looter.enums.LooterState;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.Global.sleepUntilTrue;

public class NatureRuneChestScript extends Script {

    LooterState state;
    boolean init = true;

    public boolean run(AutoLooterConfig config) {
        Microbot.enableAutoRunOn = false;

        if (config.hopWhenPlayerDetected()) {
            Microbot.showMessage("Make sure autologin plugin is enabled and randomWorld checkbox is checked!");
        }
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run()) return;
                if (!Microbot.isLoggedIn()) return;
                long startTime = System.currentTimeMillis();

                if(init){
                    getState(config);
                }

                if (Rs2Player.isMoving() || Rs2Player.isAnimating() || Microbot.pauseAllScripts) return;

                switch (state) {
                    case LOOTING:
                        if (config.hopWhenPlayerDetected()) {
                            Rs2Player.logoutIfPlayerDetected(1, 10);
                            return;
                        }
                        GameObject natureRuneChest = Rs2GameObject.findObject(config.natureRuneChestLocation().getObjectID(), config.distanceToStray(), true, config.natureRuneChestLocation().getWorldPoint());
                        if (natureRuneChest != null) {
                            Rs2GameObject.interact(natureRuneChest, "Search for traps");
                            sleepUntilTrue(() -> !Rs2Player.isInteracting(), 500, 8000);
                            sleep(Random.random(18000, 20000));
                        }
                        break;
                    case WALKING:
                        Rs2Walker.walkTo(config.natureRuneChestLocation().getWorldPoint());
                        sleepUntilTrue(() -> isNearNatureRuneChest(config, 6) && !Rs2Player.isMoving(), 600, 300000);
                        if (!isNearNatureRuneChest(config, 6)) return;
                        state = LooterState.LOOTING;
                        break;
                }

                long endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                System.out.println("Total time for loop " + totalTime);

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    private void getState(AutoLooterConfig config) {
        if (!isNearNatureRuneChest(config, 6)) {
            state = LooterState.WALKING;
            init = false;
            return;
        }
        state = LooterState.LOOTING;
        init = false;
    }

    private boolean isNearNatureRuneChest(AutoLooterConfig config, int distance) {
        return Rs2Player.getWorldLocation().distanceTo(config.natureRuneChestLocation().getWorldPoint()) <= distance;
    }
}
