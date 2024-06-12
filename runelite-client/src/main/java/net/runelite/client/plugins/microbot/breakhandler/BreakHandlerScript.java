package net.runelite.client.plugins.microbot.breakhandler;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.security.Login;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;


public class BreakHandlerScript extends Script {
    public static String version = "1.0.0";

    public static int breakIn = -1;
    public static int breakDuration = -1;

    public static int totalBreaks = 0;

    public static Duration duration;
    public boolean run(BreakHandlerConfig config) {
        Microbot.enableAutoRunOn = false;
        breakIn = Random.random(config.timeUntilBreakStart() * 60, config.timeUntilBreakEnd() * 60);
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {

                if (breakIn > 0) {
                    breakIn--;
                    duration = Duration.between(LocalDateTime.now(),LocalDateTime.now().plusSeconds(breakIn));
                }
                if (breakDuration > 0) {
                    breakDuration--;
                    duration = Duration.between(LocalDateTime.now(),LocalDateTime.now().plusSeconds(breakDuration));
                }

                if (breakDuration <= 0 && Microbot.pauseAllScripts) {
                    Microbot.pauseAllScripts = false;
                    breakIn = Random.random(config.timeUntilBreakStart() * 60, config.timeUntilBreakEnd() * 60);
                    new Login();
                    totalBreaks++;
                    return;
                }

                if (breakIn <= 0 && !Microbot.pauseAllScripts) {
                    Microbot.pauseAllScripts = true;
                    breakDuration = Random.random(config.timeUntilBreakStart() * 60, config.timeUntilBreakEnd() * 60);
                    if (config.logoutAfterBreak())
                        Rs2Player.logout();
                }


            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
