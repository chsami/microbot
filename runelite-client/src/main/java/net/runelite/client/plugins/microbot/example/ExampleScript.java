package net.runelite.client.plugins.microbot.example;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.math.Calculations;
import net.runelite.client.plugins.microbot.util.menu.Rs2Menu;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static net.runelite.api.Constants.CHUNK_SIZE;
import static net.runelite.api.ObjectID.TALL_TREE;
import static net.runelite.api.ObjectID.TALL_TREE_14843;
import static net.runelite.client.plugins.microbot.util.Global.sleep;

public class ExampleScript extends Script {

    public static double version = 1.0;

    public boolean run(ExampleConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {

              //  Rs2Menu.setOption("Deposit-all");
              //  Rs2Npc.interact("kovac", "Hand-in");
                System.out.println(Arrays.deepToString(Arrays.stream(Microbot.getClient().getMenuEntries()).toArray(MenuEntry[]::new)));
               // Inventory.drop("weeds");
               // Rs2Npc.interact(NpcID.PRISSY_SCILLA, "pay");
             //   sleepUntil(() -> Inventory.hasItemAmountStackable("tomatoes(5)", 4));
            //    //System.out.println(Microbot.getClient().getLocalPlayer().getWorldArea().canTravelInDirection(Microbot.getClient(), 0, -3));
                //Rs2Menu.setOption("Set custom quantity");

                //Rs2Bank.useBank();
                //Rs2Npc.interact("dark wizard", "attack");
             //   Rs2Npc.pickpocket("master farmer");
//                System.out.println(Microbot.getClient().getLocalPlayer().getLocalLocation());
//                int[][][] instanceTemplateChunks = Microbot.getClient().getInstanceTemplateChunks();
//                int z = Microbot.getClient().getPlane();
//                int chunkData = instanceTemplateChunks[z][Microbot.getClient().getLocalPlayer().getLocalLocation().getSceneX() / CHUNK_SIZE][Microbot.getClient().getLocalPlayer().getLocalLocation().getSceneY() / CHUNK_SIZE];
//
//                int rotation = chunkData >> 1 & 0x3;
//                int chunkY = (chunkData >> 3 & 0x7FF) * CHUNK_SIZE;
//                int chunkX = (chunkData >> 14 & 0x3FF) * CHUNK_SIZE;
//
//                Microbot.getWalker().walkSouthLocal(() -> chunkY < 2576);
//                if (!Microbot.getClient().getLocalPlayer().isInteracting()) {
//                    NPC[] npcs = Rs2Npc.getAttackableNpcs();
//                    Rs2Npc.interact(Arrays.stream(npcs).findFirst().get().getId());
//                }
                // WorldPoint betweenYellowAndRedPortal = new WorldPoint(Microbot.getClient().getLocalPlayer().getWorldLocation().getX(), Microbot.getClient().getLocalPlayer().getWorldLocation().getY() - 40, 0);
                //  Microbot.getWalker().walkTo(betweenYellowAndRedPortal, false, false);
                //code here
                //Widget widget = Rs2Widget.findWidget(">Lobster<");
                //System.out.println(widget.getName());
                //Rs2Menu.setOption("Withdraw-14");
                ///Rs2Menu.doAction("Withdraw-14", widget.getBounds());
                //  Rs2Bank.openBank();
                //Rs2Bank.closeBank();
                //Microbot.getWalker().walkTo(new WorldPoint(3275, 3192, 0));
                //Microbot.getWalker().walkTo(new WorldPoint(3141, 9915, 0));
                //Microbot.getWalker().walkTo(new WorldPoint(3093, 3488, 0));
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }
}
