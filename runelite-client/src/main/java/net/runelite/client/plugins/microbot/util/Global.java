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
        if (!Microbot.getClient().isClientThread()) {
            long startTime = System.currentTimeMillis();
            do {
            } while (System.currentTimeMillis() - startTime < start);
        }
    }

    public static void sleep(int start, int end) {
        if (!Microbot.getClient().isClientThread()) {
            long startTime = System.currentTimeMillis();
            do {
            } while (System.currentTimeMillis() - startTime < Random.random(start, end));
        }
    }

    public static void sleepUntil(BooleanSupplier awaitedCondition) {
        boolean done;
        long startTime = System.currentTimeMillis();
        do {
            done = awaitedCondition.getAsBoolean();
        } while (!done && System.currentTimeMillis() - startTime < 5000);
    }

    public static void sleepUntil(BooleanSupplier awaitedCondition, int time) {
        boolean done;
        long startTime = System.currentTimeMillis();
        do {
            done = awaitedCondition.getAsBoolean();
        } while (!done && System.currentTimeMillis() - startTime < time);
    }

    public static void sleepUntilOnClientThread(BooleanSupplier awaitedCondition) {
        sleepUntilOnClientThread(awaitedCondition, Random.random(2500, 5000));
    }

    public static void sleepUntilOnClientThread(BooleanSupplier awaitedCondition, int time) {
        boolean done;
        long startTime = System.currentTimeMillis();
        do {
            done = Microbot.getClientThread().runOnClientThread(() -> awaitedCondition.getAsBoolean());
        } while (!done && System.currentTimeMillis() - startTime < time);
    }
}