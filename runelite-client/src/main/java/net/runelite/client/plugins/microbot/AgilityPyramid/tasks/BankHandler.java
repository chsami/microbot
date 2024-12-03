package net.runelite.client.plugins.microbot.AgilityPyramid.tasks;

import net.runelite.api.Skill;
import net.runelite.api.World;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.AgilityPyramid.AgilitypyramidScript;
import net.runelite.client.plugins.microbot.AgilityPyramid.PyramidConfig;
import net.runelite.client.plugins.microbot.AgilityPyramid.data.Obstacle;
import net.runelite.client.plugins.microbot.AgilityPyramid.data.ObstacleData;
import net.runelite.client.plugins.microbot.AgilityPyramid.data.identifiers;
import net.runelite.client.plugins.microbot.globval.WidgetIndices;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.ArrayList;
import java.util.List;

import static net.runelite.client.plugins.microbot.util.Global.sleepUntil;

public class BankHandler {
    public static void HandleBank(PyramidConfig config){
        //check if we're in the clear to webwalk to bank, if not, navigate down
        if (Rs2Player.getWorldLocation().getPlane() != 0){
            NavigateDown();
        }
        else {
            WorldPoint bankAreaAnchor = new WorldPoint(3427, 2889,0);
            WorldArea bankArea = new WorldArea(bankAreaAnchor, 4,6);

            if (bankArea.contains(Rs2Player.getWorldLocation())){
                System.out.println("We are in the bank!");
                if (!Rs2Bank.isOpen()){
                    System.out.println("Opening bank");
                    Rs2Bank.openBank();
                    sleepUntil(() -> !Rs2Bank.isOpen());
                }
                else {
                    System.out.println("Handling bank");
                    Rs2Bank.depositAll();
                    Rs2Bank.withdrawX(identifiers.foodID, config.foodItemsToWithdraw());
                    Rs2Bank.withdrawX(identifiers.waterskinID, config.waterskinsToWithdraw());
                    if (Rs2Bank.hasItem(identifiers.pyramidID)){
                        Rs2Bank.withdrawAll(identifiers.pyramidID);
                    }
                    AgilitypyramidScript.state = "CompleteCourse";
                }
            }
            else {
                System.out.println("Walking to bank");
                Rs2Walker.walkTo(3427, 2891, 0);
                Rs2Player.waitForWalking();
            }
        }


    }
    public static void NavigateDown(){
        //Get current floor
        int currentLevel = Rs2Player.getWorldLocation().getPlane();

        if (currentLevel == 2 && Rs2Player.getWorldLocation().getX() < 3300){
            currentLevel = 4;
        }
        else if (currentLevel == 3 && Rs2Player.getWorldLocation().getX() < 3300){
            currentLevel = 5;
        }

        System.out.println("Current floor: " + currentLevel + " navigate down");
        System.out.println(findObstacleBehindPlayer(currentLevel).toString());

        String input = findObstacleBehindPlayer(currentLevel).toString();

        String obstacleName = input.split("obstacleName='")[1].split("'")[0];
        String interactOption = input.split("interactOption='")[1].split("'")[0];
        String obstacleID = input.split("obstacleID=")[1].split("}")[0];

        //interact with obstacle to get down
        if (Rs2Player.getBoostedSkillLevel(Skill.HITPOINTS) > 2){
            if (obstacleName.equals("Plank")){
                System.out.println("obstacle behind us is a plank and we detected that");
                Rs2GameObject.interact(10867, "Cross");
            }
            else {
                System.out.println("Obstacle behind us is a " + obstacleName);
                Rs2GameObject.interact(obstacleID, interactOption);

                Rs2Player.waitForWalking(500);
                Rs2GameObject.interact(obstacleName, interactOption);

            }

        }




    }
    //Create an empty list, to be populated with obstacles we've already passed, that way we can determine which obstacle is straight behind, allowing us to fall down

    public static Obstacle findObstacleBehindPlayer(int currentLevel) {
        // Map currentLevel to the corresponding obstacle list
        List<Obstacle> currentLayerObstacles;
        switch (currentLevel) {
            case 1:
                currentLayerObstacles = ObstacleData.obstaclesL1;
                break;
            case 2:
                currentLayerObstacles = ObstacleData.obstaclesL2;
                break;
            case 3:
                currentLayerObstacles = ObstacleData.obstaclesL3;
                break;
            case 4:
                currentLayerObstacles = ObstacleData.obstaclesL4;
                break;
            case 5:
                currentLayerObstacles = ObstacleData.obstaclesL5;
                break;
            default:
                System.out.println("Invalid level: " + currentLevel);
                return null;
        }

        if (currentLayerObstacles == null) {
            System.out.println("Invalid level: " + currentLevel);
            return null;
        }

        List<Obstacle> filteredObstacles = new ArrayList<>();
        Obstacle currentObstacle = null;

        for (Obstacle obstacle : currentLayerObstacles) {

            //get data of obstacle:
            String obstacleName = obstacle.obstacleName;
            String interactOption = obstacle.interactOption;
            int x1 = obstacle.x1;
            int y1 = obstacle.y1;
            int WIDTH = obstacle.width;
            int HEIGHT = obstacle.height;
            int obstacleID = obstacle.obstacleID;

            WorldPoint point = new WorldPoint(x1, y1, Rs2Player.getWorldLocation().getPlane());
            WorldArea area = new WorldArea(point, WIDTH, HEIGHT);

            //check if in area of current obstacle.
            if (area.contains(Rs2Player.getWorldLocation())) {
                System.out.println("Obstacle found,");
                return filteredObstacles.get(filteredObstacles.size() - 1);
            } else {
                filteredObstacles.add(obstacle); // Add non-current obstacles
            }
        }

        if (currentObstacle == null || filteredObstacles.isEmpty()) {
            System.out.println("No valid obstacle behind player.");
            return null;
        }
            System.out.println(filteredObstacles.stream().count());
        return filteredObstacles.get(filteredObstacles.size() - 1); // Return last in filtered list
    }
}
