package net.runelite.client.plugins.microbot;

import net.runelite.api.*;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.microbot.util.globval.enums.InterfaceTab;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.security.Login;
import net.runelite.client.plugins.microbot.util.tabs.Tab;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.awt.event.KeyEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

import static net.runelite.api.widgets.WidgetID.LEVEL_UP_GROUP_ID;

public abstract class Script implements IScript {

    protected ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(100);
    protected ScheduledFuture<?> scheduledFuture;
    public ScheduledFuture<?> mainScheduledFuture;
    public static boolean hasLeveledUp = false;

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
            done = Microbot.getClientThread().runOnClientThread(() -> awaitedCondition.getAsBoolean() || hasLeveledUp);
        } while (!done && System.currentTimeMillis() - startTime < time);
    }


    public void shutdown() {
        if (mainScheduledFuture != null && !mainScheduledFuture.isDone()) {
            Microbot.getNotifier().notify("Shutdown script");
            mainScheduledFuture.cancel(true);
        }
    }
    public boolean run() {
        hasLeveledUp = false;

        if (Rs2Widget.getWidget(36241409) != null) {
            Point p = Microbot.getClientThread()
                    .runOnClientThread(() -> Perspective.localToMinimap(Microbot.getClient(), Microbot.getClient().getLocalPlayer().getLocalLocation()));
            Microbot.getMouse().click(p);
        }

        if (Rs2Widget.getWidget(26345473) != null) {
            Point p = Microbot.getClientThread()
                    .runOnClientThread(() -> Perspective.localToMinimap(Microbot.getClient(), Microbot.getClient().getLocalPlayer().getLocalLocation()));
            Microbot.getMouse().click(p);
        }

        if (!Microbot.isLoggedIn()) {
            new Login();
            return false;
        }

        if (Microbot.pauseAllScripts)
            return false;

        if (Microbot.getWalker() != null && Microbot.getWalker().getPathfinder() != null && !Microbot.getWalker().getPathfinder().isDone())
            return false;

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

    public IScript click(TileObject gameObject) {
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

    public void logout() {
        Tab.switchToLogout();
        sleepUntil(() -> Tab.getCurrentTab() == InterfaceTab.LOGOUT);
        sleep(600, 1000);
        Rs2Widget.clickWidget("Click here to logout");
    }

    public static boolean toggleRunEnergy(boolean toggle) {
        if (Microbot.getVarbitPlayerValue(173) == 0 && !toggle) return true;
        if (Microbot.getVarbitPlayerValue(173) == 1 && toggle) return true;
        Widget widget = Rs2Widget.getWidget(WidgetInfo.MINIMAP_TOGGLE_RUN_ORB.getId());
        if (widget == null) return false;
        Microbot.getMouse().click(widget.getCanvasLocation());
        return true;
    }

    public void onWidgetLoaded(WidgetLoaded event) {
        int groupId = event.getGroupId();

        switch (groupId) {
            case LEVEL_UP_GROUP_ID:
                hasLeveledUp = true;
                break;
        }
    }
}
