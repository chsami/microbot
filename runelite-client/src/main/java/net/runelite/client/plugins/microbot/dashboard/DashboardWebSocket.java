package net.runelite.client.plugins.microbot.dashboard;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginInstantiationException;
import net.runelite.client.plugins.microbot.Microbot;

import javax.inject.Singleton;
import java.util.ArrayList;


@Slf4j
@Singleton
public class DashboardWebSocket {

    @Getter
    public static HubConnection hubConnection;
    @Getter
    public static String token;

    /**
     * Start websocket connection with dashboard
     * @param config dashboard Config
     */
    public static void start(DashboardConfig config) {
        if (hubConnection != null) {
            hubConnection.start().blockingAwait();
            Microbot.showMessage("Connection to https://microbot-dashboard.vercel.app opened!");
        }
        token = config.token();
        hubConnection = HubConnectionBuilder
                .create("http://localhost:5029/microbot")
                .withHeader("token", config.token())
                .build();

        hubConnection.on("ReceiveMessage", (user, message) -> Microbot.showMessage("Received a message: " + message + " from: " + user), String.class, String.class);

        hubConnection.start().blockingAwait();

        hubConnection.invoke("AddToGroup", config.token()).blockingAwait();

        SendPluginList();
    }

    /**
     *
     *
     */
    public static void stop() {
        hubConnection.invoke("RemoveFromGroup", token).blockingAwait();
        hubConnection.stop().blockingAwait();
        Microbot.showMessage("Connection to https://microbot-dashboard.vercel.app closed!");
    }

    /**
     *
     */
    public static void SendPluginList() {
        if (!isDashboardPluginEnabled()) return;
        Microbot.setBotPlugins(new ArrayList<>());
        for (Plugin plugin : Microbot.getPluginManager().getPlugins()) {
            if (plugin.getName().startsWith("<html>")) {
                Microbot.getBotPlugins().add(new PluginRequestModel(plugin.getName()
                        .split("<[^>]*>|\\[|]")[plugin.getName().split("<[^>]*>|\\[|]").length - 1]
                        .trim(), Microbot.getPluginManager().isPluginEnabled(plugin)));
            }
        }
        hubConnection.invoke("SendBotPlugins", new SendBotPluginRequestModel(token, Microbot.getBotPlugins())).blockingAwait();
    }

    /**
     *
     * @param name plugin name
     * @return the plguin
     */
    public static Plugin findPlugin(String name) {
        return Microbot.getPluginManager().getPlugins().stream().filter(x -> x.getName().equals(name)).findFirst().orElse(null);
    }

    /**
     * NOT IMPLEMENTED
     * @param pluginRequestModel requestModel
     * @throws PluginInstantiationException exception
     */
    public static void startPlugin(PluginRequestModel pluginRequestModel) throws PluginInstantiationException {
        Plugin plugin = findPlugin(pluginRequestModel.name);
        if (plugin == null) return;

        Microbot.getPluginManager().startPlugin(plugin);
    }

    /**
     * NOT IMPLEMENTED
     * @param pluginRequestModel requestModel
     * @throws PluginInstantiationException exception
     */
    public static void stopPlugin(PluginRequestModel pluginRequestModel) throws PluginInstantiationException {
        Plugin plugin = findPlugin(pluginRequestModel.name);
        if (plugin == null) return;

        Microbot.getPluginManager().stopPlugin(plugin);
    }

    private static boolean isDashboardPluginEnabled() {
        Plugin dashboard = Microbot.getPluginManager().getPlugins().stream()
                .filter(x -> x.getName().contains("dashboard"))
                .findFirst()
                .orElse(null);

        if (dashboard == null) return false;

        return Microbot.getPluginManager().isPluginEnabled(dashboard);
    }


}
