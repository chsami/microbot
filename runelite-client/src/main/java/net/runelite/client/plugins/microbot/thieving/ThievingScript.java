package net.runelite.client.plugins.microbot.thieving;

import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.timers.TimersPlugin;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

                    if (Inventory.count() > 3) {
                        Inventory.dropAllStartingFrom(3);
                        return;
                    }
                    if (Rs2Bank.walkToBank()) {
                        Rs2Bank.useBank();
                        Rs2Bank.withdrawItemX(true, "monkfish", 5);
                        final ItemComposition amulet = getEquippedItem(EquipmentInventorySlot.AMULET);
                        if (amulet == null) {
                            Rs2Bank.withdrawItem(true, "dodgy necklace");
                        }
                        Rs2Bank.closeBank();
                        sleep(1000, 2000);
                        Inventory.useItem(ItemID.DODGY_NECKLACE);
                    }
                    return;
                }
                if (Inventory.isInventoryFull()) {
                    Inventory.dropAllStartingFrom(8);
                }
                if (Inventory.hasItemAmountStackable("coin pouch", 28)) {
                    Inventory.interact("coin pouch");
                }
                if (Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS) > config.hitpoints()) {
                    if (random(1, 10) == 2)
                        sleepUntil(() -> TimersPlugin.t == null || !TimersPlugin.t.render());
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

    List<String> supportedFoods = new ArrayList<>(Arrays
            .asList("monkfish", "lobster", "bass", "tuna", "swordfish", "salmon", "trout"));

    public boolean run(net.runelite.api.NPC npc) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                Widget[] foods = Microbot.getClientThread().runOnClientThread(() -> Inventory.getInventoryFood());
                if (foods.length == 0) {

                    if (Inventory.count() > 3) {
                        Inventory.dropAllStartingFrom(3);
                        return;
                    }
                    if (Rs2Bank.walkToBank()) {
                        Rs2Bank.useBank();
                        for (String supportedFood: supportedFoods) {
                            if (Microbot
                                    .getClientThread()
                                    .runOnClientThread(() -> Inventory.getInventoryFood()).length != 0)
                                break;
                            Rs2Bank.withdrawItemX(true, supportedFood, 5);
                        }
                        final ItemComposition amulet = getEquippedItem(EquipmentInventorySlot.AMULET);
                        if (amulet == null) {
                            Rs2Bank.withdrawItem(true, "dodgy necklace");
                        }
                        Rs2Bank.closeBank();
                        sleep(1000, 2000);
                        Inventory.useItem(ItemID.DODGY_NECKLACE);
                    }
                    return;
                }
                if (Inventory.isInventoryFull()) {
                    Inventory.dropAllStartingFrom(8);
                }
                if (Inventory.hasItemAmountStackable("coin pouch", 28)) {
                    Inventory.interact("coin pouch");
                }
                if (Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS) > 20) {
                    if (random(1, 10) == 2)
                        sleepUntil(() -> TimersPlugin.t == null || !TimersPlugin.t.render());
                    if (Rs2Npc.interact(npc.getName(), "pickpocket")) {
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
