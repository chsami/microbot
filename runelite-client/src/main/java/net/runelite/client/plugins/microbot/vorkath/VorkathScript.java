/**
 * Credits to Jrod7938
 */

package net.runelite.client.plugins.microbot.vorkath;

import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.loottracker.LootTrackerRecord;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.MicrobotInventorySetup;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grandexchange.Rs2GrandExchange;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.misc.Rs2Potion;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;
import net.runelite.client.plugins.microbot.util.prayer.Rs2PrayerEnum;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


enum State {
    BANKING,
    TELEPORT_TO_RELLEKKA,
    WALK_TO_VORKATH_ISLAND,
    WALK_TO_VORKATH,
    PREPARE_FIGHT,
    FIGHT_VORKATH,
    ZOMBIE_SPAWN,
    ACID,
    LOOT_ITEMS,
    TELEPORT_AWAY,
    DEAD_WALK,
    SELLING_ITEMS
}

public class VorkathScript extends Script {
    public static String version = "1.2.6";

    State state = State.ZOMBIE_SPAWN;

    private final int whiteProjectileId = 395;
    private final int redProjectileId = 1481;
    @Getter
    public final int acidProjectileId = 1483;
    private final int acidRedProjectileId = 1482;
    NPC vorkath;
    boolean hasEquipment = false;
    boolean hasInventory = false;
    boolean init = true;
    public static VorkathConfig config;
    @Getter
    private HashSet<WorldPoint> acidPools = new HashSet<>();

    String primaryBolts = "";

    final String ZOMBIFIED_SPAWN = "Zombified Spawn";

    public int vorkathSessionKills = 0;
    public int tempVorkathKills = 0;

    private void calculateState() {
        if (Rs2Npc.getNpc(NpcID.VORKATH_8061) != null) {
            state = State.FIGHT_VORKATH;
        }
        if (Rs2Npc.getNpc(NpcID.VORKATH_8059) != null) {
            state = State.PREPARE_FIGHT;
        }
        if (Rs2GameObject.findObjectById(ObjectID.ICE_CHUNKS_31990) != null) {
            state = State.WALK_TO_VORKATH;
        }
        if (isCloseToRelleka()) {
            state = State.WALK_TO_VORKATH_ISLAND;
        }
        if (Rs2Npc.getNpc(NpcID.TORFINN_10406) != null) {
            state = State.WALK_TO_VORKATH;
        }
    }

