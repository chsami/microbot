package net.runelite.client.plugins.microbot;

import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.util.globval.enums.InterfaceTab;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
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
        if (Microbot.enableAutoRunOn)
            Rs2Player.toggleRunEnergy(true);
        if (Microbot.getClient().getMinimapZoom() > 2)
            Microbot.getClient().setMinimapZoom(2);

        if (Rs2Widget.getWidget(15269889) != null) { //levelup congratulations interface
            VirtualKeyboard.keyPress(KeyEvent.VK_SPACE);
        }
        Widget clickHereToPlayButton = Rs2Widget.getWidget(24772680); //on login screen
        if (clickHereToPlayButton != null && !Microbot.getClientThread().runOnClientThread(() -> clickHereToPlayButton.isHidden())) {
            Rs2Widget.clickWidget(clickHereToPlayButton.getId());
        }

        if (Microbot.pauseAllScripts)
            return false;

        if (Microbot.getWalker() != null && Microbot.getWalker().getPathfinder() != null && !Microbot.getWalker().getPathfinder().isDone())
            return false;

        boolean hasRunEnergy = Microbot.getClient().getEnergy() > 4000;

        if (!hasRunEnergy && useStaminaPotsIfNeeded) {
            Rs2Inventory.use("Stamina potion");
        }

        return true;
    }

    public void keyPress(char c) {
        VirtualKeyboard.keyPress(c);
    }

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
