package net.runelite.client.plugins.microbot.AgilityPyramid.tasks;

import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.AgilityPyramid.AgilitypyramidScript;
import net.runelite.client.plugins.microbot.AgilityPyramid.PyramidConfig;
import net.runelite.client.plugins.microbot.AgilityPyramid.data.Obstacle;
import net.runelite.client.plugins.microbot.AgilityPyramid.data.ObstacleData;
import net.runelite.client.plugins.microbot.AgilityPyramid.data.identifiers;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.util.List;

import static net.runelite.client.plugins.microbot.util.Global.sleep;
import static net.runelite.client.plugins.microbot.util.Global.sleepUntil;

public class HandleLevel {

    public static void HandleL1() {
        // Access the obstacles
        List<Obstacle> layer1 = ObstacleData.obstaclesL1;
        for (Obstacle obstacle : layer1) {

            //Extract data into variables
            String obstacleName = obstacle.obstacleName;
            String interactOption = obstacle.interactOption;
            int x1 = obstacle.x1;
            int y1 = obstacle.y1;
            int WIDTH = obstacle.width;
            int HEIGHT = obstacle.height;
            int obstacleID = obstacle.obstacleID;


            WorldPoint obstacleAnchorPoint = new WorldPoint(x1, y1, 1);

            WorldArea areaTest = new WorldArea(obstacleAnchorPoint, WIDTH, HEIGHT);
            if (areaTest.contains(Rs2Player.getWorldLocation())){
                System.out.println("Inside area where obstacle = " + obstacleName + ", with interaction: "+interactOption);

                if (obstacleName == "Plank") {
                    handlePlankObstacle();
                    break;

                }
                else if (!interactOption.equals("Climb-down")){
                    Rs2GameObject.interact(obstacleID, interactOption);
                    Rs2Player.waitForWalking();
                    sleepUntil(() -> !Rs2Player.isAnimating());
                    sleep(new java.util.Random().nextInt(201) + 150);
                    break;

                }





            }

        }

    }

    public static void HandleL2() {
        //access the obstacles
        List<Obstacle> layer2 = ObstacleData.obstaclesL2;
        for (Obstacle obstacle : layer2) {

            //Extract data into variables
            String obstacleName = obstacle.obstacleName;
            String interactOption = obstacle.interactOption;
            int x1 = obstacle.x1;
            int y1 = obstacle.y1;
            int WIDTH = obstacle.width;
            int HEIGHT = obstacle.height;
            int obstacleID = obstacle.obstacleID;

            WorldPoint obstacleAnchorPoint = new WorldPoint(x1, y1, 2);

            WorldArea areaTest = new WorldArea(obstacleAnchorPoint, WIDTH, HEIGHT);
            if (areaTest.contains(Rs2Player.getWorldLocation())){
                System.out.println("Inside area where obstacle = " + obstacleName + ", with interaction: "+interactOption +", "+obstacleID);

                    if ((!interactOption.equals("Climb-down"))){
                    Rs2GameObject.interact(obstacleID, interactOption);
                    Rs2Player.waitForWalking();
                        sleepUntil(() -> !Rs2Player.isAnimating() && !Rs2Player.isMoving());
                        sleep(new java.util.Random().nextInt(201) + 150);
                    break;
                    }


            }

        }

    }

    public static void HandleL3() {
        // Access the obstacles
        List<Obstacle> layer3 = ObstacleData.obstaclesL3;
        for (Obstacle obstacle : layer3) {

            //Extract data into variables
            String obstacleName = obstacle.obstacleName;
            String interactOption = obstacle.interactOption;
            int x1 = obstacle.x1;
            int y1 = obstacle.y1;
            int WIDTH = obstacle.width;
            int HEIGHT = obstacle.height;
            int obstacleID = obstacle.obstacleID;


            WorldPoint obstacleAnchorPoint = new WorldPoint(x1, y1, 3);

            WorldArea areaTest = new WorldArea(obstacleAnchorPoint, WIDTH, HEIGHT);
            if (areaTest.contains(Rs2Player.getWorldLocation())){
                System.out.println("Inside area where obstacle = " + obstacleName + ", with interaction: "+interactOption);

                if (obstacleName == "Plank") {
                    handlePlankObstacle();
                    break;

                }
                else if (!interactOption.equals("Climb-down")){
                    Rs2GameObject.interact(obstacleID, interactOption);
                    Rs2Player.waitForWalking();
                    sleepUntil(() -> !Rs2Player.isAnimating());
                    sleep(new java.util.Random().nextInt(201) + 150);
                    break;

                }





            }

        }

    }


