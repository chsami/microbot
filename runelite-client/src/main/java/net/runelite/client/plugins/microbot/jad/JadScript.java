package net.runelite.client.plugins.microbot.jad;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;
import net.runelite.client.plugins.microbot.util.prayer.Rs2PrayerEnum;
import net.runelite.client.plugins.microbot.util.reflection.Rs2Reflection;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class JadScript extends Script {
    public static double version = 1.0;
    public static Map<Integer, Long> npcCooldowns = new HashMap<>();
    public boolean run(JadConfig config) {
        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;
                //CODE HERE

                var npcs = Rs2Npc.getNpcs("Jad", false);

                for (net.runelite.api.NPC npc: npcs.collect(Collectors.toList())) {
                    if (npc == null) continue;
                    long currentTime = System.currentTimeMillis();
                    if (npcCooldowns.containsKey(npc.getIndex())) {
                        if (currentTime - npcCooldowns.get(npc.getIndex()) < 4800) {
                            continue;
                        } else {
                            npcCooldowns.remove(npc.getIndex());
                        }
                    }
                    if ( Rs2Reflection.getAnimation(npc) == 7592) {
                        var healer = Rs2Npc.getNpcs("hurkot", false)
                                .filter(x -> x != null && x.getInteracting() != Microbot.getClient().getLocalPlayer())
                                .findFirst()
                                .orElse(null);
                        Rs2Npc.interact(healer, "attack");
                        sleep(600);
                        npcCooldowns.put(npc.getIndex(), currentTime);
                        Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MAGIC, true);
                    } else if (Rs2Reflection.getAnimation(npc) == 7593) {
                        var healer = Rs2Npc.getNpcs("hurkot", false)
                                .filter(x -> x != null && x.getInteracting() != Microbot.getClient().getLocalPlayer())
                                .findFirst()
                                .orElse(null);
                        Rs2Npc.interact(healer, "attack");
                        sleep(600);
                        npcCooldowns.put(npc.getIndex(), currentTime);
                        Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_RANGE, true);
                    }

                }

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 10, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        npcCooldowns.clear();
    }
}
