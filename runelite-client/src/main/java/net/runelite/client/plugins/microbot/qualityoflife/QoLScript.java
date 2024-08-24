package net.runelite.client.plugins.microbot.qualityoflife;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.npc.Rs2NpcManager;

import java.util.concurrent.TimeUnit;


@Slf4j
public class QoLScript extends Script {

    public boolean run(QoLConfig config) {
        Microbot.enableAutoRunOn = false;
        try {
            Rs2NpcManager.loadJson();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) {
                }


            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 2000, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
