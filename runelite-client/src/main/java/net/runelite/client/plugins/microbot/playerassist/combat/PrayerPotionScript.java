package net.runelite.client.plugins.microbot.playerassist.combat;

import net.runelite.api.Skill;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.math.Random;

import java.util.concurrent.TimeUnit;

public class PrayerPotionScript extends Script {
    public boolean run(PlayerAssistConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run()) return;
                if (!config.togglePrayerPotions()) return;
                if ((Microbot.getClient().getBoostedSkillLevel(Skill.PRAYER) * 100) /  Microbot.getClient().getRealSkillLevel(Skill.PRAYER) > Random.random(25, 30)) return;
                Inventory.open();
                Widget[] potions = Microbot.getClientThread().runOnClientThread(Inventory::getPotions);
                if (potions == null || potions.length == 0) {
                    return;
                }
                for (Widget potion: potions) {
                    if (potion.getName().toLowerCase().contains("prayer")) {
                        Microbot.getMouse().click(potion.getBounds());
                        sleep(1200, 2000);
                        break;
                    }
                }
            } catch(Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }
}