    public boolean run(VorkathConfig config) {
        Microbot.enableAutoRunOn = false;
        Microbot.pauseAllScripts = false;
        init = true;
        state = State.BANKING;
        hasEquipment = false;
        hasInventory = false;
        this.config = config;

        Microbot.getSpecialAttackConfigs().setSpecialAttack(true);

        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;

                if (init) {
                    calculateState();
                    primaryBolts = Rs2Equipment.get(EquipmentInventorySlot.AMMO) != null ? Rs2Equipment.get(EquipmentInventorySlot.AMMO).name : "";
                }

                if (state == State.FIGHT_VORKATH  && Rs2Equipment.get(EquipmentInventorySlot.AMMO) == null) {
                    leaveVorkath();
                }

                switch (state) {
                    case BANKING:
                        if (checkSellingItems(config)) return;
                        if (!init && Rs2Equipment.get(EquipmentInventorySlot.AMMO) == null) {
                            Microbot.showMessage("Out of ammo!");
                            sleep(5000);
                            return;
                        }
                        if (isCloseToRelleka() && Rs2Inventory.count() >= 27) {
                            state = State.WALK_TO_VORKATH_ISLAND;
                        }
                        hasEquipment = MicrobotInventorySetup.doesEquipmentMatch("vorkath");
                        hasInventory = MicrobotInventorySetup.doesInventoryMatch("vorkath");
                        if (!Rs2Bank.isOpen()) {
                            Rs2Bank.walkToBankAndUseBank();
                        }
                        if (!hasEquipment) {
                            Rs2Bank.depositAll();
                            Rs2Bank.depositEquipment();
                            sleep(600);
                            hasEquipment = MicrobotInventorySetup.loadEquipment("vorkath", mainScheduledFuture);
                        }
                        if (!hasInventory && MicrobotInventorySetup.doesEquipmentMatch("vorkath")) {
                            sleep(600);
                            hasInventory = MicrobotInventorySetup.loadInventory("vorkath", mainScheduledFuture);
                            sleep(1000);
                        }
                        if (hasEquipment && hasInventory) {
                            healAndDrinkPrayerPotion();
                            if (hasEquipment && hasInventory) {
                                state = State.TELEPORT_TO_RELLEKKA;
                            }
                        }
                        break;
                    case TELEPORT_TO_RELLEKKA:
                        if (!Rs2Inventory.hasItem("Rellekka teleport")) {
                            state = State.BANKING;
                            return;
                        }
                        if (Rs2Bank.isOpen()) {
                            Rs2Bank.closeBank();
                            sleepUntil(() -> !Rs2Bank.isOpen());
                        }
                        if (!isCloseToRelleka()) {
                            Rs2Inventory.interact("Rellekka teleport", "break");
                            sleepUntil(this::isCloseToRelleka);
                        }
                        if (isCloseToRelleka()) {
                            state = State.WALK_TO_VORKATH_ISLAND;
                        }
                        break;
                    case WALK_TO_VORKATH_ISLAND:
                        Rs2Player.toggleRunEnergy(true);
                        Rs2Walker.walkTo(new WorldPoint(2640, 3693, 0));
                        net.runelite.api.NPC torfin = Rs2Npc.getNpc(NpcID.TORFINN_10405);
                        if (torfin != null) {
                            Rs2Npc.interact(torfin, "Ungael");
                            sleepUntil(() -> Rs2Npc.getNpc(NpcID.TORFINN_10406) != null);
                        }
                        if (Rs2Npc.getNpc(NpcID.TORFINN_10406) != null) {
                            state = State.WALK_TO_VORKATH;
                        }
                        break;
                    case WALK_TO_VORKATH:
                        Rs2Walker.walkTo(new WorldPoint(2272, 4052, 0));
                        TileObject iceChunks = Rs2GameObject.findObjectById(ObjectID.ICE_CHUNKS_31990);
                        if (iceChunks != null) {
                            Rs2GameObject.interact(ObjectID.ICE_CHUNKS_31990, "Climb-over");
                            sleepUntil(() -> Rs2GameObject.findObjectById(ObjectID.ICE_CHUNKS_31990) == null);
                        }
                        if (Rs2GameObject.findObjectById(ObjectID.ICE_CHUNKS_31990) == null) {
                            state = State.PREPARE_FIGHT;
                        }
                        break;
                    case PREPARE_FIGHT:
                        Rs2Player.toggleRunEnergy(false);

                        boolean result = drinkPotions();

                        if (result) {
                            Rs2Npc.interact(NpcID.VORKATH_8059, "Poke");
                            Rs2Player.waitForWalking();
                            Rs2Npc.interact(NpcID.VORKATH_8059, "Poke");
                            Rs2Player.waitForAnimation(10000);
                            walkToCenter();
                            Rs2Player.waitForWalking();
                            handlePrayer();
                            sleepUntil(() -> Rs2Npc.getNpc(NpcID.VORKATH_8061) != null);
                            state = State.FIGHT_VORKATH;
                        }
                        break;
                    case FIGHT_VORKATH:
                        vorkath = Rs2Npc.getNpc(NpcID.VORKATH_8061);
                        if (vorkath == null || vorkath.isDead()) {
                            vorkathSessionKills++;
                            tempVorkathKills++;
                            state = State.LOOT_ITEMS;
                            sleep(300, 600);
                            Rs2Inventory.wield(primaryBolts);
                            togglePrayer(false);
                            sleepUntil(() -> Rs2GroundItem.exists("Superior dragon bones", 20), 15000);
                            return;
                        }
                        if (Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS) <= 0) {
                            state = State.DEAD_WALK;
                            Rs2Equipment.equipmentItems = new ArrayList<>();
                            return;
                        }
                        if (Rs2Inventory.getInventoryFood().isEmpty()) {
                            double treshHold = (double) (Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS) * 100) / Microbot.getClient().getRealSkillLevel(Skill.HITPOINTS);
                            if (treshHold < 50) {
                                leaveVorkath();
                                return;
                            }
                        }

