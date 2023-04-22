package net.runelite.client.plugins.microbot.scripts.movie;

import net.runelite.api.*;
import net.runelite.client.plugins.microbot.scripts.Script;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

public class UsernameHiderScript extends Script {

    @Override
    public boolean run() {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run()) return;
                NPC npc = Rs2Npc.getNpc("Hill giant");
                Field field = npc.getClass().getSuperclass().getDeclaredField("ct");
                field.setAccessible(true);
                int value = (int) field.get(npc);
                int realAnimation = value * -1372355773;
                System.out.println(realAnimation);
              //  System.out.println(Microbot.getVarbitValue(4895));
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }

    public void shutDown() {
        super.shutdown();
    }
}
