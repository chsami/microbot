package net.runelite.client.plugins.microbot.bossassist;


import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.NpcID;
import net.runelite.api.VarPlayer;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.bossassist.api.NPCUtils;
import net.runelite.client.plugins.microbot.bossassist.models.*;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.npc.Rs2NpcManager;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;
import net.runelite.client.plugins.microbot.util.prayer.Rs2PrayerEnum;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.npc.Rs2NpcManager.attackStyleMap;


public class BossAssistScript extends Script {
    public static double version = 0.1;

    public BOSS currentBoss = BOSS.NONE;

    private BossMonster currentTarget = null;
    public PRAYSTYLE prayStyle = PRAYSTYLE.OFF;
    public BossAssistConfig config;

    public boolean run(BossAssistConfig config) {
        Microbot.enableAutoRunOn = false;
        Rs2NpcManager.loadJson();
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            if(!Microbot.isLoggedIn() || Microbot.pauseAllScripts) return;
            this.config = config;
            try {
                WorldPoint instancePoint = WorldPoint.fromLocalInstance(Microbot.getClient(), Rs2Player.getLocalLocation());
                if (config.isScurriousOn() ) {
                    //Check if player is in scurry area
                    if (instancePoint.getRegionID() != 13210 || instancePoint.getRegionX() < 23) return;
                    if (currentTarget == null || ( (currentTarget.id != NpcID.SCURRIUS_7222 && config.GET_INSTANCE() == INSTANCES.PRIVATE) || ( currentTarget.id != NpcID.SCURRIUS && config.GET_INSTANCE() == INSTANCES.PUBLIC))) {
                        // Construct the boss with the needed data to use auto pray
                        if (config.GET_INSTANCE() == INSTANCES.PRIVATE) {
                            this.currentTarget = new BossMonster(NpcID.SCURRIUS_7222, new int [] {10693} ,new int[]{10695,10694 } , new int[] {10697, 10698}); // 10694 // 98 in final stage fuck
                        } else {
                            this.currentTarget = new BossMonster(NpcID.SCURRIUS, new int [] {10693} ,new int[]{10695,10694 } , new int[] {10696, 10697});
                        }
                        System.out.println("Generated scurry in code with known values");
                    }

                    if ( isNearNPC(currentTarget.id)) {
                        currentBoss = BOSS.SCURRIUS;
                        Microbot.status = "PRAYING";
                        if(prayStyle == null || prayStyle == PRAYSTYLE.OFF) {
                            prayStyle = PRAYSTYLE.MELEE;
                        }
                        currentTarget.npc =  Rs2Npc.getNpc(currentTarget.id);
                        //System.out.println("Current prayer style " + prayStyle.toString());
                        String attackStyle = attackStyleMap.get(currentTarget.npc.getId());
                        // Maybe check this for pose
                        System.out.println("Current attack style of scurry " + attackStyle);
                        System.out.println("Current pose Animation of scurry " + currentTarget.npc.getPoseAnimation());

                        System.out.println("Current animmation of scurry " + currentTarget.npc.getAnimation());
                        //System.out.println(Arrays.stream(currentTarget.attackAnimsMelee).anyMatch(x -> x == currentTarget.npc.getAnimation()));


                    }
                    else if (currentTarget.npc != null && currentTarget.npc.isDead()) {
                        System.out.println("Boss is dead resetting");
                        handleScurryPrayers(false, config.PRAYER_MODE() == PRAY_MODE.FLICK);
                        prayStyle = PRAYSTYLE.OFF;
                        Microbot.status = "IDLE";
                        sleepUntil(() -> isNearNPC(currentTarget.id));
                    }
                }
                else if (config.isArcheoOn()) {
                    if (currentTarget == null || currentTarget.id != NpcID.DERANGED_ARCHAEOLOGIST) {
                        // Construct the boss with the needed data to use auto pray
                        this.currentTarget = new BossMonster(NpcID.DERANGED_ARCHAEOLOGIST);
                        System.out.println("Generated archeo in code with known values");
                    }

                    System.out.println("Current animmation of target " + currentTarget.npc.getAnimation());
                    System.out.println("Current world are of target " + currentTarget.npc.getWorldArea());

                    if ( isNearNPC(currentTarget.id)) {
                        currentBoss = BOSS.DERANGED_ARCHEOLGIST;
                        Microbot.status = "PRAYING";
                        if(prayStyle == null) {
                            prayStyle = PRAYSTYLE.RANGED;
                        }
                        currentTarget.npc =  Rs2Npc.getNpc(currentTarget.id);

                        switch (config.PRAYER_MODE()) {
                            case AUTO: {
                                handleArcheoPrayersAuto(true);
                            }
                            case FLICK: {

                            }
                            case NONE: {

                            }
                        }
                    }
                    else if (currentTarget.npc != null && currentTarget.npc.isDead()) {
                        System.out.println("Boss is dead resetting");
                        handleArcheoPrayersAuto(false);
                        prayStyle = null;
                        Microbot.status = "IDLE";
                        sleepUntil(() -> isNearNPC(currentTarget.id));
                    }

                }
                else if (config.isOborOn()) {
                    if (currentTarget == null || currentTarget.id != NpcID.OBOR) {
                        // Construct the boss with the needed data to use auto pray
                        this.currentTarget = new BossMonster(NpcID.OBOR, new int[] {-1}, new int[] {-1});
                        System.out.println("Generated obor in code with known values");
                    }

                    if ( isNearNPC(currentTarget.id)) {
                        currentBoss = BOSS.OBOR;
                        Microbot.status = "PRAYING";

                        System.out.println("Current animmation of target " + currentTarget.npc.getAnimation());
                        System.out.println("Current world area of target " + currentTarget.npc.getWorldArea());
                        if(prayStyle == null || prayStyle == PRAYSTYLE.OFF) {
                            prayStyle = PRAYSTYLE.RANGED;
                        }
                        currentTarget.npc =  Rs2Npc.getNpc(currentTarget.id);

                        switch (config.PRAYER_MODE()) {
                            case AUTO: {
                                handeOborAuto(true);
                            }
                            case FLICK: {

                            }
                            case NONE: {

                            }
                        }
                    }
                    else if (currentTarget.npc != null && currentTarget.npc.isDead()) {
                        System.out.println("Boss is dead resetting");
                        handeOborAuto(false);
                        prayStyle = PRAYSTYLE.OFF;
                        Microbot.status = "IDLE";
                        sleepUntil(() -> isNearNPC(currentTarget.id));
                    }

                }
                else {
                    Microbot.status = "IDLE";
                    currentBoss = BOSS.NONE;
                }

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    public void handlePrayers()
    {
        if (config == null) return;
        if (Microbot.getClient().getLocalPlayer() == null) {
            return;
        }

        if(Rs2Prayer.isOutOfPrayer()) {
            return;
        }

        if (config.isScurriousOn() && currentBoss == BOSS.SCURRIUS && prayStyle != PRAYSTYLE.OFF) {
            handleScurryPrayers(true, config.PRAYER_MODE() == PRAY_MODE.FLICK);
        }

        if(config.SPEC_WEAPON() != SPEC_WEAPON.NONE) {
            handleSpec();
        }
    }

    private  boolean isNearNPC(int id) {
        net.runelite.api.NPC boss = Rs2Npc.getNpc(id);
        return boss != null;
    }

    private void handleSpec() {
        if (Microbot.getClient().getLocalPlayer() == null) {
            return;
        }
        if(currentTarget == null || currentBoss == BOSS.NONE) {
            return;
        }

        if (!Rs2Inventory.contains(config.SPEC_WEAPON().getName()) || config.SPEC_WEAPON() == SPEC_WEAPON.NONE) {
            return;
        }

        int currentSpecEnergy = Microbot.getClient().getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT);
        if(currentSpecEnergy >= config.SPEC_WEAPON().getSpecEnergy() && Rs2Equipment.get(EquipmentInventorySlot.WEAPON).name != config.SPEC_WEAPON().getName()) {
            Rs2Inventory.wield(config.SPEC_WEAPON().getName());
            toggleSpecialAttack();
            if(Rs2Combat.getSpecState()) {
                Rs2Npc.interact(currentTarget.id, "attack");
            }

        }
        else if (currentSpecEnergy >= config.SPEC_WEAPON().getSpecEnergy() && Rs2Equipment.get(EquipmentInventorySlot.WEAPON).name == config.SPEC_WEAPON().getName()) {
            toggleSpecialAttack();
            if(Rs2Combat.getSpecState()) {
                Rs2Npc.interact(currentTarget.id, "attack");
            }
        }
    }

    public void toggleSpecialAttack() {
        System.out.println("Current spec is " + Rs2Combat.getSpecState());
        if (!Rs2Combat.getSpecState()) {
            System.out.println("Turning spec on");
            Rs2Combat.setSpecState(true, config.SPEC_WEAPON().getSpecEnergy());
        }
    }

    private void handleScurryPrayers (boolean on, boolean flick) {
        if(!on) {
            turnOffPrayers();
        }
        if(prayStyle == PRAYSTYLE.OFF) {
            return;
        }

        handleProtectMelee(flick);
        handleProtectRange(flick);
        handleProtectMagic(flick);
        handleDamagePrayers();
    }

    private void handleArcheoPrayersAuto (boolean on) {
        if(!on) {
            turnOffPrayers();
        }
        if(Rs2Prayer.isPrayerActive(Rs2PrayerEnum.PROTECT_RANGE)) {
            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_RANGE, true);
        }

        handleDamagePrayers();
    }

