package net.runelite.client.plugins.microbot.playerassist.combat;

import net.runelite.api.Skill;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;

import java.util.concurrent.TimeUnit;

public class CombatPotionScript extends Script {

    public boolean run(PlayerAssistConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run()) return;
                if (!config.toggleCombatPotion()) return;
                if (Microbot.getClient().getBoostedSkillLevel(Skill.ATTACK) > 99) return;
                Widget[] widgets = Microbot.getClientThread().runOnClientThread(() -> Inventory.getPotions());
                for (Widget widget: widgets
                ) {
                    if (widget.getName().contains("combat")) {
                        Microbot.getMouse().click(widget.getBounds());
                        sleep(600, 1200);
                    }
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
