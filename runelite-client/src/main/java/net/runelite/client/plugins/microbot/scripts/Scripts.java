package net.runelite.client.plugins.microbot.scripts;


import net.runelite.api.*;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.globval.enums.InterfaceTab;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.tabs.Tab;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

public abstract class Scripts {

    protected ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(100);
    protected ScheduledFuture<?> scheduledFuture;
    public ScheduledFuture<?> mainScheduledFuture;
    public boolean isRunning() {
        return mainScheduledFuture != null && !mainScheduledFuture.isDone();
    }

    public void sleep(int time) {
        long startTime = System.currentTimeMillis();
        do {
        } while (System.currentTimeMillis() - startTime < time);
    }

    public void sleep(int start, int end) {
        long startTime = System.currentTimeMillis();
        do {
        } while (System.currentTimeMillis() - startTime < Random.random(start, end));
    }

    private boolean isDoingAnimation(int timeout) {
        boolean isAnimating = false;
        long startTime = System.currentTimeMillis();
        do {
            if (Microbot.getClient().getLocalPlayer().getAnimation() != -1)
                isAnimating = true;
            sleep(300, 600);
        } while (!isAnimating && System.currentTimeMillis() - startTime < timeout);

        return isAnimating;
    }


    //TODO: needs renaming or fixing
    public ScheduledFuture<?> waitUntil(Runnable callback) {
        scheduledFuture = scheduledExecutorService.schedule(() -> {
            callback.run();
        }, Random.random(2500, 5000), TimeUnit.MILLISECONDS);
        return scheduledFuture;
    }

    public ScheduledFuture<?> awaitExecutionUntil(Runnable callback, BooleanSupplier awaitedCondition) {
        scheduledFuture = awaitExecutionUntil(callback, awaitedCondition, Random.random(2500, 5000));
        return scheduledFuture;
    }

    public ScheduledFuture<?> keepExecuteUntil(Runnable callback, BooleanSupplier awaitedCondition) {
        scheduledFuture = keepExecuteUntil(callback, awaitedCondition, Random.random(2500, 5000));
        return scheduledFuture;
    }

    public ScheduledFuture<?> awaitExecutionUntil(Runnable callback, BooleanSupplier awaitedCondition, int time) {
        scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (awaitedCondition.getAsBoolean()) {
                scheduledFuture.cancel(true);
                scheduledFuture = null;
                callback.run();
            }
        }, 0, time, TimeUnit.MILLISECONDS);
        return scheduledFuture;
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
        sleepUntil(awaitedCondition, Random.random(2500, 5000));
    }

    public void sleepUntil(BooleanSupplier awaitedCondition, int time) {
        boolean done;
        long startTime = System.currentTimeMillis();
        do {
            done = awaitedCondition.getAsBoolean();
        } while (!done && System.currentTimeMillis() - startTime < time);
    }

    public void sleepUntilOnClientThread(BooleanSupplier awaitedCondition) {
        sleepUntilOnClientThread(awaitedCondition, Random.random(2500, 5000));
    }

    public void sleepUntilOnClientThread(BooleanSupplier awaitedCondition, int time) {
        boolean done;
        long startTime = System.currentTimeMillis();
        do {
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
            shutdown();
        }
        if (Microbot.isBussy) return false;
        return true;
    }

    public void openInventory() {
        Microbot.getClientThread().runOnClientThread(() -> Tab.switchToInventoryTab());
        sleep(300, 1200);
        sleepUntilOnClientThread(() -> Tab.getCurrentTab() == InterfaceTab.INVENTORY);
    }

    public void openPrayerTab() {
        Microbot.getClientThread().runOnClientThread(() -> Tab.switchToPrayerTab());
        sleep(300, 1200);
        sleepUntilOnClientThread(() -> Tab.getCurrentTab() == InterfaceTab.INVENTORY);
    }

    public ItemComposition getEquippedItem(EquipmentInventorySlot slot) {
        final ItemContainer container = Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getItemContainer(InventoryID.EQUIPMENT));
        Item itemSlot = container.getItem(slot.getSlotIdx());
        return Microbot.getClientThread().runOnClientThread(() -> Microbot.getItemManager().getItemComposition(itemSlot.getId()));
    }
}
