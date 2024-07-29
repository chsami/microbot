package net.runelite.client.plugins.microbot.woodcutting;

import net.runelite.api.AnimationID;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tile.Rs2Tile;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.woodcutting.enums.WoodcuttingWalkBack;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

enum State {
    RESETTING,
    WOODCUTTING,
}

public class AutoWoodcuttingScript extends Script {

    public static String version = "1.6.0";
    public boolean cannotLightFire = false;

    State state = State.WOODCUTTING;
    private WorldPoint returnPoint;

    public boolean run(AutoWoodcuttingConfig config) {
        if (config.hopWhenPlayerDetected()) {
            Microbot.showMessage("Make sure autologin plugin is enabled and randomWorld checkbox is checked!");
        }
        initialPlayerLocation = null;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {

                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;

                if (initialPlayerLocation == null) {
                    initialPlayerLocation = Rs2Player.getWorldLocation();
                }

                if (!config.TREE().hasRequiredLevel()) {
                    Microbot.showMessage("You do not have the required woodcutting level to cut this tree.");
                    shutdown();
                    return;
                }

                if (Rs2Player.isMoving() || Rs2Player.isAnimating() || Microbot.pauseAllScripts) return;

                switch (state) {
                    case WOODCUTTING:
                        if (config.hopWhenPlayerDetected()) {
                            Rs2Player.logoutIfPlayerDetected(1, 10);
                            return;
                        }

                        if (Rs2Equipment.isWearing("Dragon axe"))
                            Rs2Combat.setSpecState(true, 1000);

                        if (Rs2Inventory.isFull()) {
                            state = State.RESETTING;
                            return;
                        }

                        GameObject tree = Rs2GameObject.findObject(config.TREE().getName(), true, config.distanceToStray(), true, getInitialPlayerLocation());

                        if (tree != null) {
                            if(Rs2GameObject.interact(tree, config.TREE().getAction())) {
                                Rs2Player.waitForAnimation();
                                if (config.walkBack().equals(WoodcuttingWalkBack.LAST_LOCATION)) {
                                    returnPoint = Microbot.getClient().getLocalPlayer().getWorldLocation();
                                }
                            }
                        }
                        break;
                    case RESETTING:
                        resetInventory(config);
                        break;
                }
            } catch (Exception ex) {
                Microbot.log(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    private void resetInventory(AutoWoodcuttingConfig config) {
        switch (config.resetOptions()) {
            case DROP:
                Rs2Inventory.dropAllExcept("axe", "tinderbox");
                state = State.WOODCUTTING;
                break;
            case BANK:
                List<String> itemNames = Arrays.stream(config.itemsToBank().split(",")).map(String::toLowerCase).collect(Collectors.toList());

                if (!Rs2Bank.bankItemsAndWalkBackToOriginalPosition(itemNames, calculateReturnPoint(config)))
                    return;

                state = State.WOODCUTTING;
                break;
            case FIREMAKE:
                do {
                    burnLog(config);
                }
                while (Rs2Inventory.contains(config.TREE().getLog()));

                walkBack(config);
                state = State.WOODCUTTING;
                break;
        }
    }

    private void burnLog(AutoWoodcuttingConfig config) {
        WorldPoint fireSpot;
        if (Rs2Player.isStandingOnGameObject() || cannotLightFire) {
            fireSpot = fireSpot(1);
            Rs2Walker.walkFastCanvas(fireSpot);
            cannotLightFire = false;
        }
        if (!isFiremake()) {
            Rs2Inventory.use("tinderbox");
            sleep(Random.random(300, 600));
            Rs2Inventory.use(config.TREE().getLog());
            sleepUntil(Rs2Inventory::waitForInventoryChanges);
        }
        sleepUntil(() -> !isFiremake() && !Rs2Player.isStandingOnGameObject() && !Rs2Player.isStandingOnGroundItem(), 2500);
    }

    private WorldPoint fireSpot(int distance) {
        List<WorldPoint> worldPoints = Rs2Tile.getWalkableTilesAroundPlayer(distance);

        for (WorldPoint walkablePoint : worldPoints) {
            if (Rs2GameObject.getGameObject(walkablePoint) == null) {
                return walkablePoint;
            }
        }

        fireSpot(distance + 1);

        return null;
    }

    private boolean isFiremake() {
        return Rs2Player.getAnimation() == AnimationID.FIREMAKING;
    }

    private WorldPoint calculateReturnPoint(AutoWoodcuttingConfig config) {
        if (config.walkBack().equals(WoodcuttingWalkBack.LAST_LOCATION)) {
            return returnPoint;
        } else {
            return initialPlayerLocation;
        }
    }

    private void walkBack(AutoWoodcuttingConfig config) {
        Rs2Walker.walkTo(new WorldPoint(calculateReturnPoint(config).getX() - Random.random(-1, 1), calculateReturnPoint(config).getY() - Random.random(-1, 1), calculateReturnPoint(config).getPlane()));
        sleepUntil(() -> Rs2Player.getWorldLocation().distanceTo(calculateReturnPoint(config)) <= 4);
    }
}