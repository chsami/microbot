package net.runelite.client.plugins.microbot.cooking.scripts;

import net.runelite.api.NPC;
import net.runelite.api.TileObject;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.cooking.AutoCookingConfig;
import net.runelite.client.plugins.microbot.cooking.AutoCookingPlugin;
import net.runelite.client.plugins.microbot.cooking.enums.CookingAreaType;
import net.runelite.client.plugins.microbot.cooking.enums.CookingItem;
import net.runelite.client.plugins.microbot.cooking.enums.CookingLocation;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.Global.sleepUntilTrue;

enum CookingState {
    COMBINE,
    COOKING,
    WALKING,
    BANKING,
    DROPPING,
}

public class AutoCookingScript extends Script {

    CookingState state;
    boolean init = true;
    CookingLocation location;

    public boolean run(AutoCookingConfig config) {
        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;

                if (init) {
                    if (initialPlayerLocation == null) {
                        initialPlayerLocation = Rs2Player.getWorldLocation();
                    }

                    if (config.useNearestCookingLocation()) {
                        location = CookingLocation.findNearestCookingLocation(config, initialPlayerLocation);
                    } else {
                        location = config.cookingLocation();
                        if (config.cookingItem().getCookingAreaType() != CookingAreaType.BOTH) {
                            if (location.getCookingAreaType() != config.cookingItem().getCookingAreaType()) {
                                Microbot.showMessage("Cooking Area does not match item's cooking area");
                                shutdown();
                                return;
                            }
                        }
                    }

                    getState(config);
                }

                if (Rs2Player.isMoving() || Rs2Player.isAnimating() || Microbot.pauseAllScripts) return;

                switch (state) {
                    case COOKING:
                        if (!config.cookingItem().hasRequirements()) {
                            Microbot.showMessage("You do not meet the requirements to cook this item");
                            shutdown();
                            return;
                        }

                        TileObject cookingObject = Rs2GameObject.findObjectById(location.getCookingObjectID());

                        if (cookingObject != null) {
                            if (!Rs2Camera.isTileOnScreen(cookingObject.getLocalLocation())) {
                                Rs2Camera.turnTo(cookingObject.getLocalLocation());
                                return;
                            }
                            Rs2Inventory.useItemOnObject(config.cookingItem().getRawItemID(), cookingObject.getId());
                            sleepUntil(() -> !Rs2Player.isMoving() && Rs2Widget.findWidget("How many would you like to cook?", null, false) != null);

                            Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
                            Microbot.status = "Cooking " + config.cookingItem().getRawItemName();
                            sleepUntilTrue(() -> !hasRawItem(config) && AutoCookingPlugin.hasPlayerStoppedAnimating(), 500, 150000);
                            if (hasBurntItem(config) && !config.cookingItem().getBurntItemName().isEmpty()) {
                                state = CookingState.DROPPING;
                                return;
                            }
                            state = CookingState.BANKING;
                            break;
                        }
                    case DROPPING:
                        Microbot.status = "Dropping " + config.cookingItem().getBurntItemName();
                        Rs2Inventory.dropAll(item -> item.name.equalsIgnoreCase(config.cookingItem().getBurntItemName()), config.getDropOrder());
                        sleepUntilTrue(() -> !hasBurntItem(config), 500, 150000);
                        state = CookingState.BANKING;
                        break;
                    case BANKING:
                        if (location == CookingLocation.ROUGES_DEN) {
                            NPC npc = Rs2Npc.getBankerNPC();
                            boolean isNPCBankOpen = Rs2Bank.openBank(npc);
                            if (!isNPCBankOpen) return;
                        } else {
                            boolean isBankOpen = Rs2Bank.walkToBankAndUseBank();
                            if (!isBankOpen || !Rs2Bank.isOpen()) return;
                        }

                        if (hasCookedItem(config)) {
                            Rs2Bank.depositAll(config.cookingItem().getCookedItemName(), true);
                            sleep(Random.random(800, 1600));
                        }

                        if (config.cookingItem().equals(CookingItem.UNCOOKED_PIZZA)) {
                            Rs2Bank.depositAll();
                            if (Rs2Bank.count("pot of flour") > 9 && Rs2Bank.count(config.humidifyItem().getFilledItemName()) > 9) {
                                Rs2Bank.withdrawX("pot of flour", 9);
                                sleep(Random.random(600, 800));
                                Rs2Bank.withdrawX(config.humidifyItem().getFilledItemName(), 9);
                                sleep(Random.random(800, 1600));
                                state = CookingState.COMBINE;
                                Rs2Bank.closeBank();
                                return;
                            }
                            if (Rs2Bank.count("pizza base") >= 1 && Rs2Bank.count("tomato") >= 1) {
                                Rs2Bank.withdrawX("pizza base", 14);
                                sleep(Random.random(600, 800));
                                Rs2Bank.withdrawX("tomato", 14);
                                sleep(Random.random(800, 1600));
                                state = CookingState.COMBINE;
                                Rs2Bank.closeBank();
                                return;
                            }
                            if (Rs2Bank.count("incomplete pizza") >= 1 && Rs2Bank.count("cheese") >= 1) {
                                Rs2Bank.withdrawX("incomplete pizza", 14);
                                sleep(Random.random(600, 800));
                                Rs2Bank.withdrawX("cheese", 14);
                                sleep(Random.random(800, 1600));
                                state = CookingState.COMBINE;
                                Rs2Bank.closeBank();
                                return;
                            }

                        }
                        if (!hasRawItem(config)) {
                            Microbot.showMessage("No Raw Food Item found in Bank");
                            shutdown();
                            return;
                        }
                        Rs2Bank.withdrawAll(config.cookingItem().getRawItemName(), true);
                        sleep(Random.random(800, 1600));
                        state = CookingState.WALKING;
                        Rs2Bank.closeBank();
                        break;
                    case WALKING:
                        if (!isNearCookingLocation(location, 10)) {
                            boolean walkTo = Rs2Walker.walkTo(location.getCookingObjectWorldPoint(), 2);
                            if (!walkTo) return;
                        } else if (!isNearCookingLocation(location, 2)) {
                            Rs2Walker.walkFastCanvas(location.getCookingObjectWorldPoint());
                        }

                        if (hasRawItem(config)) {
                            state = CookingState.COOKING;
                        } else {
                            state = CookingState.BANKING;
                        }
                        break;
                    case COMBINE:
                        if (Rs2Inventory.contains("pot of flour") && Rs2Inventory.contains(config.humidifyItem().getFilledItemName())) {
                            Rs2Inventory.combine("pot of flour", config.humidifyItem().getFilledItemName());
                            Rs2Widget.sleepUntilHasWidget("what sort of dough do you wish to make?");
                            Rs2Widget.clickWidget("pizza base");
                            sleep(Random.random(10600, 11800));
                            state = CookingState.BANKING;
                            return;
                        }

                        if (Rs2Inventory.contains("pizza base") && Rs2Inventory.contains("tomato")) {
                            Rs2Inventory.combine("pizza base", "tomato");
                            Rs2Widget.sleepUntilHasWidget("how many do you wish to make?");
                            Rs2Widget.clickWidget("pizza base with tomato");
                            sleep(Random.random(10600, 11800));
                            state = CookingState.BANKING;
                            return;
                        }

                        if (Rs2Inventory.contains("incomplete pizza") && Rs2Inventory.contains("cheese")) {
                            Rs2Inventory.combine("incomplete pizza", "cheese");
                            Rs2Widget.sleepUntilHasWidget("how many do you wish to make?");
                            Rs2Widget.clickWidget("uncooked pizza");
                            sleep(Random.random(10600, 11800));
                            state = CookingState.BANKING;
                            return;
                        }
                        break;
                }
            } catch (Exception ex) {
                Microbot.log(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    private void getState(AutoCookingConfig config) {
        if (!hasRawItem(config)) {
            if (hasBurntItem(config)) {
                state = CookingState.DROPPING;
                init = false;
                return;
            }
            state = CookingState.BANKING;
            init = false;
            return;
        }

        if (!isNearCookingLocation(location, 4)) {
            state = CookingState.WALKING;
            init = false;
            return;
        }

        state = CookingState.COOKING;
        init = false;
    }

    private boolean isNearCookingLocation(CookingLocation location, int distance) {
        return Rs2Player.getWorldLocation().distanceTo(location.getCookingObjectWorldPoint()) <= distance && !Rs2Player.isMoving();
    }

    private boolean hasRawItem(AutoCookingConfig config) {
        if (Rs2Bank.isOpen()) {
            return Rs2Bank.hasBankItem(config.cookingItem().getRawItemName(), true);
        }
        return Rs2Inventory.hasItem(config.cookingItem().getRawItemName(), true);
    }

    private boolean hasCookedItem(AutoCookingConfig config) {
        return Rs2Inventory.hasItem(config.cookingItem().getCookedItemName(), true);
    }

    private boolean hasBurntItem(AutoCookingConfig config) {
        return Rs2Inventory.hasItem(config.cookingItem().getBurntItemName(), true);
    }
}
