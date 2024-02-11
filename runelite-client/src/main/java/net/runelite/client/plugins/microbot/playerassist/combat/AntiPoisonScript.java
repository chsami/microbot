package net.runelite.client.plugins.microbot.playerassist.combat;

import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.concurrent.TimeUnit;

public class AntiPoisonScript extends Script {
    public boolean run(PlayerAssistConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run()) return;
                if (!config.useAntiPoison()) return;
                if (Rs2Player.hasAntiPoisonActive()) {
                    Inventory.open();
                    Widget[] potions = Microbot.getClientThread().runOnClientThread(Inventory::getPotions);
                    if (potions == null || potions.length == 0) {
                        return;
                    }
                    for (Widget potion: potions) {
                        if (potion.getName().toLowerCase().contains("poison")) {
                            Microbot.getMouse().click(potion.getBounds());
                            sleep(1200, 2000);
                            break;
                        }
                    }
                }
            } catch(Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }
}