                        if (Rs2Npc.attack(vorkath))
                            sleep(600);
                        if (Microbot.getClient().getLocalPlayer().getLocalLocation().getSceneY() >= 59) {
                            walkToCenter();
                        }
                        drinkPotions();
                        handlePrayer();
                        Rs2Player.eatAt(75);
                        handleRedBall();
                        if (doesProjectileExistById(whiteProjectileId)) {
                            state = State.ZOMBIE_SPAWN;
                            walkToCenter();
                            Rs2Tab.switchToMagicTab();
                        }
                        if ((doesProjectileExistById(acidProjectileId) || doesProjectileExistById(acidRedProjectileId))) {
                            state = State.ACID;
                        }
                        if (vorkath.getHealthRatio() < 60 && vorkath.getHealthRatio() != -1) {
                            Rs2Inventory.wield("diamond bolts (e)", "diamond dragon bolts (e)");
                        } else if (vorkath.getHealthRatio() >= 60 && !Rs2Equipment.isWearing(primaryBolts)) {
                            Rs2Inventory.wield(primaryBolts);
                        }
                        break;
                    case ZOMBIE_SPAWN:
                        if (Rs2Npc.getNpc(NpcID.VORKATH_8061) == null) {
                            state = State.FIGHT_VORKATH;
                        }
                        togglePrayer(false);
                        Rs2Player.eatAt(80);
                        drinkPrayer();
                        NPC zombieSpawn = Rs2Npc.getNpc(ZOMBIFIED_SPAWN);
                        if (zombieSpawn != null) {
                            while (Rs2Npc.getNpc(ZOMBIFIED_SPAWN) != null && !Rs2Npc.getNpc(ZOMBIFIED_SPAWN).isDead()
                                    && !doesProjectileExistById(146)) {
                                Rs2Magic.castOn(MagicAction.CRUMBLE_UNDEAD, zombieSpawn);
                                sleep(600);
                            }
                            Rs2Player.eatAt(75);
                            togglePrayer(true);
                            Rs2Tab.switchToInventoryTab();
                            state = State.FIGHT_VORKATH;
                            sleepUntil(() -> Rs2Npc.getNpc("Zombified Spawn") == null);
                            sleep(1000);
                        }
                        break;
                    case ACID:
                        Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_RANGE, false);
                        handleAcidWalk();
                        break;
                    case LOOT_ITEMS:
                        if (Rs2Inventory.isFull()) {
                            boolean hasFood = !Rs2Inventory.getInventoryFood().isEmpty();
                            if (hasFood) {
                                Rs2Player.eatAt(100);
                                Rs2Player.waitForAnimation();
                            } else {
                                state = State.PREPARE_FIGHT;
                            }
                        }
                        togglePrayer(false);
                        Rs2GroundItem.loot("Vorkath's head", 20);
                        Rs2GroundItem.lootAllItemBasedOnValue(config.priceOfItemsToLoot(), 20);
                        int foodInventorySize = Rs2Inventory.getInventoryFood().size();
                        boolean hasVenom = Rs2Inventory.hasItem("venom");
                        boolean hasSuperAntifire = Rs2Inventory.hasItem("super antifire");
                        boolean hasPrayerPotion = Rs2Inventory.hasItem(Rs2Potion.getPrayerPotionsVariants());
                        boolean hasRangePotion = Rs2Inventory.hasItem(Rs2Potion.getRangePotionsVariants());
                        sleep(600, 2000);
                        if (!Rs2GroundItem.isItemBasedOnValueOnGround(config.priceOfItemsToLoot(), 20) && !Rs2GroundItem.exists("Vorkath's head", 20)) {
                            if (foodInventorySize < 3 || !hasVenom || !hasSuperAntifire || !hasRangePotion || (!hasPrayerPotion && !Rs2Player.hasPrayerPoints())) {
                                leaveVorkath();
                            } else {
                                walkToCenter();
                                Rs2Player.waitForWalking();
                                calculateState();
                            }
                        }
                        break;
                    case TELEPORT_AWAY:
                        togglePrayer(false);
                        Rs2Player.toggleRunEnergy(true);
                        Rs2Inventory.wield(primaryBolts);
                        boolean reachedDestination = Rs2Bank.walkToBank();
                        if (reachedDestination) {
                            healAndDrinkPrayerPotion();
                            state = State.BANKING;
                        }
                        break;
                    case DEAD_WALK:
                        if (isCloseToRelleka()) {
                            Rs2Walker.walkTo(new WorldPoint(2640, 3693, 0));
                            torfin = Rs2Npc.getNpc(NpcID.TORFINN_10405);
                            if (torfin != null) {
                                Rs2Npc.interact(torfin, "Collect");
                                sleepUntil(() -> Rs2Widget.hasWidget("Retrieval Service"), 1500);
                                if (Rs2Widget.hasWidget("I'm afraid I don't have anything")) { // this means we looted all our stuff
                                    leaveVorkath();
                                    return;
                                }
                                final int invSize = Rs2Inventory.size();
                                Rs2Widget.clickWidget(39452678);
                                sleep(600);
                                Rs2Widget.clickWidget(39452678);
                                sleepUntil(() -> Rs2Inventory.size() != invSize);
                                boolean isWearingOriginalEquipment = MicrobotInventorySetup.wearEquipment("vorkath");
                                if (!isWearingOriginalEquipment) {
                                    int finalInvSize = Rs2Inventory.size();
                                    Rs2Widget.clickWidget(39452678);
                                    sleepUntil(() -> Rs2Inventory.size() != finalInvSize);
                                    MicrobotInventorySetup.wearEquipment("vorkath");
                                }
                            }
                        } else {
                            togglePrayer(false);
                            if (Rs2Inventory.hasItem("Rellekka teleport")) {
                                Rs2Inventory.interact("Rellekka teleport", "break");
                                Rs2Player.waitForAnimation();
                                return;
                            }
                            Rs2Bank.walkToBank();
                            Rs2Bank.openBank();
                            Rs2Bank.withdrawItem("Rellekka teleport");
                            sleep(150, 400);
                            Rs2Bank.closeBank();
                            sleepUntil(() -> Rs2Inventory.hasItem("Rellekka teleport"), 1000);
                        }
                        break;
                    case SELLING_ITEMS:
                        boolean soldAllItems = Rs2GrandExchange.sellLoot("vorkath");
                        if (soldAllItems) {
                            state = State.BANKING;
                        }
                        break;
                }

                init = false;

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        return true;
    }

    /**
     * Checks if we have killed x amount of vorkaths based on a config to sell items
     * @param config
     * @return true if we need to sell items
     */
    private boolean checkSellingItems(VorkathConfig config) {
        if (tempVorkathKills == 0) return false;
        LootTrackerRecord lootRecord = Microbot.getAggregateLootRecords("vorkath");
        if (lootRecord != null) {
            if (tempVorkathKills % config.SellItemsAtXKills() == 0) {
                state = State.SELLING_ITEMS;
                tempVorkathKills = 0;
                return true;
            }
        }
        return false;
    }

    private static void walkToCenter() {
        Rs2Walker.walkFastLocal(
                LocalPoint.fromScene(48, 58, Microbot.getClient().getTopLevelWorldView().getScene())
        );
    }

    /**
     * will heal and drink pray pots
     */
    private void healAndDrinkPrayerPotion() {
        while (!Rs2Player.isFullHealth() && !Rs2Inventory.getInventoryFood().isEmpty()) {
            Rs2Bank.closeBank();
            Rs2Player.eatAt(99);
            Rs2Player.waitForAnimation();
            hasInventory = false;
        }
        while (Microbot.getClient().getRealSkillLevel(Skill.PRAYER) != Microbot.getClient().getBoostedSkillLevel(Skill.PRAYER) && Rs2Inventory.hasItem(Rs2Potion.getPrayerPotionsVariants())) {
            Rs2Bank.closeBank();
            Rs2Inventory.interact(Rs2Potion.getPrayerPotionsVariants(), "drink");
            Rs2Player.waitForAnimation();
            hasInventory = false;
        }
    }

    private void leaveVorkath() {
        if (Rs2Inventory.hasItem(config.teleportMode().getItemName())) {
            togglePrayer(false);
            Rs2Player.toggleRunEnergy(true);
            switch(config.teleportMode()) {
                case VARROCK_TAB:
                    Rs2Inventory.interact(config.teleportMode().getItemName(), config.teleportMode().getAction());
                    break;
                case CRAFTING_CAPE:
                    Rs2Inventory.interact("crafting cape", "teleport");
                    break;
            }
            Rs2Player.waitForAnimation();
            sleepUntil(() -> !Microbot.getClient().isInInstancedRegion());
            state = State.TELEPORT_AWAY;
        }
    }

    private boolean drinkPotions() {
        if (Rs2Player.isAnimating()) return false;
        boolean drinkRangePotion = !Rs2Player.hasDivineBastionActive() && !Rs2Player.hasDivineRangedActive() && !Rs2Player.hasRangingPotionActive();
        boolean drinkAntiFire = !Rs2Player.hasAntiFireActive() && !Rs2Player.hasSuperAntiFireActive();
        boolean drinkAntiVenom = !Rs2Player.hasAntiVenomActive();

        if (drinkRangePotion) {
             Rs2Inventory.interact(Rs2Potion.getRangePotionsVariants(), "drink");
        }
        if (drinkAntiFire) {
             Rs2Inventory.interact("super antifire", "drink");
        }
        if (drinkAntiVenom) {
             Rs2Inventory.interact("venom", "drink");
        }

        if (!Microbot.getClient().getLocalPlayer().isInteracting() && state == State.PREPARE_FIGHT && (drinkRangePotion || drinkAntiFire || drinkAntiVenom))
            Rs2Player.waitForAnimation();

        return !drinkRangePotion && !drinkAntiFire && !drinkAntiVenom;
    }

    public void togglePrayer(boolean onOff) {
        if (Rs2Prayer.isOutOfPrayer()) return;
        if (Microbot.getClient().getRealSkillLevel(Skill.PRAYER) >= 74 && Microbot.getClient().getRealSkillLevel(Skill.DEFENCE) >= 70 && config.activateRigour()) {
            Rs2Prayer.toggle(Rs2PrayerEnum.RIGOUR, onOff);
        } else {
            Rs2Prayer.toggle(Rs2PrayerEnum.EAGLE_EYE, onOff);
        }
        Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_RANGE, onOff);
    }

    private void handleRedBall() {
        if (doesProjectileExistById(redProjectileId)) {
            redBallWalk();
            sleep(600);
            Rs2Npc.interact("Vorkath", "attack");
        }
    }

    private void handlePrayer() {
        drinkPrayer();
        togglePrayer(true);
    }

    private static void drinkPrayer() {
        if ((Microbot.getClient().getBoostedSkillLevel(Skill.PRAYER) * 100) / Microbot.getClient().getRealSkillLevel(Skill.PRAYER) < Random.random(25, 30)) {
            Rs2Inventory.interact(Rs2Potion.getPrayerPotionsVariants(), "drink");
        }
    }

    private boolean doesProjectileExistById(int id) {
        for (Projectile projectile : Microbot.getClient().getProjectiles()) {
            if (projectile.getId() == id) {
                //println("Projectile $id found")
                return true;
            }
        }
        return false;
    }

    private boolean isCloseToRelleka() {
        if (Microbot.getClient().getLocalPlayer() == null) return false;
        return Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(new WorldPoint(2670, 3634, 0)) < 80;
    }

    private void redBallWalk() {
        WorldPoint currentPlayerLocation = Microbot.getClient().getLocalPlayer().getWorldLocation();
        WorldPoint sideStepLocation = new WorldPoint(currentPlayerLocation.getX() + 2, currentPlayerLocation.getY(), 0);
        if (Random.random(0, 2) == 1) {
            sideStepLocation = new WorldPoint(currentPlayerLocation.getX() - 2, currentPlayerLocation.getY(), 0);
        }
        final WorldPoint _sideStepLocation = sideStepLocation;
        Rs2Walker.walkFastLocal(LocalPoint.fromWorld(Microbot.getClient(), _sideStepLocation));
        Rs2Player.waitForWalking();
        sleepUntil(() -> Microbot.getClient().getLocalPlayer().getWorldLocation().equals(_sideStepLocation));
    }

    WorldPoint findSafeTile() {
        WorldPoint swPoint = new WorldPoint(vorkath.getWorldLocation().getX() + 1, vorkath.getWorldLocation().getY() - 8, 0);
        WorldArea wooxWalkArea = new WorldArea(swPoint, 5, 1);

        List<WorldPoint> safeTiles = wooxWalkArea.toWorldPointList().stream().filter(this::isTileSafe).collect(Collectors.toList());

        // Find the closest safe tile by x-coordinate to the player
        return safeTiles.stream().min(Comparator.comparingInt(tile -> Math.abs(tile.getX() - Microbot.getClient().getLocalPlayer().getWorldLocation().getX()))).orElse(null);
    }


    boolean isTileSafe(WorldPoint tile) {
        return !acidPools.contains(tile)
                && !acidPools.contains(new WorldPoint(tile.getX(), tile.getY() + 1, tile.getPlane()))
                && !acidPools.contains(new WorldPoint(tile.getX(), tile.getY() + 2, tile.getPlane()))
                && !acidPools.contains(new WorldPoint(tile.getX(), tile.getY() + 3, tile.getPlane()));

    }

    private void handleAcidWalk() {
        if (!doesProjectileExistById(acidProjectileId) && !doesProjectileExistById(acidRedProjectileId) && Rs2GameObject.getGameObjects(ObjectID.ACID_POOL_32000).isEmpty()) {
            Rs2Npc.interact("Vorkath", "attack");
            state = State.FIGHT_VORKATH;
            acidPools.clear();
            return;
        }

        if (acidPools.isEmpty()) {
            Rs2GameObject.getGameObjects(ObjectID.ACID_POOL_32000).forEach(tileObject -> acidPools.add(tileObject.getWorldLocation()));
            Rs2GameObject.getGameObjects(ObjectID.ACID_POOL).forEach(tileObject -> acidPools.add(tileObject.getWorldLocation()));
            Rs2GameObject.getGameObjects(ObjectID.ACID_POOL_37991).forEach(tileObject -> acidPools.add(tileObject.getWorldLocation()));
        }

        WorldPoint safeTile = findSafeTile();
        WorldPoint playerLocation = Microbot.getClient().getLocalPlayer().getWorldLocation();

        if (safeTile != null) {
            if (playerLocation.equals(safeTile)) {
                Rs2Npc.interact(vorkath, "attack");
            } else {
                Rs2Player.eatAt(75);
                Rs2Walker.walkFastLocal(LocalPoint.fromWorld(Microbot.getClient(), safeTile));
            }
        }
    }
}
