package net.runelite.client.plugins.microbot.prayerflicker;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

@PluginDescriptor(
        name = PluginDescriptor.Pumster + "Prayer Flicker",
        description = "Pumster prayer flicker",
        tags = {"prayer", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class FlickerPlugin extends Plugin {
    @Inject
    private FlickerConfig config;

    @Provides
    FlickerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(FlickerConfig.class);
    }

    public static double version = 1.0;

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private FlickerOverlay exampleOverlay;

    @Inject
    private ClientThread clientThread;


    private long tickDelayMS=85;
    private long tockDelayMS=515;

    private Timer tickTimer = new Timer("Tick Timer");
    private Timer tockTimer = new Timer("Tock Timer");

    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(exampleOverlay);
        }

    }

    @Subscribe
    public void onGameTick(GameTick gameTick)
    {
        if(!Microbot.isLoggedIn() || Microbot.pauseAllScripts) {
            return;
        }
        TimerTask delayedTick = new TimerTask() {
            public void run() {
                clientThread.invoke(() -> {
                    if(config.inCombatOnly()) {
                        if(Rs2Combat.inCombat()) {
                            Rs2Prayer.toggleQuickPrayer(false);

                        }
                    } else {
                        Rs2Prayer.toggleQuickPrayer(false);
                    }

                });
            }
        };

        TimerTask delayedTock = new TimerTask() {
            public void run() {
                clientThread.invoke(() -> {
                    if(config.inCombatOnly()) {
                        if(Rs2Combat.inCombat()) {
                            Rs2Prayer.toggleQuickPrayer(true);

                        }
                    } else {
                        Rs2Prayer.toggleQuickPrayer(true);
                    }


                });

            }
        };

        tickTimer.schedule(delayedTick,tickDelayMS);

        tockTimer.schedule(delayedTock,tockDelayMS);

    }

    protected void shutDown() {
        overlayManager.remove(exampleOverlay);
    }

}
