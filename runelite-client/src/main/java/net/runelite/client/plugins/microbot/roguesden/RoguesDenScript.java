package net.runelite.client.plugins.microbot.roguesden;

import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.walker.WalkerState;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static net.runelite.client.plugins.microbot.roguesden.Obstacles.OBSTACLES;
import static net.runelite.client.plugins.microbot.util.Global.sleepGaussian;

public class RoguesDenScript extends Script {

    int currentObstacleIndex;
    boolean hasStaminaPotionInBank = true;

    boolean init = false;

    private void initObstacles() {
        currentObstacleIndex = 0;
        OBSTACLES = new Obstacles.Obstacle[]{
                new Obstacles.Obstacle(3048, 4997, 7251),
                new Obstacles.Obstacle(3039, 4999, "Stand"),
                new Obstacles.Obstacle(3029, 5003, "Run"),
                new Obstacles.Obstacle(3023, 5001, "Open", 7255, 0),
                new Obstacles.Obstacle(3011, 5005, "Run"),
                new Obstacles.Obstacle(3004, 5003, "Run"),
                new Obstacles.Obstacle(2988, 5004, 7240),
                new Obstacles.Obstacle(2969, 5016, "Stand"),
                new Obstacles.Obstacle(2967, 5016, "Stand"),
                new Obstacles.Obstacle(2958, 5031, 7239),
                new Obstacles.Obstacle(2958, 5035, "Stand"),
                new Obstacles.Obstacle(2962, 5050, "Stand"),
                new Obstacles.Obstacle(2963, 5056, "Run"),
                new Obstacles.Obstacle(2968, 5061, "Stand", 7246, 0),
                new Obstacles.Obstacle(2974, 5059, "Stand",7251, 0),
                new Obstacles.Obstacle(2989, 5058, "Stand"),
                new Obstacles.Obstacle(2990, 5058, "Open", 7255, 0),
                new Obstacles.Obstacle(2992, 5067, "Stand"),
                new Obstacles.Obstacle(2992, 5075, "Run"),
                new Obstacles.Obstacle(3009, 5063, "Take"),
                new Obstacles.Obstacle(3028, 5056, "Run"),
                new Obstacles.Obstacle(3028, 5047, "Walk", 1200),
                new Obstacles.Obstacle(3018, 5047, "Crack", 7237, 2000)};
    }

