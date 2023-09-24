package net.runelite.client.plugins.microbot.farming.tithefarm;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.farming.tithefarm.farming.enums.TitheFarmMaterial;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.math.Calculations;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.tithefarm.TitheFarmPlant;
import net.runelite.client.plugins.tithefarm.TitheFarmPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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

public class TitheFarmScript extends Script {

    final int FARM_DOOR = 27445;
    final String FERTILISER = "gricoller's fertiliser";

    public static List<RegionModel> regions = new ArrayList<>();

    public static int times = 4;

    public static long getPlantedPlants() {
        return regions.stream().filter(x -> x.hasPlanted).count();
    }


    public boolean run(TitheFarmConfig config) {
        reset();
        times = 4;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run()) return;
                DropFertiliser();
                final String seed = TitheFarmMaterial.getSeedForLevel().getName();
                boolean isInMinigame = Rs2Widget.getWidget(15794178) != null;

                if (isInMinigame) {
                    if (!Rs2Inventory.hasItem(seed) && TitheFarmPlugin.getPlants().size() == 0) {
                        leave();
                        return;
                    }
                    fillWaterCans();
                    times++;
                    for (RegionModel regionModel : regions) {
                        Microbot.getWalker().walkFastRegion(regionModel.worldPoint.getX(), regionModel.worldPoint.getY());
                        if (!regionModel.hasPlanted) {
                            Rs2Inventory.useItemUnsafe(TitheFarmMaterial.getSeedForLevel().getName());
                        }
                        sleepUntil(Microbot::isMoving);
                        Point point = new Point(regionModel.worldPoint.getX(), regionModel.worldPoint.getY());
                        sleepUntil(() -> point.distanceTo(new Point(Microbot.getClient().getLocalPlayer().getWorldLocation().getRegionX(), Microbot.getClient().getLocalPlayer().getWorldLocation().getRegionY())) == 0);
                        if (!regionModel.hasPlanted) {
                            clickPatch(regionModel);
                            sleepUntil(Microbot::isAnimating);
                            regionModel.hasPlanted = true;
                            sleep(600);
                        }
                        clickPatch(regionModel);
                        sleepUntil(Microbot::isAnimating);
                        WorldPoint worldPoint = WorldPoint.fromRegion(Microbot.getClient().getLocalPlayer().getWorldLocation().getRegionID(),
                                regionModel.x,
                                regionModel.y,
                                Microbot.getClient().getPlane());
                        sleepUntil(() -> {
                            GameObject gameObject = Rs2GameObject.getGameObject(LocalPoint.fromWorld(Microbot.getClient(), worldPoint.getX(), worldPoint.getY()));
                            if (gameObject.getId() == ObjectID.TITHE_PATCH) {
                                Rs2Inventory.useItemUnsafe(TitheFarmMaterial.getSeedForLevel().getName());
                                sleep(500);
                                clickPatch(regionModel);
                                sleepUntil(Microbot::isAnimating);
                                regionModel.hasPlanted = true;
                                sleep(600);
                                clickPatch(regionModel);
                                sleepUntil(Microbot::isAnimating);
                                return true;
                            }
                            ObjectComposition objectComposition = Rs2GameObject.findObject(gameObject.getId());
                            if (Microbot.getClient().getLocalPlayer().getAnimation() == 830) {
                                regionModel.hasPlanted = false;
                                sleep(600);
                                return true;
                            }
                            if (Arrays.stream(objectComposition.getActions()).allMatch(x -> x == null)) {
                                return true;
                            }
                            return false;
                        });
                    }
                } else {
                    takeSeeds();
                    if (Rs2Inventory.hasItem(seed)) {
                        enter();
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

        }, 0, 100, TimeUnit.MILLISECONDS);
        return true;
    }

    private static void clickPatch(RegionModel regionModel) {
        WorldPoint worldPoint = WorldPoint.fromRegion(Microbot.getClient().getLocalPlayer().getWorldLocation().getRegionID(),
                regionModel.x,
                regionModel.y,
                Microbot.getClient().getPlane());

        Point point = Calculations.worldToCanvas(worldPoint.getX(), worldPoint.getY());
        Microbot.getMouse().click(point);
    }

    private static void DropFertiliser() {
        if (Rs2Inventory.hasItem("Gricoller's fertiliser")) {
            Rs2Inventory.drop("Gricoller's fertiliser");
        }
    }

    private void runToClosestPlantThatNeedsWater() {
        TitheFarmPlant plant = TitheFarmPlugin.getPlants().stream().sorted(Comparator.comparingInt((TitheFarmPlant x) -> x.getIndex())).findFirst().get();
        if (Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(plant.getGameObject().getWorldLocation()) > 3)
            click(plant.getGameObject());
    }

    public void fillWaterCans() {
        if (times > 3) {
            times = 0;
            Microbot.getWalker().walkFastRegion(regions.get(0).x, regions.get(0).y);
            sleepUntil(Microbot::isMoving);
            sleepUntil(() -> !Microbot.isMoving());
            if (Rs2Inventory.hasItem("Gricoller's can")) {
                Rs2Inventory.useItemFast("Gricoller's can", "Use");
                Rs2GameObject.interact("Water barrel");
                sleepUntil(Microbot::isAnimating);
            } else if (!Rs2Inventory.hasItemAmount("Watering can(8)", 16)) {
                Rs2Inventory.useItemFast("Watering can", "Use");
                Rs2GameObject.interact("Water barrel", "Use");
                sleepUntil(() -> Rs2Inventory.hasItemAmount("Watering can(8)", 16), 60000);
            }
        }
    }

    public void shutDown() {
        super.shutdown();
    }

    public void takeSeeds() {
        GameObject seedTable = Rs2GameObject.findObject("Seed table");
        click(seedTable);
        boolean result = Rs2Widget.sleepUntilHasWidget(TitheFarmMaterial.getSeedForLevel().getName());
        if (!result) return;
        keyPress(TitheFarmMaterial.getSeedForLevel().getOption());
        sleep(1000);
        VirtualKeyboard.typeString("10000");
        sleep(600);
        VirtualKeyboard.enter();
        sleepUntil(() -> Rs2Inventory.hasItem(TitheFarmMaterial.getSeedForLevel().getName()));
    }

    public void enter() {
        WallObject farmDoor = Rs2GameObject.findDoor(FARM_DOOR);
        click(farmDoor);
        sleep(3000);
    }

    public void leave() {
        WallObject farmDoor = Rs2GameObject.findDoor(FARM_DOOR);
        click(farmDoor);
        sleepUntil(() -> !Rs2Inventory.hasItem(FERTILISER), 8000);
    }

    private void reset() {
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
    }
}
