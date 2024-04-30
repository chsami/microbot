package net.runelite.client.plugins.microbot.mining.motherloadmine;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.mining.motherloadmine.enums.MLMMiningSpot;
import net.runelite.client.plugins.microbot.mining.motherloadmine.enums.MLMStatus;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.math.Random.random;
import static net.runelite.client.plugins.natepainthelper.Info.*;

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
        emptySack = false;

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            if (!Microbot.isLoggedIn()) return;
            if (expstarted == 0) {
                expstarted = Microbot.getClient().getSkillExperience(Skill.MINING);
                startinglevel = Microbot.getClient().getRealSkillLevel(Skill.MINING);
                timeBegan = System.currentTimeMillis();
            }
            try {
                if (Microbot.isAnimating() || Microbot.getClient().getLocalPlayer().isInteracting()) {
                    sleep(2000);
                    return;
                }

                if (!Rs2Inventory.hasItem("hammer"))
                {
                    bank();
                    return;
                }

                if (Microbot.getVarbitValue(Varbits.SACK_NUMBER) > 80 || (emptySack && !Rs2Inventory.contains("pay-dirt"))) {
                    status = MLMStatus.EMPTY_SACK;
                } else if (!Rs2Inventory.isFull()) {
                    status = MLMStatus.MINING;
                } else if (Rs2Inventory.isFull()) {
                    miningSpot = MLMMiningSpot.IDLE;
                    if (Rs2Inventory.hasItem(ItemID.PAYDIRT)) {
                        if (Rs2GameObject.findObjectById(ObjectID.BROKEN_STRUT) != null && Rs2Inventory.hasItem("hammer")) {
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
                            if (Rs2Inventory.size() <= 1) {
                                Rs2GameObject.interact(SACKID);
                                sleepUntil(() -> Rs2Inventory.size() > 1, 10000);
                            }
                            bank();
                        }
                        emptySack = false;
                        status = MLMStatus.IDLE;
                        break;
                    case FIXING_WATERWHEEL:
                            Rs2GameObject.interact(ObjectID.BROKEN_STRUT);
                        break;
                    case DEPOSIT_HOPPER:
                        if (Rs2GameObject.interact(ObjectID.HOPPER_26674)) {
                            sleepUntil(() -> !Rs2Inventory.isFull());
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
            sleep(100, 300);
            Rs2Bank.withdrawOne("hammer", true);
            sleep(600);
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
            Rs2Walker.walkTo(miningWorldPoint);
            return false;
        }
        return true;
    }

    private boolean mineVein() {
        WallObject closest = Rs2GameObject.getWallObjects()
                .stream()
                .filter(x -> x.getId() == 26661 || x.getId() == 26662 || x.getId() == 26663 || x.getId() == 26664)
                .sorted(Comparator.comparingInt(x -> Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(x.getWorldLocation())))
                .filter(x -> Rs2GameObject.hasLineOfSight(x))
                .findFirst()
                .orElse(null);

        if (closest == null) return true;

        Rs2GameObject.interact(closest);

        sleepUntil(Microbot::isAnimating);
        return false;
    }
}