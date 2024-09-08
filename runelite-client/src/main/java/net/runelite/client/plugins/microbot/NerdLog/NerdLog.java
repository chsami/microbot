package net.runelite.client.plugins.zkPlugins.NerdLog;


import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.concurrent.Executors;

@PluginDescriptor(
        name = PluginDescriptor.zuk + "Nerd Log Plugin",
        description = "Extends Logout timer past 5 min/25 mins to 6 Hours",
        tags = {"neverlog", "nerdlog"},
        enabledByDefault = false
)
@Slf4j
public class NerdLog extends Plugin {
    @Inject
    private Client client;
    private Random random = new Random();
    private long randomDelay;

    @Override
    protected void startUp() throws Exception {
        this.randomDelay = this.randomDelay();
    }

    @Override
    protected void shutDown() throws Exception {
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (this.checkIdleLogout()) {
            this.randomDelay = this.randomDelay();
            Executors.newSingleThreadExecutor().submit(this::pressKey);
        }
    }

    private boolean checkIdleLogout() {
        int idleClientTicks = this.client.getKeyboardIdleTicks();
        if (this.client.getMouseIdleTicks() < idleClientTicks) {
        }
        return (long)idleClientTicks >= this.randomDelay;
    }

    private long randomDelay() {
        return (long)clamp(Math.round(this.random.nextGaussian() * 8000.0));
    }

    private static double clamp(double val) {
        return Math.max(1.0, Math.min(13000.0, val));
    }
    private void pressKey() {
        KeyEvent keyPress = new KeyEvent(this.client.getCanvas(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_SPACE, KeyEvent.CHAR_UNDEFINED);
        this.client.getCanvas().dispatchEvent(keyPress);
        KeyEvent keyRelease = new KeyEvent(this.client.getCanvas(), KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_SPACE, KeyEvent.CHAR_UNDEFINED);
        this.client.getCanvas().dispatchEvent(keyRelease);
        KeyEvent keyTyped = new KeyEvent(this.client.getCanvas(), KeyEvent.KEY_TYPED, System.currentTimeMillis(), 0, KeyEvent.VK_SPACE, KeyEvent.CHAR_UNDEFINED);
        this.client.getCanvas().dispatchEvent(keyTyped);
    }
}
