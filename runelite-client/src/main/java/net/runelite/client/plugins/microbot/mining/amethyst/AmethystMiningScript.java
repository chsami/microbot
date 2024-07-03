package net.runelite.client.plugins.microbot.mining.amethyst;

import net.runelite.api.TileObject;
import net.runelite.api.WallObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.mining.amethyst.enums.MiningSpot;
import net.runelite.client.plugins.microbot.mining.amethyst.enums.Status;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.Comparator;
import java.util.concurrent.TimeUnit;

public class AmethystMiningScript extends Script {
    public static String version = "1.0.0";
    public static Status status = Status.IDLE;
    public static WallObject oreVein;
    private static AmethystMiningConfig config;
    private static MiningSpot miningSpot;
    private String pickAxeInInventory = "";

    public boolean run(AmethystMiningConfig config) {
        AmethystMiningScript.config = config;
        initialize();

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(this::executeTask, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }

    private void executeTask() {
        if (!super.run() || !Microbot.isLoggedIn()) {
            oreVein = null;
            miningSpot = MiningSpot.NULL;
            return;
        }

        if (pickAxeInInventory.isEmpty() && config.pickAxeInInventory()) {
            Microbot.showMessage("Pickaxe was not found in your inventory");
            sleep(5000);
            return;
        }

        if (Rs2Player.isAnimating() || Microbot.getClient().getLocalPlayer().isInteracting()) return;

        handleDragonPickaxeSpec();
        handleInventory();

        switch (status) {
            case IDLE:
                return;
            case MINING:
                handleMining();
                break;
            case BANKING:
                bankItems();
                break;
        }

    }

    private void handleDragonPickaxeSpec() {
        if (Rs2Equipment.isWearing("dragon pickaxe")) {
            Rs2Combat.setSpecState(true, 1000);
        }
    }

    private void handleInventory() {

        if (!Rs2Inventory.isFull()) {
            status = Status.MINING;
        } else if (Rs2Inventory.isFull()) {
            oreVein = null;
            miningSpot = MiningSpot.NULL;
            status = Status.BANKING;
        }
    }

    private void bankItems() {
        if (Rs2Walker.walkTo(BankLocation.MINING_GUILD.getWorldPoint()))
            bank();
    }

    private void bank() {
        TileObject bank = Rs2GameObject.findObjectById(4483);
        if (Rs2Bank.openBank(bank)) {
            sleepUntil(Rs2Bank::isOpen);
            Rs2Bank.depositAllExcept(pickAxeInInventory);
            sleep(100, 300);


            if (config.pickAxeInInventory() && !Rs2Inventory.hasItem(pickAxeInInventory)) {
                Rs2Bank.withdrawOne(pickAxeInInventory);
            }
            sleep(600);
        }
    }

    private void handleMining() {
        if (oreVein != null) return;
        if (miningSpot == MiningSpot.NULL)
            miningSpot = MiningSpot.getRandomMiningSpot();
        if (walkToMiningSpot()) {
            mineVein();
        }
    }

    private boolean mineVein() {
        if (Rs2Player.isMoving()) return false;

        WallObject closestVein = findClosestVein();
        if (closestVein == null) {
            moveToMiningSpot();
            return false;
        }

        interactWithVein(closestVein);
        return true;
    }

    private WallObject findClosestVein() {
        return Rs2GameObject.getWallObjects().stream()
                .filter(this::isVein)
                .sorted(Comparator.comparingInt(this::distanceToPlayer))
                .filter(Rs2GameObject::hasLineOfSight)
                .findFirst()
                .orElse(null);
    }

    private boolean isVein(WallObject wallObject) {
        int id = wallObject.getId();
        return id == 11388 || id == 11389;
    }

    private int distanceToPlayer(WallObject wallObject) {
        return Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(wallObject.getWorldLocation());
    }

    private void interactWithVein(WallObject vein) {
        Rs2GameObject.interact(vein);
        oreVein = vein;
        sleepUntil(Rs2Player::isAnimating);
    }

    private boolean walkToMiningSpot() {
        WorldPoint miningWorldPoint = miningSpot.getWorldPoint();
        Rs2Walker.walkTo(miningWorldPoint);
        moveToMiningSpot();
        return true;
    }

    private void moveToMiningSpot() {
        Rs2Walker.walkFastCanvas(miningSpot.getWorldPoint());
    }

    private void initialize() {
        status = Status.IDLE;
        miningSpot = MiningSpot.NULL;
        oreVein = null;
        if (config.pickAxeInInventory()) {
            pickAxeInInventory = Rs2Inventory.get("pickaxe").name;
        }
    }

}
