package net.runelite.client.plugins.microbot.breakhandler;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.concurrent.TimeUnit;


public class BreakHandlerScript extends Script {
    public static double version = 1.0;

    public static net.runelite.api.NPC npc = null;

    public String test() {
        return "hello world from test";
    }

    public boolean run(BreakHandlerConfig config) {
        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {

                int timeUntilBreak = Random.random(1, 10);

                int breakDuration = Random.random(1, 5);


                Thread.sleep(timeUntilBreak * 60 * 1000);

                Microbot.pauseAllScripts = true;

                System.out.println("Start break");

                Rs2Player.logout();

                Thread.sleep(breakDuration * 60 * 1000);



            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 300, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
