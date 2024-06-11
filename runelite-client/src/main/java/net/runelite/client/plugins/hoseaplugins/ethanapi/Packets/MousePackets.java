package net.runelite.client.plugins.hoseaplugins.ethanapi.Packets;

import net.runelite.client.plugins.hoseaplugins.ethanapi.PacketUtils.ObfuscatedNames;
import net.runelite.client.plugins.hoseaplugins.ethanapi.PacketUtils.PacketDef;
import net.runelite.client.plugins.hoseaplugins.ethanapi.PacketUtils.PacketReflection;
import lombok.SneakyThrows;
import net.runelite.api.Client;
import net.runelite.client.RuneLite;

import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.Executors;

import static java.awt.event.InputEvent.BUTTON1_DOWN_MASK;

public class MousePackets{
    
    static Client client = RuneLite.getInjector().getInstance(Client.class);
    private static final Random random = new Random();
    private static long randomDelay = randomDelay();
    public static BigInteger modInverse(BigInteger val, int bits) {
        try {
            BigInteger shift = BigInteger.ONE.shiftLeft(bits);
            return val.modInverse(shift);
        } catch (ArithmeticException e) {
            return val;
        }
    }

    public static long modInverse(long val) {
        return modInverse(BigInteger.valueOf(val), 64).longValue();
    }

    @SneakyThrows
    public static void queueClickPacket(int x, int y) {
        long mouseHandlerMS = System.currentTimeMillis();
        setMouseHandlerLastMillis(mouseHandlerMS);
        long clientMS = getClientLastMillis();
        long deltaMs = mouseHandlerMS - clientMS;
        setClientLastMillis(mouseHandlerMS);
        if (deltaMs < 0) {
            deltaMs = 0L;
        }
        if (deltaMs > 32767) {
            deltaMs = 32767L;
        }
        int mouseInfo = ((int) deltaMs << 1);
        PacketReflection.sendPacket(PacketDef.getEventMouseClick(), mouseInfo, x, y);
        if (checkIdleLogout()) {
            randomDelay = randomDelay();
            Executors.newSingleThreadExecutor()
                    .submit(MousePackets::pressKey);
        }
    }

    public static void queueClickPacket() {
        queueClickPacket(0, 0);
    }

    private static boolean checkIdleLogout() {
        int idleClientTicks = client.getKeyboardIdleTicks();

        if (client.getMouseIdleTicks() < idleClientTicks) {
            idleClientTicks = client.getMouseIdleTicks();
        }

        return idleClientTicks >= randomDelay;
    }

    private static long randomDelay() {
        return (long) clamp(
                Math.round(random.nextGaussian() * 8000)
        );
    }

    private static double clamp(double val) {
        return Math.max(1, Math.min(13000, val));
    }

    private static void pressKey() {
        KeyEvent keyPress = new KeyEvent(client.getCanvas(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), BUTTON1_DOWN_MASK, KeyEvent.VK_BACK_SPACE);
        client.getCanvas().dispatchEvent(keyPress);
        KeyEvent keyRelease = new KeyEvent(client.getCanvas(), KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_BACK_SPACE);
        client.getCanvas().dispatchEvent(keyRelease);
        KeyEvent keyTyped = new KeyEvent(client.getCanvas(), KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, KeyEvent.VK_UNDEFINED);
        client.getCanvas().dispatchEvent(keyTyped);
    }
    @SneakyThrows
    public static long getMouseHandlerLastMillis() {
        Class<?> mouseHandler = client.getClass().getClassLoader().loadClass(ObfuscatedNames.MouseHandler_lastPressedTimeMillisClass);
        Field mouseHandlerLastPressedTime = mouseHandler.getDeclaredField(ObfuscatedNames.MouseHandler_lastPressedTimeMillisField);
        mouseHandlerLastPressedTime.setAccessible(true);
        long retValue = mouseHandlerLastPressedTime.getLong(null) * Long.parseLong(ObfuscatedNames.mouseHandlerMillisMultiplier);
        mouseHandlerLastPressedTime.setAccessible(false);
        return retValue;
    }

    @SneakyThrows
    public static long getClientLastMillis() {
        Field clientLastPressedTimeMillis = client.getClass().getDeclaredField(ObfuscatedNames.clientMillisField);
        clientLastPressedTimeMillis.setAccessible(true);
        long retValue = clientLastPressedTimeMillis.getLong(client) * Long.parseLong(ObfuscatedNames.clientMillisMultiplier);
        clientLastPressedTimeMillis.setAccessible(false);
        return retValue;
    }

    @SneakyThrows
    public static void setMouseHandlerLastMillis(long time) {
        Class<?> mouseHandler = client.getClass().getClassLoader().loadClass(ObfuscatedNames.MouseHandler_lastPressedTimeMillisClass);
        Field mouseHandlerLastPressedTime = mouseHandler.getDeclaredField(ObfuscatedNames.MouseHandler_lastPressedTimeMillisField);
        mouseHandlerLastPressedTime.setAccessible(true);
        mouseHandlerLastPressedTime.setLong(null, time * modInverse(Long.parseLong(ObfuscatedNames.mouseHandlerMillisMultiplier)));
        mouseHandlerLastPressedTime.setAccessible(false);
    }

    @SneakyThrows
    public static void setClientLastMillis(long time) {
        Field clientLastPressedTimeMillis = client.getClass().getDeclaredField(ObfuscatedNames.clientMillisField);
        clientLastPressedTimeMillis.setAccessible(true);
        clientLastPressedTimeMillis.setLong(client, time * modInverse(Long.parseLong(ObfuscatedNames.clientMillisMultiplier)));
        clientLastPressedTimeMillis.setAccessible(false);
    }
}