package net.runelite.client.plugins.microbot.jadautoprayers;


import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;
import net.runelite.client.plugins.microbot.util.prayer.Rs2PrayerEnum;
import net.runelite.client.plugins.microbot.util.reflection.Rs2Reflection;

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

    private final Map<Integer, Rs2PrayerEnum> prayerMap = new HashMap<>();
    private Rs2PrayerEnum shouldPray = Rs2PrayerEnum.PROTECT_MAGIC;

    @Provides
    private JadAutoPrayersConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(JadAutoPrayersConfig.class);
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (!inFight()) {
            return;
        }

        Rs2PrayerEnum nextPrayer = prayerMap.get(client.getTickCount());

        if (nextPrayer != null) {
            if (shouldPray != nextPrayer && Rs2Prayer.isPrayerActive(shouldPray)) {
                Rs2Prayer.toggle(shouldPray, true);
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
        if (Rs2Prayer.isPrayerActive(shouldPray)) {
            Rs2Prayer.toggle(shouldPray);
        }

        if (config.rigourUnlocked()) {
            if (Rs2Prayer.isPrayerActive(Rs2PrayerEnum.RIGOUR)) {
                Rs2Prayer.toggle(Rs2PrayerEnum.RIGOUR);
            }
            Rs2Prayer.toggle(Rs2PrayerEnum.RIGOUR);
        } else {
            if (Rs2Prayer.isPrayerActive(Rs2PrayerEnum.EAGLE_EYE)) {
                Rs2Prayer.toggle(Rs2PrayerEnum.EAGLE_EYE);
            }
            Rs2Prayer.toggle(Rs2PrayerEnum.EAGLE_EYE);
        }

        Rs2Prayer.toggle(shouldPray);
    }

    private void autoPrayer() {
        if (!Rs2Prayer.isPrayerActive(shouldPray)) {
            Rs2Prayer.toggle(shouldPray);
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
        int animationID = Rs2Reflection.getAnimation(npc);
        if (animationID == 2656 || animationID == 7592) {
            prayerMap.put(client.getTickCount() + 2, Rs2PrayerEnum.PROTECT_MAGIC);
        } else if (animationID == 2652 || animationID == 7593) {
            prayerMap.put(client.getTickCount() + 2, Rs2PrayerEnum.PROTECT_RANGE);
        }
    }

    private boolean inFight() {
        return Rs2Npc.getNpc("-jad") != null
                && Rs2Npc.getNpc("jalrek-jad") == null
                && Rs2Npc.getNpc("tzrek-jad") == null;
    }
}
