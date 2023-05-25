package net.runelite.client.plugins.microbot.example;

import net.runelite.api.MenuEntry;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.farming.tithefarm.farming.enums.TitheFarmMaterial;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

class RegionModel {
    public WorldPoint worldPoint;
    public int x;
    public int y;
    public boolean hasPlanted = false;

    RegionModel(WorldPoint worldPoint, int x, int y) {
        this.worldPoint = worldPoint;
        this.x = x;
        this.y = y;
    }
}

public class ExampleScript extends Script {

    public static double version = 1.0;
    public List<RegionModel> regions = new ArrayList<>();

    public boolean run(ExampleConfig config) {
        regions = new ArrayList<>(Arrays.asList(
                new RegionModel(new WorldPoint(44, 21, 0), 45, 19),
                new RegionModel(new WorldPoint(42, 25, 0), 40, 25),
                new RegionModel(new WorldPoint(43, 26, 0), 45, 25),
                new RegionModel(new WorldPoint(42, 27, 0), 40, 28),
                new RegionModel(new WorldPoint(43, 29, 0), 45, 28),
                new RegionModel(new WorldPoint(42, 30, 0), 40, 31),
                new RegionModel(new WorldPoint(43, 32, 0), 45, 31),
                new RegionModel(new WorldPoint(42, 33, 0), 40, 34),
                new RegionModel(new WorldPoint(43, 35, 0), 45, 34),
                new RegionModel(new WorldPoint(42, 39, 0), 40, 40),
                new RegionModel(new WorldPoint(43, 41, 0), 45, 40),
                new RegionModel(new WorldPoint(42, 42, 0), 40, 43),
                new RegionModel(new WorldPoint(43, 44, 0), 45, 43),
                new RegionModel(new WorldPoint(42, 45, 0), 40, 46),
                new RegionModel(new WorldPoint(43, 47, 0), 45, 46),
                new RegionModel(new WorldPoint(42, 48, 0), 40, 49),
                new RegionModel(new WorldPoint(43, 50, 0), 45, 49),
                new RegionModel(new WorldPoint(48, 50, 0), 50, 49),
                new RegionModel(new WorldPoint(48, 46, 0), 50, 46),
                new RegionModel(new WorldPoint(48, 44, 0), 50, 43),
                new RegionModel(new WorldPoint(48, 40, 0), 50, 40),
                new RegionModel(new WorldPoint(48, 34, 0), 50, 34),
                new RegionModel(new WorldPoint(48, 32, 0), 50, 31),
                new RegionModel(new WorldPoint(48, 28, 0), 50, 28),
                new RegionModel(new WorldPoint(48, 26, 0), 50, 25)));
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {

                //  Rs2Menu.setOption("Deposit-all");
                //  Rs2Npc.interact("kovac", "Hand-in");
                // System.out.println(Arrays.deepToString(Arrays.stream(Microbot.getClient().getMenuEntries()).toArray(MenuEntry[]::new)));
                for (RegionModel regionModel : regions) {
                    Microbot.getWalker().walkFastRegionCanvas(regionModel.worldPoint.getX(), regionModel.worldPoint.getY());
                    sleepUntil(() -> Microbot.isWalking());
                    Point point = new Point(regionModel.worldPoint.getX(), regionModel.worldPoint.getY());
                    sleepUntil(() -> {
                        //Microbot.getWalker().walkFastRegionCanvas(regionModel.worldPoint.getX(), regionModel.worldPoint.getY());
                        //sleep(600);
                        return  point.distanceTo(new Point(Microbot.getClient().getLocalPlayer().getWorldLocation().getRegionX(), Microbot.getClient().getLocalPlayer().getWorldLocation().getRegionY())) < 1;
                    });
                }
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
        }, 0, 100, TimeUnit.MILLISECONDS);
        return true;
    }
}
