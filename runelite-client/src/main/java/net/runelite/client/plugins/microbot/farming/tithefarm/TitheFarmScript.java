package net.runelite.client.plugins.microbot.farming.tithefarm;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.farming.tithefarm.farming.enums.TitheFarmMaterial;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.tithefarm.TitheFarmPlant;
import net.runelite.client.plugins.tithefarm.TitheFarmPlantState;
import net.runelite.client.plugins.tithefarm.TitheFarmPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TitheFarmScript extends Script {

    final int FARM_DOOR = 27445;
    final String FERTILISER = "gricoller's fertiliser";
    static int currentPlant = 0;

    final int TOTAL_PLANTS = 14;

    public List<WorldPoint> regions = new ArrayList<>(Arrays.asList(new WorldPoint(68, 37, 0), new WorldPoint(66, 41, 0)));


    public boolean run(TitheFarmConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run()) return;
                final String seed = TitheFarmMaterial.getSeedForLevel().getName();
                boolean isInMinigame = Rs2Widget.getWidget(15794178) != null;
                int amountOfPlants = Rs2GameObject.countObjectBetween(27384, 27248);

                if (isInMinigame) {
                    DropFertiliser();
                    if (!Inventory.hasItem(seed) && TitheFarmPlugin.getPlants().size() == 0) {
                        leave();
                        return;
                    }
                    if (amountOfPlants == 0) {
                        //fillWaterCans();
                        for (WorldPoint worldPoint : regions) {
                            Microbot.getWalker().walkFastRegionCanvas(worldPoint.getX(), worldPoint.getY());
                            sleep(600);
                            sleepUntil(() -> Microbot.isWalking());
                            sleepUntil(() -> !Microbot.isWalking());
                            sleepUntil(() -> Microbot.getClient().getLocalPlayer().getWorldLocation().getRegionX() == worldPoint.getX()
                                    && Microbot.getClient().getLocalPlayer().getWorldLocation().getRegionY() == worldPoint.getY());
                        }
                    }
                } else {
                    takeSeeds();
                    if (Inventory.hasItem(seed)) {
                        enter();
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

        }, 0, 100, TimeUnit.MILLISECONDS);
        return true;
    }

    private static void DropFertiliser() {
        if (Inventory.hasItem("Gricoller's fertiliser")) {
            Inventory.drop("Gricoller's fertiliser");
        }
    }

    public void plantSeeds() {
        currentPlant = 0;
        LocalPoint startLocation = new LocalPoint(7360, 6592);
        if (!Microbot.getClient().getLocalPlayer().getLocalLocation().equals(startLocation)) {
            Point point = Perspective.localToCanvas(Microbot.getClient(), startLocation, Microbot.getClient().getPlane());
            Microbot.getMouse().click(point);
            sleepUntil(() -> Microbot.getClient().getLocalPlayer().getLocalLocation().distanceTo(startLocation) < 2);
            return;
        }
        while (TitheFarmPlugin.getPlants().size() < TOTAL_PLANTS) {
            if (!Inventory.hasItem(TitheFarmMaterial.getSeedForLevel().getName())) break;
            Inventory.useItem(TitheFarmMaterial.getSeedForLevel().getName());
            TileObject gameObject = Rs2GameObject.interactAndGetObject(ObjectID.TITHE_PATCH);
            sleepUntil(() -> Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(gameObject.getWorldLocation()) < 3);
            sleepUntilOnClientThread(() -> Microbot.getClient().getLocalPlayer().getAnimation() != -1);
            sleep(Random.random(1000, 1200));
            click(gameObject);
            sleepUntil(() -> Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(gameObject.getWorldLocation()) < 3);
            sleepUntilOnClientThread(() -> Microbot.getClient().getLocalPlayer().getAnimation() != -1);
            sleep(Random.random(2200, 2500));
        }
        runToClosestPlantThatNeedsWater();
    }

    private void runToClosestPlantThatNeedsWater() {
        TitheFarmPlant plant = TitheFarmPlugin.getPlants().stream().sorted(Comparator.comparingInt((TitheFarmPlant x) -> x.getIndex())).findFirst().get();
        if (Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(plant.getGameObject().getWorldLocation()) > 3)
            click(plant.getGameObject());
    }

    public void waterSeeds() {
        int totalFarmingExp = Microbot.getClient().getSkillExperience(Skill.FARMING);
        for (TitheFarmPlant plant :
                TitheFarmPlugin.getPlants().stream().sorted(Comparator.comparingInt((TitheFarmPlant x) -> x.getIndex())).collect(Collectors.toList())) {
            if (plant.getIndex() == currentPlant) {
                if (Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(plant.getGameObject().getWorldLocation()) > 5) {
                    click(plant.getGameObject());
                    sleepUntil(() -> Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(plant.getGameObject().getWorldLocation()) < 3);
                    break;
                }
                //if we missed a plant
                if (plant.getState() == TitheFarmPlantState.UNWATERED && plant.getIndex() < currentPlant) {
                    click(plant.getGameObject());
                    sleepUntil(() -> TitheFarmPlugin.getPlants()
                            .stream()
                            .filter(x -> x.getIndex() == currentPlant)
                            .findFirst()
                            .get().getState() == TitheFarmPlantState.WATERED);
                    break;
                }
                if (plant.getState() == TitheFarmPlantState.WATERED) {
                    currentPlant++;
                    if (currentPlant >= TOTAL_PLANTS)
                        currentPlant = 0;
                    break;
                }
                if (plant.getState() != TitheFarmPlantState.GROWN
                        && plant.getState() != TitheFarmPlantState.DEAD
                        && plant.getState() != TitheFarmPlantState.WATERED) {
                    click(plant.getGameObject());
                    sleepUntil(() -> TitheFarmPlugin.getPlants()
                            .stream()
                            .filter(x -> x.getIndex() == currentPlant)
                            .findFirst()
                            .get().getState() == TitheFarmPlantState.WATERED);
                    break;
                } else if (plant.getState() != TitheFarmPlantState.WATERED) {
                    while (totalFarmingExp == Microbot.getClient().getSkillExperience(Skill.FARMING)) {
                        click(plant.getGameObject());
                        sleepUntilOnClientThread(() -> Microbot.getClient().getLocalPlayer().getAnimation() != -1, 2000);
                        if (Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getLocalPlayer().getAnimation() != -1))
                            sleep(1000, 1200);
                    }
                    currentPlant++;
                    break;
                }
            }
        }
    }

    public void fillWaterCans() {
        if (Inventory.hasItem("Gricoller's can")) {
            Inventory.useItemSlot(0);
            Rs2GameObject.interact("Water barrel");
            sleepUntil(() -> Microbot.isAnimating());
        } else if (!Inventory.hasItemAmount("Watering can(8)", 8)) {
            Inventory.useItemSlot(0);
            Rs2GameObject.interact("Water barrel");
            sleepUntil(() -> Inventory.hasItemAmount("Watering can(8)", 8), 60000);
        }
    }

    public void shutDown() {
        super.shutdown();
    }

    public void takeSeeds() {
        GameObject seedTable = Rs2GameObject.findObject("Seed table");
        click(seedTable);
        Rs2Widget.sleepUntilHasWidget(TitheFarmMaterial.getSeedForLevel().getName());
        keyPress(TitheFarmMaterial.getSeedForLevel().getOption());
        sleep(1000);
        VirtualKeyboard.typeString("10000");
        sleep(600);
        VirtualKeyboard.enter();
        sleepUntil(() -> Inventory.hasItem(TitheFarmMaterial.getSeedForLevel().getName()));
    }

    public void enter() {
        WallObject farmDoor = Rs2GameObject.findDoor(FARM_DOOR);
        click(farmDoor);
        sleep(3000);
    }

    public void leave() {
        WallObject farmDoor = Rs2GameObject.findDoor(FARM_DOOR);
        click(farmDoor);
        sleepUntil(() -> !Inventory.hasItem(FERTILISER), 8000);
    }
}
