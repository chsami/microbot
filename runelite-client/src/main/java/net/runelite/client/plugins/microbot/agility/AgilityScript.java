package net.runelite.client.plugins.microbot.agility;

import net.runelite.api.Skill;
import net.runelite.api.Tile;
import net.runelite.api.TileObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.agility.AgilityPlugin;
import net.runelite.client.plugins.agility.Obstacle;
import net.runelite.client.plugins.agility.Obstacles;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.agility.models.AgilityObstacleModel;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grounditem.Rs2GroundItem;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.models.RS2Item;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static net.runelite.client.plugins.microbot.util.math.Random.random;
import static net.runelite.client.plugins.worldmap.AgilityCourseLocation.GNOME_STRONGHOLD_AGILITY_COURSE;

public class AgilityScript extends Script {

    public static double version = 1.0;
    final int MAX_DISTANCE = 2350;

    public List<AgilityObstacleModel> draynorCourse = new ArrayList<>();
    public List<AgilityObstacleModel> alkharidCourse = new ArrayList<>();
    public List<AgilityObstacleModel> varrockCourse = new ArrayList<>();
    public List<AgilityObstacleModel> gnomeStrongholdCourse = new ArrayList<>();
    public List<AgilityObstacleModel> canafisCourse = new ArrayList<>();
    public List<AgilityObstacleModel> faladorCourse = new ArrayList<>();
    public List<AgilityObstacleModel> seersCourse = new ArrayList<>();
    public List<AgilityObstacleModel> polnivCourse = new ArrayList<>();


    WorldPoint startCourse = null;

    public static int currentObstacle = 0;

    private List<AgilityObstacleModel> getCurrentCourse(MicroAgilityConfig config) {
        switch (config.agilityCourse()) {
            case DRAYNOR_VILLAGE_ROOFTOP_COURSE:
                return draynorCourse;
            case AL_KHARID_ROOFTOP_COURSE:
                return alkharidCourse;
            case VARROCK_ROOFTOP_COURSE:
                return varrockCourse;
            case GNOME_STRONGHOLD_AGILITY_COURSE:
                return gnomeStrongholdCourse;
            case CANIFIS_ROOFTOP_COURSE:
                return canafisCourse;
            case FALADOR_ROOFTOP_COURSE:
                return faladorCourse;
            case SEERS_VILLAGE_ROOFTOP_COURSE:
                return seersCourse;
            case POLLNIVNEACH_ROOFTOP_COURSE:
                return polnivCourse;
            default:
                return canafisCourse;
        }
    }

    private void init(MicroAgilityConfig config) {
        switch (config.agilityCourse()) {
            case GNOME_STRONGHOLD_AGILITY_COURSE:
                startCourse = new WorldPoint(2474, 3436, 0);
                break;
            case DRAYNOR_VILLAGE_ROOFTOP_COURSE:
                startCourse = new WorldPoint(3103, 3279, 0);
                break;
            case AL_KHARID_ROOFTOP_COURSE:
                startCourse = new WorldPoint(3273, 3195, 0);
                break;
            case VARROCK_ROOFTOP_COURSE:
                startCourse = new WorldPoint(3221, 3414, 0);
                break;
            case CANIFIS_ROOFTOP_COURSE:
                startCourse = new WorldPoint(3507, 3489, 0);
                break;
            case FALADOR_ROOFTOP_COURSE:
                startCourse = new WorldPoint(3036, 3341, 0);
                break;
            case SEERS_VILLAGE_ROOFTOP_COURSE:
                startCourse = new WorldPoint(2729, 3486, 0);
                break;
            case POLLNIVNEACH_ROOFTOP_COURSE:
                startCourse = new WorldPoint(3351,2961,0);
                break;
        }
    }

