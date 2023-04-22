package net.runelite.client.plugins.microbot.scripts.minigames.tithefarm;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.scripts.Script;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.tithefarm.TitheFarmPlant;
import net.runelite.client.plugins.tithefarm.TitheFarmPlantState;
import net.runelite.client.plugins.tithefarm.TitheFarmPlugin;

import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TitheFarmScript extends Script {

    final int FARM_DOOR = 27445;
    final String seed = "Logavano seed";
    final String LOGAVANO_SEEDLING = "Logavano seedling";
    final String LOGAVANO_FRUIT = "Logavano fruit";

    final String FERTILISER = "gricoller's fertiliser";
    static int currentPlant = 0;

    final int TOTAL_PLANTS = 14;

    @Override
    public boolean run() {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run()) return;
                boolean isInMinigame = Inventory.hasItem("gricoller's fertiliser");
                if (!Inventory.hasItem("Logavano seed") && TitheFarmPlugin.getPlants().size() == 0) {
                    if (Inventory.hasItem(LOGAVANO_FRUIT)) {
                        Rs2GameObject.interact("sack");
                        sleepUntil(() -> !Inventory.hasItem(LOGAVANO_FRUIT));
                        if (!Inventory.hasItem(LOGAVANO_FRUIT)) {
                            leave();
                        }
                    } else if (isInMinigame) {
                        leave();
                    } else {
                        takeSeeds();
                    }
                }
                if (hasItem(seed) && !isInMinigame) {
                    enter();
                }
                if (isInMinigame) {
                    if (TitheFarmPlugin.getPlants().isEmpty()) {
                        fillWaterCans();
                        plantSeeds();
                    } else {
                        waterSeeds();
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }

    public void plantSeeds() {
        currentPlant = 0;
        LocalPoint startLocation = new LocalPoint(7360, 6592);
        if (!Microbot.getClient().getLocalPlayer().getLocalLocation().equals(startLocation)) {
            Point point = Perspective.localToCanvas(Microbot.getClient(), startLocation, Microbot.getClient().getPlane());
            Microbot.getMouse().click(point);
            sleepUntil(() -> Microbot.getClient().getLocalPlayer().getLocalLocation().distanceTo(startLocation)  < 2);
            return;
        }
        while (TitheFarmPlugin.getPlants().size() < TOTAL_PLANTS) {
            if (!Inventory.hasItem(seed)) break;
            Inventory.useItem(seed);
            GameObject gameObject = Rs2GameObject.interactAndGetObject(ObjectID.TITHE_PATCH);
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
        if (Inventory.hasItemAmount("Watering can(8)", 8)) return;
        Inventory.useItemSlot(0);
        Rs2GameObject.interact("Water barrel");
        sleepUntil(() -> Inventory.hasItemAmount("Watering can(8)", 8), 60000);
    }

    public void shutDown() {
        super.shutdown();
    }

    public void takeSeeds() {
        GameObject seedTable = Rs2GameObject.findObject("Seed table");
        click(seedTable);
        sleepUntilHasWidget("level 74");
        keyPress('3');
        sleep(3000);
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
