package net.runelite.client.plugins.microbot.CrashedStar;

import net.runelite.api.GameObject;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.concurrent.TimeUnit;

public class CrashedStarScript extends Script {
    public static String version = "1.2.1";

    CrashedStarConfig config;

    public static int stardustMined;

    int miningAnim = 6746;

    public int animationID = -1;


    //Mining Animation 6746
    //GameObject ID for corners of star 29733


    public boolean run(CrashedStarConfig config) {
        Microbot.enableAutoRunOn = true;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn() || !super.run() || Rs2Player.isMoving()) return;
                long startTime = System.currentTimeMillis();

                if (Rs2GameObject.exists(29733)) {
                    handleMining();
                    Microbot.status = "Mining Crashed Star";
                } else return;

                long endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                System.out.println("Total time for loop " + totalTime);

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }



    public void handleMining() {
        GameObject star = Rs2GameObject.get("Crashed Star");
        if (star == null) {
            //System.out.println("No Star Found / You do not have the level required");
            Microbot.status = "Cannot Mine Star";
            return;
        }
        if (!Rs2Player.isAnimating() && star != null && Rs2Player.getSkillRequirement(Skill.MINING, 10) && Rs2GameObject.exists(ObjectID.CRASHED_STAR_41229)) {
            Rs2GameObject.interact(star, "Mine");
            System.out.println("Mining Tier 1");
            //Microbot.status = "Mining Crashed Star T1";
            sleepUntil(Rs2Player::isAnimating);
        } else if (!Rs2Player.isAnimating() && star != null && Rs2Player.getSkillRequirement(Skill.MINING, 20) && Rs2GameObject.exists(ObjectID.CRASHED_STAR_41228)) {
            Rs2GameObject.interact(star, "Mine");
            System.out.println("Mining Tier 2");
            //Microbot.status = "Mining Crashed Star T2";
            sleepUntil(Rs2Player::isAnimating);
        } else if (!Rs2Player.isAnimating() && star != null && Rs2Player.getSkillRequirement(Skill.MINING, 30) && Rs2GameObject.exists(ObjectID.CRASHED_STAR_41227)) {
            Rs2GameObject.interact(star, "Mine");
            System.out.println("Mining Tier 3");
            //Microbot.status = "Mining Crashed Star T3";
            sleepUntil(Rs2Player::isAnimating);
        } else if (!Rs2Player.isAnimating() && star != null && Rs2Player.getSkillRequirement(Skill.MINING, 40) && Rs2GameObject.exists(ObjectID.CRASHED_STAR_41226)) {
            Rs2GameObject.interact(star, "Mine");
            System.out.println("Mining Tier 4");
            //Microbot.status = "Mining Crashed Star T4";
            sleepUntil(Rs2Player::isAnimating);
        } else if (!Rs2Player.isAnimating() && star != null && Rs2Player.getSkillRequirement(Skill.MINING, 50) && Rs2GameObject.exists(ObjectID.CRASHED_STAR_41225)) {
            Rs2GameObject.interact(star, "Mine");
            System.out.println("Mining Tier 5");
            //Microbot.status = "Mining Crashed Star T5";
            sleepUntil(Rs2Player::isAnimating);
        } else if (!Rs2Player.isAnimating() && star != null && Rs2Player.getSkillRequirement(Skill.MINING, 60) && Rs2GameObject.exists(ObjectID.CRASHED_STAR_41224)) {
            Rs2GameObject.interact(star, "Mine");
            System.out.println("Mining Tier 6");
            //Microbot.status = "Mining Crashed Star T6";
            sleepUntil(Rs2Player::isAnimating);
        } else if (!Rs2Player.isAnimating() && star != null && Rs2Player.getSkillRequirement(Skill.MINING, 70) && Rs2GameObject.exists(ObjectID.CRASHED_STAR_41223)) {
            Rs2GameObject.interact(star, "Mine");
            System.out.println("Mining Tier 7");
            //Microbot.status = "Mining Crashed Star T7";
            sleepUntil(Rs2Player::isAnimating);
        } else if (!Rs2Player.isAnimating() && star != null && Rs2Player.getSkillRequirement(Skill.MINING, 80) && Rs2GameObject.exists(ObjectID.CRASHED_STAR_41021)) {
            Rs2GameObject.interact(star, "Mine");
            System.out.println("Mining Tier 8");
            //Microbot.status = "Mining Crashed Star T8";
            sleepUntil(Rs2Player::isAnimating);
        } else if (!Rs2Player.isAnimating() && star != null && Rs2Player.getSkillRequirement(Skill.MINING, 90) && Rs2GameObject.exists(ObjectID.CRASHED_STAR)) {
            Rs2GameObject.interact(star, "Mine");
            System.out.println("Mining Tier 9");
            //Microbot.status = "Mining Crashed Star T9";
            sleepUntil(Rs2Player::isAnimating);
        }
    }



    //Dpick special
    private void handleDragonPickaxeSpec() {
        if (Rs2Equipment.isWearing("dragon pickaxe")) {
            Rs2Combat.setSpecState(true, 1000);
        }
    }

    public int getStardustMined() {
        return stardustMined;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

}
