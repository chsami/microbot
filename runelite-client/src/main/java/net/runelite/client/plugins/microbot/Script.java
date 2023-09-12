package net.runelite.client.plugins.microbot;

import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.TileObject;
import net.runelite.api.WallObject;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.client.plugins.microbot.util.globval.enums.InterfaceTab;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.menu.Rs2Menu;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
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
            Rs2Menu.setOption("");
            mainScheduledFuture.cancel(true);
        }
    }

    public boolean run() {
        hasLeveledUp = false;
        if (Microbot.enableAutoRunOn)
            Rs2Player.toggleRunEnergy(true);
        if (Microbot.getClient().getMinimapZoom() > 2)
            Microbot.getClient().setMinimapZoom(2);

        if (Rs2Widget.getWidget(15269889) != null) { //levelup congratulations interface
            VirtualKeyboard.keyPress(KeyEvent.VK_SPACE);
        }

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

        if (Microbot.pauseAllScripts)
            return false;

        if (Microbot.getWalker() != null && Microbot.getWalker().getPathfinder() != null && !Microbot.getWalker().getPathfinder().isDone())
            return false;

        boolean hasRunEnergy = Microbot.getClient().getEnergy() > 4000;

        if (!hasRunEnergy) {
            Inventory.useItemContains("Stamina potion");
        }

        return true;
    }

    public boolean run(int world) {
        hasLeveledUp = false;
        Rs2Player.toggleRunEnergy(true);

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

        if (Microbot.pauseAllScripts)
            return false;

        if (Microbot.getWalker() != null && Microbot.getWalker().getPathfinder() != null && !Microbot.getWalker().getPathfinder().isDone())
            return false;

        return true;
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

    public void onWidgetLoaded(WidgetLoaded event) {
        int groupId = event.getGroupId();

        switch (groupId) {
            case LEVEL_UP_GROUP_ID:
                hasLeveledUp = true;
                break;
        }
    }
}
