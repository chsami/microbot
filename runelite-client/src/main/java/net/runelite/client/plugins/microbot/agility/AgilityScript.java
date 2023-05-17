package net.runelite.client.plugins.microbot.agility;

import net.runelite.api.Skill;
import net.runelite.api.Tile;
import net.runelite.api.TileObject;
import net.runelite.api.World;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.agility.AgilityPlugin;
import net.runelite.client.plugins.agility.Obstacle;
import net.runelite.client.plugins.agility.Obstacles;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.agility.models.AgilityObstacleModel;
import net.runelite.client.plugins.microbot.util.camera.Camera;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.math.Calculations;
import net.runelite.client.plugins.worldmap.AgilityCourseLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static net.runelite.client.plugins.worldmap.AgilityCourseLocation.CANIFIS_ROOFTOP_COURSE;
import static net.runelite.client.plugins.worldmap.AgilityCourseLocation.FALADOR_ROOFTOP_COURSE;

public class AgilityScript extends Script {

    public static double version = 1.0;
    final int MAX_DISTANCE = 2350;

    public List<AgilityObstacleModel> canafisCourse = new ArrayList<>();
    public List<AgilityObstacleModel> faladorCourse = new ArrayList<>();
    public List<AgilityObstacleModel> seersCourse = new ArrayList<>();


    WorldPoint startCourse = new WorldPoint(0, 0, 0);

    int currentObstacle = 0;

    private List<AgilityObstacleModel> getCurrentCourse(MicroAgilityConfig config) {
        switch (config.agilityCourse()) {
            case CANIFIS_ROOFTOP_COURSE:
                return canafisCourse;
            case FALADOR_ROOFTOP_COURSE:
                return faladorCourse;
            case SEERS_VILLAGE_ROOFTOP_COURSE:
                return seersCourse;
            default:
                return canafisCourse;
        }
    }

    private void init(MicroAgilityConfig config) {
        switch (config.agilityCourse()) {
            case CANIFIS_ROOFTOP_COURSE:
                startCourse = new WorldPoint(3507, 3489, 0);
                break;
            case FALADOR_ROOFTOP_COURSE:
                startCourse = new WorldPoint(3036, 3341, 0);
                break;
            case SEERS_VILLAGE_ROOFTOP_COURSE:
                startCourse = new WorldPoint(2729, 3486, 0);
                break;
        }
    }

    public boolean run(MicroAgilityConfig config) {
        currentObstacle = 0;
        init(config);
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            try {
                final List<Tile> marksOfGrace = AgilityPlugin.getMarksOfGrace();
                final LocalPoint playerLocation = Microbot.getClient().getLocalPlayer().getLocalLocation();
                final WorldPoint playerWorldLocation = Microbot.getClient().getLocalPlayer().getWorldLocation();

                if (Microbot.isWalking()) return;
                if (Microbot.isAnimating()) return;

                if (Microbot.getClient().getPlane() == 0 && playerWorldLocation.distanceTo(startCourse) > 6) {
                    currentObstacle = 0;
                    LocalPoint startCourseLocal = LocalPoint.fromWorld(Microbot.getClient(), startCourse);
                    if (!Camera.isTileOnScreen(LocalPoint.fromWorld(Microbot.getClient(), startCourse))
                            || playerLocation.distanceTo(startCourseLocal) >= MAX_DISTANCE) {
                        Microbot.getWalker().walkTo(startCourse, true, false);
                        return;
                    }
                }

                if (!marksOfGrace.isEmpty()) {
                    for (Tile markOfGraceTile : marksOfGrace) {
                        if (Microbot.getClient().getPlane() != markOfGraceTile.getPlane()) continue;
                        //seers needs 7, falador needs 5 for the distance to
                        if (Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(markOfGraceTile.getWorldLocation()) > 7)
                            continue;
                        Rs2GameObject.interact(markOfGraceTile);
                        sleepUntil(() -> marksOfGrace.isEmpty());
                        if (!marksOfGrace.isEmpty()) {
                            Rs2GameObject.interact(markOfGraceTile);
                            sleepUntil(() -> marksOfGrace.isEmpty());
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
                        Shape objectClickbox = object.getClickbox();
                        if (objectClickbox != null) {
                            AgilityObstacleModel courseObstacle = getCurrentCourse(config).get(currentObstacle);
                            if (Rs2GameObject.interact(courseObstacle.getObjectID())) {
                                final int agilityExp = Microbot.getClient().getSkillExperience(Skill.AGILITY);
                                sleepUntilOnClientThread(() -> agilityExp != Microbot.getClient().getSkillExperience(Skill.AGILITY)
                                        || (Microbot.getClient().getPlane() == 0 && currentObstacle != 0), 10000);
                                sleepUntilOnClientThread(() -> !Microbot.isWalking() && !Microbot.isAnimating(), 10000);


                                if (agilityExp != Microbot.getClient().getSkillExperience(Skill.AGILITY)) {
                                    currentObstacle++;
                                    break;
                                }
                            }


                            if (Obstacles.PORTAL_OBSTACLE_IDS.contains(object.getId())) {
                                //empty for now
                            }
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
}
