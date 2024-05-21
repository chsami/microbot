package net.runelite.client.plugins.microbot.derangedarchaeologist;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.playerassist.combat.FlickerScript;
import net.runelite.client.plugins.microbot.util.MicrobotInventorySetup;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;
import net.runelite.client.plugins.microbot.util.prayer.Rs2PrayerEnum;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.MicrobotInventorySetup.doesEquipmentMatch;
import static net.runelite.client.plugins.microbot.util.MicrobotInventorySetup.doesInventoryMatch;
import static net.runelite.client.plugins.microbot.util.npc.Rs2Npc.interact;


enum State {
    BANKING,
    FIGHTING_BOSS,
    WAITING_FOR_RESPAWN,
    WALKING_TO_BOSS,
    DEAD,
    PREPARE_FIGHT,
    DODGING_BOOKS,
    LOOT_ITEMS,
    TELEPORT_AWAY,
    CROSSING_LOG
}
public class DerangedAchaeologistScript extends Script {

    State state = State.CROSSING_LOG;

    DerangedaAchaeologistConfig config;

    private final int LOG_TO_CROSS = 31842;

    private boolean init = true;

    // BOOK ATTACK 1260
    public boolean run(DerangedaAchaeologistConfig config) {
        this.config = config;
        Microbot.enableAutoRunOn = false;
        this.init = true;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                if (!Microbot.isLoggedIn()) return;
                if (init) {
                    calculateState();
                }

                init = false;


                // this the main loop i hope
                switch (state) {
                    case CROSSING_LOG: {
                        if (isNearTrunk() && !Rs2Player.isAnimating()) {
//                            Rs2GameObject.interact(ObjectID.DECAYING_TRUNK, "Climb-over");
//                            sleep(400);
//                            state = State.FIGHTING_BOSS;
                        }
                    }
                    case WAITING_FOR_RESPAWN: {
                        if(isNearArcheo()) {
                            net.runelite.api.NPC boss = Rs2Npc.getNpc(NpcID.DERANGED_ARCHAEOLOGIST);
                            Rs2Npc.interact(boss, "attack");
                            sleepUntil(() -> Rs2Combat.inCombat());
                            state = State.FIGHTING_BOSS;
                        }
                    }
                    case FIGHTING_BOSS: {
                        if (config.PRAYER_MODE() == PRAY_MODE.AUTO) {
                            if(!Rs2Prayer.isQuickPrayerEnabled()) {
                                setPrayer(true);
                            }
                        } else {
                            setPrayer(true);
                        }

                        if(!Rs2Combat.inCombat() && isNearArcheo()) {
                            net.runelite.api.NPC boss = Rs2Npc.getNpc(NpcID.DERANGED_ARCHAEOLOGIST);
                            Rs2Npc.interact(boss, "attack");
                            sleepUntil(() -> Rs2Combat.inCombat());

                        } else {
                            state = State.WAITING_FOR_RESPAWN;
                        }

                        if(doesProjectileExistById(1260)) {
                            state = State.DODGING_BOOKS;
                            handleBooks();

                        }
                    }


                }

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 100, TimeUnit.MILLISECONDS);




        return true;
    }

    private void calculateState() {
        if (isNearArcheo()) {
            state = State.FIGHTING_BOSS;
        }
        if (isNearTrunk()) {
            state = State.CROSSING_LOG;
        }
        if (isCloseToBossLair()) {
            state = State.WALKING_TO_BOSS;
        }
    }

    private void handleBooks() {
        if (doesProjectileExistById(1260)) {
            bookDodge();
            net.runelite.api.NPC boss = Rs2Npc.getNpc(NpcID.DERANGED_ARCHAEOLOGIST);
            Rs2Npc.interact(boss, "attack");
            state = State.FIGHTING_BOSS;
        }
    }

    private void bookDodge() {
        Projectile currBooks = null;
        for (Projectile projectile : Microbot.getClient().getProjectiles()) {
            if (projectile.getId() == 1260) {
                currBooks = projectile;
            }
        }

        if (currBooks != null) {
            LocalPoint currentPlayerLocation = Microbot.getClient().getLocalPlayer().getLocalLocation();
            System.out.println("Current distance to projectiles is" + currentPlayerLocation.distanceTo(currBooks.getTarget()));

            if (currentPlayerLocation.distanceTo(currBooks.getTarget()) > 2) {
                return;
            }

            WorldPoint sideStepLocation = new WorldPoint(currentPlayerLocation.getX() + 3, currentPlayerLocation.getY() - 3, 0);
            if (Random.random(0, 2) == 1) {
                sideStepLocation = new WorldPoint(currentPlayerLocation.getX() - 3, currentPlayerLocation.getY() + 3, 0);
            }
            final WorldPoint _sideStepLocation = sideStepLocation;
            Rs2Walker.walkFastLocal(LocalPoint.fromWorld(Microbot.getClient(), _sideStepLocation));
            Rs2Player.waitForWalking();
            Projectile finalCurrBooks = currBooks;
            sleepUntil(() -> Microbot.getClient().getLocalPlayer().getWorldLocation().equals(currentPlayerLocation.distanceTo(finalCurrBooks.getTarget()) > 2));
        }

    }


    private boolean doesProjectileExistById(int id) {
        for (Projectile projectile : Microbot.getClient().getProjectiles()) {
            if (projectile.getId() == id) {
                System.out.println("Found projectile with id " + id);

                return true;
            }
        }
        return false;
    }
    private  boolean isNearArcheo () {
        net.runelite.api.NPC boss = Rs2Npc.getNpc(NpcID.DERANGED_ARCHAEOLOGIST);
        return boss != null;
    }

    private boolean isCloseToBossLair() {
        if (Microbot.getClient().getLocalPlayer() == null) return false;
        return Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(new WorldPoint(2670, 3634, 0)) < 80;
    }

    private boolean isNearTrunk () {
        TileObject trunk = Rs2GameObject.findObjectById(ObjectID.DECAYING_TRUNK);
        return trunk != null;

    }

    private void setPrayer (boolean on) {
        if(config.PRAYER_MODE() == PRAY_MODE.AUTO) {
            if (Rs2Prayer.isOutOfPrayer()) return;
            if (Microbot.getClient().getRealSkillLevel(Skill.PRAYER) >= 77 && Microbot.getClient().getRealSkillLevel(Skill.PRAYER) >= 77 && config.activateAugyury()) {
                Rs2Prayer.toggle(Rs2PrayerEnum.AUGURY, on);
            }
            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_RANGE, on);
        } else if (config.PRAYER_MODE() == PRAY_MODE.FLICK) {
            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_RANGE, true);
            if (Microbot.getClient().getRealSkillLevel(Skill.PRAYER) >= 77 && Microbot.getClient().getRealSkillLevel(Skill.PRAYER) >= 77 && config.activateAugyury()) {
                Rs2Prayer.toggle(Rs2PrayerEnum.AUGURY, true);
            }
            sleep(400);
            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_RANGE, false);
            if (Microbot.getClient().getRealSkillLevel(Skill.PRAYER) >= 77 && Microbot.getClient().getRealSkillLevel(Skill.PRAYER) >= 77 && config.activateAugyury()) {
                Rs2Prayer.toggle(Rs2PrayerEnum.AUGURY, false);
            }
        }

    }
}
