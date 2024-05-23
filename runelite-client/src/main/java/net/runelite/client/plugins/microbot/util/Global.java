package net.runelite.client.plugins.microbot.util;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.math.Random;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

public class Global {
    static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
    static ScheduledFuture<?> scheduledFuture;

    public static ScheduledFuture<?> awaitExecutionUntil(Runnable callback, BooleanSupplier awaitedCondition, int time) {
        scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (awaitedCondition.getAsBoolean()) {
                scheduledFuture.cancel(true);
                scheduledFuture = null;
                callback.run();
            }
        }, 0, time, TimeUnit.MILLISECONDS);
        return scheduledFuture;
    }

    public static void sleep(int start) {
        if (Microbot.getClient().isClientThread()) return;
        try {
            Thread.sleep(start);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void sleep(int start, int end) {
        int randomSleep = Random.random(start, end);
        try {
            Thread.sleep(randomSleep);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void sleepUntil(BooleanSupplier awaitedCondition) {
      sleepUntil(awaitedCondition, 5000);
    }

    public static void sleepUntil(BooleanSupplier awaitedCondition, int time) {
        if (Microbot.getClient().isClientThread()) return;
        boolean done;
        long startTime = System.currentTimeMillis();
        do {
            done = awaitedCondition.getAsBoolean();
            sleep(100);
        } while (!done && System.currentTimeMillis() - startTime < time);
    }

    public static boolean sleepUntilTrue(BooleanSupplier awaitedCondition, int time, int timeout) {
        if (Microbot.getClient().isClientThread()) return false;
        long startTime = System.currentTimeMillis();
        do {
            if (awaitedCondition.getAsBoolean()) {
                return true;
            }
            sleep(time);
        } while (System.currentTimeMillis() - startTime < timeout);
        return false;
    }

    public static void sleepUntilOnClientThread(BooleanSupplier awaitedCondition) {
        sleepUntilOnClientThread(awaitedCondition, Random.random(2500, 5000));
    }

    public static void sleepUntilOnClientThread(BooleanSupplier awaitedCondition, int time) {
        if (Microbot.getClient().isClientThread()) return;
        boolean done;
        long startTime = System.currentTimeMillis();
        do {
            done = Microbot.getClientThread().runOnClientThread(() -> awaitedCondition.getAsBoolean());
        } while (!done && System.currentTimeMillis() - startTime < time);
    }
}