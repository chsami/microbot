package net.runelite.client.plugins.ogPlugins.ogTest;

import net.runelite.api.Varbits;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;

import java.util.concurrent.TimeUnit;


public class TestScript extends Script {

    public static double version = 1.0;

    public boolean run(TestConfig config) {
        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                System.out.println("Varbit 2176: "+ Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getVarbitValue(2176)));
                System.out.println("Varbit 6719: "+ Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getVarbitValue(6719)));


            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }
    public void onVarbitChanged(VarbitChanged varbitChanged){
        if (varbitChanged.getVarpId() == 2176 || varbitChanged.getVarpId() == 6719)
        {
            System.out.println("Varbit: " + varbitChanged.getVarbitId() + " Varbit value: " + varbitChanged.getValue());
        }
    }
}
