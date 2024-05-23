package net.runelite.client.plugins.microbot.bossassist;


import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.NpcID;
import net.runelite.api.VarPlayer;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.bossassist.models.*;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;
import net.runelite.client.plugins.microbot.util.prayer.Rs2PrayerEnum;

import java.util.concurrent.TimeUnit;


public class BossAssistScript extends Script {
    public static double version = 0.1;

    public BOSS currentBoss = BOSS.NONE;

    private BossMonster currentTarget = null;
    public PrayStyle prayStyle;
    public BossAssistConfig config;

    public boolean run(BossAssistConfig config) {
        Microbot.enableAutoRunOn = false;
        this.config = config;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                if (config.isScurriousOn()) {
                    if (currentTarget == null || currentTarget.id != NpcID.SCURRIUS_7222 || currentTarget.id != NpcID.SCURRIUS) {
                        // Construct the boss with the needed data to use auto pray
                        if (config.GET_INSTANCE() == INSTANCES.PRIVATE) {
                            this.currentTarget = new BossMonster(NpcID.SCURRIUS_7222, 10693 ,10695, 10697);
                        } else {
                            this.currentTarget = new BossMonster(NpcID.SCURRIUS, 10693 ,10695, 10697);
                        }
                        System.out.println("Generated scurry in code with known values");
                    }
                    if ( isNearNPC(currentTarget.id)) {
                        currentBoss = BOSS.SCURRIUS;
                        Microbot.status = "PRAYING";
                        if(prayStyle == null) {
                            prayStyle = PrayStyle.MELEE;
                        }
                        currentTarget.npc =  Rs2Npc.getNpc(currentTarget.id);
                        //System.out.println("Current prayer style " + prayStyle.toString());
                        //.out.println("Current animmation of scurry " + currentTarget.npc.getAnimation());

                        if(config.SPEC_WEAPON() != SPEC_WEAPON.NONE) {
                            handleSpec();
                        }

                        switch (config.PRAYER_MODE()) {
                            case AUTO: {
                                handleScurryPrayersAuto(true);
                            }
                            case FLICK: {

                            }
                            case NONE: {

                            }
                        }
                    }
                    else if (currentTarget.npc != null && currentTarget.npc.isDead()) {
                        System.out.println("Boss is dead resetting");
                        handleScurryPrayersAuto(false);
                        prayStyle = null;
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
                            prayStyle = PrayStyle.RANGED;
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
                        this.currentTarget = new BossMonster(NpcID.OBOR, -1, -1);
                        System.out.println("Generated obor in code with known values");
                    }

                    System.out.println("Current animmation of target " + currentTarget.npc.getAnimation());
                    System.out.println("Current world are of target " + currentTarget.npc.getWorldArea());

                    if ( isNearNPC(currentTarget.id)) {
                        currentBoss = BOSS.OBOR;
                        Microbot.status = "PRAYING";
                        if(prayStyle == null) {
                            prayStyle = PrayStyle.MELEE;
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
                        prayStyle = null;
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


    private  boolean isNearNPC(int id) {
        net.runelite.api.NPC boss = Rs2Npc.getNpc(id);
        return boss != null;
    }

    private void handleSpec() {
        if (!Rs2Inventory.contains(config.SPEC_WEAPON().getName())) {
            return;
        }
        int currentSpecEnergy = Microbot.getClient().getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT);

        if(currentSpecEnergy >= config.SPEC_WEAPON().getSpecEnergy() && Rs2Equipment.getEquippedItem(EquipmentInventorySlot.WEAPON).name != config.SPEC_WEAPON().getName()) {
            Rs2Inventory.wield(config.SPEC_WEAPON().getName());
            sleep(0, 400);
            Rs2Combat.setSpecState(true);
        }
        else if (currentSpecEnergy >= config.SPEC_WEAPON().getSpecEnergy() && Rs2Equipment.getEquippedItem(EquipmentInventorySlot.WEAPON).name == config.SPEC_WEAPON().getName()) {
            Rs2Combat.setSpecState(true);
        }
    }

    private void handleScurryPrayersAuto (boolean on) {
        if(!on) {
            turnOffPrayers();
        }

        int currentAnimation = currentTarget.npc.getAnimation();
        handleProtectMelee(currentAnimation);
        handleProtectRange();
        handleProtectMagic();
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

    private void handleProtectMelee(int animation) {
        if ((currentTarget.attackAnimMelee == currentTarget.npc.getAnimation() && !Rs2Prayer.isPrayerActive(Rs2PrayerEnum.PROTECT_MELEE)) || (prayStyle == PrayStyle.MELEE && !Rs2Prayer.isPrayerActive(Rs2PrayerEnum.PROTECT_MELEE))) {
            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MELEE, true);
            prayStyle = PrayStyle.MELEE;
        }
    }

    private void handleProtectRange () {
        if ((currentTarget.npc.getAnimation() == currentTarget.attackAnimRange && !Rs2Prayer.isPrayerActive(Rs2PrayerEnum.PROTECT_RANGE)) || (prayStyle == PrayStyle.RANGED && !Rs2Prayer.isPrayerActive(Rs2PrayerEnum.PROTECT_RANGE))) {
            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_RANGE, true);
            prayStyle = PrayStyle.RANGED;
        }
    }

    private void handleProtectMagic () {
        if ((currentTarget.npc.getAnimation() == currentTarget.attackAnimMage && !Rs2Prayer.isPrayerActive(Rs2PrayerEnum.PROTECT_MAGIC))|| (prayStyle == PrayStyle.MAGE && !Rs2Prayer.isPrayerActive(Rs2PrayerEnum.PROTECT_MAGIC))) {
            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MAGIC, true);
            prayStyle = PrayStyle.MAGE;
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
