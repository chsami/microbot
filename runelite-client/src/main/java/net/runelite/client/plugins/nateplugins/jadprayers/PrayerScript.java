package net.runelite.client.plugins.nateplugins.jadprayers;

import net.runelite.api.NPC;
import net.runelite.api.Prayer;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.reflection.Rs2Reflection;
import net.runelite.client.plugins.nateplugins.jadprayers.util.PrayerUtil;
import net.runelite.client.plugins.nateplugins.jadprayers.util.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class PrayerScript extends Script {

    public static double version = 0.1;
    private final Map<Integer, Prayer> prayerMap = new HashMap<>();
    private Prayer shouldPray = Prayer.PROTECT_FROM_MAGIC;
    private final Util util;
    public PrayerScript(List<NPC> npcs) {
        this.util = new Util(npcs);
    }

    public boolean run(PrayerConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            if (!Microbot.isLoggedIn()) return;
            try {

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }
    @Subscribe
    public void onGameTick(GameTick event) {
        if (!Fighting()) {
            return;
        }

        Prayer nextPrayer = prayerMap.get(Microbot.getClient().getTickCount());

        if (nextPrayer != null) {
            if (shouldPray != nextPrayer && PrayerUtil.isPrayerActive(shouldPray)) {
                PrayerUtil.toggle(shouldPray);
            }
            shouldPray = nextPrayer;
        }
            autoPrayer();
    }



    private void autoPrayer() {
        if (!PrayerUtil.isPrayerActive(shouldPray)) {
            PrayerUtil.toggle(shouldPray);
        }
    }


    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        if (!Fighting()) return;
        if (event.getActor() instanceof NPC) {
            NPC npc = (NPC) event.getActor();
            if (npc.getName() != null && npc.getName().toLowerCase().contains("-jad")) {
                addJadPrayers(npc);
            }
        }
    }

    private void addJadPrayers(NPC npc) {
        int animationID = Rs2Reflection.getAnimation(npc);
        if (animationID == 2656 || animationID == 7592) {
            prayerMap.put(Microbot.getClient().getTickCount() + 2, Prayer.PROTECT_FROM_MAGIC);
        } else if (animationID == 2652 || animationID == 7593) {
            prayerMap.put(Microbot.getClient().getTickCount() + 2, Prayer.PROTECT_FROM_MISSILES);
        }
    }

  /*  private boolean inFight() {
        return Util.nameContainsNoCase("-jad").filter(npc -> !npc.getName().toLowerCase().contains("jalrek-jad") && !npc.getName().toLowerCase().contains("tzrek-jad")) != null;
    }*/
    private boolean Fighting() {
        // Filter NPCs containing "-jad" and excluding "jalrek-jad" and "tzrek-jad"
        List<NPC> filteredNpcs = util.nameContainsNoCase("-jad")
                .filter(npc -> !npc.getName().toLowerCase().contains("jalrek-jad")
                        && !npc.getName().toLowerCase().contains("tzrek-jad")).npcs;

        // Check if any NPCs match the criteria
        return !filteredNpcs.isEmpty();
    }
}

