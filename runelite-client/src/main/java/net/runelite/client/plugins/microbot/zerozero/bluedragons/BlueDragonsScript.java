package net.runelite.client.plugins.microbot.zerozero.bluedragons;

import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;
import net.runelite.client.plugins.microbot.util.grounditem.LootingParameters;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.RunePouch;
import net.runelite.client.plugins.microbot.util.misc.Rs2Food;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.concurrent.TimeUnit;

public class BlueDragonsScript extends Script {

    public static BlueDragonState currentState;
    String lastChatMessage = "";
    private BlueDragonsConfig config;
    private static final WorldPoint SAFE_SPOT = new WorldPoint(2918, 9781, 0);
    private Integer currentTargetId = null;

    public boolean run(BlueDragonsConfig config) {
        currentState = BlueDragonState.BANKING;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!super.run() || !Microbot.isLoggedIn()) return;

                switch (currentState) {
                    case STARTING:
                        determineStartingState(config);
                        break;

                    case BANKING:
                        handleBanking(config);
                        break;

                    case TRAVEL_TO_DRAGONS:
                        handleTravelToDragons();
                        break;

                    case FIGHTING:
                        handleFighting(config);
                        break;
                }
            } catch (Exception ex) {
                Microbot.log(ex.getMessage());
            }
        }, 0, 100, TimeUnit.MILLISECONDS);

        return true;
    }

    private void handleBanking(BlueDragonsConfig config) {
        logOnceToChat("Traveling to Falador West bank for depositing looted items.", true, config);
        logOnceToChat("Current location: " + Microbot.getClient().getLocalPlayer().getWorldLocation(), true, config);

        if (Rs2Bank.walkToBankAndUseBank(BankLocation.FALADOR_WEST)) {
            logOnceToChat("Opened bank. Depositing loot.", true, config);
            Rs2Bank.depositAll("Dragon bones");
            Rs2Bank.depositAll("Dragon spear");
            Rs2Bank.depositAll("Shield left half");
            Rs2Bank.depositAll("Scaly blue dragonhide");

            if (config.lootEnsouledHead()) {
                Rs2Bank.depositAll("Ensouled dragon head");
            }

            if (config.lootDragonhide()) {
                Rs2Bank.depositAll("Blue dragonhide");
            }
            logOnceToChat("Withdrawing food for combat.", true, config);
            withdrawFood(config);
            Rs2Bank.closeBank();
            logOnceToChat("Banking complete. Transitioning to travel state.", true, config);
            currentState = BlueDragonState.TRAVEL_TO_DRAGONS;
        }
        else {
            logOnceToChat("Failed to reach the bank.", true, config);

        }
    }

    private void determineStartingState(BlueDragonsConfig config) {
        boolean hasTeleport = hasTeleportToFalador();
        boolean hasAgilityOrKey = Microbot.getClient().getRealSkillLevel(Skill.AGILITY) >= 70 || hasDustyKey();

        if (!hasTeleport) {
            logOnceToChat("Missing teleport to Falador or required runes.", false, config);
        }

        if (!hasAgilityOrKey) {
            logOnceToChat("Requires Agility level 70 or a Dusty Key.", false, config);
        }

        // Check if all requirements are met
        if (hasTeleport && hasAgilityOrKey) {
            currentState = BlueDragonState.BANKING;
        } else {
            logOnceToChat("Starting conditions not met. Stopping the plugin.", false, config);
            stop();
        }
    }

    private boolean hasTeleportToFalador() {
        logOnceToChat("Checking for Falador teleport or required runes.", true, config);

        if (Rs2Inventory.contains("Falador teleport")) {
            logOnceToChat("Found Falador teleport in inventory.", true, config);
            return true;
        }

        int lawRuneId = ItemID.LAW_RUNE;
        int waterRuneId = ItemID.WATER_RUNE;
        int dustRuneId = ItemID.DUST_RUNE;
        int airRuneId = ItemID.AIR_RUNE;

        int requiredLawRunes = 1;
        int requiredAirRunes = 3;
        int requiredWaterRunes = 1;

        boolean runePouchInInventory = Rs2Inventory.contains("Rune pouch") || Rs2Inventory.contains("Divine rune pouch");
        boolean hasLawRunes = checkRuneAvailability(lawRuneId, requiredLawRunes, runePouchInInventory);
        boolean hasWaterRunes = checkRuneAvailability(waterRuneId, requiredWaterRunes, runePouchInInventory);
        boolean hasAirOrDustRunes = checkRuneAvailability(dustRuneId, requiredAirRunes, runePouchInInventory) ||
                checkRuneAvailability(airRuneId, requiredAirRunes, runePouchInInventory);

        return hasLawRunes && hasWaterRunes && hasAirOrDustRunes;
    }

    private boolean checkRuneAvailability(int runeId, int requiredAmount, boolean checkRunePouch) {
        boolean inInventory = Rs2Inventory.hasItemAmount(runeId, requiredAmount);
        boolean inRunePouch = checkRunePouch && RunePouch.contains(runeId, requiredAmount);
        return inInventory || inRunePouch;
    }

    private boolean hasDustyKey() {
        return Rs2Inventory.contains("Dusty key");
    }

    private void handleTravelToDragons() {
        logOnceToChat("Traveling to dragons.", false, config);
        logOnceToChat("Player location before travel: " + Microbot.getClient().getLocalPlayer().getWorldLocation(), true, config);

        Rs2Walker.walkTo(SAFE_SPOT);
        sleepUntil(this::isPlayerAtSafeSpot);

        if (hopIfPlayerAtSafeSpot()) {
            logOnceToChat("Hopped worlds due to player detection at safe spot.", true, config);
            return;
        }
        logOnceToChat("Reached safe spot. Transitioning to FIGHTING state.", true, config);
        currentState = BlueDragonState.FIGHTING;
    }

    private void handleFighting(BlueDragonsConfig config) {
        if (!isPlayerAtSafeSpot()) {
            logOnceToChat("Not at safe spot. Moving back before continuing to fight.", true, config);
            moveToSafeSpot();
            return;
        }

        if (attemptLooting(config)) {
            return;
        }

        Rs2Player.eatAt(config.eatAtHealthPercent());

        NPC dragon = getAvailableDragon();
        if (dragon != null && attackDragon(dragon)) {
            currentTargetId = dragon.getId();

            if (!isPlayerAtSafeSpot()) {
                logOnceToChat("Attacked dragon, moving back to safe spot.", true, config);
                moveToSafeSpot();
            }
        }
    }

    private boolean attemptLooting(BlueDragonsConfig config) {
        logOnceToChat("Attempting to loot items.", true, config);

        if (Rs2Inventory.isFull()) {
            logOnceToChat("Inventory is full after looting, switching to BANKING state.", false, config);
            currentState = BlueDragonState.BANKING;
            return true;
        }

        lootItem("Dragon bones");
        lootItem("Dragon spear");
        lootItem("Shield left half");

        if (config.lootDragonhide()) {
            lootItem("Blue dragonhide");
        }

        if (config.lootEnsouledHead()) {
            lootItem("Ensouled dragon head");
        }

        if (!isPlayerAtSafeSpot()) {
            logOnceToChat("Returning to safe spot after looting.", true, config);
            moveToSafeSpot();
        }

        return false;
    }

    private void lootItem(String itemName) {
        if (!Rs2Inventory.isFull()) {
            LootingParameters params = new LootingParameters(10, 1, 1, 0, false, true, itemName);
            Rs2GroundItem.lootItemsBasedOnNames(params);
        }
    }

    private void withdrawFood(BlueDragonsConfig config) {
        Rs2Food food = config.foodType();
        int requiredAmount = config.foodAmount();

        if (food == null || requiredAmount <= 0) {
            logOnceToChat("Invalid food type or amount in configuration.", true, config);
            return;
        }

        int currentFoodInInventory = Rs2Inventory.count(food.getName());
        int deficit = requiredAmount - currentFoodInInventory;

        if (deficit <= 0) {
            logOnceToChat("Inventory already contains the required amount of " + food.getName() + ".", true, config);
            return;
        }

        if (!Rs2Bank.isOpen()) {
            logOnceToChat("Bank is not open. Cannot withdraw food.", true, config);
            return;
        }

        boolean bankLoaded = sleepUntil(() -> !Rs2Bank.bankItems().isEmpty(), 5000);
        if (!bankLoaded) {
            logOnceToChat("Bank items did not load in time.", true, config);
            return;
        }

        if (!Rs2Bank.hasItem(food.getName())) {
            logOnceToChat(food.getName() + " not found in the bank. Stopping script.", true, config);
            stop();
            return;
        }

        logOnceToChat("Attempting to withdraw " + deficit + "x " + food.getName(), false, config);

        int retryCount = 0;
        final int maxRetries = 3;
        boolean success = false;

        while (retryCount < maxRetries && !success) {
            success = Rs2Bank.withdrawX(false, food.getName(), deficit, true);
            if (!success) {
                retryCount++;
                sleep(500);
                logOnceToChat("Retrying withdrawal of " + food.getName() + " (" + retryCount + ")", true, config);
            }
        }

        if (!success) {
            logOnceToChat("Unable to withdraw " + food.getName() + " after multiple attempts. Stopping script.", true, config);
            stop();
        } else {
            logOnceToChat("Successfully withdrew " + deficit + "x " + food.getName(), false, config);
        }
    }

    private NPC getAvailableDragon() {
        NPC dragon = Rs2Npc.getNpc("Blue dragon");
        if (dragon != null && (dragon.getId() == 265 || dragon.getId() == 266)) {
            return dragon;
        }
        return null;
    }

    private boolean attackDragon(NPC dragon) {
        final int dragonId = dragon.getId();
        if (Rs2Npc.attack(dragon)) {
            sleepUntil(() -> Rs2Npc.getNpc(dragonId) == null, 5000);
            return true;
        }
        return false;
    }

    private boolean isPlayerAtSafeSpot() {
        return SAFE_SPOT.equals(Microbot.getClient().getLocalPlayer().getWorldLocation());
    }

    private void moveToSafeSpot() {
        Microbot.pauseAllScripts = true;
        Rs2Walker.walkFastCanvas(SAFE_SPOT);
        sleepUntil(this::isPlayerAtSafeSpot);

        if (hopIfPlayerAtSafeSpot()) {
            return;
        }

        Microbot.pauseAllScripts = false;
    }

    private boolean hopIfPlayerAtSafeSpot() {
        if (Rs2Player.hopIfPlayerDetected(1, 5000, 3)) {
            logOnceToChat("Player detected at safe spot. Pausing script and hopping worlds.", true, config);
            Microbot.pauseAllScripts = true;
            sleep(1000);
            Microbot.pauseAllScripts = false;
            return true;
        }
        return false;
    }

    void logOnceToChat(String message, boolean isDebug, BlueDragonsConfig config) {
        if (message == null || message.trim().isEmpty()) {
            message = "Unknown log message (null or empty)...";
        }
        if (isDebug && (config == null || !config.debugLogs())) {
            return;
        }
        if (!message.equals(lastChatMessage)) {
            Microbot.log(message);
            lastChatMessage = message;
        }
    }

    public void updateConfig(BlueDragonsConfig config) {
        logOnceToChat("Applying new configuration to Blue Dragons script.", true, config);
        withdrawFood(config);
    }

    public void stop() {
        super.shutdown();
    }
}