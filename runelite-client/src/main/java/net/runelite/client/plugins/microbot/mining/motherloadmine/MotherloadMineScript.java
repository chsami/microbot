package net.runelite.client.plugins.microbot.mining.motherloadmine;

import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.Varbits;
import net.runelite.api.WallObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.mining.motherloadmine.enums.MLMMiningSpot;
import net.runelite.client.plugins.microbot.mining.motherloadmine.enums.MLMStatus;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;

import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.math.Random.random;

public class MotherloadMineScript extends Script {
    public static double version = 1.0;

    final int SACKID = 26688;

    public static MLMStatus status = MLMStatus.IDLE;

    MLMMiningSpot miningSpot = MLMMiningSpot.IDLE;
boolean emptySack = false;
    public boolean run() {
        Microbot.enableAutoRunOn = true;
        miningSpot = MLMMiningSpot.IDLE;
        status = MLMStatus.IDLE;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            if (!Microbot.isLoggedIn()) return;
            try {
                if (Microbot.isAnimating() || Microbot.getClient().getLocalPlayer().isInteracting()) {
                    return;
                }

                if (Microbot.getVarbitValue(Varbits.SACK_NUMBER) > 80 || emptySack) {
                    status = MLMStatus.EMPTY_SACK;
                } else if (!Inventory.isFull()) {
                    status = MLMStatus.MINING;
                } else if (Inventory.isFull()) {
                    miningSpot = MLMMiningSpot.IDLE;
                    if (Inventory.hasItem(ItemID.PAYDIRT)) {
                        if (Rs2GameObject.findObjectById(ObjectID.BROKEN_STRUT) != null) {
                            status = MLMStatus.FIXING_WATERWHEEL;
                        } else {
                            status = MLMStatus.DEPOSIT_HOPPER;
                        }
                    } else {
                        status = MLMStatus.BANKING;
                    }
                }

                switch (status) {
                    case IDLE:
                        //antiban
                        break;
                    case MINING:
                        if (miningSpot == MLMMiningSpot.IDLE) {
                            findRandomMiningSpot();
                        } else {
                            if (walkToMiningSpot())
                                mineVein();
                        }
                        break;
                    case EMPTY_SACK:
                        while (Microbot.getVarbitValue(Varbits.SACK_NUMBER) > 10) {
                            if (Inventory.count() <= 1) {
                                Rs2GameObject.interact(SACKID);
                                sleepUntil(Inventory::isFull, 10000);
                            }
                            bank();
                            emptySack = false;
                        }
                        status = MLMStatus.IDLE;
                        break;
                    case FIXING_WATERWHEEL:
                        Rs2GameObject.interact(ObjectID.BROKEN_STRUT);
                        break;
                    case DEPOSIT_HOPPER:
                        if (Rs2GameObject.interact(ObjectID.HOPPER_26674)) {
                            sleepUntil(() -> !Inventory.isFull());
                            if (Microbot.getVarbitValue(Varbits.SACK_NUMBER) > 50) {
                                emptySack = true;
                            }
                        }
                        break;
                    case BANKING:
                        bank();
                        break;
                }

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    private void bank() {
        if (Rs2Bank.useBank()) {
            sleepUntil(Rs2Bank::isOpen);
            Rs2Bank.depositAll();
            Rs2Bank.withdrawItem("hammer");
        }
    }

    private void findRandomMiningSpot() {
        if (random(1, 5) == 2) {
            miningSpot = MLMMiningSpot.SOUTH;
            Collections.shuffle(miningSpot.getWorldPoint());
        } else {
            miningSpot = MLMMiningSpot.WEST_LOWER;
            Collections.shuffle(miningSpot.getWorldPoint());
        }
    }

    private boolean walkToMiningSpot() {
        WorldPoint miningWorldPoint = miningSpot.getWorldPoint().get(0);
        if (Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo2D(miningWorldPoint) > 8) {
            Microbot.getWalker().walkFastCanvas(miningWorldPoint);
            return false;
        }
        return true;
    }

    private boolean mineVein() {
        WallObject closest = Rs2GameObject.getWallObjects()
                .stream()
                .filter(x -> x.getId() == 26661 || x.getId() == 26662 || x.getId() == 26663 || x.getId() == 26664)
                .sorted(Comparator.comparingInt(x -> Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(x.getWorldLocation())))
                .filter(x -> Microbot.getWalker().canInteract(x.getWorldLocation()))
                .findFirst()
                .orElse(null);

        if (closest == null) return true;

        Rs2GameObject.interact(closest);

        sleepUntil(Microbot::isAnimating);
        return false;
    }
}