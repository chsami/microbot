package net.runelite.client.plugins.microbot.qualityoflife.scripts;

import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.qualityoflife.QoLConfig;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

public class AutoRunScript extends Script {

    @Inject
    public ConfigManager configManager;


    public boolean run(QoLConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) {
                    return;
                }
                if (Microbot.enableAutoRunOn != config.autoRun()) {
                    configManager.setConfiguration("QoL", "autoRun", Microbot.enableAutoRunOn);
                }
                if(Microbot.useStaminaPotsIfNeeded != config.autoStamina()) {
                    configManager.setConfiguration("QoL", "autoStamina", Microbot.useStaminaPotsIfNeeded);
                }
                if(Microbot.runEnergyThreshold/100 != config.staminaThreshold()) {
                    configManager.setConfiguration("QoL", "staminaThreshold", Microbot.runEnergyThreshold/100);
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    public void shutdown() {
        super.shutdown();
    }
}
