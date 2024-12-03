package net.runelite.client.plugins.microbot.AgilityPyramid.tasks;

import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.AgilityPyramid.AgilitypyramidScript;
import net.runelite.client.plugins.microbot.AgilityPyramid.PyramidConfig;
import net.runelite.client.plugins.microbot.AgilityPyramid.data.identifiers;
import net.runelite.client.plugins.microbot.globval.WidgetIndices;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.Random;

import static net.runelite.client.plugins.microbot.util.Global.sleep;
import static net.runelite.client.plugins.microbot.util.Global.sleepUntil;
import static net.runelite.client.plugins.microbot.util.walker.Rs2Walker.isInArea;
import static net.runelite.client.plugins.microbot.util.walker.Rs2Walker.isNear;

public class CompleteCourse {


    public static void main(PyramidConfig config) {


        //handle last 2 floors, since we aparently don't have a 4th and 5th plane
        int currentLevel = Rs2Player.getWorldLocation().getPlane();

        if (currentLevel == 2 && Rs2Player.getWorldLocation().getX() < 3300) {
            currentLevel = 4;
        } else if (currentLevel == 3 && Rs2Player.getWorldLocation().getX() < 3300) {
            currentLevel = 5;
        }

        System.out.println("We are on layer: " + currentLevel);


        //check for waterksins
        // Check for the specified IDs in inventory



        //handle eating
        if (Rs2Player.getBoostedSkillLevel(Skill.HITPOINTS) <= 10){
            int foodItemsCount = Rs2Inventory.count(identifiers.foodID);
            if (Rs2Inventory.contains(identifiers.foodID)){
                Rs2Inventory.interact(identifiers.foodID, "eat");
                sleepUntil(() -> foodItemsCount > Rs2Inventory.count(identifiers.foodID), 1000);
            }
        }


        switch (currentLevel) {
            case 0:
                System.out.println("You are on ground level.");
                //add code to handle walking to pyramid if not there, and starting the course
                HandleLevel.handleGroundLevel(config);
                break;
            case 1:
                System.out.println("You are on the first level.");
                HandleLevel.HandleL1();

                break;
            case 2:
                System.out.println("You are on the second floor.");
                HandleLevel.HandleL2();
                break;
            case 3:
                System.out.println("You are on the third floor.");
                HandleLevel.HandleL3();
                break;
            case 4:
                System.out.println("You are on the fourth floor.");
                HandleLevel.HandleL4();
                break;
            case 5:
                System.out.println("You are on the fifth floor.");
                HandleLevel.HandleL5();
                break;
            case 6:
                System.out.println("You are on the sixth floor.");
                break;
            default:
                System.out.println("Invalid level.");
                break;
        }

    }
}
