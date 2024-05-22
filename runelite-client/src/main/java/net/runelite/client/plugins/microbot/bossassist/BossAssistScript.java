package net.runelite.client.plugins.microbot.bossassist;

import net.runelite.api.Animation;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.bossassist.models.BossMonster;
import net.runelite.client.plugins.microbot.bossassist.models.DAMAGE_PRAYERS;
import net.runelite.client.plugins.microbot.bossassist.models.PrayStyle;
import net.runelite.client.plugins.microbot.playerassist.enums.AttackStyle;
import net.runelite.client.plugins.microbot.playerassist.model.Monster;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.npc.Rs2NpcManager;
import net.runelite.client.plugins.microbot.util.npc.Rs2NpcStats;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;
import net.runelite.client.plugins.microbot.util.prayer.Rs2PrayerEnum;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.npc.Rs2NpcManager.attackStyleMap;


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

                long startTime = System.currentTimeMillis();

                if (config.isScurriousOn()) {
                    if (currentTarget == null) {
                        // Construct the boss with the needed data to use auto pray
                        this.currentTarget = new BossMonster(NpcID.SCURRIUS_7222, 10693 ,10695, 10697);
                        System.out.println("Generated scurry in code with known values");
                    } //isNearNPC(NpcID.SCURRIUS) ||
                    if ( isNearNPC(NpcID.SCURRIUS_7222)) {
                        currentBoss = BOSS.SCURRIUS;
                        if(prayStyle == null) {
                            prayStyle = PrayStyle.MELEE;
                        }
                        currentTarget.npc =  Rs2Npc.getNpc(NpcID.SCURRIUS_7222);
                        System.out.println("Current prayer style " + prayStyle);
                        System.out.println("Current animmation of scurry " + currentTarget.npc.getAnimation());

                        switch (config.PRAYER_MODE()) {
                            case AUTO: {
                                handleScurryPrayersAuto();
                            }
                            case FLICK: {

                            }
                            case NONE: {

                            }
                        }
                    }
                } else {
                    currentBoss = BOSS.NONE;
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


    private  boolean isNearNPC(int id) {
        net.runelite.api.NPC boss = Rs2Npc.getNpc(id);
        return boss != null;
    }

    private void handleScurryPrayersAuto () {
        int currentAnimation = currentTarget.npc.getAnimation();
        if (currentTarget.attackAnimMelee == currentTarget.npc.getAnimation() || (prayStyle == PrayStyle.MELEE && !Rs2Prayer.isPrayerActive(Rs2PrayerEnum.PROTECT_MELEE))) {
            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MELEE, true);
            prayStyle = PrayStyle.MELEE;
        }
        if (currentAnimation == currentTarget.attackAnimRange || (prayStyle == PrayStyle.RANGED && !Rs2Prayer.isPrayerActive(Rs2PrayerEnum.PROTECT_RANGE))) {
            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_RANGE, true);
            prayStyle = PrayStyle.RANGED;

        }
        if (currentAnimation == currentTarget.attackAnimMage || (prayStyle == PrayStyle.MAGE && !Rs2Prayer.isPrayerActive(Rs2PrayerEnum.PROTECT_MAGIC))) {
            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MAGIC, true);
            prayStyle = PrayStyle.MAGE;
        }

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

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
