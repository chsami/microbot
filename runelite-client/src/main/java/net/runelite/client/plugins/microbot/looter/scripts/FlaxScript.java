package net.runelite.client.plugins.microbot.looter.scripts;

import net.runelite.api.GameObject;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.looter.AutoLooterConfig;
import net.runelite.client.plugins.microbot.looter.enums.LooterState;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.Global.sleepUntilTrue;

public class FlaxScript extends Script {

    LooterState state;
    boolean init = true;

    public boolean run(AutoLooterConfig config) {
        Microbot.enableAutoRunOn = false;
        initialPlayerLocation = null;

        if (config.hopWhenPlayerDetected()) {
            Microbot.showMessage("Make sure autologin plugin is enabled and randomWorld checkbox is checked!");
        }
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run()) return;
                if (!Microbot.isLoggedIn()) return;
                long startTime = System.currentTimeMillis();

                if (init) {
                    getState(config);
                }

                if (initialPlayerLocation == null) {
                    initialPlayerLocation = Rs2Player.getWorldLocation();
                }

                if (Rs2Player.isMoving() || Rs2Player.isAnimating() || Microbot.pauseAllScripts) return;

                switch (state) {
                    case LOOTING:
                        if (Rs2Inventory.getEmptySlots() <= config.minFreeSlots()) {
                            state = LooterState.BANKING;
                            return;
                        }
                        if (config.hopWhenPlayerDetected()) {
                            Rs2Player.logoutIfPlayerDetected(1, 10);
                            return;
                        }
                        GameObject flaxObject = Rs2GameObject.findObject("flax", false, config.distanceToStray(), true, initialPlayerLocation);
                        if (flaxObject != null) {
                            Rs2GameObject.interact(flaxObject, "pick");
                        }
                        break;
                    case BANKING:
                        if (!Rs2Bank.bankItemsAndWalkBackToOriginalPosition(List.of("flax"), initialPlayerLocation, config.minFreeSlots()))
                            return;
                        state = LooterState.LOOTING;
                        break;
                    case WALKING:
                        Rs2Walker.walkTo(config.flaxLocation().getWorldPoint(), 6);
                        sleepUntilTrue(() -> isNearFlaxLocation(config, 6) && !Rs2Player.isMoving(), 600, 300000);
                        if (!isNearFlaxLocation(config, 6)) return;
                        initialPlayerLocation = Rs2Player.getWorldLocation();
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
        if (!isNearFlaxLocation(config, 6)) {
            state = LooterState.WALKING;
            init = false;
            return;
        }
        if (Rs2Inventory.getEmptySlots() <= config.minFreeSlots()) {
            state = LooterState.BANKING;
            init = false;
            return;
        }
        state = LooterState.LOOTING;
        init = false;
    }

    private boolean isNearFlaxLocation(AutoLooterConfig config, int distance) {
        return Rs2Player.getWorldLocation().distanceTo(config.flaxLocation().getWorldPoint()) <= distance;
    }
}
