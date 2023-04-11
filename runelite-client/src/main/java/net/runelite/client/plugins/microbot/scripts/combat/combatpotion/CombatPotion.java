package net.runelite.client.plugins.microbot.scripts.combat.combatpotion;

import net.runelite.api.Skill;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.scripts.Scripts;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;

import java.util.concurrent.TimeUnit;

public class CombatPotion extends Scripts {

    public boolean run() {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            super.run();
            if (Microbot.getClient().getBoostedSkillLevel(Skill.ATTACK) > 99) return;
            Widget[] widgets = Microbot.getClientThread().runOnClientThread(() -> Inventory.getPotions());
            for (Widget widget: widgets
                 ) {
                if (widget.getName().contains("combat")) {
                    Microbot.getMouse().click(widget.getBounds());
                    sleep(600, 1200);
                }
            }

        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }

    public void shutdown() {
        super.shutdown();
    }

}
