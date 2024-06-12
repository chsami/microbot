package net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.*;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.query.QuickPrayer;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.PathFinding.Node;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import lombok.SneakyThrows;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.RuneLite;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginInstantiationException;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.util.Text;
import net.runelite.client.util.WildcardMatcher;

import javax.swing.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static net.runelite.api.Varbits.QUICK_PRAYER;

@PluginDescriptor(
        name = "EthanApiPlugin",
        description = "",
        tags = {"ethan"},
        hidden = false
)
public class EthanApiPlugin extends Plugin {

    static ClientUI clientUI = RuneLite.getInjector().getInstance(ClientUI.class);
    static Client client = RuneLite.getInjector().getInstance(Client.class);
    static PluginManager pluginManager = RuneLite.getInjector().getInstance(PluginManager.class);
    static ItemManager itemManager = RuneLite.getInjector().getInstance(ItemManager.class);
    static Method doAction = null;
    static String animationField = null;
    static final HashSet<WorldPoint> EMPTY_SET = new HashSet<>();
    public static final int[][] directionsMap = {
            {-2, 0},
            {0, 2},
            {2, 0},
            {0, -2},
            {1, 0},
            {0, 1},
            {-1, 0},
            {0, -1},
            {1, 1},
            {-1, -1},
            {-1, 1},
            {1, -1},
            {-2, 2},
            {-2, -2},
            {2, 2},
            {2, -2},
            {-2, -1},
            {-2, 1},
            {-1, -2},
            {-1, 2},
            {1, -2},
            {1, 2},
            {2, -1},
            {2, 1}
    };
    @Inject
    EventBus eventBus;
    public static LoadingCache<Integer, ItemComposition> itemDefs = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(20, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<Integer, ItemComposition>() {
                        @Override
                        public ItemComposition load(Integer itemId) {
                            return itemManager.getItemComposition(itemId);
                        }
                    });


    public static boolean loggedIn() {
        return client.getGameState() == GameState.LOGGED_IN;
    }

    public static boolean inRegion(int regionID) {
        List<Integer> mapRegions = Arrays.stream(client.getMapRegions()).boxed().collect(Collectors.toList());
        return mapRegions.contains(regionID);
    }

    public static WorldPoint playerPosition() {
        return client.getLocalPlayer().getWorldLocation();
    }

