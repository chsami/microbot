package net.runelite.client.plugins.microbot.scripts.combat.prayer;

import net.runelite.api.Skill;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.scripts.Scripts;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;

import java.util.concurrent.TimeUnit;

public class PrayerPotion extends Scripts {
    public boolean run() {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            super.run();
            if (Microbot.getClient().getBoostedSkillLevel(Skill.PRAYER) > 40) return;
            openInventory();
            Widget[] potions = Microbot.getClientThread().runOnClientThread(() -> Inventory.getPotions());
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
