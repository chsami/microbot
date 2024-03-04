package net.runelite.client.plugins.microbot.example;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.MicrobotInventorySetup;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.prayer.Prayer;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;
import net.runelite.client.plugins.microbot.util.reflection.Rs2Reflection;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.vorkath.VorkathScript.togglePrayer;


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
//                var vorkath = Rs2Npc.getNpc(NpcID.VORKATH_8059);
//               var centerTile =
//                        new WorldPoint(vorkath.getWorldLocation().getX() + 3, vorkath.getWorldLocation().getY() - 5, vorkath.getWorldLocation().getPlane());
//                Microbot.getWalker().walkFastCanvas(centerTile);
//               // System.out.println(vorkath.getHealthRatio());
//                System.out.println(Microbot.getClient().getLocalPlayer().getLocalLocation().getSceneY());
               // System.out.println(Rs2Equipment.isWearing("diamond dragon bolts"));
                //System.out.println(Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(Rs2Npc.getNpc("vorkath").getWorldLocation()));
//                Microbot.getWalkerForKotlin().walkFastLocal(
//                        LocalPoint.fromScene(48, 54)
//                );
//                sleep(400, 600);
//                System.out.println(Microbot.getClient().getLocalPlayer().getLocalLocation().getSceneY());
                //sleepUntil(() -> Microbot.getClient().getLocalPlayer().getLocalLocation().getSceneY() <= 55);
//                Rs2Magic.castOn(MagicAction.EARTH_BOLT, Rs2Npc.getNpc("guard"));
                long startTime = System.currentTimeMillis();
                System.out.println( MicrobotInventorySetup.loadInventory("vorkath"));
                long endTime   = System.currentTimeMillis();
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