    public static SkullIcon getSkullIcon(Player player) {
        Field skullField = null;
        try {
            skullField = player.getClass().getDeclaredField("aj");
            skullField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
        int var1 = -1;
        try {
            var1 = skullField.getInt(player) * 1181672555;
            skullField.setAccessible(false);
        } catch (IllegalAccessException | NullPointerException e) {
            e.printStackTrace();
        }
        switch (var1) {
            case 0:
                return SkullIcon.SKULL;
            case 1:
                return SkullIcon.SKULL_FIGHT_PIT;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            default:
                return null;
            case 8:
                return SkullIcon.DEAD_MAN_FIVE;
            case 9:
                return SkullIcon.DEAD_MAN_FOUR;
            case 10:
                return SkullIcon.DEAD_MAN_THREE;
            case 11:
                return SkullIcon.DEAD_MAN_TWO;
            case 12:
                return SkullIcon.DEAD_MAN_ONE;
        }
    }

    public static boolean isQuickPrayerActive(QuickPrayer prayer) {
        return (client.getVarbitValue(4102) & (int) Math.pow(2, prayer.getIndex())) == Math.pow(2, prayer.getIndex());
    }

    public static boolean isQuickPrayerEnabled() {
        return client.getVarbitValue(QUICK_PRAYER) == 1;
    }


    @SneakyThrows
    public static int getAnimation(NPC npc) {
        if (npc == null) {
            return -1;
        }
        if (animationField == null) {
            for (Field declaredField : npc.getClass().getSuperclass().getDeclaredFields()) {
                if (declaredField == null) {
                    continue;
                }
                declaredField.setAccessible(true);
                if (declaredField.getType() != int.class) {
                    continue;
                }
                if (Modifier.isFinal(declaredField.getModifiers())) {
                    continue;
                }
                if (Modifier.isStatic(declaredField.getModifiers())) {
                    continue;
                }
                int value = declaredField.getInt(npc);
                declaredField.setInt(npc, 4795789);
                if (npc.getAnimation() == 2002995319 * 4795789) {
                    animationField = declaredField.getName();
                    declaredField.setInt(npc, value);
                    declaredField.setAccessible(false);
                    break;
                }
                declaredField.setInt(npc, value);
                declaredField.setAccessible(false);
            }
        }
        if (animationField == null) {
            return -1;
        }
        Field animation = npc.getClass().getSuperclass().getDeclaredField(animationField);
        animation.setAccessible(true);
        int anim = animation.getInt(npc) * 2002995319;
        animation.setAccessible(false);
        return anim;
    }


//    @SneakyThrows
//    public static int pathLength(NPC npc) {
//        Field pathLength = npc.getClass().getSuperclass().getDeclaredField("dk");
//        pathLength.setAccessible(true);
//        int path = pathLength.getInt(npc) * -1259578643;
//        pathLength.setAccessible(false);
//        return path;
//    }
//
//    @SneakyThrows
//    public static int pathLength(Player player) {
//        Field pathLength = player.getClass().getSuperclass().getDeclaredField("dk");
//        pathLength.setAccessible(true);
//        int path = pathLength.getInt(player) * -1259578643;
//        pathLength.setAccessible(false);
//        return path;
//    }

    @SneakyThrows
    public static HeadIcon getHeadIcon(NPC npc) {
        Method getHeadIconMethod = null;
        for (Method declaredMethod : npc.getComposition().getClass().getDeclaredMethods()) {
            if (declaredMethod.getName().length() == 2 && declaredMethod.getReturnType() == short.class && declaredMethod.getParameterCount() == 1) {
                getHeadIconMethod = declaredMethod;
                getHeadIconMethod.setAccessible(true);
                short headIcon = (short) getHeadIconMethod.invoke(npc.getComposition(), 0);
                getHeadIconMethod.setAccessible(false);

                if (headIcon == -1) {
                    continue;
                }

                return HeadIcon.values()[headIcon];
            }
        }
        return null;
    }

    @Deprecated
    public int countItem(String str, WidgetInfo container) {
        Widget[] items = client.getWidget(container).getDynamicChildren();
        int count = 0;
        for (int i = 0; i < items.length; i++) {
            if (WildcardMatcher.matches(str.toLowerCase(), Text.removeTags(items[i].getName()).toLowerCase())) {
                count++;
            }
        }
        return count;
    }

    @Deprecated
    public static Widget getItem(String str) {
        Widget[] items = client.getWidget(WidgetInfo.INVENTORY).getDynamicChildren();
        for (int i = 0; i < items.length; i++) {
            if (WildcardMatcher.matches(str.toLowerCase(), Text.removeTags(items[i].getName()).toLowerCase())) {
                return items[i];
            }
        }
        return null;
    }

    public static List<WorldPoint> sceneWorldPoints() {
        List<WorldPoint> allWorldPoints = new ArrayList<>();
        Scene scene = client.getScene();
        Tile[][][] tiles = scene.getTiles();
        int z = client.getPlane();
        for (int x = 0; x < 104; ++x) {
            for (int y = 0; y < 104; ++y) {
                Tile tile = tiles[z][x][y];
                if (tile == null) {
                    continue;
                }
                allWorldPoints.add(tile.getWorldLocation());
            }
        }
        return allWorldPoints;
    }

    public static List<WorldPoint> reachableTiles() {
        boolean[][] visited = new boolean[104][104];
        int[][] flags = client.getCollisionMaps()[client.getPlane()].getFlags();
        WorldPoint playerLoc = client.getLocalPlayer().getWorldLocation();
        int firstPoint = (playerLoc.getX()-client.getBaseX() << 16) | playerLoc.getY()-client.getBaseY();
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        queue.add(firstPoint);
        while (!queue.isEmpty()) {
            int point = queue.poll();
            short x =(short)(point >> 16);
            short y = (short)point;
            if (y < 0 || x < 0 || y > 104 || x > 104) {
                continue;
            }
            if ((flags[x][y] & CollisionDataFlag.BLOCK_MOVEMENT_SOUTH) == 0 && (flags[x][y - 1] & CollisionDataFlag.BLOCK_MOVEMENT_FULL) == 0 && !visited[x][y - 1]) {
                queue.add((x << 16) | (y - 1));
                visited[x][y - 1] = true;
            }
            if ((flags[x][y] & CollisionDataFlag.BLOCK_MOVEMENT_NORTH) == 0 && (flags[x][y + 1] & CollisionDataFlag.BLOCK_MOVEMENT_FULL) == 0 && !visited[x][y + 1]) {
                queue.add((x << 16) | (y + 1));
                visited[x][y + 1] = true;
            }
            if ((flags[x][y] & CollisionDataFlag.BLOCK_MOVEMENT_WEST) == 0 && (flags[x - 1][y] & CollisionDataFlag.BLOCK_MOVEMENT_FULL) == 0 && !visited[x - 1][y]) {
                queue.add(((x - 1) << 16) | y);
                visited[x - 1][y] = true;
            }
            if ((flags[x][y] & CollisionDataFlag.BLOCK_MOVEMENT_EAST) == 0 && (flags[x + 1][y] & CollisionDataFlag.BLOCK_MOVEMENT_FULL) == 0 && !visited[x + 1][y]) {
                queue.add(((x + 1) << 16) | y);
                visited[x + 1][y] = true;
            }
        }
        int baseX = client.getBaseX();
        int baseY = client.getBaseY();
        int plane = client.getPlane();
        List<WorldPoint> finalPoints = new ArrayList<>();
        for (int x = 0; x < 104; ++x) {
            for (int y = 0; y < 104; ++y) {
                if (visited[x][y]) {
                    finalPoints.add(new WorldPoint(baseX + x, baseY + y, plane));
                }
            }
        }
        return finalPoints;
    }

//    public static List<WorldPoint> reachableTiles() {
//        return new ArrayList<>(Arrays.stream(client.getScene().getTiles()).flatMap(Arrays::stream).flatMap(Arrays::stream).filter(Objects::nonNull).filter(x -> canPathToTile(x.getWorldLocation()).isReachable()).map(Tile::getWorldLocation).filter(Objects::nonNull).collect(Collectors.toList()));
//    }

    static boolean canMoveWest(int flag) {
        return (flag & CollisionDataFlag.BLOCK_MOVEMENT_WEST) != CollisionDataFlag.BLOCK_MOVEMENT_WEST;
    }

    static boolean canMoveEast(int flag) {
        return (flag & CollisionDataFlag.BLOCK_MOVEMENT_EAST) != CollisionDataFlag.BLOCK_MOVEMENT_EAST;
    }

    static boolean canMoveNorth(int flag) {
        return (flag & CollisionDataFlag.BLOCK_MOVEMENT_NORTH) != CollisionDataFlag.BLOCK_MOVEMENT_NORTH;
    }

    static boolean canMoveSouth(int flag) {
        return (flag & CollisionDataFlag.BLOCK_MOVEMENT_SOUTH) != CollisionDataFlag.BLOCK_MOVEMENT_SOUTH;
    }

    static boolean canMoveTo(int flag) {
        if ((flag & CollisionDataFlag.BLOCK_MOVEMENT_FULL) == CollisionDataFlag.BLOCK_MOVEMENT_FULL) {
            return false;
        }
        if ((flag & CollisionDataFlag.BLOCK_MOVEMENT_FLOOR) == CollisionDataFlag.BLOCK_MOVEMENT_FLOOR) {
            return false;
        }
        if ((flag & CollisionDataFlag.BLOCK_MOVEMENT_OBJECT) == CollisionDataFlag.BLOCK_MOVEMENT_OBJECT) {
            return false;
        }
        return (flag & CollisionDataFlag.BLOCK_MOVEMENT_FLOOR_DECORATION) != CollisionDataFlag.BLOCK_MOVEMENT_FLOOR_DECORATION;
    }


    @Deprecated
    public static Widget getItem(int id, WidgetInfo container) {
        if (client.getWidget(container) == null) {
            return null;
        }
        Widget[] items = client.getWidget(container).getDynamicChildren();
        for (int i = 0; i < items.length; i++) {
            if (items[i].getItemId() == id) {
                return items[i];
            }
        }
        return null;
    }

    public int getFirstFreeSlot(WidgetInfo container) {
        Widget[] items = client.getWidget(container).getDynamicChildren();
        for (int i = 0; i < items.length; i++) {
            if (items[i].getItemId() == 6512) {
                return i;
            }
        }
        return -1;
    }

    @Deprecated
    public int getEmptySlots(WidgetInfo widgetInfo) {
        List<Widget> inventoryItems = Arrays.asList(client.getWidget(widgetInfo.getId()).getDynamicChildren());
        return (int) inventoryItems.stream().filter(item -> item.getItemId() == 6512).count();
    }

    public static boolean isMoving() {
        return client.getLocalPlayer().getPoseAnimation()
                != client.getLocalPlayer().getIdlePoseAnimation();
    }

    @Deprecated
    public TileObject findObject(String objectName) {
        ArrayList<TileObject> validObjects = new ArrayList<>();
        for (Tile[][] tile : client.getScene().getTiles()) {
            for (Tile[] tiles : tile) {
                for (Tile tile1 : tiles) {
                    if (tile1 == null) {
                        continue;
                    }
                    if (tile1.getGameObjects() == null) {
                        continue;
                    }
                    if (tile1.getGameObjects().length != 0) {
                        GameObject returnVal =
                                Arrays.stream(tile1.getGameObjects()).filter(gameObject -> gameObject != null && client.getObjectDefinition(gameObject.getId()).getName().toLowerCase().contains(objectName.toLowerCase())).findFirst().orElse(null);
                        if (returnVal != null) {
                            validObjects.add(returnVal);
                        }
                    }
                }
            }
        }
        if (validObjects.size() > 0) {
            return validObjects.stream().sorted(Comparator.comparingInt(x -> x.getWorldLocation().distanceTo(client.getLocalPlayer().getWorldLocation()))).findFirst().orElse(null);
        }
        return null;
    }

    @SneakyThrows
    public static void invoke(int var0, int var1, int var2, int var3, int var4,int var5, String var6, String var7, int var8,
                              int var9) {
        if (doAction == null) {
            Field classes = ClassLoader.class.getDeclaredField("classes");
            classes.setAccessible(true);
            ClassLoader classLoader = client.getClass().getClassLoader();
            Vector<Class<?>> classesVector = (Vector<Class<?>>) classes.get(classLoader);
            Class<?>[] params = new Class[]{int.class, int.class, int.class, int.class, int.class, int.class, String.class, String.class, int.class, int.class};
            for (Class<?> aClass : classesVector) {
                if (doAction != null) {
                    break;
                }
                for (Method declaredMethod : aClass.getDeclaredMethods()) {
                    if (declaredMethod.getParameterCount() != 11) {
                        continue;
                    }
                    if (declaredMethod.getReturnType() != void.class) {
                        continue;
                    }
                    if (!Arrays.equals(Arrays.copyOfRange(declaredMethod.getParameterTypes(), 0, 10), params)) {
                        continue;
                    }
                    doAction = declaredMethod;
                    System.out.println(doAction);
                    break;
                }
            }
        }
        doAction.setAccessible(true);
        doAction.invoke(null, var0, var1, var2, var3, var4, var5, var6, var7, var8,var9, Byte.MAX_VALUE);
        doAction.setAccessible(false);
    }

    @Deprecated
    public TileObject findObject(int id) {
        ArrayList<TileObject> validObjects = new ArrayList<>();
        Arrays.stream(client.getScene().getTiles()).flatMap(Arrays::stream).flatMap(Arrays::stream).filter(Objects::nonNull).filter(tile -> tile.getGameObjects() != null && tile.getGameObjects().length != 0).forEach(tile ->
        {
            GameObject returnVal =
                    Arrays.stream(tile.getGameObjects()).filter(gameObject -> gameObject != null && gameObject.getId() == id).findFirst().orElse(null);
            if (returnVal != null) {
                validObjects.add(returnVal);
            }
        });
        return validObjects.stream().min(Comparator.comparingInt(x -> x.getWorldLocation().distanceTo(client.getLocalPlayer().getWorldLocation()))).orElse(null);
    }

    @Deprecated
    public Widget getItemFromList(int[] list, WidgetInfo container) {
        for (int i : list) {
            Widget item = getItem(i, container);
            if (item != null) {
                return item;
            }
        }
        return null;
    }

    @Deprecated
    public int checkIfWearing(int[] ids) {

        if (client.getItemContainer(InventoryID.EQUIPMENT) != null) {
            Item[] equipment = client.getItemContainer(InventoryID.EQUIPMENT).getItems();
            for (Item item : equipment) {
                for (int id : ids) {
                    if (id == item.getId()) {
                        return item.getId();
                    }
                }
            }
        }
        return -1;
    }

    public static PathResult canPathToTile(WorldPoint destinationTile) {
        int z = client.getPlane();
        if (z != destinationTile.getPlane()) {
            return new PathResult(false, Integer.MAX_VALUE);
        }

        CollisionData[] collisionData = client.getCollisionMaps();
        if (collisionData == null) {
            return new PathResult(false, Integer.MAX_VALUE);
        }

        int[][] directions = new int[128][128];
        int[][] distances = new int[128][128];
        int[] bufferX = new int[4096];
        int[] bufferY = new int[4096];

        // Initialise directions and distances
        for (int i = 0; i < 128; ++i) {
            for (int j = 0; j < 128; ++j) {
                directions[i][j] = 0;
                distances[i][j] = Integer.MAX_VALUE;
            }
        }

        int pSX = client.getLocalPlayer().getLocalLocation().getSceneX();

        int pSY = client.getLocalPlayer().getLocalLocation().getSceneY();
        Point p1 = client.getScene().getTiles()[client.getPlane()][pSX][pSY].getSceneLocation();
        LocalPoint lp = LocalPoint.fromWorld(client, destinationTile);
        if (lp == null || !lp.isInScene()) {
            return new PathResult(false, Integer.MAX_VALUE);
        }
        Point p2 = new Point(lp.getSceneX(), lp.getSceneY());

        int middleX = p1.getX();
        int middleY = p1.getY();
        int currentX = middleX;
        int currentY = middleY;
        int offsetX = 64;
        int offsetY = 64;
        // Initialise directions and distances for starting tile
        directions[offsetX][offsetY] = 99;
        distances[offsetX][offsetY] = 0;
        int index1 = 0;
        bufferX[0] = currentX;
        int index2 = 1;
        bufferY[0] = currentY;
        int[][] collisionDataFlags = collisionData[z].getFlags();

        int currentDistance = Integer.MAX_VALUE;
        boolean isReachable = false;

        while (index1 != index2) {
            currentX = bufferX[index1];
            currentY = bufferY[index1];
            index1 = index1 + 1 & 4095;
            // currentX is for the local coordinate while currentMapX is for the index in the directions and distances arrays
            int currentMapX = currentX - middleX + offsetX;
            int currentMapY = currentY - middleY + offsetY;
            if ((currentX == p2.getX()) && (currentY == p2.getY())) {
                isReachable = true;
                break;
            }

            currentDistance = distances[currentMapX][currentMapY] + 1;
            if (currentMapX > 0 && directions[currentMapX - 1][currentMapY] == 0 && (collisionDataFlags[currentX - 1][currentY] & 19136776) == 0) {
                // Able to move 1 tile west
                bufferX[index2] = currentX - 1;
                bufferY[index2] = currentY;
                index2 = index2 + 1 & 4095;
                directions[currentMapX - 1][currentMapY] = 2;
                distances[currentMapX - 1][currentMapY] = currentDistance;
            }

            if (currentMapX < 127 && directions[currentMapX + 1][currentMapY] == 0 && (collisionDataFlags[currentX + 1][currentY] & 19136896) == 0) {
                // Able to move 1 tile east
                bufferX[index2] = currentX + 1;
                bufferY[index2] = currentY;
                index2 = index2 + 1 & 4095;
                directions[currentMapX + 1][currentMapY] = 8;
                distances[currentMapX + 1][currentMapY] = currentDistance;
            }

            if (currentMapY > 0 && directions[currentMapX][currentMapY - 1] == 0 && (collisionDataFlags[currentX][currentY - 1] & 19136770) == 0) {
                // Able to move 1 tile south
                bufferX[index2] = currentX;
                bufferY[index2] = currentY - 1;
                index2 = index2 + 1 & 4095;
                directions[currentMapX][currentMapY - 1] = 1;
                distances[currentMapX][currentMapY - 1] = currentDistance;
            }

            if (currentMapY < 127 && directions[currentMapX][currentMapY + 1] == 0 && (collisionDataFlags[currentX][currentY + 1] & 19136800) == 0) {
                // Able to move 1 tile north
                bufferX[index2] = currentX;
                bufferY[index2] = currentY + 1;
                index2 = index2 + 1 & 4095;
                directions[currentMapX][currentMapY + 1] = 4;
                distances[currentMapX][currentMapY + 1] = currentDistance;
            }

            if (currentMapX > 0 && currentMapY > 0 && directions[currentMapX - 1][currentMapY - 1] == 0 && (collisionDataFlags[currentX - 1][currentY - 1] & 19136782) == 0 && (collisionDataFlags[currentX - 1][currentY] & 19136776) == 0 && (collisionDataFlags[currentX][currentY - 1] & 19136770) == 0) {
                // Able to move 1 tile south-west
                bufferX[index2] = currentX - 1;
                bufferY[index2] = currentY - 1;
                index2 = index2 + 1 & 4095;
                directions[currentMapX - 1][currentMapY - 1] = 3;
                distances[currentMapX - 1][currentMapY - 1] = currentDistance;
            }

            if (currentMapX < 127 && currentMapY > 0 && directions[currentMapX + 1][currentMapY - 1] == 0 && (collisionDataFlags[currentX + 1][currentY - 1] & 19136899) == 0 && (collisionDataFlags[currentX + 1][currentY] & 19136896) == 0 && (collisionDataFlags[currentX][currentY - 1] & 19136770) == 0) {
                // Able to move 1 tile north-west
                bufferX[index2] = currentX + 1;
                bufferY[index2] = currentY - 1;
                index2 = index2 + 1 & 4095;
                directions[currentMapX + 1][currentMapY - 1] = 9;
                distances[currentMapX + 1][currentMapY - 1] = currentDistance;
            }

            if (currentMapX > 0 && currentMapY < 127 && directions[currentMapX - 1][currentMapY + 1] == 0 && (collisionDataFlags[currentX - 1][currentY + 1] & 19136824) == 0 && (collisionDataFlags[currentX - 1][currentY] & 19136776) == 0 && (collisionDataFlags[currentX][currentY + 1] & 19136800) == 0) {
                // Able to move 1 tile south-east
                bufferX[index2] = currentX - 1;
                bufferY[index2] = currentY + 1;
                index2 = index2 + 1 & 4095;
                directions[currentMapX - 1][currentMapY + 1] = 6;
                distances[currentMapX - 1][currentMapY + 1] = currentDistance;
            }

            if (currentMapX < 127 && currentMapY < 127 && directions[currentMapX + 1][currentMapY + 1] == 0 && (collisionDataFlags[currentX + 1][currentY + 1] & 19136992) == 0 && (collisionDataFlags[currentX + 1][currentY] & 19136896) == 0 && (collisionDataFlags[currentX][currentY + 1] & 19136800) == 0) {
                // Able to move 1 tile north-east
                bufferX[index2] = currentX + 1;
                bufferY[index2] = currentY + 1;
                index2 = index2 + 1 & 4095;
                directions[currentMapX + 1][currentMapY + 1] = 12;
                distances[currentMapX + 1][currentMapY + 1] = currentDistance;
            }
        }
        return new PathResult(isReachable, currentDistance);
    }

    public static class PathResult {
        private final boolean reachable;
        private final int distance;

        public PathResult(boolean reachable, int distance) {
            this.reachable = reachable;
            this.distance = distance;
        }

        public boolean isReachable() {
            return reachable;
        }

        public int getDistance() {
            return distance;
        }
    }

    @SneakyThrows
    public static void stopPlugin(Plugin plugin) {

        SwingUtilities.invokeAndWait(() ->
        {
            try {
                pluginManager.stopPlugin(plugin);
                pluginManager.setPluginEnabled(plugin, false);
            } catch (PluginInstantiationException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static Client getClient() {
        return client;
    }

    public static ClientUI getClientUI() {
        return clientUI;
    }


    public static List<WorldPoint> pathToGoalSetFromPlayerUsingReachableTiles(HashSet<WorldPoint> goalSet, HashSet<WorldPoint> dangerous, HashSet<WorldPoint> impassible) {
        return pathToGoalSet(goalSet, dangerous, impassible, new HashSet<>(reachableTiles()), playerPosition());
    }

    public static List<WorldPoint> pathToGoalSetFromPlayerNoCustomTiles(HashSet<WorldPoint> goalSet) {
        return pathToGoalSet(goalSet, EMPTY_SET, EMPTY_SET, new HashSet<>(reachableTiles()), playerPosition());
    }
    public static List<WorldPoint> pathToGoalFromPlayerUsingCustomDangerous(WorldPoint goal, HashSet<WorldPoint> dangerous) {
        return pathToGoalSet(new HashSet<>(List.of(goal)), dangerous, EMPTY_SET, new HashSet<>(reachableTiles()), playerPosition());
    }

    public static List<WorldPoint> pathToGoalFromPlayerUsingReachableTiles(WorldPoint goal, HashSet<WorldPoint> dangerous, HashSet<WorldPoint> impassible) {
        return pathToGoalSet(new HashSet<>(List.of(goal)), dangerous, impassible, new HashSet<>(reachableTiles()), playerPosition());
    }

    public static List<WorldPoint> pathToGoalFromPlayerNoCustomTiles(WorldPoint goal) {
        return pathToGoalSet(new HashSet<>(List.of(goal)), EMPTY_SET, EMPTY_SET, new HashSet<>(reachableTiles()), playerPosition());
    }

    public static List<WorldPoint> pathToGoalSet(HashSet<WorldPoint> goalSet, HashSet<WorldPoint> dangerous, HashSet<WorldPoint> impassible, HashSet<WorldPoint> walkable, WorldPoint starting) {
        if (Collections.disjoint(goalSet, walkable)) {
            return null;
        }
        ArrayDeque<Node> queue = new ArrayDeque<Node>();
        HashSet<WorldPoint> visited = new HashSet<>();
        visited.add(starting);
        queue.add(new Node(starting));
        while (!queue.isEmpty()) {
            Node current = queue.poll();
            if (goalSet.contains(current.getData())) {
                List<WorldPoint> ret = new ArrayList<>();
                while (current != null) {
                    ret.add(current.getData());
                    current = current.getPrevious();
                }
                Collections.reverse(ret);
                ret.remove(0);
                return ret;
            }
            for (int[] direction : directionsMap) {
                int x = direction[0];
                int y = direction[1];
                if (x == 0 && y == 0) {
                    continue;
                }
                WorldPoint currentPoint = current.getData();
                WorldPoint nextPoint = current.getData().dy(y).dx(x);
                if (!walkable.contains(nextPoint) || impassible.contains(nextPoint) || dangerous.contains(nextPoint) || visited.contains(nextPoint)) {
                    continue;
                }
                if (x == -2 && y == 0) {
                    if (farWObstructed(currentPoint, impassible, walkable)) {
                        continue;
                    }
                    visited.add(nextPoint);
                    queue.add(new Node(nextPoint, current));
                    continue;
                }
                //Far East
                if (x == 2 && y == 0) {
                    if (farEObstructed(currentPoint, impassible, walkable)) {
                        continue;
                    }
                    visited.add(nextPoint);
                    queue.add(new Node(nextPoint, current));
                    continue;
                }
                //Far South
                if (x == 0 && y == -2) {
                    if (farSObstructed(currentPoint, impassible, walkable)) {
                        continue;
                    }
                    visited.add(nextPoint);
                    queue.add(new Node(nextPoint, current));
                    continue;
                }
                //Far North
                if (x == 0 && y == 2) {
                    if (farNObstructed(currentPoint, impassible, walkable)) {
                        continue;
                    }
                    visited.add(nextPoint);
                    queue.add(new Node(nextPoint, current));
                    continue;
                }
                //far movements
                //L movement in here so i dont get lost in the saauce down there
                if (Math.abs(x) + Math.abs(y) == 3) {
                    //North east
                    if (x == 1 && y == 2) {
                        if (northEastLObstructed(currentPoint, impassible, walkable)) {
                            continue;
                        }
                        visited.add(nextPoint);
                        queue.add(new Node(nextPoint, current));
                        continue;
                    }
                    //East north
                    if (x == 2 && y == 1) {
                        if (eastNorthLObstructed(currentPoint, impassible, walkable)) {
                            continue;
                        }
                        visited.add(nextPoint);
                        queue.add(new Node(nextPoint, current));
                        continue;
                    }
                    //East south
                    if (x == 2 && y == -1) {
                        if (eastSouthLObstructed(currentPoint, impassible, walkable)) {
                            continue;
                        }
                        visited.add(nextPoint);
                        queue.add(new Node(nextPoint, current));
                        continue;
                    }
                    //South east
                    if (x == 1 && y == -2) {
                        if (southEastLObstructed(currentPoint, impassible, walkable)) {
                            continue;
                        }
                        visited.add(nextPoint);
                        queue.add(new Node(nextPoint, current));
                        continue;
                    }
                    //South west
                    if (x == -1 && y == -2) {
                        if (southWestLObstructed(currentPoint, impassible, walkable)) {
                            continue;
                        }
                        visited.add(nextPoint);
                        queue.add(new Node(nextPoint, current));
                        continue;
                    }
                    //West south
                    if (x == -2 && y == -1) {
                        if (westSouthLObstructed(currentPoint, impassible, walkable)) {
                            continue;
                        }
                        visited.add(nextPoint);
                        queue.add(new Node(nextPoint, current));
                        continue;
                    }
                    //West north
                    if (x == -2 && y == 1) {
                        if (westNorthLObstructed(currentPoint, impassible, walkable)) {
                            continue;
                        }
                        visited.add(nextPoint);
                        queue.add(new Node(nextPoint, current));
                        continue;
                    }
                    //North west
                    if (x == -1 && y == 2) {
                        if (northWestLObstructed(currentPoint, impassible, walkable)) {
                            continue;
                        }
                        visited.add(nextPoint);
                        queue.add(new Node(nextPoint, current));
                        continue;
                    }
                } else {
                    //One tile movement

                    //diagonal SE
                    if (x == 1 && y == -1) {
                        if (seObstructed(currentPoint, impassible, walkable)) {
                            continue;
                        }
                        visited.add(nextPoint);
                        queue.add(new Node(nextPoint, current));
                        continue;
                    }
                    //diagonal NE
                    if (x == 1 && y == 1) {
                        if (neObstructed(currentPoint, impassible, walkable)) {
                            continue;
                        }
                        visited.add(nextPoint);
                        queue.add(new Node(nextPoint, current));
                        continue;
                    }
                    //diagonal NW
                    if (x == -1 && y == 1) {
                        if (nwObstructed(currentPoint, impassible, walkable)) {
                            continue;
                        }
                        visited.add(nextPoint);
                        queue.add(new Node(nextPoint, current));
                        continue;
                    }
                    //diagonal SW
                    if (x == -1 && y == -1) {
                        if (swObstructed(currentPoint, impassible, walkable)) {
                            continue;
                        }
                        visited.add(nextPoint);
                        queue.add(new Node(nextPoint, current));
                        continue;
                    }

                    //Two tile movement

                    //Diagonal SW
                    if (x == -2 && y == -2) {
                        if (farSWObstructed(currentPoint, impassible, walkable)) {
                            continue;
                        }
                        visited.add(nextPoint);
                        queue.add(new Node(nextPoint, current));
                        continue;
                    }
                    //Diagonal NW
                    if (x == -2 && y == 2) {
                        if (farNWObstructed(currentPoint, impassible, walkable)) {
                            continue;
                        }
                        visited.add(nextPoint);
                        queue.add(new Node(nextPoint, current));
                        continue;
                    }
                    //Diagonal SE
                    if (x == 2 && y == -2) {
                        if (farSEObstructed(currentPoint, impassible, walkable)) {
                            continue;
                        }
                        visited.add(nextPoint);
                        queue.add(new Node(nextPoint, current));
                        continue;
                    }
                    //Diagonal NE
                    if (x == 2 && y == 2) {
                        if (farNEObstructed(currentPoint, impassible, walkable)) {
                            continue;
                        }
                        visited.add(nextPoint);
                        queue.add(new Node(nextPoint, current));
                        continue;
                    }
                }
            }
        }
        return null;
    }


    static boolean nwObstructed(WorldPoint starting, HashSet<WorldPoint> impassible, HashSet<WorldPoint> walkable) {
        if (impassible.contains(starting.dx(-1).dy(0)) || !walkable.contains(starting.dx(-1).dy(0))) {
            return true;
        }
        return impassible.contains(starting.dx(0).dy(1)) || !walkable.contains(starting.dx(0).dy(1));
    }

    public static void sendClientMessage(String message) {
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", message, null);
    }

    static boolean neObstructed(WorldPoint starting, HashSet<WorldPoint> impassible, HashSet<WorldPoint> walkable) {
        if (impassible.contains(starting.dx(1).dy(0)) || !walkable.contains(starting.dx(1).dy(0))) {
            return true;
        }
        return impassible.contains(starting.dx(0).dy(1)) || !walkable.contains(starting.dx(0).dy(1));
    }

    //L movement of 2 north one east, as opposed to 2 east one north (what should we call this difference?)
    static boolean northEastLObstructed(WorldPoint starting, HashSet<WorldPoint> impassible, HashSet<WorldPoint> walkable) {
        if (impassible.contains(starting.dx(1).dy(1)) || !walkable.contains(starting.dx(1).dy(1))) {
            return true;
        }
        if (impassible.contains(starting.dx(0).dy(1)) || !walkable.contains(starting.dx(0).dy(1))) {
            return true;
        }
        return impassible.contains(starting.dx(0).dy(2)) || !walkable.contains(starting.dx(0).dy(2));
    }

    //L movement of 2 east one North, as opposed to 2 north one east
    static boolean eastNorthLObstructed(WorldPoint starting, HashSet<WorldPoint> impassible, HashSet<WorldPoint> walkable) {
        if (impassible.contains(starting.dx(1).dy(1)) || !walkable.contains(starting.dx(1).dy(1))) {
            return true;
        }
        if (impassible.contains(starting.dx(1).dy(0)) || !walkable.contains(starting.dx(1).dy(0))) {
            return true;
        }
        return impassible.contains(starting.dx(2).dy(0)) || !walkable.contains(starting.dx(2).dy(0));
    }

    static boolean eastSouthLObstructed(WorldPoint starting, HashSet<WorldPoint> impassible, HashSet<WorldPoint> walkable) {
        if (impassible.contains(starting.dx(1).dy(-1)) || !walkable.contains(starting.dx(1).dy(-1))) {
            return true;
        }
        if (impassible.contains(starting.dx(1).dy(0)) || !walkable.contains(starting.dx(1).dy(0))) {
            return true;
        }
        return impassible.contains(starting.dx(2).dy(0)) || !walkable.contains(starting.dx(2).dy(0));
    }

    static boolean southEastLObstructed(WorldPoint starting, HashSet<WorldPoint> impassible, HashSet<WorldPoint> walkable) {
        if (impassible.contains(starting.dx(1).dy(-1)) || !walkable.contains(starting.dx(1).dy(-1))) {
            return true;
        }
        if (impassible.contains(starting.dx(0).dy(-1)) || !walkable.contains(starting.dx(0).dy(-1))) {
            return true;
        }
        return impassible.contains(starting.dx(0).dy(-2)) || !walkable.contains(starting.dx(0).dy(-2));
    }

    static boolean southWestLObstructed(WorldPoint starting, HashSet<WorldPoint> impassible, HashSet<WorldPoint> walkable) {
        if (impassible.contains(starting.dx(-1).dy(-1)) || !walkable.contains(starting.dx(-1).dy(-1))) {
            return true;
        }
        if (impassible.contains(starting.dx(0).dy(-1)) || !walkable.contains(starting.dx(0).dy(-1))) {
            return true;
        }
        return impassible.contains(starting.dx(0).dy(-2)) || !walkable.contains(starting.dx(0).dy(-2));
    }

    static boolean westSouthLObstructed(WorldPoint starting, HashSet<WorldPoint> impassible, HashSet<WorldPoint> walkable) {
        if (impassible.contains(starting.dx(-1).dy(-1)) || !walkable.contains(starting.dx(-1).dy(-1))) {
            return true;
        }
        if (impassible.contains(starting.dx(-1).dy(0)) || !walkable.contains(starting.dx(-1).dy(0))) {
            return true;
        }
        return impassible.contains(starting.dx(-2).dy(0)) || !walkable.contains(starting.dx(-2).dy(0));
    }

    static boolean westNorthLObstructed(WorldPoint starting, HashSet<WorldPoint> impassible, HashSet<WorldPoint> walkable) {
        if (impassible.contains(starting.dx(-1).dy(1)) || !walkable.contains(starting.dx(-1).dy(1))) {
            return true;
        }
        if (impassible.contains(starting.dx(-1).dy(0)) || !walkable.contains(starting.dx(-1).dy(0))) {
            return true;
        }
        return impassible.contains(starting.dx(-2).dy(0)) || !walkable.contains(starting.dx(-2).dy(0));
    }

    static boolean northWestLObstructed(WorldPoint starting, HashSet<WorldPoint> impassible, HashSet<WorldPoint> walkable) {
        if (impassible.contains(starting.dx(-1).dy(1)) || !walkable.contains(starting.dx(-1).dy(1))) {
            return true;
        }
        if (impassible.contains(starting.dx(0).dy(1)) || !walkable.contains(starting.dx(0).dy(1))) {
            return true;
        }
        return impassible.contains(starting.dx(0).dy(2)) || !walkable.contains(starting.dx(0).dy(2));
    }

    static boolean seObstructed(WorldPoint starting, HashSet<WorldPoint> impassible, HashSet<WorldPoint> walkable) {
        if (impassible.contains(starting.dx(1).dy(0)) || !walkable.contains(starting.dx(1).dy(0))) {
            return true;
        }
        return impassible.contains(starting.dx(0).dy(-1)) || !walkable.contains(starting.dx(0).dy(-1));
    }

    static boolean swObstructed(WorldPoint starting, HashSet<WorldPoint> impassible, HashSet<WorldPoint> walkable) {
        if (impassible.contains(starting.dx(-1).dy(0)) || !walkable.contains(starting.dx(-1).dy(0))) {
            return true;
        }
        return impassible.contains(starting.dx(0).dy(-1)) || !walkable.contains(starting.dx(0).dy(-1));
    }

    static boolean farNObstructed(WorldPoint starting, HashSet<WorldPoint> impassible, HashSet<WorldPoint> walkable) {
        return impassible.contains(starting.dx(0).dy(1)) || !walkable.contains(starting.dx(0).dy(1));
    }

    static boolean farSObstructed(WorldPoint starting, HashSet<WorldPoint> impassible, HashSet<WorldPoint> walkable) {
        return impassible.contains(starting.dx(0).dy(-1)) || !walkable.contains(starting.dx(0).dy(-1));
    }

    static boolean farEObstructed(WorldPoint starting, HashSet<WorldPoint> impassible, HashSet<WorldPoint> walkable) {
        return impassible.contains(starting.dx(1).dy(0)) || !walkable.contains(starting.dx(1).dy(0));
    }

    static boolean farWObstructed(WorldPoint starting, HashSet<WorldPoint> impassible, HashSet<WorldPoint> walkable) {
        return impassible.contains(starting.dx(-1).dy(0)) || !walkable.contains(starting.dx(-1).dy(0));
    }

    static boolean farSWObstructed(WorldPoint starting, HashSet<WorldPoint> impassible, HashSet<WorldPoint> walkable) {
        if (impassible.contains(starting.dx(-1).dy(-2)) || !walkable.contains(starting.dx(-1).dy(-2))) {
            return true;
        }
        if (impassible.contains(starting.dx(-2).dy(-1)) || !walkable.contains(starting.dx(-2).dy(-1))) {
            return true;
        }
        if (impassible.contains(starting.dx(0).dy(-1)) || !walkable.contains(starting.dx(0).dy(-1))) {
            return true;
        }
        if (impassible.contains(starting.dx(-1).dy(0)) || !walkable.contains(starting.dx(-1).dy(0))) {
            return true;
        }
        return impassible.contains(starting.dx(-1).dy(-1)) || !walkable.contains(starting.dx(-1).dy(-1));
    }

    static boolean farNWObstructed(WorldPoint starting, HashSet<WorldPoint> impassible, HashSet<WorldPoint> walkable) {
        if (impassible.contains(starting.dx(-1).dy(2)) || !walkable.contains(starting.dx(-1).dy(2))) {
            return true;
        }
        if (impassible.contains(starting.dx(-2).dy(1)) || !walkable.contains(starting.dx(-2).dy(1))) {
            return true;
        }
        if (impassible.contains(starting.dx(0).dy(1)) || !walkable.contains(starting.dx(0).dy(1))) {
            return true;
        }
        if (impassible.contains(starting.dx(-1).dy(0)) || !walkable.contains(starting.dx(-1).dy(0))) {
            return true;
        }
        return impassible.contains(starting.dx(-1).dy(1)) || !walkable.contains(starting.dx(-1).dy(1));
    }

    static boolean farNEObstructed(WorldPoint starting, HashSet<WorldPoint> impassible, HashSet<WorldPoint> walkable) {
        if (impassible.contains(starting.dx(1).dy(2)) || !walkable.contains(starting.dx(1).dy(2))) {
            return true;
        }
        if (impassible.contains(starting.dx(2).dy(1)) || !walkable.contains(starting.dx(2).dy(1))) {
            return true;
        }
        if (impassible.contains(starting.dx(0).dy(1)) || !walkable.contains(starting.dx(0).dy(1))) {
            return true;
        }
        if (impassible.contains(starting.dx(1).dy(0)) || !walkable.contains(starting.dx(1).dy(0))) {
            return true;
        }
        return impassible.contains(starting.dx(1).dy(1)) || !walkable.contains(starting.dx(1).dy(1));
    }

    static boolean farSEObstructed(WorldPoint starting, HashSet<WorldPoint> impassible, HashSet<WorldPoint> walkable) {
        if (impassible.contains(starting.dx(1).dy(-2)) || !walkable.contains(starting.dx(1).dy(-2))) {
            return true;
        }
        if (impassible.contains(starting.dx(2).dy(-1)) || !walkable.contains(starting.dx(2).dy(-1))) {
            return true;
        }
        if (impassible.contains(starting.dx(0).dy(-1)) || !walkable.contains(starting.dx(0).dy(-1))) {
            return true;
        }
        if (impassible.contains(starting.dx(1).dy(0)) || !walkable.contains(starting.dx(1).dy(0))) {
            return true;
        }
        return impassible.contains(starting.dx(1).dy(-1)) || !walkable.contains(starting.dx(1).dy(-1));
    }

    @Override
    public void startUp() throws Exception {
        eventBus.register(RuneLite.getInjector().getInstance(Inventory.class));
        eventBus.register(RuneLite.getInjector().getInstance(Bank.class));
        eventBus.register(RuneLite.getInjector().getInstance(BankInventory.class));
        eventBus.register(RuneLite.getInjector().getInstance(NPCs.class));
        eventBus.register(RuneLite.getInjector().getInstance(TileObjects.class));
        eventBus.register(RuneLite.getInjector().getInstance(Players.class));
        eventBus.register(RuneLite.getInjector().getInstance(Equipment.class));
        eventBus.register(RuneLite.getInjector().getInstance(DepositBox.class));
        eventBus.register(RuneLite.getInjector().getInstance(ShopInventory.class));
        eventBus.register(RuneLite.getInjector().getInstance(Shop.class));
    }
}
