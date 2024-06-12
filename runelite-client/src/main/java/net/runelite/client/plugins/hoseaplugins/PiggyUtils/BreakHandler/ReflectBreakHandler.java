package net.runelite.client.plugins.hoseaplugins.PiggyUtils.BreakHandler;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginManager;

import com.google.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Reflection utility for Owain's Chin Break Handler:
 * https://github.com/Owain94/OpenOSRS-external-plugins/tree/master/chinbreakhandler
 * <p>
 * Let's you 'optionally' use it, without the dependency in your plugins code.
 * Author: Soxs
 */
@Slf4j
@Singleton
public class ReflectBreakHandler {

    @Inject
    private PluginManager pluginManager;

    private Object instance = null;
    private HashMap<String, Method> chinMethods = null;
    private boolean chinBreakHandlerInstalled = true;

    public void registerPlugin(Plugin p, boolean configure) {
        performReflection("registerPlugin2", p, configure);
    }

    public void registerPlugin(Plugin p) {
        performReflection("registerPlugin1", p);
    }

    public void unregisterPlugin(Plugin p) {
        performReflection("unregisterPlugin1", p);
    }

    public void startPlugin(Plugin p) {
        performReflection("startPlugin1", p);
    }

    public void stopPlugin(Plugin p) {
        performReflection("stopPlugin1", p);
    }

    public boolean isBreakActive(Plugin p) {
        Object o = performReflection("isBreakActive1", p);
        if (o != null)
            return (boolean) o;
        return false;
    }

    public boolean shouldBreak(Plugin p) {
        Object o = performReflection("shouldBreak1", p);
        if (o != null) {
            boolean b = (boolean) o;
            return (boolean) o;
        }
        return false;
    }

    public boolean needsBankPin(Client c) {
        Object o = performReflection("needsBankPin1", c);
        if (o != null) {
            return (boolean) o;
        }

        return false;
    }

    public String getBankPin(ConfigManager configManager) {
        Object o = performReflection("getBankPin1", configManager);
        if (o != null) {
            return (String) o;
        }

        return null;
    }

    public void startBreak(Plugin p) {
        performReflection("startBreak1", p);
    }

    private Object performReflection(String methodName, Object... args) {
        if (checkReflection() && chinMethods.containsKey(methodName = methodName.toLowerCase()))
            try {
                return chinMethods.get(methodName).invoke(instance, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        return null;
    }

    private boolean checkReflection() {

        if (!chinBreakHandlerInstalled) {
            return false;
        }

        if (chinMethods != null && instance != null) {
            return true;
        }

        chinMethods = new HashMap<>();
        for (Plugin p : pluginManager.getPlugins()) {
            if (p.getClass().getSimpleName().toLowerCase().equals("chinbreakhandlerplugin")) {
                for (Field f : p.getClass().getDeclaredFields()) {
                    if (f.getName().toLowerCase().equals("chinbreakhandler")) {
                        f.setAccessible(true);
                        try {
                            instance = f.get(p);
                            for (Method m : instance.getClass().getDeclaredMethods()) {
                                m.setAccessible(true);
                                chinMethods.put(m.getName().toLowerCase() + m.getParameterCount(), m);
                            }
                            return true;
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                }
            }
        }
        chinBreakHandlerInstalled = false;
        return false;
    }

}