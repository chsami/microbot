package net.runelite.client.plugins.microbot.sideloading;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginInstantiationException;
import net.runelite.client.plugins.config.PluginListPanel;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.SplashScreen;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "SideLoader",
        description = "Microbot SideLoader",
        tags = {"SideLoader"},
        enabledByDefault = true
)
@Slf4j
public class SideLoadingPlugin extends Plugin {

    @Inject
    MicrobotWebClient webClient;
    @Inject
    private Provider<PluginListPanel> pluginListPanelProvider;

    @Provides
    SideLoadingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(SideLoadingConfig.class);
    }

    @Inject
    MicrobotPluginManager microbotPluginManager;

    @Inject
    ClientThread clientThread;

    @Override
    protected void startUp() throws AWTException, IOException {
        clientThread.runOnSeperateThread(() -> {
            net.runelite.client.ui.SplashScreen.init();

            SplashScreen.stage(0, "Load metadata", "");

            java.util.List<String> releaseScripts = webClient.loadScriptsMetaData("release", "");

            File[] files = microbotPluginManager.createSideloadingFolder();


            for (String script : releaseScripts) {
                SplashScreen.stage(0.4 + (script.indexOf(script) / 10), "Loading scripts", (script.indexOf(script) + 1) + "/" + releaseScripts.size());

                int lastIndex = script.lastIndexOf('/');

                // Extract the part after the last '/'
                String fileName = script.substring(lastIndex + 1);

                boolean exists = false;

                for (File file : files) {
                    if (file.getName().contains(fileName)) {
                        Microbot.log("Skipping " + fileName + " because it already exists.");
                        exists = true;
                    }
                }


                if (exists) continue;


                byte[] bytes = webClient.downloadScript(script);

                SplashScreen.stage(0.8, "Finished Loading plugins", "");
                loadPlugin(bytes);
                SplashScreen.stage(0.9, "Refreshing plugins", "");

                PluginListPanel pluginListPanel = pluginListPanelProvider.get();
                pluginListPanel.rebuildPluginList();
                Microbot.getPluginManager().refreshPlugins();

                Microbot.showMessage(releaseScripts.size() + " scripts have been succesfully installed.");
            }

            SplashScreen.stop();
            return true;
        });

    }

    protected void shutDown() {
    }

    public static String getHWID() {
        try {
            String toEncrypt = System.getenv("COMPUTERNAME") + System.getProperty("user.name") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_LEVEL");
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(toEncrypt.getBytes());
            StringBuffer hexString = new StringBuffer();

            byte byteData[] = md.digest();

            for (byte aByteData : byteData) {
                String hex = Integer.toHexString(0xff & aByteData);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }


            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }

    public void loadPlugin(byte[] jarBytes) {
        if (jarBytes == null || jarBytes.length == 0) {
            return;
        }

        Microbot.log("Side-loading plugin from byte array");

        try {

            List<Class<?>> plugins = new ArrayList<>();

            ByteArrayClassLoader classLoader = new ByteArrayClassLoader(jarBytes, getClass().getClassLoader());

            // Assuming you know the class names you want to load
            Set<String> classNamesToLoad = classLoader.classBytes.keySet();

            for (String className : classNamesToLoad) {
                try {
                    Class<?> clazz = classLoader.loadClass(className);
                    plugins.add(clazz);
                } catch (ClassNotFoundException e) {
                    Microbot.log("Class not found: " + className + " - " + e.getMessage());
                }
            }

            for (Class<?> clazz : plugins) {
                if (clazz.getSuperclass() == Plugin.class) {

                    PluginDescriptor pluginToAddDescriptor = clazz.getAnnotation(PluginDescriptor.class);

                    if (pluginToAddDescriptor == null) continue;
                    Collection<Plugin> existingPlugins = Microbot.getPluginManager().getPlugins();

                    for (Plugin plugin : existingPlugins) {

                        PluginDescriptor pluginDescriptor = plugin.getClass().getAnnotation(PluginDescriptor.class);
                        if (pluginDescriptor == null) continue;

                        if (pluginToAddDescriptor.name().equals(pluginDescriptor.name())) {
                            SwingUtilities.invokeAndWait(() ->
                            {
                                try {
                                    Microbot.getPluginManager().stopPlugin(plugin);
                                } catch (PluginInstantiationException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                            Microbot.getClientThread().runOnClientThread(() -> {
                                Microbot.getPluginManager().remove(plugin);
                                return true;
                            });
                        }
                    }
                }
            }

            Microbot.getPluginManager().loadPlugins(plugins, null);
        } catch (PluginInstantiationException | IOException ex) {
            Microbot.log("error sideloading plugin - " + ex.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
