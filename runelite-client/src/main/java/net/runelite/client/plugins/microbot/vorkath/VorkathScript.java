package net.runelite.client.plugins.microbot.vorkath;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.MicrobotInventorySetup;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.prayer.Prayer;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;

import java.util.List;
import java.util.concurrent.TimeUnit;


enum State {
    BANKING,
    TELEPORT_TO_RELLEKKA,
    WALK_TO_VORKATH_ISLAND,
    WALK_TO_VORKATH,
    PREPARE_FIGHT,
    FIGHT_VORKATH,
    ZOMBIE_SPAWN,
    ACID,
    TELEPORT_AWAY
}

public class VorkathScript extends Script {
    public static double version = 1.0;

    State state = State.BANKING;

    private final int rangeProjectileId = 1477;
    private final int magicProjectileId = 393;
    private final int purpleProjectileId = 1471;
    private final int blueProjectileId = 1479;
    private final int whiteProjectileId = 395;
    private final int redProjectileId = 1481;
    private final int acidProjectileId = 1483;
    private final int acidRedProjectileId = 1482;

    private WorldPoint centerTile;
    private WorldPoint rightTile;
    private WorldPoint leftTile;

    public boolean run(VorkathConfig config) {
        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                /*
                 * Important classes:
                 * Inventory
                 * Rs2GameObject
                 * Rs2GroundObject
                 * Rs2NPC
                 * Rs2Bank
                 * etc...
                 */

                switch (state) {
                    case BANKING:
                        boolean hasEquipment = MicrobotInventorySetup.loadEquipment("vorkath");
                        boolean hasInventory = MicrobotInventorySetup.loadInventory("vorkath");
                        if (hasEquipment && hasInventory)
                            state = State.TELEPORT_TO_RELLEKKA;
                        break;
                    case TELEPORT_TO_RELLEKKA:
                        if (!isCloseToRelleka()) {
                            Rs2Inventory.interact("Rellekka teleport", "break");
                            sleepUntil(this::isCloseToRelleka);
                        }
                        if (isCloseToRelleka()) {
                            state = State.WALK_TO_VORKATH_ISLAND;
                        }
                        break;
                    case WALK_TO_VORKATH_ISLAND:
                        Microbot.getWalker().staticWalkTo(new WorldPoint(2640, 3693, 0));
                        net.runelite.api.NPC torfin = Rs2Npc.getNpc(NpcID.TORFINN_10405);
                        if (torfin != null) {
                            Rs2Npc.interact(torfin, "Ungael");
                            sleepUntil(() -> Rs2Npc.getNpc(NpcID.TORFINN_10405) == null);
                        }
                        if (Rs2Npc.getNpc(NpcID.TORFINN_10406) != null) {
                            state = State.WALK_TO_VORKATH;
                        }
                        break;
                    case WALK_TO_VORKATH:
                        Microbot.getWalker().walkMiniMap(new WorldPoint(2272, 4052, 0));
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
                        boolean drinkRangePotion = !Rs2Player.hasDivineBastionActive() && !Rs2Player.hasDivineRangedActive();
                        boolean drinkAntiFire = !Rs2Player.hasAntiFireActive() && !Rs2Player.hasSuperAntiFireActive();
                        boolean drinkAntiVenom = !Rs2Player.hasAntiVenomActive();

                        if (drinkRangePotion) {
                            Rs2Inventory.interact(config.rangePotion().toString(), "drink");
                        }
                        if (drinkAntiFire) {
                            Rs2Inventory.interact("super antifire", "drink");
                        }
                        if (drinkAntiVenom) {
                            Rs2Inventory.interact("venom", "drink");
                        }

                        if (!drinkRangePotion && !drinkAntiFire && !drinkAntiVenom) {
                            Rs2Npc.interact(NpcID.VORKATH_8059, "Poke");
                        }
                        NPC vorkath = Rs2Npc.getNpc(NpcID.VORKATH_8060);
                        if (vorkath != null) {
                            centerTile =
                                    new WorldPoint(vorkath.getWorldLocation().getX() + 3, vorkath.getWorldLocation().getY() - 5, vorkath.getWorldLocation().getPlane());
                            rightTile = new WorldPoint(centerTile.getX() + 2, centerTile.getY() - 3, centerTile.getPlane());
                            leftTile = new WorldPoint(centerTile.getX() - 2, centerTile.getY() - 3, centerTile.getPlane());
                            state = State.FIGHT_VORKATH;
                        }
                        break;
                    case FIGHT_VORKATH:
                        handlePrayer(config);
                        eatAt(51);
                        handleRedBall();
                        if (doesProjectileExistById(whiteProjectileId) || Rs2Npc.getNpc("Zombified Spawn") != null) {
                            state = State.ZOMBIE_SPAWN;
                        }
                        if ((doesProjectileExistById(acidProjectileId) || doesProjectileExistById(
                                acidRedProjectileId
                        ) || Rs2GameObject.findObjectById(ObjectID.ACID_POOL) != null
                        )) {
                            state = State.ACID;
                        }
                        break;
                    case ZOMBIE_SPAWN:
                        togglePrayer(false);
                        eatAt(75);
                        while (Rs2Npc.getNpc("Zombified Spawn") == null) {
                            sleep(100, 200);
                        }
                        if (config.SLAYERSTAFF().toString().equals("Cast")) {
                            Rs2Magic.castOn(MagicAction.CRUMBLE_UNDEAD, Rs2Npc.getNpc("Zombified Spawn"));
                        } else {
                            Rs2Inventory.wield(config.SLAYERSTAFF().toString());
                        }
                        Rs2Npc.attack("Zombified Spawn");
                        sleep(2300, 2500);
                        Rs2Inventory.wield(config.CROSSBOW().toString());
                        eatAt(75);
                        sleep(600, 1000);
                        Rs2Npc.attack("Vorkath");
                        state = State.FIGHT_VORKATH;
                        break;
                    case ACID:
                        togglePrayer(false);
                        handleAcidWalk();
                        break;
                    case TELEPORT_AWAY:
                        Rs2Inventory.interact(config.teleportMode().getItemName(), config.teleportMode().getAction());
                        boolean result = Rs2Bank.walkToBank();
                        if (result) {
                            state = State.BANKING;
                        }
                        break;
                }

                //interact with npc

                //teleport out
                //Rs2Inventory.interact(config.teleportMode().getItemName(), config.teleportMode().getAction());
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        return true;
    }

