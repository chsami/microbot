package net.runelite.client.plugins.hoseaplugins.ethanapi.Packets;


import net.runelite.client.plugins.hoseaplugins.ethanapi.PacketUtils.ObfuscatedNames;

import java.lang.reflect.Field;

public class BufferMethods {

    public static void setOffset(Object bufferInstance, int offset) {
        try {
            Field offsetField = bufferInstance.getClass().getField(ObfuscatedNames.bufferOffsetField);
            offsetField.setAccessible(true);
            offsetField.setInt(bufferInstance, offset);
            offsetField.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static int getOffset(Object bufferInstance) {
        try {
            Field offsetField = bufferInstance.getClass().getField(ObfuscatedNames.bufferOffsetField);
            offsetField.setAccessible(true);
            int offset = offsetField.getInt(bufferInstance);
            offsetField.setAccessible(false);
            return offset;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void setArray(Object bufferInstance, byte[] array) {
        try {
            Field arrayField = bufferInstance.getClass().getField(ObfuscatedNames.bufferArrayField);
            arrayField.setAccessible(true);
            arrayField.set(bufferInstance, array);
            arrayField.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static byte[] getArray(Object bufferInstance) {
        try {
            Field arrayField = bufferInstance.getClass().getField(ObfuscatedNames.bufferArrayField);
            arrayField.setAccessible(true);
            byte[] array = (byte[]) arrayField.get(bufferInstance);
            arrayField.setAccessible(false);
            return array;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeValue(String writeDescription, int value, Object bufferInstance) {
        int writeTypeMagnitude = writeDescription.contains("v") ? 0 : Integer.parseInt(writeDescription.substring(1).trim());
        byte[] arr = getArray(bufferInstance);
        int index = nextIndex(getOffset(bufferInstance));
        setOffset(bufferInstance, index);
        index = index * Integer.parseInt(ObfuscatedNames.indexMultiplier) - 1;
        //System.out.println("Index: " + index);
        switch (writeDescription.charAt(0)) {
            case 's':
                setArray(bufferInstance, writeSub(writeTypeMagnitude, value, arr, index));
                break;
            case 'a':
                setArray(bufferInstance, writeAdd(writeTypeMagnitude, value, arr, index));
                break;
            case 'r':
                setArray(bufferInstance, writeRightShifted(writeTypeMagnitude, value, arr, index));
                break;
            case 'v':
                setArray(bufferInstance, writeVar(value, arr, index));
                break;
        }
    }

    static byte[] writeSub(int subValue, int value, byte[] arr, int index) {
        arr[index] = (byte) (subValue - value);
        return arr;
    }

    static byte[] writeAdd(int addValue, int value, byte[] arr, int index) {
        arr[index] = (byte) (addValue + value);
        return arr;
    }

    static byte[] writeRightShifted(int shiftAmount, int value, byte[] arr, int index) {
        arr[index] = (byte) (value >> shiftAmount);
        return arr;
    }

    static byte[] writeVar(int value, byte[] arr, int index) {
        arr[index] = (byte) (value);
        return arr;
    }

    static public int nextIndex(int offset) {
        offset += (int) Long.parseLong(ObfuscatedNames.offsetMultiplier);
        return offset;
    }

}