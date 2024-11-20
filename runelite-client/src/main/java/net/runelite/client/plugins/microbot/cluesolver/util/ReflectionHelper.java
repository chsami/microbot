package net.runelite.client.plugins.microbot.cluesolver.util;

import java.lang.reflect.Field;

public class ReflectionHelper {

    public static Field getFieldFromClassHierarchy(Class<?> clazz, String fieldName) {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

}


