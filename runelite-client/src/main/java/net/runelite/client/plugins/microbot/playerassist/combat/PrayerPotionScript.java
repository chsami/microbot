package net.runelite.client.plugins.microbot.playerassist.combat;

import net.runelite.api.Skill;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

public class PrayerPotionScript extends Script {
    @Inject
    Inventory inventory;
    public boolean run(PlayerAssistConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            if (!config.togglePrayerPotions()) return;
            if (Microbot.getClient().getBoostedSkillLevel(Skill.PRAYER) > 40) return;
            Inventory.open();
            Widget[] potions = Microbot.getClientThread().runOnClientThread(() -> inventory.getPotions());
            if (potions == null || potions.length == 0) {
                    Microbot.getNotifier().notify("No more prayer potions left");
                return;
            }
            for (Widget potion: potions) {
                if (potion.getName().toLowerCase().contains("prayer")) {
                    Microbot.getMouse().click(potion.getBounds());
                    sleep(1200, 2000);
                    break;
                }
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }
}
