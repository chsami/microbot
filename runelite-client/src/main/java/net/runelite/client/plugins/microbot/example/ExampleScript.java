package net.runelite.client.plugins.microbot.example;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;


public class ExampleScript extends Script {

    public static double version = 1.0;


    public boolean run(ExampleConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                Rs2Npc.pickpocket("master farmer");
               // Rs2Magic.cast(MagicAction.VARROCK_TELEPORT);
                System.out.println(Arrays.toString(Microbot.getClient().getMenuEntries()));
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        Rs2Npc.npcInteraction = null;
    }
}
