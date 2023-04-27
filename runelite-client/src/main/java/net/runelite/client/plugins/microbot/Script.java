package net.runelite.client.plugins.microbot;

import net.runelite.api.*;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.math.Random;

import java.awt.event.KeyEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

public abstract class Script implements IScript {

    protected ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(100);
    protected ScheduledFuture<?> scheduledFuture;
    public ScheduledFuture<?> mainScheduledFuture;
    public boolean isRunning() {
        return mainScheduledFuture != null && !mainScheduledFuture.isDone();
    }

    public void sleep(int time) {
        long startTime = System.currentTimeMillis();
        do {
            Microbot.status = "[Sleeping] for " + time + " ms";
        } while (System.currentTimeMillis() - startTime < time);
    }

    public void sleep(int start, int end) {
        long startTime = System.currentTimeMillis();
        do {
            Microbot.status = "[Sleeping] between " + start + " ms and " + end + " ms";
        } while (System.currentTimeMillis() - startTime < Random.random(start, end));
    }

    public ScheduledFuture<?> keepExecuteUntil(Runnable callback, BooleanSupplier awaitedCondition, int time) {
        scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (awaitedCondition.getAsBoolean()) {
                scheduledFuture.cancel(true);
                scheduledFuture = null;
                return;
            }
            callback.run();
        }, 0, time, TimeUnit.MILLISECONDS);
        return scheduledFuture;
    }

    public void sleepUntil(BooleanSupplier awaitedCondition) {
        sleepUntil(awaitedCondition, 5000);
    }

    public void sleepUntil(BooleanSupplier awaitedCondition, int time) {
        boolean done;
        long startTime = System.currentTimeMillis();
        do {
            done = awaitedCondition.getAsBoolean();
        } while (!done && System.currentTimeMillis() - startTime < time);
    }

    public void sleepUntilOnClientThread(BooleanSupplier awaitedCondition) {
        sleepUntilOnClientThread(awaitedCondition, 5000);
    }

    public void sleepUntilOnClientThread(BooleanSupplier awaitedCondition, int time) {
        boolean done;
        long startTime = System.currentTimeMillis();
        do {
            Microbot.status = "[ConditionalSleep] for " + time / 1000 + " seconds";
            done = Microbot.getClientThread().runOnClientThread(() -> awaitedCondition.getAsBoolean());
        } while (!done && System.currentTimeMillis() - startTime < time);
    }


    public void shutdown() {
        if (mainScheduledFuture != null && !mainScheduledFuture.isDone()) {
            mainScheduledFuture.cancel(true);
        }
    }
    public boolean run() {
        if (!Microbot.isLoggedIn()) {
            return false;
        }
        if (Microbot.pauseAllScripts) return false;
        return true;
    }

    //getEquippedItem should be moved to a propper class
    public ItemComposition getEquippedItem(EquipmentInventorySlot slot) {
        final ItemContainer container = Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getItemContainer(InventoryID.EQUIPMENT));
        if (container == null) return null;
        Item itemSlot = container.getItem(slot.getSlotIdx());
        if (itemSlot == null) return null;
        return Microbot.getClientThread().runOnClientThread(() -> Microbot.getItemManager().getItemComposition(itemSlot.getId()));
    }

    public IScript click(GameObject gameObject) {
        if (gameObject != null)
            Microbot.getMouse().click(gameObject.getClickbox().getBounds());
        else
            System.out.println("GameObject is null");
        return this;
    }

    public IScript click(WallObject wall) {
        if (wall != null)
            Microbot.getMouse().click(wall.getClickbox().getBounds());
        else
            System.out.println("wall is null");
        return this;
    }

    public void keyPress(char c) {
        VirtualKeyboard.keyPress(c);
    }
}
