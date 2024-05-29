package net.runelite.client.plugins.microbot.bossassist;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Preferences;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

@PluginDescriptor(
        name = PluginDescriptor.Pumster + "Boss Assist",
        description = "Helps u defeat bosses legit",
        tags = {"pumster", "assist", "bossing"},
        enabledByDefault = false
)
@Slf4j
public class BossAssistPlugin extends Plugin {
    @Inject
    public BossAssistConfig config;
    @Provides
    BossAssistConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BossAssistConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BossAssistOverlay exampleOverlay;

    @Inject
    private ClientThread clientThread;

    @Inject
    BossAssistScript bossAssistScript;


    @Subscribe
    public void onGameTick(GameTick gameTick)
    {
        bossAssistScript.handlePrayers();
//        TimerTask delayedTick = new TimerTask() {
//            public void run() {
//                clientThread.invoke(() -> {
//                    //System.out.println("Tick");
//                    System.out.println(Rs2Prayer.isQuickPrayerEnabled());
//                    Rs2Prayer.toggleQuickPrayer(false);
//
//                });
//            }
//        };
//
//        TimerTask delayedTock = new TimerTask() {
//            public void run() {
//                clientThread.invoke(() -> {
//                    Rs2Prayer.toggleQuickPrayer(true);
//
//
//                });
//
//            }
//        };
//
//        tickTimer.schedule(delayedTick,tickDelayMS);
//
//        tockTimer.schedule(delayedTock,tockDelayMS);

    }


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(exampleOverlay);
        }
        bossAssistScript.run(config);
    }

    protected void shutDown() {
        bossAssistScript.shutdown();
        overlayManager.remove(exampleOverlay);
    }
}
