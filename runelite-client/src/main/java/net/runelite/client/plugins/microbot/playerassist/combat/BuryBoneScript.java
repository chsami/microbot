package net.runelite.client.plugins.microbot.playerassist.combat;

import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class BuryBoneScript extends Script {
    public boolean run(PlayerAssistConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;
                if (!config.toggleBuryBones()) return;
                // check if we have any bones in inventory and bury them
                List<Rs2Item> bones = Rs2Inventory.getBones();
                if (bones == null || bones.isEmpty()) return;

                for (Rs2Item bone: bones) {
                    Rs2Inventory.interact(bone, "bury");
                    sleep(1200, 2000);
                    break;
                }

            } catch(Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }

    public void shutdown() {
        super.shutdown();
    }
}
