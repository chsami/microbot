package net.runelite.client.plugins.microbot.hunter.scripts;

import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.hunter.AutoHunterConfig;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;

import java.util.concurrent.TimeUnit;

enum State {
    IDLE,
    CATCHING,
    LAYING
}


public class AutoChinScript extends Script {

    public static boolean test = false;
    public static String version = "1.0.0";
    State currentState = State.IDLE;
    public boolean run(AutoHunterConfig config) {
        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;
                long startTime = System.currentTimeMillis();

                switch(currentState) {
                    case IDLE:
                        handleIdleState();
                        break;
                    case CATCHING:
                        handleCatchingState(config);
                        break;
                    case LAYING:
                        handleLayingState(config);
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

    private void handleIdleState() {
        try {
            // If there are box traps on the floor, interact with them first
            if (Rs2GroundItem.interact(ItemID.BOX_TRAP, "lay", 4)) {
                currentState = State.LAYING;
                return;
            }

            // If there are shaking boxes, interact with them
            if (Rs2GameObject.interact(ObjectID.SHAKING_BOX_9383, "reset", 4)) {
                currentState = State.CATCHING;
                return;
            }

            // Interact with traps that have not caught anything
            if (Rs2GameObject.interact(ObjectID.BOX_TRAP_9385, "reset", 4)) {
                currentState = State.CATCHING;
            }
        } catch (Exception ex) {
            Microbot.log(ex.getMessage());
            ex.printStackTrace();
            currentState = State.CATCHING;
        }
    }

    private void handleCatchingState(AutoHunterConfig config) {
        sleep(config.minSleepAfterCatch(), config.maxSleepAfterCatch());
        currentState = State.IDLE;
    }

    private void handleLayingState(AutoHunterConfig config) {
        sleep(config.minSleepAfterLay(), config.maxSleepAfterLay());
        currentState = State.IDLE;
    }
}
