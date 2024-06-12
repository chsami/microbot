package net.runelite.client.plugins.hoseaplugins.JadAutoPrayers;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.EthanApiPlugin;
import com.google.inject.Inject;
import com.google.inject.Provides;
import net.runelite.client.plugins.hoseaplugins.PiggyUtils.API.NpcUtil;
import net.runelite.client.plugins.hoseaplugins.PiggyUtils.API.PrayerUtil;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Prayer;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.util.HashMap;
import java.util.Map;

@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> Jad Auto Prayers</html>",
        description = "Automatically switches & flicks prayer at Jad (multiple jads not supported)",
        tags = {"ethan", "piggy"}
)
@Slf4j
public class JadAutoPrayersPlugin extends Plugin {

    @Inject
    private Client client;
    @Inject
    private JadAutoPrayersConfig config;

    private final Map<Integer, Prayer> prayerMap = new HashMap<>();
    private Prayer shouldPray = Prayer.PROTECT_FROM_MAGIC;

    @Provides
    private JadAutoPrayersConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(JadAutoPrayersConfig.class);
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (!inFight()) {
            return;
        }

        Prayer nextPrayer = prayerMap.get(client.getTickCount());

        if (nextPrayer != null) {
            if (shouldPray != nextPrayer && client.isPrayerActive(shouldPray)) {
                PrayerUtil.togglePrayer(shouldPray);
            }
            shouldPray = nextPrayer;
        }

        if (config.oneTickFlick()) {
            oneTickFlick();
        } else {
            autoPrayer();
        }
    }

    private void oneTickFlick() {
        if (PrayerUtil.isPrayerActive(shouldPray)) {
            PrayerUtil.togglePrayer(shouldPray);
        }

        if (config.rigourUnlocked()) {
            if (PrayerUtil.isPrayerActive(Prayer.RIGOUR)) {
                PrayerUtil.togglePrayer(Prayer.RIGOUR);
            }
            PrayerUtil.togglePrayer(Prayer.RIGOUR);
        } else {
            if (PrayerUtil.isPrayerActive(Prayer.EAGLE_EYE)) {
                PrayerUtil.togglePrayer(Prayer.EAGLE_EYE);
            }
            PrayerUtil.togglePrayer(Prayer.EAGLE_EYE);
        }

        PrayerUtil.togglePrayer(shouldPray);
    }

    private void autoPrayer() {
        if (!PrayerUtil.isPrayerActive(shouldPray)) {
            PrayerUtil.togglePrayer(shouldPray);
        }
    }


    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        if (!inFight()) return;
        if (event.getActor() instanceof NPC) {
            NPC npc = (NPC) event.getActor();
            if (npc.getName() != null && npc.getName().toLowerCase().contains("-jad")) {
                setupJadPrayers(npc);
            }
        }
    }

    private void setupJadPrayers(NPC npc) {
        int animationID = EthanApiPlugin.getAnimation(npc);
        if (animationID == 2656 || animationID == 7592) {
            prayerMap.put(client.getTickCount() + 2, Prayer.PROTECT_FROM_MAGIC);
        } else if (animationID == 2652 || animationID == 7593) {
            prayerMap.put(client.getTickCount() + 2, Prayer.PROTECT_FROM_MISSILES);
        }
    }

    private boolean inFight() {
        return NpcUtil.nameContainsNoCase("-jad")
                .filter(npc -> !npc.getName().toLowerCase().contains("jalrek-jad")
                        && !npc.getName().toLowerCase().contains("tzrek-jad"))
                .nearestToPlayer().isPresent();
    }
}