    private void handeOborAuto (boolean on) {
        if(!on) {
            turnOffPrayers();
        }
        if(Rs2Prayer.isPrayerActive(Rs2PrayerEnum.PROTECT_RANGE)) {
            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_RANGE, true);
        }
        boolean isNear = NPCUtils.isNearNPC(currentTarget.id);
        System.out.println(isNear);

        handleDamagePrayers();

    }

    private void turnOffPrayers () {
        switch (prayStyle) {
            case MAGE: {
                if(Rs2Prayer.isPrayerActive(Rs2PrayerEnum.PROTECT_MAGIC)) {
                    Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MAGIC, false);
                }
            }
            case RANGED: {
                if(Rs2Prayer.isPrayerActive(Rs2PrayerEnum.PROTECT_RANGE)) {
                    Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_RANGE, false);
                }
            }
            case MELEE: {
                if(Rs2Prayer.isPrayerActive(Rs2PrayerEnum.PROTECT_MELEE)) {
                    Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MELEE, false);
                }
            }
        }

        if(config.DAMAGE_PRAYER() != DAMAGE_PRAYERS.NONE) {
            if(config.DAMAGE_PRAYER() == DAMAGE_PRAYERS.PIETY && Rs2Prayer.isPrayerActive(Rs2PrayerEnum.PIETY)) {
                Rs2Prayer.toggle(Rs2PrayerEnum.PIETY, false);
            }
            if(config.DAMAGE_PRAYER() == DAMAGE_PRAYERS.AUGURY && Rs2Prayer.isPrayerActive(Rs2PrayerEnum.AUGURY)) {
                Rs2Prayer.toggle(Rs2PrayerEnum.AUGURY, false);
            }
            if(config.DAMAGE_PRAYER() == DAMAGE_PRAYERS.RIGOUR && Rs2Prayer.isPrayerActive(Rs2PrayerEnum.RIGOUR)) {
                Rs2Prayer.toggle(Rs2PrayerEnum.RIGOUR, false);
            }
        }

        prayStyle = PRAYSTYLE.OFF;
    }

    private void handleDamagePrayers () {
        if(config.DAMAGE_PRAYER() != DAMAGE_PRAYERS.NONE) {
            if(config.DAMAGE_PRAYER() == DAMAGE_PRAYERS.PIETY && !Rs2Prayer.isPrayerActive(Rs2PrayerEnum.PIETY)) {
                    Rs2Prayer.toggle(Rs2PrayerEnum.PIETY, true);
            }
            if(config.DAMAGE_PRAYER() == DAMAGE_PRAYERS.AUGURY && !Rs2Prayer.isPrayerActive(Rs2PrayerEnum.AUGURY)) {
                Rs2Prayer.toggle(Rs2PrayerEnum.AUGURY, true);
            }
            if(config.DAMAGE_PRAYER() == DAMAGE_PRAYERS.RIGOUR && !Rs2Prayer.isPrayerActive(Rs2PrayerEnum.RIGOUR)) {
                Rs2Prayer.toggle(Rs2PrayerEnum.RIGOUR, true);
            }
        }
    }

    private void handleProtectMelee(boolean flick) {
        if ((Arrays.stream(currentTarget.attackAnimsMelee).anyMatch(x -> x == currentTarget.npc.getAnimation())  && !Rs2Prayer.isPrayerActive(Rs2PrayerEnum.PROTECT_MELEE)) || (prayStyle == PRAYSTYLE.MELEE && !Rs2Prayer.isPrayerActive(Rs2PrayerEnum.PROTECT_MELEE))) {
            if(!flick) {
                Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MELEE, true);
            } else {
                Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MELEE, true);
                sleep(400);
                Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MELEE, false);

            }
            prayStyle = PRAYSTYLE.MELEE;
        }
    }

    private void handleProtectRange (boolean flick) {
        if ((Arrays.stream(currentTarget.attackAnimsRange).anyMatch(x -> x == currentTarget.npc.getAnimation())  && !Rs2Prayer.isPrayerActive(Rs2PrayerEnum.PROTECT_RANGE)) || (prayStyle == PRAYSTYLE.RANGED && !Rs2Prayer.isPrayerActive(Rs2PrayerEnum.PROTECT_RANGE))) {
            if(!flick) {
                Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_RANGE, true);

            } else {
                Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_RANGE, true);
                sleep(400);
                Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_RANGE, false);
            }
            prayStyle = PRAYSTYLE.RANGED;
        }
    }

    private void handleProtectMagic (boolean flick) {
        if ((Arrays.stream(currentTarget.attackAnimsMage).anyMatch(x -> x == currentTarget.npc.getAnimation())  && !Rs2Prayer.isPrayerActive(Rs2PrayerEnum.PROTECT_MAGIC))|| (prayStyle == PRAYSTYLE.MAGE && !Rs2Prayer.isPrayerActive(Rs2PrayerEnum.PROTECT_MAGIC))) {
            if(!flick) {
                Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MAGIC, true);

            } else {
                Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MAGIC, true);
                sleep(400);
                Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MAGIC, false);
            }
            prayStyle = PRAYSTYLE.MAGE;
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
