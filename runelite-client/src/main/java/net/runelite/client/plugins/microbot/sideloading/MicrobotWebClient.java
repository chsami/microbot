package net.runelite.client.plugins.microbot.sideloading;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingInputStream;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.RuneLite;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginInstantiationException;
import net.runelite.client.plugins.config.PluginListPanel;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.SplashScreen;
import net.runelite.client.util.CountingInputStream;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Singleton
@Slf4j
public class MicrobotWebClient {

    @Inject
    private OkHttpClient client;

    @Inject
    private Gson gson;

    @Inject
    @Named("microbot.api")
    private String microbotApi;

    @Inject
    MicrobotPluginManager microbotPluginManager;

    @Inject
    ClientThread clientThread;

    @Inject
    private Provider<PluginListPanel> pluginListPanelProvider;

    public List<String> loadScriptsMetaData(String environment, String fileName) {
        Request request = new Request.Builder().url(microbotApi + "/file/list/" + environment + "?fileName=" + fileName).build();
        try (Response response = client.newCall(request).execute()) {

            if (response.code() == 401) {
                Microbot.showMessage("Unauthorized access!");
                return null;
            } else if (!response.isSuccessful()) {
                Microbot.showMessage("Something went wrong! Please contact Mocrosoft on discord!");
                return null;
            }

            String responseBody = response.body().string();
            return gson.fromJson(responseBody, new TypeToken<List<String>>() {
            }.getType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] downloadScript(String path) {
        if (path == null) return null;
        try (Response response = client.newCall(new Request.Builder().url(microbotApi + "/file/download?path=" + path)
                        .build())
                .execute()) {

            if (response.code() == 401) {
                Microbot.showMessage("Unauthorized access!");
                return null;
            } else if (!response.isSuccessful()) {
                Microbot.showMessage("Something went wrong! Please contact Mocrosoft on discord!");
                return null;
            }

            ResponseBody body = response.body();

            HashingInputStream his = new HashingInputStream(Hashing.sha256(),
                    new CountingInputStream(body.byteStream(), i ->
                    {
                        System.out.println(i);
                    }));

            int lastIndex = path.lastIndexOf('/');

            // Extract the part after the last '/'
            String fileName = path.substring(lastIndex + 1);

            Files.asByteSink(new File(RuneLite.RUNELITE_DIR, "microbot-plugins/" + fileName)).writeFrom(his);

            byte[] bytes = his.readAllBytes();

            his.close();

            SplashScreen.stage(0.8, "Finished Loading plugins", "");
            loadPlugin(bytes);
            SplashScreen.stage(0.9, "Refreshing plugins", "");

            PluginListPanel pluginListPanel = pluginListPanelProvider.get();
            pluginListPanel.rebuildPluginList();
            Microbot.getPluginManager().refreshPlugins();

            Microbot.showMessage(fileName + " has been succesfully installed.");

            return bytes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getScriptMetaDataList() {
        List<String> scripts = new ArrayList<>();
        java.util.List<String> nightlyScripts = loadScriptsMetaData("nightly", "");
        java.util.List<String> releaseScripts = loadScriptsMetaData("release", "");

        scripts.addAll(nightlyScripts);
        scripts.addAll(releaseScripts);

        return scripts;
    }

    public List<String> getScriptMetaDataList(String scriptName) {
        List<String> scripts = new ArrayList<>();
        java.util.List<String> nightlyScripts = loadScriptsMetaData("nightly", scriptName);
        java.util.List<String> releaseScripts = loadScriptsMetaData("release", scriptName);

        scripts.addAll(nightlyScripts);
        scripts.addAll(releaseScripts);

        return scripts;
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