    public boolean run() {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;
                long startTime = System.currentTimeMillis();

                if (!init) {
                    if (!Rs2Player.getSkillRequirement(Skill.THIEVING, 80)) {
                        Microbot.log("RoguesDen script requires atleast 80 thieving...shutting down.");
                        shutdown();
                        return;
                    }
                    Rs2Camera.setPitch(Random.random(300, 383));
                    initObstacles();
                    Microbot.enableAutoRunOn = false;
                    Rs2Walker.disableTeleports = true;
                    init = true;
                }

                if (Rs2Player.isAnimating() || Rs2Player.isWalking()) return;

                boolean isInMinigame = Rs2Inventory.hasItem(ItemID.MYSTIC_JEWEL);

                if (!isInMinigame) {
                    currentObstacleIndex = 0;

                    if (storeAllItemsInBank()) return;

                    if (useStaminaPotion()) return;

                    enterMinigame();
                    return;
                }

                if (useFlashPowder()) return;


                if (clickObstacle()) return;


                long endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                System.out.println("Total time for loop " + totalTime);

            } catch (Exception ex) {
                ex.printStackTrace();
                Microbot.log(ex.fillInStackTrace().getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }

    private boolean clickObstacle() {
        int closestIndex = IntStream.range(currentObstacleIndex, OBSTACLES.length)
                .boxed()
                .min((i1, i2) -> {
                    double distanceToO1 = Rs2Player.getWorldLocation().distanceTo(OBSTACLES[i1].getTile());
                    double distanceToO2 = Rs2Player.getWorldLocation().distanceTo(OBSTACLES[i2].getTile());
                    return Double.compare(distanceToO1, distanceToO2);
                })
                .orElse(-1); // Return null if OBSTACLES array is empty

        if (closestIndex + 1 > OBSTACLES.length) {
            Microbot.log("wrong index!");
            return true;
        }

        currentObstacleIndex = closestIndex;

        Obstacles.Obstacle currentObstacle = OBSTACLES[closestIndex];
        Obstacles.Obstacle nextObstacle = OBSTACLES[closestIndex + 1];

        if (closestIndex == 0 && !Rs2Player.getWorldLocation().equals(currentObstacle.getTile())) {
            Rs2GameObject.interact(currentObstacle.getObjectId());
            sleepUntil(() -> Rs2Player.getWorldLocation().equals(currentObstacle));
            return true;
        }

        if (Rs2Player.getWorldLocation().equals(currentObstacle.getTile())) {
            // interact with nextObstacle
            handleObstacle(nextObstacle);
        } else {
            if (currentObstacle.getTile().equals(new WorldPoint(3028, 5056, 0))) { //item requirement before going to obstacle
                if (!Rs2Inventory.hasItem(ItemID.FLASH_POWDER)) {
                    handleObstacle(currentObstacle);
                    return true;
                }
            }

            // interact with currentObstacle
            handleObstacle(currentObstacle);
        }
        return false;
    }

    private boolean useFlashPowder() {
        if (Rs2Inventory.hasItem(ItemID.FLASH_POWDER) && Rs2Player.getWorldLocation().getX() < 3026) {
            Microbot.log("Stunning guard");
            if (Rs2Inventory.useItemOnNpc(ItemID.FLASH_POWDER, NpcID.ROGUE_GUARD_3191)) {
                if (Rs2Inventory.waitForInventoryChanges(5000)) {
                    handleObstacle(OBSTACLES[OBSTACLES.length - 3]);
                }
                return true;
            }
        }
        return false;
    }

    private static boolean storeAllItemsInBank() {
        if (!Rs2Inventory.isEmpty() || !Rs2Equipment.isNaked()) {
            if (Rs2Bank.openBank()) {
                Rs2Bank.depositEquipment();
                Rs2Bank.depositAll();
                sleepGaussian(1200, 400);
            }
            return true;
        }
        return false;
    }

    private boolean useStaminaPotion() {
        if (!Rs2Player.hasStaminaActive() && hasStaminaPotionInBank) {
            Microbot.log("Looking to withdraw stamina potion...");
            if (Rs2Bank.openBank()) {
                if (!Rs2Bank.isOpen()) return true;
                if (Rs2Bank.hasItem("stamina potion")) {
                    Rs2Bank.withdrawOne("stamina potion");
                    sleepUntil(() -> Rs2Inventory.hasItem("stamina potion"));
                    Rs2Inventory.interact("stamina potion", "drink");
                    sleepGaussian(600, 150);
                    return true;
                } else {
                    hasStaminaPotionInBank = false;
                    Microbot.log("No stamina potion found in the bank. Continue without it...");
                }
                Rs2Bank.depositAll();
                sleepGaussian(600, 150);
            }
        }
        return false;
    }

    private void enterMinigame() {
        WalkerState state = Rs2Walker.walkWithState(new WorldPoint(3056, 4991, 1));
        if (state == WalkerState.ARRIVED) {
            Rs2GameObject.interact(ObjectID.DOORWAY_7256);
            sleepUntil(() -> Rs2Inventory.hasItem(ItemID.MYSTIC_JEWEL));
        }
    }

    private void handleObstacle(Obstacles.Obstacle obstacle) {
        if ((obstacle.getHint().equalsIgnoreCase("run") || obstacle.getHint().equalsIgnoreCase("take")) && Rs2Player.getRunEnergy() < 10) {
            Microbot.log("Restoring run energy...");
            sleep(60_000, 120_000);
            return;
        }
        if (Random.random(1, 10) == 7) {
            Rs2Camera.angleToTile(obstacle.getTile());
        }
        if (obstacle.getObjectId() != -1) {
            Rs2GameObject.interact(obstacle.getObjectId());
                Rs2Player.waitForWalking();

        } else if (obstacle.getHint().equalsIgnoreCase("take") && !Rs2Inventory.hasItem(ItemID.FLASH_POWDER)) {
            Rs2GroundItem.loot(obstacle.getTile(), ItemID.FLASH_POWDER);
            sleepUntil(() -> Rs2Inventory.hasItem(ItemID.FLASH_POWDER));
        } else {
            if (!Rs2Walker.walkFastCanvas(obstacle.getTile())) {
                Rs2Walker.walkTo(obstacle.getTile());
            }
        }

        sleepGaussian(obstacle.getWait(), obstacle.getWait() / 4);
    }

    @Override
    public void shutdown() {
        super.shutdown();
        init = false;
    }
}
