package net.runelite.client.plugins.microbot;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.shortestpath.ShortestPathPlugin;
import net.runelite.client.plugins.microbot.util.globval.enums.InterfaceTab;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

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
    public static boolean hasLeveledUp = false;
    public static boolean useStaminaPotsIfNeeded = true;

    public boolean isRunning() {
        return mainScheduledFuture != null && !mainScheduledFuture.isDone();
    }

    @Getter
    protected static WorldPoint initialPlayerLocation;

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

    public boolean sleepUntil(BooleanSupplier awaitedCondition) {
        return sleepUntil(awaitedCondition, 5000);
    }

    public boolean sleepUntil(BooleanSupplier awaitedCondition, int time) {
        boolean done;
        long startTime = System.currentTimeMillis();
        do {
            done = awaitedCondition.getAsBoolean();
        } while (!done && System.currentTimeMillis() - startTime < time);
        return done;
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
            mainScheduledFuture.cancel(true);
            Microbot.pauseAllScripts = false;
            ShortestPathPlugin.exit();
            if (Microbot.getClientThread().scheduledFuture != null)
                Microbot.getClientThread().scheduledFuture.cancel(true);
            initialPlayerLocation = null;
        }
    }

    public boolean run() {
        hasLeveledUp = false;
        if (Microbot.enableAutoRunOn)
            Rs2Player.toggleRunEnergy(true);

        if (Rs2Widget.getWidget(15269889) != null) { //levelup congratulations interface
            VirtualKeyboard.keyPress(KeyEvent.VK_SPACE);
        }
        Widget clickHereToPlayButton = Rs2Widget.getWidget(24772680); //on login screen
        if (clickHereToPlayButton != null && !Microbot.getClientThread().runOnClientThread(clickHereToPlayButton::isHidden)) {
            Rs2Widget.clickWidget(clickHereToPlayButton.getId());
        }

        if (Microbot.pauseAllScripts)
            return false;

        boolean hasRunEnergy = Microbot.getClient().getEnergy() > 4000;

        if (!hasRunEnergy && useStaminaPotsIfNeeded) {
            Rs2Inventory.interact("Stamina potion", "drink");
        }

        if (Microbot.isLoggedIn()) {
            if (initialPlayerLocation == null)
                initialPlayerLocation = Microbot.getClient().getLocalPlayer().getWorldLocation();
        }

        return true;
    }

    public void keyPress(char c) {
        VirtualKeyboard.keyPress(c);
    }

    @Deprecated(since="Use Rs2Player.logout()", forRemoval = true)
    public void logout() {
        Rs2Tab.switchToLogout();
        sleepUntil(() -> Rs2Tab.getCurrentTab() == InterfaceTab.LOGOUT);
        sleep(600, 1000);
        Rs2Widget.clickWidget("Click here to logout");
    }

    public void onWidgetLoaded(WidgetLoaded event) {
        int groupId = event.getGroupId();

        if (groupId == InterfaceID.LEVEL_UP) {
            hasLeveledUp = true;
        }
    }
}