    public static void HandleL4() {
        //access the obstacles
        List<Obstacle> layer4 = ObstacleData.obstaclesL4;
        for (Obstacle obstacle : layer4) {

            //Extract data into variables
            String obstacleName = obstacle.obstacleName;
            String interactOption = obstacle.interactOption;
            int x1 = obstacle.x1;
            int y1 = obstacle.y1;
            int WIDTH = obstacle.width;
            int HEIGHT = obstacle.height;
            int obstacleID = obstacle.obstacleID;

            WorldPoint obstacleAnchorPoint = new WorldPoint(x1, y1, 2);

            //handle two obstacles too close to eachother. For now I will just be walking closer to the correct obstacle,
            //later I should probably just filter for the obstacle, and chose the one at a certain coordinate.

            WorldPoint runAreaPoint = new WorldPoint(3043, 4701, 2);
            WorldArea runArea = new WorldArea(runAreaPoint, 4 , 2);
            if (runArea.contains(Rs2Player.getWorldLocation())){
                System.out.println("Inside the area where the wrong obstacle is the closes - Handling in a shitty way!");
                Rs2Walker.walkFastCanvas(new WorldPoint(3048, 4698, 2));
                Rs2Player.waitForWalking();
            }

            WorldArea areaTest = new WorldArea(obstacleAnchorPoint, WIDTH, HEIGHT);
            if (areaTest.contains(Rs2Player.getWorldLocation())){
                System.out.println("Inside area where obstacle = " + obstacleName + ", with interaction: "+interactOption +", "+obstacleID);


                if ((!interactOption.equals("Climb-down"))){
                    Rs2GameObject.interact(obstacleID, interactOption);
                    Rs2Player.waitForWalking();
                    sleepUntil(() -> !Rs2Player.isAnimating());
                    sleep(new java.util.Random().nextInt(201) + 150);
                    break;
                }
            }

        }

    }



    public static boolean hasTakenPyramid = false;
    public static void HandleL5() {
        // Access the obstacles
        List<Obstacle> layer5 = ObstacleData.obstaclesL5;
        for (Obstacle obstacle : layer5) {

            //Extract data into variables
            String obstacleName = obstacle.obstacleName;
            String interactOption = obstacle.interactOption;
            int x1 = obstacle.x1;
            int y1 = obstacle.y1;
            int WIDTH = obstacle.width;
            int HEIGHT = obstacle.height;
            int obstacleID = obstacle.obstacleID;


            WorldPoint obstacleAnchorPoint = new WorldPoint(x1, y1, 3);

            WorldArea areaTest = new WorldArea(obstacleAnchorPoint, WIDTH, HEIGHT);
            if (areaTest.contains(Rs2Player.getWorldLocation())){
                System.out.println("Inside area where obstacle = " + obstacleName + ", with interaction: "+interactOption);

                if (obstacleName == "Climbing rocks") {
                    System.out.println("Current obstacle is the climbing rocks!!");
                    if (!hasTakenPyramid){
                        System.out.println("We have not yet taken the pyramid top, so we're interacting with the climbing rocks");
                        Rs2GameObject.interact(obstacleID, interactOption);
                        sleep(850); //no waiting for walking, we want to force another interaction while we're getting the pyramid
                        hasTakenPyramid = true; //set true
                        break;
                    } else if (hasTakenPyramid) {
                        System.out.println("We've already taken the pyramid, so we're continuing to next obstacle");
                        Rs2GameObject.interact(10859, "Jump");
                        Rs2Player.waitForWalking();
                        sleepUntil(() -> !Rs2Player.isAnimating());
                        sleep(new java.util.Random().nextInt(201) + 150);
                        break;
                    }


                }
                else {
                    Rs2GameObject.interact(obstacleID, interactOption);
                    Rs2Player.waitForWalking();
                    sleep(540);
                    hasTakenPyramid = false;

                }





            }

        }

    }


    public static void  handleGroundLevel(PyramidConfig config){
        WorldPoint pyramidVercinityAnchor = new WorldPoint(3352, 2826, 0);
        WorldArea pyramidVercinityArea = new WorldArea(pyramidVercinityAnchor, 15, 10);


        //check if we have enough pyramids to turn in, then switch states if neccesary
        if (Rs2Inventory.count(identifiers.pyramidID) >= config.maxPyramids()){
            System.out.println("We have enough pyramids to turn in");
            AgilitypyramidScript.state = "TurnInPyramids";
        }
        if (config.minFood() > Rs2Inventory.count(identifiers.foodID) || config.minWaterskins() > Rs2Inventory.count(identifiers.waterskinID)){
            System.out.println("Less tha minimum food or waterskins, we will now be banking");
            if (Rs2Inventory.contains(identifiers.pyramidID)){
                //Forcefully turning in myramids before we go bank
                AgilitypyramidScript.state="TurnInPyramids";
            }

            else {AgilitypyramidScript.state = "BankHandler";}
        }

        //if inside area, start course
        else if (pyramidVercinityArea.contains(Rs2Player.getWorldLocation())){
            Rs2GameObject.interact("Stairs", "Climb-up");
            Rs2Player.waitForWalking(400);
        }
        //else, walk to the area
        else {
            Rs2Walker.walkTo(pyramidVercinityAnchor.getX()+2, pyramidVercinityAnchor.getY()+4, pyramidVercinityAnchor.getPlane());
            Rs2Player.waitForWalking(3000);
        }
    }

        public static void handlePlankObstacle(){
            if(Rs2GameObject.exists(10868)){
                Rs2GameObject.interact(10868, "Cross");
                Rs2Player.waitForWalking();
                sleepUntil(() -> !Rs2Player.isAnimating());
                sleep(new java.util.Random().nextInt(201) + 150);
            }
        }
    }



