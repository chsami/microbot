package net.runelite.client.plugins.microbot.scripts;


import net.runelite.api.*;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.Config;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.NPCManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.construction.ConstructionConfig;
import net.runelite.client.plugins.microbot.util.globval.enums.InterfaceTab;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.tabs.Tab;
import net.runelite.client.plugins.microbot.util.walker.Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;

import javax.inject.Inject;
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
            return false;
        }
        if (Microbot.pauseAllScripts) return false;
        return true;
    }

    public void openInventory() {
        Microbot.getClientThread().runOnClientThread(() -> Tab.switchToInventoryTab());
        sleep(300, 1200);
        sleepUntilOnClientThread(() -> Tab.getCurrentTab() == InterfaceTab.INVENTORY);
    }

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

    public boolean sleepUntilHasWidget(String text) {
        return Rs2Widget.sleepUntilHasWidget(text);
    }

    public boolean hasWidget(String text) {
        return Rs2Widget.hasWidget(text);
    }

    public boolean hasItem(String text) {
        return Inventory.hasItem(text);
    }
}
