package net.runelite.client.plugins.microbot.scripts.minigames.tithefarm;

import net.runelite.api.GameObject;
import net.runelite.api.WallObject;
import net.runelite.api.coords.WorldArea;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.scripts.Script;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
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
    @Override
    public boolean run() {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run()) return;
                if (!Inventory.hasItem("Logavano seed") && TitheFarmPlugin.getPlants().size() == 0) {
                    if (Inventory.hasItem(LOGAVANO_FRUIT)) {
                        Rs2GameObject.interact("sack");
                        sleepUntil(() -> !Inventory.hasItem(LOGAVANO_FRUIT));
                        if (!Inventory.hasItem(LOGAVANO_FRUIT)) {
                            leave();
                        }
                    } else {
                        takeSeeds();
                    }
                }
                boolean isInMinigame = Inventory.hasItem("gricoller's fertiliser");
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
        while (TitheFarmPlugin.getPlants().size() < 12) {
            if (!Inventory.hasItem(seed)) break;
            int currentSize = TitheFarmPlugin.getPlants().size();
            Rs2GameObject.findObject(LOGAVANO_SEEDLING);
            Inventory.useItem(seed);
            GameObject gameObject = Rs2GameObject.interactAndGetObject(27383);
            sleep(2500);
            sleepUntil(() -> TitheFarmPlugin.getPlants().size() > currentSize);
            click(gameObject);
            sleep(1900, 2500);
        }
        runToClosestPlantThatNeedsWater();
    }

    private void runToClosestPlantThatNeedsWater() {
        TitheFarmPlant plant = TitheFarmPlugin.getPlants().stream().sorted(Comparator.comparingDouble(TitheFarmPlant::getPlantTimeRelative).reversed()).findFirst().get();
        if (Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(plant.getGameObject().getWorldLocation()) > 3)
            click(plant.getGameObject());
    }

    public void waterSeeds() {
        int currentSize = TitheFarmPlugin.getPlants().size();
        for (TitheFarmPlant plant :
                TitheFarmPlugin.getPlants().stream().sorted(Comparator.comparingDouble((TitheFarmPlant x) -> x.getPlantTimeRelative()).reversed()).collect(Collectors.toList())) {
            if (plant.getState() != TitheFarmPlantState.GROWN
                    && plant.getState() != TitheFarmPlantState.DEAD
                    && plant.getState() != TitheFarmPlantState.WATERED) {
                click(plant.getGameObject());
                sleepUntil(() -> Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(plant.getWorldLocation()) < 3);
                sleep(Random.random(2200, 2500));
                runToClosestPlantThatNeedsWater();
            } else if (plant.getState() != TitheFarmPlantState.WATERED) {
                click(plant.getGameObject());
                sleepUntil(() -> Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(plant.getWorldLocation()) < 3);
                sleepUntil(() -> TitheFarmPlugin.getPlants().size() < currentSize);
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