    private static void togglePrayer(boolean onOff) {
        if (Microbot.getClient().getRealSkillLevel(Skill.PRAYER) >= 77) {
            Rs2Prayer.fastPray(Prayer.RIGOUR, onOff);
        } else {
            Rs2Prayer.fastPray(Prayer.EAGLE_EYE, onOff);
        }
    }

    private void handleRedBall() {
        if (Microbot.getClient().getLocalPlayer().getIdlePoseAnimation() == 1 ||
                doesProjectileExistById(redProjectileId)) {
            redBallWalk();
            sleep(2100, 2200);
            Rs2Npc.attack("Vorkath");
        }
    }

    private static void handlePrayer(VorkathConfig config) {
        if ((Microbot.getClient().getBoostedSkillLevel(Skill.PRAYER) * 100) / Microbot.getClient().getRealSkillLevel(Skill.PRAYER) > Random.random(25, 30)) {
            Rs2Inventory.interact(config.prayerPotion().toString(), "drink");
        }
        Rs2Prayer.fastPray(Prayer.PROTECT_RANGE, true);
        togglePrayer(true);
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
        return Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(new WorldPoint(2670, 3634, 0)) < 50;
    }

    private void eatAt(int number) {
        double treshHold = (double) (Microbot.getClient().getRealSkillLevel(Skill.HITPOINTS) * Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS)) / 100;
        if (treshHold < number) {
            List<Rs2Item> foods = Microbot.getClientThread().runOnClientThread(Rs2Inventory::getInventoryFood);
            for (Rs2Item food : foods) {
                Rs2Inventory.interact(food, "eat");
                break;
            }
        }
    }

    private void redBallWalk() {
        WorldPoint currentPlayerLocation = Microbot.getClient().getLocalPlayer().getWorldLocation();
        WorldPoint twoTilesEastFromCurrentLocation = new WorldPoint(currentPlayerLocation.getX() + 2, currentPlayerLocation.getY(), 0);
        Microbot.getWalkerForKotlin().walkFastLocal(LocalPoint.fromWorld(Microbot.getClient(), twoTilesEastFromCurrentLocation));
    }

    private void handleAcidWalk() {
        if (Rs2GameObject.findObjectById(ObjectID.ACID_POOL) == null) {
            Rs2Npc.attack("Vorkath");
            state = State.FIGHT_VORKATH;
        }

        Microbot.getWalkerForKotlin().walkFastLocal(
                LocalPoint.fromScene(48, 54)
        );
        sleepUntil(() -> Microbot.getClient().getLocalPlayer().getLocalLocation().getSceneY() <= 55);
        Microbot.getWalkerForKotlin().walkFastLocal(
                LocalPoint.fromScene(42, 54)
        );
        sleep(2400, 3600);
        Microbot.getWalkerForKotlin().walkFastLocal(
                LocalPoint.fromScene(52, 54)
        );
        sleep(2400, 3600);
    }

    /**
     * Equipment + inventory
     * Relleka teleport
     * Walk to npc on dock
     * travel to npc on dock
     * walk to stone & skip stone
     * pot up & pray & wake up vorkath
     * kill
     * loot
     * varrock teleport
     * walk to bank
     * repeat
     */
}
