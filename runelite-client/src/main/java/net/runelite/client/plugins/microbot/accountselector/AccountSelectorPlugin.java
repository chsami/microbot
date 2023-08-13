package net.runelite.client.plugins.microbot.accountselector;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigProfile;
import net.runelite.client.config.ProfileManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.WorldService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.mouse.VirtualMouse;
import net.runelite.client.plugins.microbot.util.walker.Walker;
import net.runelite.client.util.WorldUtil;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.Global.sleep;

@PluginDescriptor(
        name = "AutoLogin",
        description = "Microbot autologin plugin",
        tags = {"account", "microbot", "login"}
)
@Slf4j
public class AccountSelectorPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    Notifier notifier;
    @Inject
    ProfileManager profileManager;
    @Inject
    WorldService worldService;
    @Inject
    AccountSelectorScript accountSelectorScript;

    private ConfigProfile getProfile() {
        try (ProfileManager.Lock lock = Microbot.getProfileManager().lock()) {
            return lock.getProfiles().stream().filter(x -> x.isActive()).findFirst().get();
        }
    }

    public void setWorld(int worldNumber) {
        Microbot.getClientThread().runOnClientThread(() -> {
            net.runelite.http.api.worlds.World world = Microbot.getWorldService().getWorlds().findWorld(worldNumber);
            final net.runelite.api.World rsWorld = Microbot.getClient().createWorld();
            rsWorld.setActivity(world.getActivity());
            rsWorld.setAddress(world.getAddress());
            rsWorld.setId(world.getId());
            rsWorld.setPlayerCount(world.getPlayers());
            rsWorld.setLocation(world.getLocation());
            rsWorld.setTypes(WorldUtil.toWorldTypes(world.getTypes()));
            Microbot.getClient().changeWorld(rsWorld);
            return true;
        });
    }

    @Override
    protected void startUp() throws AWTException {
        Microbot.pauseAllScripts = false;
        Microbot.setClient(client);
        Microbot.setClientThread(clientThread);
        Microbot.setNotifier(notifier);
        Microbot.setMouse(new VirtualMouse());
        Microbot.setProfileManager(profileManager);
        Microbot.setWorldService(worldService);
        accountSelectorScript.run();
    }

    protected void shutDown() {
        accountSelectorScript.shutdown();
    }

}
