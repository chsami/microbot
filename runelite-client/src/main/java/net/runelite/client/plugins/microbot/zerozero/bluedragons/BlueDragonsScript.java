package net.runelite.client.plugins.microbot.zerozero.bluedragons;

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
import net.runelite.client.plugins.microbot.util.magic.Runes;
import net.runelite.client.plugins.microbot.util.misc.Rs2Food;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.concurrent.TimeUnit;

public class BlueDragonsScript extends Script {

    public static BlueDragonState currentState;

    private static final WorldPoint SAFE_SPOT = new WorldPoint(2918, 9781, 0);
    private final int[] dragonIds = {265, 266};
    private Integer currentTargetId = null;

    public boolean run(BlueDragonsConfig config) {
        currentState = BlueDragonState.STARTING;
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
        Microbot.log("Traveling to Falador West bank for depositing looted items.");

        if (Rs2Bank.walkToBankAndUseBank(BankLocation.FALADOR_WEST)) {
            Rs2Bank.depositAll("Dragon bones");
            Rs2Bank.depositAll("Dragon spear");
            Rs2Bank.depositAll("Shield left half");

            if (config.lootEnsouledHead()) {
                Rs2Bank.depositAll("Ensouled dragon head");
            }

            if (config.lootDragonhide()) {
                Rs2Bank.depositAll("Blue dragonhide");
            }

            withdrawFood(config);
            Rs2Bank.closeBank();
            currentState = BlueDragonState.TRAVEL_TO_DRAGONS;
        }
    }

    private void determineStartingState(BlueDragonsConfig config) {
        boolean hasFood = hasRequiredFood(config);
        boolean hasTeleport = hasTeleportToFalador();
        boolean hasAgilityOrKey = Microbot.getClient().getRealSkillLevel(Skill.AGILITY) >= 70 || hasDustyKey();

        if (!hasFood) {
            Microbot.log("Missing required food for the trip.");
        }

        if (!hasTeleport) {
            Microbot.log("Missing teleport to Falador or required runes.");
        }

        if (!hasAgilityOrKey) {
            Microbot.log("Requires Agility level 70 or a Dusty Key.");
        }

        // Check if all requirements are met
        if (hasFood && hasTeleport && hasAgilityOrKey) {
            currentState = BlueDragonState.TRAVEL_TO_DRAGONS;
        } else {
            Microbot.log("Starting conditions not met. Stopping the plugin.");
            stop();
        }
    }


    private boolean hasRequiredFood(BlueDragonsConfig config) {
        Rs2Food food = config.foodType();
        int amount = config.foodAmount();
        return food != null && Rs2Inventory.count(food.getName()) >= amount;
    }

    private boolean hasTeleportToFalador() {
        if (Rs2Inventory.contains("Falador teleport")) {
            Microbot.log("Found Falador teleport in inventory.");
            return true;
        }

        int lawRuneId = Runes.LAW.getItemId();
        int waterRuneId = Runes.WATER.getItemId();
        int dustRuneId = Runes.DUST.getItemId();
        int airRuneId = Runes.AIR.getItemId();

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
        boolean inInventory = Rs2Inventory.contains(runeId) && Rs2Inventory.count(runeId) >= requiredAmount;
        boolean inRunePouch = checkRunePouch && RunePouch.contains(runeId, requiredAmount);
        return inInventory || inRunePouch;
    }



    private boolean hasDustyKey() {
        return Rs2Inventory.contains("Dusty key");
    }

    private void handleTravelToDragons() {
        Microbot.log("Traveling to dragons...");
        Rs2Walker.walkTo(SAFE_SPOT);
        sleepUntil(this::isPlayerAtSafeSpot);
        currentState = BlueDragonState.FIGHTING;
    }

    private void handleFighting(BlueDragonsConfig config) {
        if (!isPlayerAtSafeSpot()) {
            Microbot.log("Not at safe spot. Moving back before continuing to fight.");
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
                Microbot.log("Attacked dragon, moving back to safe spot.");
                moveToSafeSpot();
            }
        }
    }


    private boolean attemptLooting(BlueDragonsConfig config) {
        if (Rs2Inventory.isFull()) {
            Microbot.log("Inventory is full after looting, switching to BANKING state.");
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
            Microbot.log("Returning to safe spot after looting.");
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
        int amount = config.foodAmount();
        if (food != null && amount > 0 && Rs2Bank.isOpen() && !hasRequiredFood(config)) {
            Microbot.log("Withdrawing " + amount + "x " + food.getName() + " for dragon fight.");
            Rs2Bank.withdrawX(food.getName(), amount);
        } else if (hasRequiredFood(config)) {
            Microbot.log("Already have the required amount of food in inventory. No need to withdraw.");
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
        Microbot.pauseAllScripts = false;
    }

    public void updateConfig(BlueDragonsConfig config) {
        Microbot.log("Applying new configuration to Blue Dragons script.");
        withdrawFood(config);
    }

    public void stop() {
        Microbot.log("Blue Dragons plugin stopped.");
        if (mainScheduledFuture != null) {
            mainScheduledFuture.cancel(true);
            super.shutdown();
        }
    }
}