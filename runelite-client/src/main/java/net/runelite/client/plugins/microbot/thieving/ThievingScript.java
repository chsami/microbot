package net.runelite.client.plugins.microbot.thieving;

import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.thieving.enums.ThievingNpc;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.timers.TimersPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment.getEquippedItem;
import static net.runelite.client.plugins.microbot.util.math.Random.random;

public class ThievingScript extends Script {

    public static double version = 1.2;

    public boolean run(ThievingConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                List<Rs2Item> foods = Microbot.getClientThread().runOnClientThread(Rs2Inventory::getInventoryFood);
                if (foods.isEmpty()) {

                    if (Rs2Inventory.size() > 3) {
                        Rs2Inventory.dropAllExcept(x -> x.slot <= 3);
                        return;
                    }
                    if (Rs2Bank.walkToBank()) {
                        Rs2Bank.useBank();
                        Rs2Bank.withdrawX(true, config.food().getName(), 5);
                        final Rs2Item amulet = getEquippedItem(EquipmentInventorySlot.AMULET);
                        if (amulet == null) {
                            Rs2Bank.withdrawItem(true, "dodgy necklace");
                        }
                        Rs2Bank.closeBank();
                        sleep(1000, 2000);
                        Rs2Inventory.wield("dodgy necklace");
                    }
                    return;
                }
                if (Rs2Inventory.isFull()) {
                    Rs2Inventory.dropAllExcept(x -> x.slot <= 8);
                }
                openCoinPouches(config);
                if (Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS) > config.hitpoints()) {
                    if (config.THIEVING_NPC() != ThievingNpc.NONE) {
                        if (random(1, 10) == 2)
                            sleepUntil(() -> TimersPlugin.t == null || !TimersPlugin.t.render());
                        if (Rs2Npc.interact(config.THIEVING_NPC().getName(), "pickpocket")) {
                            Microbot.status = "Pickpocketting " + config.THIEVING_NPC().getName();
                            sleep(300, 600);
                        }
                    }
                } else {
                    for (Rs2Item food : foods) {
                        Rs2Inventory.interact(food, "eat");
                        if (random(1, 10) == 2) { //double eat
                            Rs2Inventory.interact(food, "eat");
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

    private void openCoinPouches(ThievingConfig config) {
        if (Rs2Inventory.hasItemAmount("coin pouch", config.coinPouchTreshHold(), true)) {
            Rs2Inventory.interact("coin pouch", "open-all");
        }
    }
}
