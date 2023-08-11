package net.runelite.client.plugins.microbot.example;

import net.runelite.api.Prayer;
import net.runelite.api.TileObject;
import net.runelite.api.Varbits;
import net.runelite.api.World;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.GroundItem;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;
import net.runelite.client.plugins.microbot.util.walker.pathfinder.CheckedNode;
import net.runelite.client.plugins.microbot.util.walker.pathfinder.CollisionMap;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.globval.VarbitIndices.BANK_WITHDRAW_QUANTITY;


public class ExampleScript extends Script {

    public static double version = 1.0;


    public boolean run(ExampleConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;

            try {
//                WorldPoint[] path = {
//                        new WorldPoint(3235, 3225, 0),
//                        new WorldPoint(3243, 3225, 0),
//                        new WorldPoint(3252, 3225, 0),
//                        new WorldPoint(3258, 3230, 0),
//                        new WorldPoint(3259, 3236, 0),
//                        new WorldPoint(3259, 3241, 0),
//                        new WorldPoint(3256, 3246, 0),
//                        new WorldPoint(3253, 3250, 0),
//                        new WorldPoint(3250, 3256, 0),
//                        new WorldPoint(3250, 3264, 0)
//                };
//                boolean reachedEnd = Microbot.getWalker().walkPath(path);

                // WorldArea[] blockingAreas = new WorldArea[] {new WorldArea(new WorldPoint(3085, 3333, 0), 50, 50)};
                //for (WorldPoint w: Arrays.stream(blockingAreas).findFirst().get().toWorldPointList()
                  //   ) {
                   // System.out.println(w);
                //}
              //  System.out.println(Arrays.stream(blockingAreas).anyMatch(x -> x.contains(new WorldPoint(3109, 3340, 0))));
             //   Rs2GameObject.findObjectById(11797);
/*
 WEST(-1, 0),
    EAST(1, 0),
    SOUTH(0, -1),
    NORTH(0, 1),
                net.runelite.api.NPC npc1 = Microbot.getClient().getNpcs().stream().filter(x -> x.getName().contains("YOURNPCNAMEHERE")).findFirst().orElseGet(null);
                net.runelite.a<pi.NPC npc2 = Microbot.getClient().getNpcs().stream().filter(x -> x.getName().contains("YOURNPCNAMEHERE")).findFirst().orElseGet(null);

                if (npc1 != null) {
                    int distance = 10;
                    boolean isNpcCloseToPlayer = npc1.getWorldLocation().distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) < distance;
                    if (isNpcCloseToPlayer) {
                        Rs2Prayer.turnOnMeleePrayer();
                    } else {
                        Rs2Prayer.turnOnRangedPrayer();
                    }
                } else if (npc2 != null) {
                    int distance = 10;
                    boolean isNpcCloseToPlayer = npc2.getWorldLocation().distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation()) < distance;
                    if (isNpcCloseToPlayer) {
                        Rs2Prayer.turnOnMeleePrayer();
                    } else {
                        Rs2Prayer.turnOnRangedPrayer();
                    }
                }*/

               // System.out.println(Arrays.toString(Microbot.getClient().getMenuEntries()));
//               GroundItem.loot("air rune", 14);
//                System.out.println(Rs2GameObject.findObjectById(1524));
//                TileObject tile = Rs2GameObject.findObjectByLocation(new WorldPoint(3101, 3509, 0));
//                if (tile != null) {
//                    System.out.println(tile.getId());
//                }
               // Microbot.getClientThread().runOnClientThread(() ->   { Microbot.getClient().setVarbit(4150, 1); return true;});
                //Rs2GameObject.interact(43732, "Take-10");
                //System.out.println(Arrays.toString(Microbot.getClient().getMenuEntries()));
               // Rs2Bank.depositXContains("Coins", 500);
                //System.out.println(Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(BankLocation.LUMBRIDGE_CASTLE_TOP.getWorldPoint()));
//                boolean result = Microbot.getWalker().walkTo(BankLocation.LUMBRIDGE_CASTLE_TOP.getWorldPoint());
//                Microbot.getWalker().walkTo(BankLocation.EDGEVILLE_BANK.getWorldPoint());
                //System.out.println(Microbot.getClient().getWorldType());
              //  Microbot.getWalker().walkTo(new WorldPoint(3120, 3441, 0));
              //  Rs2Equipment.useRingAction("Castle wars");
                boolean result = Microbot.getWalker().walkTo(BankLocation.EDGEVILLE_BANK.getWorldPoint());
                //System.out.println(Microbot.getWalker().canReach(new WorldPoint(3183, 3435, 0)));
//                boolean result = Microbot.getWalker().canReach(new WorldPoint(3136, 3519, 0));
                //System.out.println(result);
               /// Microbot.getWalker().walkTo(new WorldPoint(3085, 3491, 0), true);
//Microbot.hopToWorld(302);
              //  WorldArea area = Microbot.getClient().getLocalPlayer().getWorldArea();

                //System.out.println(area.canTravelInDirection(Microbot.getClient(), 0, 2));
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

}
