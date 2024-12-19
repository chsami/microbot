package net.runelite.client.plugins.microbot.util;

import lombok.SneakyThrows;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.math.Random;

import java.util.concurrent.*;
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

    public static void sleepGaussian(int mean, int stddev) {
        int randomSleep = Random.randomGaussian(mean, stddev);
        try {
            Thread.sleep(randomSleep);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void sleepUntil(BooleanSupplier awaitedCondition) {
      sleepUntil(awaitedCondition, 5000);
    }
    @SneakyThrows
    public static <T> T sleepUntilNotNull(Callable<T> method, int time) {
        if (Microbot.getClient().isClientThread()) return null;
        boolean done;
        T methodResponse;
        long startTime = System.currentTimeMillis();
        do {
            methodResponse = method.call();
            done = methodResponse != null;
            sleep(100);
        } while (!done && System.currentTimeMillis() - startTime < time);
        return methodResponse;
    }


    public static boolean sleepUntil(BooleanSupplier awaitedCondition, int time) {
        if (Microbot.getClient().isClientThread()) return false;
        boolean done;
        long startTime = System.currentTimeMillis();
        do {
            done = awaitedCondition.getAsBoolean();
            sleep(100);
        } while (!done && System.currentTimeMillis() - startTime < time);

        return done;
    }

    /**
     * Sleeps until a specified condition is met, running an action periodically, or until a timeout is reached.
     *
     * @param awaitedCondition The condition to wait for.
     * @param action           The action to run periodically while waiting.
     * @param timeoutMillis    The maximum time to wait in milliseconds.
     * @param sleepMillis      The time to sleep between action executions in milliseconds.
     * @return true if the condition was met within the timeout, false otherwise.
     */
    public static boolean sleepUntil(BooleanSupplier awaitedCondition, Runnable action, long timeoutMillis, int sleepMillis) {
        long startTime = System.nanoTime();
        long timeoutNanos = TimeUnit.MILLISECONDS.toNanos(timeoutMillis);
        try {
            while (System.nanoTime() - startTime < timeoutNanos) {
                action.run();
                if (awaitedCondition.getAsBoolean()) {
                    return true;
                }
                sleep(sleepMillis);
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt(); // Restore the interrupt status
        }
        return false; // Timeout reached without satisfying the condition
    }

    public static boolean sleepUntilTrue(BooleanSupplier awaitedCondition) {
        if (Microbot.getClient().isClientThread()) return false;
        long startTime = System.currentTimeMillis();
        do {
            if (awaitedCondition.getAsBoolean()) {
                return true;
            }
            sleep(100);
        } while (System.currentTimeMillis() - startTime < 5000);
        return false;
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