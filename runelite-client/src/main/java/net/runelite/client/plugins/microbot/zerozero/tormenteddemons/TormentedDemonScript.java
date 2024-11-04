package net.runelite.client.plugins.microbot.zerozero.tormenteddemons;

import lombok.SneakyThrows;
import net.runelite.api.HeadIcon;
import net.runelite.api.NPC;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.Rs2InventorySetup;
import net.runelite.client.plugins.microbot.util.grounditem.LootingParameters;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;
import net.runelite.client.plugins.microbot.util.prayer.Rs2PrayerEnum;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TormentedDemonScript extends Script {

    public static final double VERSION = 1.0;
    private boolean isRunning = false;
    private Rs2PrayerEnum currentDefensivePrayer = null;
    private Rs2PrayerEnum currentOffensivePrayer = null;
    private HeadIcon currentOverheadIcon = null;
    private NPC currentTarget;

    private static final int MAGIC_ATTACK_ANIMATION = 11388;
    private static final int RANGE_ATTACK_ANIMATION = 11389;
    private static final int MELEE_ATTACK_ANIMATION = 11392;

    private static final WorldPoint SAFE_LOCATION = new WorldPoint(3150, 3634, 0);

    private enum State { BANKING, TRAVEL_TO_TORMENTED, FIGHTING }
    public static State BOT_STATUS = State.BANKING;

    private enum TravelStep { LOCATION_ONE, CLIMB_FIRST_STAIRS, CLIMB_SECOND_STAIRS, CLIMB_THROUGH, LOCATION_THREE }
    private TravelStep travelStep = TravelStep.LOCATION_ONE;

    private enum BankingStep { DRINK, BANK, LOAD_INVENTORY }
    private BankingStep bankingStep = BankingStep.DRINK;

    public boolean run(TormentedDemonConfig config) {
        // Set starting state based on config
        if (config.fullAuto()) {
            BOT_STATUS = State.BANKING;
        } else if (config.combatOnly()) {
            BOT_STATUS = State.FIGHTING;
        }

        bankingStep = BankingStep.DRINK;
        travelStep = TravelStep.LOCATION_ONE;
        Microbot.enableAutoRunOn = false;
        isRunning = true;

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn() || !super.run()) return;

                switch (BOT_STATUS) {
                    case BANKING:
                        handleBanking(config);
                        break;
                    case TRAVEL_TO_TORMENTED:
                        handleTravel(config);
                        break;
                    case FIGHTING:
                        handleFighting(config);
                        break;
                }
            } catch (Exception ex) {
                System.out.println("Error in main loop: " + ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }


    private void handleTravel(TormentedDemonConfig config) {
        WorldPoint targetLocationOne = new WorldPoint(4062, 4558, 0);
        WorldPoint targetLocationThree = new WorldPoint(4073, 4432, 0);
        WorldPoint playerLocation = Microbot.getClient().getLocalPlayer().getWorldLocation();

        switch (travelStep) {
            case LOCATION_ONE:
                Microbot.status = "Teleporting to Guthixian temple...";
                if (Rs2Inventory.interact("Guthixian temple teleport", "Teleport")) {
                    Rs2Player.waitForAnimation();
                    sleepUntil(() -> !Rs2Player.isAnimating());
                    sleepUntil(() -> playerLocation.distanceTo(targetLocationOne) <= 5);
                    travelStep = TravelStep.CLIMB_FIRST_STAIRS;
                }
                break;

            case CLIMB_FIRST_STAIRS:
                Microbot.status = "Climbing first stairs...";
                if (Rs2GameObject.interact(53623, "Climb-up")) {
                    Rs2Player.waitForAnimation();
                    sleepUntil(() -> !Rs2Player.isAnimating());
                    travelStep = TravelStep.CLIMB_SECOND_STAIRS;
                }
                break;

            case CLIMB_SECOND_STAIRS:
                Microbot.status = "Climbing second stairs...";
                if (Rs2GameObject.interact(53624, "Climb-up")) {
                    Rs2Player.waitForAnimation();
                    sleepUntil(() -> !Rs2Player.isAnimating());
                    travelStep = TravelStep.CLIMB_THROUGH;
                }
                break;

            case CLIMB_THROUGH:
                Microbot.status = "Climbing through the path...";
                if (Rs2GameObject.interact(54082, "Climb-through")) {
                    Rs2Player.waitForAnimation();
                    sleepUntil(() -> !Rs2Player.isAnimating());
                    travelStep = TravelStep.LOCATION_THREE;
                }
                break;

            case LOCATION_THREE:
                Microbot.status = "Approaching Tormented Demon location...";
                if (Rs2Walker.walkTo(targetLocationThree, 2)) {
                    sleepUntil(() -> Microbot.getClient().getLocalPlayer().getWorldLocation().equals(targetLocationThree), 5000);
                    BOT_STATUS = State.FIGHTING;
                }
                break;
        }
    }

    private void handleBanking(TormentedDemonConfig config) {
        final int FEROX_POOL_ID = 39651;

        switch (bankingStep) {
            case DRINK:
                Microbot.status = "Drinking at Ferox Enclave pool...";
                int currentHealth = Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS);
                int maxHealth = Microbot.getClient().getRealSkillLevel(Skill.HITPOINTS);
                int currentPrayer = Microbot.getClient().getBoostedSkillLevel(Skill.PRAYER);
                int maxPrayer = Microbot.getClient().getRealSkillLevel(Skill.PRAYER);

                if (currentHealth < maxHealth || currentPrayer < maxPrayer) {
                    if (Rs2GameObject.interact(FEROX_POOL_ID, "Drink")) {
                        Rs2Player.waitForAnimation();
                        sleepUntil(() ->
                                Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS) == maxHealth &&
                                        Microbot.getClient().getBoostedSkillLevel(Skill.PRAYER) == maxPrayer
                        );
                        bankingStep = BankingStep.BANK;
                    }
                } else {
                    bankingStep = BankingStep.BANK;
                }
                break;

            case BANK:
                Microbot.status = "Opening bank...";
                Rs2Bank.openBank();
                sleepUntil(Rs2Bank::isOpen);
                Rs2Bank.depositAll();
                Rs2Bank.depositEquipment();
                bankingStep = BankingStep.LOAD_INVENTORY;
                break;

            case LOAD_INVENTORY:
                Microbot.status = "Loading inventory and equipment setup...";
                Rs2InventorySetup inventorySetup = new Rs2InventorySetup("tormented", mainScheduledFuture);
                boolean equipmentLoaded = inventorySetup.loadEquipment();
                boolean inventoryLoaded = inventorySetup.loadInventory();

                if (equipmentLoaded && inventoryLoaded) {
                    Rs2Bank.closeBank();
                    bankingStep = BankingStep.DRINK;
                    BOT_STATUS = State.TRAVEL_TO_TORMENTED;
                } else {
                    shutdown();
                }
                break;
        }
    }

    private void handleFighting(TormentedDemonConfig config) {
        if (currentTarget == null || currentTarget.isDead()) {
            disableAllPrayers();

            if (currentTarget != null && currentTarget.isDead()) {
                Microbot.pauseAllScripts = true;
                attemptLooting(config);
                sleep(3000);
                Microbot.pauseAllScripts = false;
            }

            currentTarget = findNewTarget(config);
            if (currentTarget != null) {
                currentOverheadIcon = getHeadIcon(currentTarget);                if (currentOverheadIcon == null) {
                    System.out.println("Failed to retrieve HeadIcon for target.");
                    return;
                }
                switchGear(config, currentOverheadIcon);
            } else {
                System.out.println("No target found for attack.");
                return;
            }
        }

        // Only check retreat if fullAuto is enabled
        if (config.fullAuto() && shouldRetreat(config)) {
            Microbot.pauseAllScripts = true;
            Rs2Walker.walkTo(SAFE_LOCATION);
            sleepUntil(() -> Microbot.getClient().getLocalPlayer().getWorldLocation().equals(SAFE_LOCATION), 5000);
            Microbot.pauseAllScripts = false;
            BOT_STATUS = State.BANKING;
            return;
        }

        // Eating and drinking thresholds
        Rs2Player.eatAt(config.minEatPercent());
        Rs2Player.drinkPrayerPotionAt(config.minPrayerPercent());

        // Engage with the target
        if (Microbot.getClient().getLocalPlayer().getInteracting() != currentTarget) {
            if (Rs2Npc.attack(currentTarget)) {
                Rs2Player.waitForAnimation();
                sleepUntil(() -> Microbot.getClient().getLocalPlayer().getInteracting() == currentTarget, 3000);
            } else {
                System.out.println("Attack failed for target: " + (currentTarget != null ? currentTarget.getName() : "null"));
                currentTarget = null;
                return;
            }
        }

        // Gear switching based on target overhead icon
        HeadIcon newOverheadIcon = getHeadIcon(currentTarget);
        if (newOverheadIcon != currentOverheadIcon) {
            currentOverheadIcon = newOverheadIcon;
            if (!Rs2Inventory.isOpen()) {
                Rs2Inventory.open();
                sleepUntil(Rs2Inventory::isOpen, 1000);
            }
            switchGear(config, currentOverheadIcon);
            sleep(100);
        }

        // Defensive prayer based on target animation
        int npcAnimation = currentTarget.getAnimation();
        if (config.enableDefensivePrayer()) {
            Rs2PrayerEnum newDefensivePrayer = null;
            if (npcAnimation == MAGIC_ATTACK_ANIMATION) {
                newDefensivePrayer = Rs2PrayerEnum.PROTECT_MAGIC;
            } else if (npcAnimation == RANGE_ATTACK_ANIMATION) {
                newDefensivePrayer = Rs2PrayerEnum.PROTECT_RANGE;
            } else if (npcAnimation == MELEE_ATTACK_ANIMATION) {
                newDefensivePrayer = Rs2PrayerEnum.PROTECT_MELEE;
            }
            if (newDefensivePrayer != null && newDefensivePrayer != currentDefensivePrayer) {
                switchDefensivePrayer(newDefensivePrayer);
            }
        }

        if (config.enableOffensivePrayer()) {
            activateOffensivePrayer(config);
        }
    }


    private void switchDefensivePrayer(Rs2PrayerEnum newDefensivePrayer) {
        if (currentDefensivePrayer != null) {
            Rs2Prayer.toggle(currentDefensivePrayer, false);
        }
        Rs2Prayer.toggle(newDefensivePrayer, true);
        currentDefensivePrayer = newDefensivePrayer;
    }

    private void activateOffensivePrayer(TormentedDemonConfig config) {
        Rs2PrayerEnum newOffensivePrayer = null;
        if (config.useMagicStyle() && isGearEquipped(parseGear(config.magicGear()))) {
            newOffensivePrayer = Rs2PrayerEnum.AUGURY;
        } else if (config.useRangeStyle() && isGearEquipped(parseGear(config.rangeGear()))) {
            newOffensivePrayer = Rs2PrayerEnum.RIGOUR;
        } else if (config.useMeleeStyle() && isGearEquipped(parseGear(config.meleeGear()))) {
            newOffensivePrayer = Rs2PrayerEnum.PIETY;
        }

        if (newOffensivePrayer != null && newOffensivePrayer != currentOffensivePrayer) {
            switchOffensivePrayer(newOffensivePrayer);
            sleep(100);
        }
    }

    private void switchOffensivePrayer(Rs2PrayerEnum newOffensivePrayer) {
        if (currentOffensivePrayer != null) {
            Rs2Prayer.toggle(currentOffensivePrayer, false);
        }
        Rs2Prayer.toggle(newOffensivePrayer, true);
        currentOffensivePrayer = newOffensivePrayer;
    }

    private NPC findNewTarget(TormentedDemonConfig config) {
        return Rs2Npc.getAttackableNpcs("Tormented Demon")
                .filter(npc -> npc.getInteracting() == null || npc.getInteracting() == Microbot.getClient().getLocalPlayer())
                .filter(npc -> {
                    HeadIcon demonHeadIcon = getHeadIcon(npc);
                    if (demonHeadIcon != null) {
                        switchGear(config, demonHeadIcon);
                        return true;
                    }
                    System.out.println("Null HeadIcon for NPC " + npc.getName());
                    return false;
                })
                .findFirst()
                .orElse(null);
    }

    private void switchGear(TormentedDemonConfig config, HeadIcon combatNpcHeadIcon) {
        if (!config.autoGearSwitch()) {
            return;
        }

        List<String> gearToEquip = new ArrayList<>();
        boolean useRange = config.useRangeStyle();
        boolean useMagic = config.useMagicStyle();
        boolean useMelee = config.useMeleeStyle();

        switch (combatNpcHeadIcon) {
            case RANGED:
                if (useMelee && useMagic) {
                    gearToEquip = Math.random() < 0.5 ? parseGear(config.meleeGear()) : parseGear(config.magicGear());
                } else if (useMelee) {
                    gearToEquip = parseGear(config.meleeGear());
                } else if (useMagic) {
                    gearToEquip = parseGear(config.magicGear());
                }
                break;

            case MAGIC:
                if (useRange && useMelee) {
                    gearToEquip = Math.random() < 0.5 ? parseGear(config.rangeGear()) : parseGear(config.meleeGear());
                } else if (useRange) {
                    gearToEquip = parseGear(config.rangeGear());
                } else if (useMelee) {
                    gearToEquip = parseGear(config.meleeGear());
                }
                break;

            case MELEE:
                if (useRange && useMagic) {
                    gearToEquip = Math.random() < 0.5 ? parseGear(config.rangeGear()) : parseGear(config.magicGear());
                } else if (useRange) {
                    gearToEquip = parseGear(config.rangeGear());
                } else if (useMagic) {
                    gearToEquip = parseGear(config.magicGear());
                }
                break;
        }

        if (!isGearEquipped(gearToEquip)) {
            equipGear(gearToEquip);
        }
    }

    private List<String> parseGear(String gearString) {
        return Arrays.asList(gearString.split(","));
    }

    private boolean isGearEquipped(List<String> gear) {
        return gear.stream().allMatch(Rs2Equipment::isWearing);
    }

    private void equipGear(List<String> gear) {
        for (String item : gear) {
            Rs2Inventory.wield(item);
            sleep(50);
        }
    }

    private boolean shouldRetreat(TormentedDemonConfig config) {
        int currentHealth = Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS);
        int currentPrayer = Microbot.getClient().getBoostedSkillLevel(Skill.PRAYER);
        boolean noFood = Rs2Inventory.getInventoryFood().isEmpty();
        boolean noPrayerPotions = Rs2Inventory.items().stream()
                .noneMatch(item -> item != null && item.getName() != null && item.getName().toLowerCase().contains("prayer potion"));

        return (noFood && currentHealth <= config.healthThreshold()) || (noPrayerPotions && currentPrayer < 10);
    }

    public void disableAllPrayers() {
        Rs2Prayer.disableAllPrayers();
        currentDefensivePrayer = null;
        currentOffensivePrayer = null;
    }

    private void attemptLooting(TormentedDemonConfig config) {
        List<String> lootItems = parseLootItems(config.lootItems());

        LootingParameters nameParams = new LootingParameters(10, 1, 1, 0, false, true, lootItems.toArray(new String[0]));
        Rs2GroundItem.lootItemsBasedOnNames(nameParams);

        LootingParameters valueParams = new LootingParameters(10, 1, config.lootValueThreshold(), 0, false, true);
        Rs2GroundItem.lootItemBasedOnValue(valueParams);
    }

    private List<String> parseLootItems(String lootFilter) {
        return Arrays.asList(lootFilter.toLowerCase().split(","));
    }

    @SneakyThrows
    public static HeadIcon getHeadIcon(NPC npc) {
        Field aq = npc.getClass().getDeclaredField("ay");
        aq.setAccessible(true);
        Object aqObj = aq.get(npc);
        if (aqObj == null) {
            aq.setAccessible(false);
            System.out.println("Error: aqObj is null for NPC " + npc.getName());
            return getOldHeadIcon(npc);
        }
        Field aeField = aqObj.getClass().getDeclaredField("aw");
        aeField.setAccessible(true);
        short[] ae = (short[]) aeField.get(aqObj);
        aeField.setAccessible(false);
        aq.setAccessible(false);
        if (ae == null) {
            System.out.println("Error: ae is null for NPC " + npc.getName());
            return getOldHeadIcon(npc);
        }
        short headIcon = ae[0];
        return headIcon == -1 ? getOldHeadIcon(npc) : HeadIcon.values()[headIcon];
    }

    private static HeadIcon getOldHeadIcon(NPC npc) {
        return null;
    }
    private static HeadIcon getOlderHeadicon(NPC npc) {
        return null;
    }

    @Override
    public void shutdown() {
        super.shutdown();
        isRunning = false;
        disableAllPrayers();
        BOT_STATUS = State.BANKING;
        travelStep = TravelStep.LOCATION_ONE;
        Microbot.log("Shutting down Tormented Demon script");
    }
}
