package net.runelite.client.plugins.hoseaplugins.ethanapi.PathingTesting;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.TileObjects;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.EthanApiPlugin;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.PathFinding.GlobalCollisionMap;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.MousePackets;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.MovementPackets;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.ObjectPackets;
import com.google.inject.Inject;
import com.google.inject.Provides;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Tile;
import net.runelite.api.WallObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@PluginDescriptor(name = "PathingTesting", description = "", enabledByDefault = false, tags = {"Testing"})
public class PathingTesting extends Plugin {
    static List<WorldPoint> path = new ArrayList<>();
    static List<WorldPoint> fullPath = new ArrayList<>();
    static WorldPoint currentPathDestination = null;
    static WorldPoint goal = null;
    @Inject
    PathingTestingConfig config;
    Random rand = new Random();
    @Inject
    ClientThread clientThread;
    @Inject
    OverlayManager overlayManager;
    PathingTestingOverlay overlay;
    @Provides
    public PathingTestingConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(PathingTestingConfig.class);
    }


    @Override
    protected void startUp() throws Exception {
        currentPathDestination = null;
        path = null;
        goal = null;
        fullPath = null;
        overlay = new PathingTestingOverlay(EthanApiPlugin.getClient(), this,config);
        overlayManager.add(overlay);
    }
    @Override
    protected void shutDown() throws Exception {
        currentPathDestination = null;
        path = null;
        goal = null;
        fullPath = null;
        overlayManager.remove(overlay);
    }
    public static boolean pathingTo(WorldPoint a){
        return goal!=null&& goal.equals(a);
    }
    public static boolean pathing(){
        return goal!=null;
    }
    public static boolean walkTo(WorldPoint goal){
        currentPathDestination = null;
        path = GlobalCollisionMap.findPath(goal);
        fullPath = new ArrayList<>(path);
        PathingTesting.goal = goal;
        currentPathDestination = null;
        return path != null;
    }
    @Subscribe
    private void onConfigChanged(ConfigChanged e) {
        if (e.getGroup().equals("PathingTesting") && e.getKey().equals("run")) {
            currentPathDestination = null;
            path = GlobalCollisionMap.findPath(new WorldPoint(config.x(), config.y(), EthanApiPlugin.getClient().getPlane()));
            fullPath = new ArrayList<>(path);
            goal = new WorldPoint(config.x(), config.y(), EthanApiPlugin.getClient().getPlane());
        }
        if (e.getGroup().equals("PathingTesting") && e.getKey().equals("stop")) {
            currentPathDestination = null;
            path = null;
            fullPath = null;
            clientThread.invoke(() -> {
                TileObjects.search().filter(x -> x instanceof WallObject).withAction("Open").nearestToPlayer().ifPresent(
                        tileObject -> {
//                            WallObject x = (WallObject) tileObject;
//                            System.out.println(x.getWorldLocation());
//                            System.out.println("Open A: " + x.getOrientationA());
//                            System.out.println("Open B: " + x.getOrientationB());
                        });
                TileObjects.search().filter(x -> x instanceof WallObject).withAction("Close").nearestToPlayer().ifPresent(
                        tileObject -> {
//                            WallObject x = (WallObject) tileObject;
//                            System.out.println(x.getWorldLocation());
//                            System.out.println("Close A: " + x.getOrientationA());
//                            System.out.println("Close B: " + x.getOrientationB());
                        });
                System.out.println(getTile(EthanApiPlugin.playerPosition()).getWallObject() == null);
            });
        }
    }

    @Subscribe
    private void onGameTick(GameTick e) {
        if(goal!=null&&goal.equals(EthanApiPlugin.playerPosition())){
            System.out.println("reached goal");
            goal = null;
            path = null;
            currentPathDestination = null;
            return;
        }
        if (path != null && path.size() >= 1) {
            if(currentPathDestination !=null&&!currentPathDestination.equals(EthanApiPlugin.playerPosition())&&!EthanApiPlugin.isMoving()){
                System.out.println("stopped walking. clicking destination again");
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(currentPathDestination);
            }
            if (currentPathDestination == null || currentPathDestination.equals(EthanApiPlugin.playerPosition()) || !EthanApiPlugin.isMoving()) {
                int step = rand.nextInt((35 - 10) + 1) + 10;
                int max = step;
                for (int i = 0; i < step; i++) {
                    if (path.size() - 2 >= i) {
                        if (isDoored(path.get(i), path.get(i + 1))) {
                            max = i;
                            break;
                        }
                    }
                }
                if(isDoored(EthanApiPlugin.playerPosition(), path.get(0))){
                    System.out.println("doored");
                    WallObject wallObject = getTile(EthanApiPlugin.playerPosition()).getWallObject();
                    if(wallObject == null){
                        wallObject = getTile(path.get(0)).getWallObject();
                    }
                    ObjectPackets.queueObjectAction(wallObject,false,"Open","Close");
                    return;
                }
                step = Math.min(max, path.size() - 1);
                currentPathDestination = path.get(step);
                if (path.indexOf(currentPathDestination) == path.size() - 1) {
                    path = null;
                } else {
                    path = path.subList(step + 1, path.size());
                }
                if (currentPathDestination.equals(EthanApiPlugin.playerPosition())) {
                    return;
                }
                System.out.println("taking a step");
                MousePackets.queueClickPacket();
                MovementPackets.queueMovement(currentPathDestination);
            }
        }
    }

    private boolean isDoored(WorldPoint a, WorldPoint b) {
        Tile tA = getTile(a);
        Tile tB = getTile(b);
        if (tA == null || tB == null) {
            return false;
        }
        return isDoored(tA, tB);
    }

    private boolean isDoored(Tile a, Tile b) {
        WallObject wallObject = a.getWallObject();
        if (wallObject != null) {
            ObjectComposition objectComposition = EthanApiPlugin.getClient().getObjectDefinition(wallObject.getId());
            if (objectComposition == null) {
                return false;
            }
            boolean found = false;
            for (String action : objectComposition.getActions()) {
                if (action != null && action.equals("Open")) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
            int orientation = wallObject.getOrientationA();
            if (orientation == 1) {
                //blocks west
                if (a.getWorldLocation().dx(-1).equals(b.getWorldLocation())) {
                    return true;
                }
            }
            if (orientation == 4) {
                //blocks east
                if (a.getWorldLocation().dx(+1).equals(b.getWorldLocation())) {
                    return true;
                }
            }
            if (orientation == 2) {
                //blocks north
                if (a.getWorldLocation().dy(1).equals(b.getWorldLocation())) {
                    return true;
                }
            }
            if (orientation == 8) {
                //blocks south
                return a.getWorldLocation().dy(-1).equals(b.getWorldLocation());
            }
        }
        WallObject wallObjectb = b.getWallObject();
        if (wallObjectb == null) {
            return false;
        }
        ObjectComposition objectCompositionb = EthanApiPlugin.getClient().getObjectDefinition(wallObjectb.getId());
        if (objectCompositionb == null) {
            return false;
        }
        boolean foundb = false;
        for (String action : objectCompositionb.getActions()) {
            if (action != null && action.equals("Open")) {
                foundb = true;
                break;
            }
        }
        if (!foundb) {
            return false;
        }
        int orientationb = wallObjectb.getOrientationA();
        if (orientationb == 1) {
            //blocks east
            if (b.getWorldLocation().dx(-1).equals(a.getWorldLocation())) {
                return true;
            }
        }
        if (orientationb == 4) {
            //blocks south
            if (b.getWorldLocation().dx(+1).equals(a.getWorldLocation())) {
                return true;
            }
        }
        if (orientationb == 2) {
            //blocks south
            if (b.getWorldLocation().dy(+1).equals(a.getWorldLocation())) {
                return true;
            }
        }
        if (orientationb == 8) {
            //blocks north
            return b.getWorldLocation().dy(-1).equals(a.getWorldLocation());
        }
        return false;
    }

    private Tile getTile(WorldPoint point) {
        LocalPoint a = LocalPoint.fromWorld(EthanApiPlugin.getClient(), point);
        if (a == null) {
            return null;
        }
        return EthanApiPlugin.getClient().getScene().getTiles()[point.getPlane()][a.getSceneX()][a.getSceneY()];
    }
}
