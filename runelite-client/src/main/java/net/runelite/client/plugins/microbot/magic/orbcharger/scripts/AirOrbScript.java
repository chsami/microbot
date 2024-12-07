package net.runelite.client.plugins.microbot.magic.orbcharger.scripts;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.magic.orbcharger.OrbChargerPlugin;
import net.runelite.client.plugins.microbot.magic.orbcharger.enums.OrbChargerState;
import net.runelite.client.plugins.microbot.magic.orbcharger.enums.Teleport;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;
import net.runelite.client.plugins.microbot.util.antiban.enums.Activity;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.microbot.util.misc.Rs2Potion;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.player.Rs2Pvp;
import net.runelite.client.plugins.microbot.util.security.Login;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;

import javax.inject.Inject;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AirOrbScript extends Script {

    private final OrbChargerPlugin plugin;
    public OrbChargerState state;
    List<Player> dangerousPlayers = new ArrayList<>();
    public boolean hasDied = false;
    private int unpoweredOrbAmount = 0;
    private int cosmicRuneAmount = 0;

    @Inject
    public AirOrbScript(OrbChargerPlugin plugin) {
        this.plugin = plugin;
    }

    private final WorldPoint airObelisk = new WorldPoint(3088, 3568, 0);
    private final WorldPoint outsideOfWilderness = new WorldPoint(3131, 9910, 0);
    private final int[] airStaves = {
            ItemID.STAFF_OF_AIR,
            ItemID.AIR_BATTLESTAFF,
            ItemID.MYSTIC_AIR_STAFF,
    };

    private final String gloryRegex = "^Amulet of glory\\(\\d\\)$";


    public boolean run() {
        Microbot.enableAutoRunOn = false;
        Rs2Antiban.resetAntibanSettings();
        Rs2Antiban.antibanSetupTemplates.applyGeneralBasicSetup();
        Rs2Antiban.setActivity(Activity.CHARGING_AIR_ORBS);
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;
                long startTime = System.currentTimeMillis();

                if (hasStateChanged()) {
                    state = updateState();
                }

                if (state == null) {
                    Microbot.showMessage("Unable to evaluate state");
                    shutdown();
                    return;
                }

                if (state != OrbChargerState.BANKING && Rs2Pvp.isInWilderness() && dangerousPlayers.isEmpty()) {
                    dangerousPlayers = Rs2Player.getPlayersInCombatLevelRange().stream()
                            .filter(player -> !Rs2Player.hasPlayerEquippedItem(player, airStaves))
                            .collect(Collectors.toList());

                    if (!dangerousPlayers.isEmpty()) {
                        Rs2Walker.setTarget(null);
                        return;
                    }
                }

                switch (state) {
                    case BANKING:
                        if (Rs2Bank.isNearBank(plugin.getTeleport().getBankLocation(), 15) && !dangerousPlayers.isEmpty()) {
                            dangerousPlayers.clear();
                        }
                        if (!Rs2Bank.isOpen()) return;

                        if (!Rs2Equipment.isWearing("air")) {
                            List<Rs2Item> filteredAirStaves = Rs2Bank.bankItems().stream()
                                    .filter(item -> Arrays.stream(airStaves).anyMatch(id -> item.getId() == id))
                                    .collect(Collectors.toList());

                            if (filteredAirStaves.isEmpty()) {
                                Microbot.showMessage("Missing Air Staff in Bank!");
                                shutdown();
                                return;
                            }

                            Rs2Bank.withdrawOne(filteredAirStaves.get(0).getId());
                            Rs2Inventory.waitForInventoryChanges(1200);
                            Rs2Inventory.equip(filteredAirStaves.get(0).getId());
                            Rs2Inventory.waitForInventoryChanges(1200);
                        }

                        if (!Rs2Inventory.isEmpty()) {
                            List<String> importantPotionNames = new ArrayList<>(Rs2Potion.getRestoreEnergyPotionsVariants());
                            importantPotionNames.add(Rs2Potion.getStaminaPotion());

                            List<Rs2Item> filteredPotions = Rs2Inventory.getFilteredPotionItemsInInventory(importantPotionNames);

                            List<String> importantItemNames = filteredPotions.stream()
                                    .map(Rs2Item::getName)
                                    .collect(Collectors.toList());

                            if (Rs2Bank.depositAllExcept(importantItemNames)) {
                                Rs2Inventory.waitForInventoryChanges(1200);
                            }
                        }

                        handleReplaceTeleportItem();

                        if (Rs2Player.getHealthPercentage() <= plugin.getEatAtPercent()) {
                            while (Rs2Player.getHealthPercentage() < 100 && isRunning()) {
                                if (!Rs2Bank.hasItem(plugin.getRs2Food().getId())) {
                                    Microbot.showMessage("Missing Food in Bank!");
                                    shutdown();
                                    break;
                                }

                                Rs2Bank.withdrawOne(plugin.getRs2Food().getId());
                                Rs2Inventory.waitForInventoryChanges(1200);
                                Rs2Player.useFood();
                                sleep(1000);
                            }

                            if (Rs2Inventory.hasItem(ItemID.JUG)) {
                                Rs2Bank.depositAll(ItemID.JUG);
                                Rs2Inventory.waitForInventoryChanges(1200);
                            }
                        }

                        if (plugin.isUseEnergyPotions()) {
                            if (!Rs2Inventory.hasItem(Rs2Potion.getRestoreEnergyPotionsVariants())) {
                                if (!Rs2Bank.hasItem(Rs2Potion.getRestoreEnergyPotionsVariants())) {
                                    Microbot.showMessage("Missing Energy Restore Potions");
                                    shutdown();
                                    return;
                                }

                                Rs2Item energyRestoreItem = Rs2Bank.bankItems()
                                        .stream()
                                        .filter(item -> Rs2Potion.getRestoreEnergyPotionsVariants().stream()
                                                .anyMatch(potion -> item.name.contains(potion)))
                                        .findFirst()
                                        .orElse(null);

                                if (energyRestoreItem == null) {
                                    Microbot.showMessage("Unable to find Energy Restore Potions");
                                    System.out.println("break");
                                    shutdown();
                                    return;
                                }

                                Rs2Bank.withdrawOne(energyRestoreItem.getId());
                                Rs2Inventory.waitForInventoryChanges(1200);
                            }
                        }

                        if (plugin.isUseStaminaPotions()) {
                            if (!Rs2Inventory.hasItem(Rs2Potion.getStaminaPotion())) {
                                if (!Rs2Bank.hasItem(Rs2Potion.getStaminaPotion())) {
                                    Microbot.showMessage("Missing Stamina Potions");
                                    shutdown();
                                    return;
                                }

                                Rs2Bank.withdrawOne(Rs2Potion.getStaminaPotion());
                                Rs2Inventory.waitForInventoryChanges(1200);
                            }
                        }

                        unpoweredOrbAmount = Rs2Inventory.getEmptySlots() - 1;
                        int cosmicRuneAmount = unpoweredOrbAmount * 3;

                        if (!Rs2Inventory.hasItemAmount(ItemID.COSMIC_RUNE, cosmicRuneAmount)) {
                            if (!Rs2Bank.hasBankItem(ItemID.COSMIC_RUNE, cosmicRuneAmount)) {
                                Microbot.showMessage("Missing Cosmic Runes");
                                shutdown();
                                return;
                            }

                            Rs2Bank.withdrawX(ItemID.COSMIC_RUNE, cosmicRuneAmount);
                        }

                        if (!Rs2Inventory.hasItemAmount(ItemID.UNPOWERED_ORB, unpoweredOrbAmount)) {
                            if (!Rs2Bank.hasBankItem(ItemID.UNPOWERED_ORB, unpoweredOrbAmount)) {
                                Microbot.showMessage("Missing Unpowered Orbs");
                                shutdown();
                                return;
                            }

                            Rs2Bank.withdrawX(ItemID.UNPOWERED_ORB, unpoweredOrbAmount);
                        }

                        Rs2Bank.closeBank();
                        sleepUntil(() -> !Rs2Bank.isOpen());
                        break;
                    case CHARGING:
                        Rs2Walker.setTarget(null);
                        Rs2Magic.cast(MagicAction.CHARGE_AIR_ORB);
                        Rs2GameObject.interact(ObjectID.OBELISK_OF_AIR);
                        Rs2Dialogue.sleepUntilHasCombinationDialogue();
                        Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
                        sleepUntil(() -> Rs2Player.isAnimating(1200));
                        Rs2Tab.switchToInventoryTab();
                        sleepUntil(() -> !Rs2Player.isAnimating(5000) || !Rs2Inventory.hasItem(ItemID.UNPOWERED_ORB), 96000);
                        break;
                    case DRINKING:
                        Rs2GameObject.interact(ObjectID.POOL_OF_REFRESHMENT);
                        sleepUntil(() -> Rs2Player.getRunEnergy() == 100 && !Rs2Player.isAnimating(2000));
                        break;
                    default:
                        break;
                }

                long endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                System.out.println("Total time for loop " + totalTime);

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    public boolean handleWalk() {
        scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!shouldBank() && !shouldCharge() && !shouldDrinkFromPool() && hasRequiredItems() && dangerousPlayers.isEmpty()) {
                    Rs2Walker.walkTo(airObelisk, 2);
                } else if ((shouldBank() && !hasRequiredItems()) || (!dangerousPlayers.isEmpty() || hasDied)) {
                    if (hasDied) {
                        dangerousPlayers.clear();
                        Rs2Walker.setTarget(null);
                        hasDied = false;
                    }
                    if (Rs2Player.isInCombat() || Rs2Player.isTeleBlocked()) {
                        Rs2Walker.walkTo(outsideOfWilderness, 2);
                        if (Rs2Player.distanceTo(outsideOfWilderness) <= 2 && !Rs2Player.isMoving()) {
                            if (Rs2Player.isInCombat()) {
                                sleepUntil(() -> !Rs2Player.isInCombat(), 12000);
                            }
                            Rs2Player.logout();
                            sleepUntil(() -> Microbot.getClient().getGameState() != GameState.LOGGED_IN);
                            dangerousPlayers.clear();
                            new Login(Login.getRandomWorld(Login.activeProfile.isMember()));
                            sleepUntil(() -> Microbot.getClient().getGameState() == GameState.LOGGED_IN);
                        }
                    } else {
                        Rs2Bank.walkToBankAndUseBank(plugin.getTeleport().getBankLocation());
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        Rs2Antiban.resetAntibanSettings();
        super.shutdown();
    }

    private boolean hasStateChanged() {
        if (shouldBank()) return true;
        if (shouldCharge()) return true;
        if (hasRequiredItems()) return true;
        return false;
    }

    private OrbChargerState updateState() {
        if (shouldBank()) return OrbChargerState.BANKING;
        if (shouldDrinkFromPool()) return OrbChargerState.DRINKING;
        if (shouldCharge()) return OrbChargerState.CHARGING;
        if (hasRequiredItems()) return OrbChargerState.WALKING;
        return null;
    }

    private boolean shouldBank() {
        return (Rs2Inventory.hasItem(ItemID.AIR_ORB) && !Rs2Inventory.hasItem(ItemID.COSMIC_RUNE)) || !Rs2Inventory.hasItem(ItemID.UNPOWERED_ORB);
    }

    private boolean hasRequiredItems() {
        return Rs2Inventory.hasItemAmount(ItemID.COSMIC_RUNE, cosmicRuneAmount) && Rs2Inventory.hasItemAmount(ItemID.UNPOWERED_ORB, unpoweredOrbAmount);
    }

    private boolean shouldCharge() {
        return Rs2Player.getWorldLocation().distanceTo(airObelisk) <= 3 || Rs2GameObject.exists(ObjectID.OBELISK_OF_AIR);
    }

    private boolean shouldDrinkFromPool() {
        return plugin.getTeleport() == Teleport.RING_OF_DUELING && Rs2Player.getRunEnergy() < 80 && hasRequiredItems() && Rs2GameObject.exists(ObjectID.POOL_OF_REFRESHMENT);
    }

    private void handleReplaceTeleportItem() {
        final Pattern regexPattern = Pattern.compile(plugin.getTeleport().getRegexPattern());
        switch (plugin.getTeleport()) {
            case AMULET_OF_GLORY:
                Rs2Item equippedAmulet = Rs2Equipment.get(EquipmentInventorySlot.AMULET);

                boolean needsAmulet = equippedAmulet == null
                        || !regexPattern.matcher(equippedAmulet.getName()).matches()
                        || Rs2Equipment.hasEquipped(ItemID.AMULET_OF_GLORY);
                
                if (needsAmulet) {
                    Rs2Item amuletOfGlory = Rs2Bank.bankItems()
                            .stream()
                            .filter(item -> regexPattern.matcher(item.name).matches())
                            .findFirst()
                            .orElse(null);

                    if (amuletOfGlory == null) {
                        Microbot.showMessage("Unable to find Amulet of Glory");
                        shutdown();
                        return;
                    }

                    Rs2Bank.withdrawOne(amuletOfGlory.getId());
                    Rs2Inventory.waitForInventoryChanges(1200);
                    Rs2Inventory.equip(amuletOfGlory.getId());
                    Rs2Inventory.waitForInventoryChanges(1200);

                    if (equippedAmulet != null) {
                        Rs2Bank.depositOne(equippedAmulet.getId());
                        Rs2Inventory.waitForInventoryChanges(1200);
                    }
                }
                break;
            case RING_OF_DUELING:
                Rs2Item equippedRing = Rs2Equipment.get(EquipmentInventorySlot.RING);
                boolean needsRing = equippedRing == null
                        || !regexPattern.matcher(equippedRing.getName()).matches();

                if (needsRing) {
                    Rs2Item ringOfDueling = Rs2Bank.bankItems()
                            .stream()
                            .filter(item -> regexPattern.matcher(item.name).matches())
                            .findFirst()
                            .orElse(null);

                    if (ringOfDueling == null) {
                        Microbot.showMessage("Unable to find Ring of Dueling");
                        shutdown();
                        return;
                    }

                    Rs2Bank.withdrawOne(ringOfDueling.getId());
                    Rs2Inventory.waitForInventoryChanges(1200);
                    Rs2Inventory.equip(ringOfDueling.getId());
                    Rs2Inventory.waitForInventoryChanges(1200);
                    if (equippedRing != null) {
                        Rs2Bank.depositOne(equippedRing.getId());
                        Rs2Inventory.waitForInventoryChanges(1200);
                    }
                }
                break;
        }
    }
}