    public boolean run(MicroAgilityConfig config) {
        currentObstacle = 0;
        init(config);
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            if (startCourse == null) {
                Microbot.showMessage("Agility course: " + config.agilityCourse().name() + " is not supported.");
            }
            try {
                final List<RS2Item> marksOfGrace = AgilityPlugin.getMarksOfGrace();
                final LocalPoint playerLocation = Microbot.getClient().getLocalPlayer().getLocalLocation();
                final WorldPoint playerWorldLocation = Microbot.getClient().getLocalPlayer().getWorldLocation();

                // Eat food.
                if (Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS) > config.hitpoints()) {
                    if (random(1, 10) == 2 && config.pauseRandomly()) {
                        sleep(random(config.pauseMinTime(), config.pauseMaxTime()));
                    }
                }
                else if (config.hitpoints() > 0) {
                    List<Rs2Item> foods = Microbot.getClientThread().runOnClientThread(Rs2Inventory::getInventoryFood);
                    if (foods.isEmpty()) {
                        return;
                    }

                    for (Rs2Item food : foods) {
                        Rs2Inventory.interact(food, "eat");
                        if (random(1, 10) == 2) { //double eat
                            Rs2Inventory.interact(food, "eat");
                        }
                        break;
                    }
                }

                if (Microbot.isMoving()) return;
                if (Microbot.isAnimating()) return;

                if (currentObstacle >= getCurrentCourse(config).size()) {
                    currentObstacle = 0;
                }

                if (Microbot.getClient().getPlane() == 0 && playerWorldLocation.distanceTo(startCourse) > 6 && config.agilityCourse() != GNOME_STRONGHOLD_AGILITY_COURSE) {
                    currentObstacle = 0;
                    LocalPoint startCourseLocal = LocalPoint.fromWorld(Microbot.getClient(), startCourse);
                    if (playerLocation.distanceTo(startCourseLocal) >= MAX_DISTANCE) {
                        Rs2Walker.walkTo(startCourse);
                        return;
                    }
                }

                if (!marksOfGrace.isEmpty()) {
                    for (RS2Item markOfGraceTile : marksOfGrace) {
                        if (Microbot.getClient().getPlane() != markOfGraceTile.getTile().getPlane()) continue;
                        //seers needs 7, falador needs 5 for the distance to
                        if (Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(markOfGraceTile.getTile().getWorldLocation()) > 7)
                            continue;
                        Rs2GroundItem.loot(markOfGraceTile.getItem().getId());
                        sleepUntil(() -> markOfGraceTile.getTile().getGroundItems() == null || markOfGraceTile.getTile().getGroundItems().isEmpty());
                        if (!marksOfGrace.isEmpty()) {
                            Rs2GroundItem.loot(markOfGraceTile.getItem().getId());
                            sleepUntil(() -> markOfGraceTile.getTile().getGroundItems() == null || markOfGraceTile.getTile().getGroundItems().isEmpty());
                        }
                        break;
                    }
                }

                for (Map.Entry<TileObject, Obstacle> entry : AgilityPlugin.getObstacles().entrySet()) {


                    TileObject object = entry.getKey();
                    Obstacle obstacle = entry.getValue();

                    Tile tile = obstacle.getTile();
                    if (tile.getPlane() == Microbot.getClient().getPlane()
                            && object.getLocalLocation().distanceTo(playerLocation) < MAX_DISTANCE) {
                        // This assumes that the obstacle is not clickable.
                        if (Obstacles.TRAP_OBSTACLE_IDS.contains(object.getId())) {
                            Polygon polygon = object.getCanvasTilePoly();
                            if (polygon != null) {
                                //empty for now
                            }
                            return;
                        }

                        AgilityObstacleModel courseObstacle = getCurrentCourse(config).get(currentObstacle);
                        final int agilityExp = Microbot.getClient().getSkillExperience(Skill.AGILITY);
                        //exception for weird objects
                        if (Rs2GameObject.interact(courseObstacle.getObjectID())) {
                            if (waitForAgilityObstabcleToFinish(agilityExp))
                                break;
                        }

                        if (Obstacles.PORTAL_OBSTACLE_IDS.contains(object.getId())) {
                            //empty for now
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
        return true;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    private boolean waitForAgilityObstabcleToFinish(int agilityExp) {
        sleepUntilOnClientThread(() -> agilityExp != Microbot.getClient().getSkillExperience(Skill.AGILITY)
                || (Microbot.getClient().getPlane() == 0 && currentObstacle != 0), 15000);
        sleepUntilOnClientThread(() -> !Microbot.isMoving() && !Microbot.isAnimating(), 10000);


        if (agilityExp != Microbot.getClient().getSkillExperience(Skill.AGILITY) || Microbot.getClient().getPlane() == 0) {
            currentObstacle++;
            sleep(400, 800);
            return true;
        }
        return false;
    }
}