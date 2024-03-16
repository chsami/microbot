package net.runelite.client.plugins.microbot.example;

import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.TileObject;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.staticwalker.WorldDestinations;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.MicrobotInventorySetup;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.reflection.Rs2Reflection;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

import static net.runelite.api.MenuAction.CC_OP;
import static net.runelite.client.plugins.microbot.util.Global.sleepUntil;


public class ExampleScript extends Script {
    public static double version = 1.0;

    public boolean run(ExampleConfig config) {
        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                /*
                 * Important classes:
                 * Inventory
                 * Rs2GameObject
                 * Rs2GroundObject
                 * Rs2NPC
                 * Rs2Bank
                 * etc...
                 */

                long startTime = System.currentTimeMillis();
//                TileObject altar = Rs2GameObject.findObjectById(ObjectID.ALTAR_40878);
//                if (altar == null) {
//                    altar = Rs2GameObject.findObjectById(ObjectID.ALTAR_13197);
//                }
//                if (altar != null) {
//                    //Rs2GameObject.interact(altar);
//                    Rs2Inventory.useUnNotedItemOnObject("bones", altar);
//                }
                Rs2Inventory.use("bones");
//                while (!Rs2Inventory.isItemSelected()) {
//                    System.out.println("item not selected");
//                }
//                BooleanSupplier s = Rs2Inventory::isItemSelected;
//                do {
//                    System.out.println("item not selected");
//                } while (!s.getAsBoolean());
                Global.sleepUntil(Rs2Inventory::isItemSelected);
                System.out.println("item selected!!!");
                Rs2Inventory.use("bones");


                long endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                System.out.println(totalTime);

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    private void eatAt(int percentage) {
        double treshHold = (double) (Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS) * 100) / Microbot.getClient().getRealSkillLevel(Skill.HITPOINTS);
        int missingHitpoints = Microbot.getClient().getRealSkillLevel(Skill.HITPOINTS) - Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS);
        if (treshHold < percentage) {
            List<Rs2Item> foods = Microbot.getClientThread().runOnClientThread(Rs2Inventory::getInventoryFood);
            for (Rs2Item food : foods) {
                if (missingHitpoints >= 40) {
                    //double eat
                    Rs2Inventory.interact(food, "eat");
                    sleep(1000);
                    Rs2Inventory.interact(Rs2Inventory.get("Cooked karambwan"), "eat");
                } else {
                    Rs2Inventory.interact(food, "eat");
                }
                break;
            }
        }
    }
}
