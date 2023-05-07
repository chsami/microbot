package net.runelite.client.plugins.microbot.thieving;

import net.runelite.api.Skill;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.timers.TimersPlugin;

import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.inventory.Inventory.eat;
import static net.runelite.client.plugins.microbot.util.math.Random.random;

public class ThievingScript extends Script {

    public static double version = 1.0;

    public boolean run(ThievingConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                Widget[] foods = Microbot.getClientThread().runOnClientThread(() -> Inventory.getInventoryFood());
                if (foods.length == 0) {
                    shutdown();
                    return;
                }
                if (Inventory.hasItemAmountStackable("coin pouch", 28)) {
                    Inventory.interact("coin pouch");
                }
                if (Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS) > config.hitpoints()) {
                    if (random(1, 10) == 2)
                        sleepUntil(() -> !TimersPlugin.t.render());
                    if (Rs2Npc.interact(config.THIEVING_NPC().getName(), "pickpocket")) {
                        sleep(300, 600);
                    }
                } else {

                    for (Widget food : foods) {
                        eat(food);
                        if (random(1, 10) == 2) { //double eat
                            eat(food);
                        }
                        break;
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }
}
