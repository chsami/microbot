package net.runelite.client.plugins.microbot.scripts.combat.jad;

import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.microbot.scripts.Scripts;
import net.runelite.client.plugins.microbot.util.npc.Npc;
import net.runelite.client.plugins.microbot.util.prayer.Prayer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

class JadModel {
    public net.runelite.api.NPC npc;
    public boolean hasCasted = false;

    public JadModel(net.runelite.api.NPC npc) {
        this.npc = npc;
    }
}

/**
 * Currently can kill 2 jads at the same time
 */
public class Jad extends Scripts {

    List<LocalPoint> jads = new ArrayList<>();

    @Override
    public boolean run() {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                List<net.runelite.api.NPC> npcs = Npc.getNpcs("Jaltok-jad");
                for (net.runelite.api.NPC npc : npcs
                ) {
                    System.out.println(Arrays.deepToString(jads.toArray()));
                    if (jads.stream().filter(x -> x.equals(npc.getLocalLocation())).findAny().isPresent()) {
                        continue;
                    }
                    Field field = npc.getClass().getSuperclass().getDeclaredField("cf");
                    field.setAccessible(true);
                    int value = (int) field.get(npc);
                    int realAnimation = value * -2121799935;
                    if (realAnimation == 7592) { //magic
                        jads.add(npc.getLocalLocation());
                        scheduledExecutorService.schedule(() -> {
                            jads.remove(npc.getLocalLocation());
                        }, 2000, TimeUnit.MILLISECONDS);
                        Prayer.turnOnMagePrayer();
                    } else if (realAnimation == 7593) { //range
                        jads.add(npc.getLocalLocation());
                        scheduledExecutorService.schedule(() -> {
                            jads.remove(npc.getLocalLocation());
                        }, 2000, TimeUnit.MILLISECONDS);
                        Prayer.turnOnRangePrayer();
                    }
                }
            } catch (IllegalAccessException e) {
            } catch (NoSuchFieldException e) {
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        return true;
    }
}