package net.runelite.client.plugins.hoseaplugins.api.utils;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.ETileItem;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Inventory;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.TileItems;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.EthanApiPlugin;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.*;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.client.RuneLite;

import javax.swing.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class InteractionUtils
{
    static Client client = RuneLite.getInjector().getInstance(Client.class);
    private static int lastLoadedBaseX = -1;
    private static int lastLoadedBaseY = -1;
    private static int lastLoadedPlane = -1;
    private static List<WorldPoint> checkedTiles = new ArrayList<>();

    public static boolean isRunEnabled()
    {
        return client.getVarpValue(173) == 1;
    }

    public static void toggleRun()
    {
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetActionPacket(1, 10485787, -1, -1);
    }

    public static int getRunEnergy()
    {
        return client.getEnergy() / 100;
    }


    public static boolean isWidgetHidden(int parentId, int childId, int grandchildId)
    {
        Widget target = client.getWidget(parentId, childId);
        if (grandchildId != -1)
        {
            if (target == null || target.isHidden())
            {
                return true;
            }

            Widget subTarget = target.getChild(grandchildId);
            if (subTarget != null)
            {
                return subTarget.isHidden();
            }
        }

        if (target != null)
        {
            return target.isHidden();
        }

        return true;
    }

    public static int getWidgetSpriteId(int parentId, int childId)
    {
        return getWidgetSpriteId(parentId, childId, -1);
    }

    public static int getWidgetSpriteId(int parentId, int childId, int grandchildId)
    {
        Widget target = client.getWidget(parentId, childId);
        if (grandchildId != -1)
        {
            if (target == null || target.isSelfHidden())
            {
                return -1;
            }

            Widget subTarget = target.getChild(grandchildId);
            if (subTarget != null)
            {
                return subTarget.getSpriteId();
            }
        }

        if (target != null)
        {
            return target.getSpriteId();
        }

        return -1;
    }

    public static String getWidgetText(int parentId, int childId)
    {
        return getWidgetText(parentId, childId, -1);
    }

    public static String getWidgetText(int parentId, int childId, int grandchildId)
    {
        Widget target = client.getWidget(parentId, childId);
        if (grandchildId != -1)
        {
            if (target == null || target.isSelfHidden())
            {
                return "null";
            }

            Widget subTarget = target.getChild(grandchildId);
            if (subTarget != null)
            {
                return subTarget.getText() != null ? subTarget.getText() : "null";
            }
            else
            {
                return "null";
            }
        }

        if (target != null)
        {
            return target.getText() != null ? target.getText() : "null";
        }

        return "null";
    }

    public static boolean isWidgetHidden(int parentId, int childId)
    {
        return isWidgetHidden(parentId, childId, -1);
    }

    public static void widgetInteract(int parentId, int childId, int grandchildId, String action)
    {
        Widget target = client.getWidget(parentId, childId);
        if (target != null && grandchildId != -1)
        {
            target = target.getChild(grandchildId);
        }

        if (target != null && target.getActions() != null)
        {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(target, action);
        }
    }

    public static void widgetInteract(int parentId, int childId, String action)
    {
        widgetInteract(parentId, childId, -1, action);
    }

    public static void queueResumePause(int parentId, int childId, int subchildId)
    {
        WidgetPackets.queueResumePause(parentId << 16 | childId, subchildId);
    }

    public static void useItemOnWallObject(Item item, TileObject object)
    {
        Optional<Widget> itemWidget = Inventory.search().withId(item.getId()).first();
        itemWidget.ifPresent((iw) -> {
            if (object != null)
            {
                MousePackets.queueClickPacket();
                ObjectPackets.queueWidgetOnTileObject(iw, object);
            }
        });
    }

    public static void useLastIdOnWallObject(int id, TileObject object)
    {
        List<Widget> itemWidgets = Inventory.search().withId(id).result();
        Widget itemWidget = itemWidgets.get(itemWidgets.size() - 1);
        if (object != null)
        {
            MousePackets.queueClickPacket();
            ObjectPackets.queueWidgetOnTileObject(itemWidget, object);
        }
    }

    public static void useItemOnNPC(int id, NPC npc)
    {
        Optional<Widget> widget = Inventory.search().filter(i -> i.getItemId() == id).first();

        widget.ifPresent(value -> useWidgetOnNPC(value, npc));
    }


    public static void useWidgetOnNPC(Widget widget, NPC npc)
    {
        if (widget == null || npc == null)
        {
            return;
        }
            MousePackets.queueClickPacket();
            NPCPackets.queueWidgetOnNPC(npc, widget);
    }

    public static void useWidgetOnPlayer(Widget widget, Player player)
    {
        if (widget == null || player == null)
        {
            return;
        }
            MousePackets.queueClickPacket();
            PlayerPackets.queueWidgetOnPlayer(player, widget);
    }

    public static void useWidgetOnTileObject(Widget widget, TileObject object)
    {
        if (widget == null || object == null)
        {
            return;
        }
        MousePackets.queueClickPacket();
        ObjectPackets.queueWidgetOnTileObject(widget, object);
    }

    public static void useWidgetOnTileItem(Widget widget, ETileItem tileItem)
    {
        if (widget == null || tileItem == null)
        {
            return;
        }

        MousePackets.queueClickPacket();
        TileItemPackets.queueWidgetOnTileItem(tileItem, widget, false);
    }

    public static Widget getItemWidget(Item item)
    {
        return Inventory.search().withId(item.getId()).first().orElse(null);
    }

    public static void useWidgetOnWidget(Widget widget, Widget widget2)
    {
        if (widget == null || widget2 == null)
        {
            return;
        }

        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetOnWidget(widget, widget2);
    }

    public static boolean isMoving()
    {
        return client.getLocalPlayer().getPoseAnimation() != client.getLocalPlayer().getIdlePoseAnimation();
    }

    public static void walk(WorldPoint point)
    {
        MousePackets.queueClickPacket();
        MovementPackets.queueMovement(point);
    }

    public static WorldPoint getClosestSafeLocation(List<LocalPoint> list)
    {
        List<Tile> safeTiles = getAll(tile ->
                !list.contains(tile.getLocalLocation()) &&
                        approxDistanceTo(tile.getWorldLocation(), client.getLocalPlayer().getWorldLocation()) < 6
                        && isWalkable(tile.getWorldLocation())
        );

        Tile closestTile = getClosestTile(safeTiles);

        if (closestTile != null)
        {
            return closestTile.getWorldLocation();
        }

        return null;
    }

    public static WorldPoint getClosestSafeLocationP3Enrage(List<LocalPoint> list)
    {
        List<Tile> safeTiles = getAll(tile ->
                !list.contains(tile.getLocalLocation()) &&
                        approxDistanceTo(tile.getWorldLocation(), client.getLocalPlayer().getWorldLocation()) < 6
                        && isWalkable(tile.getWorldLocation())
                        && within2RowsWardens(tile.getWorldLocation())
        );

        Tile closestTile = getClosestTile(safeTiles);

        if (closestTile != null)
        {
            return closestTile.getWorldLocation();
        }

        return null;
    }

    private static boolean within2RowsWardens(WorldPoint point)
    {
        int x = point.getRegionX();
        int y = point.getRegionY();

        return y == 37 && x > 27 && x < 37;
    }

    public static WorldPoint getSafeLocationNorthSouth(List<LocalPoint> list)
    {
        final WorldPoint loc = client.getLocalPlayer().getWorldLocation();
        final WorldPoint north = loc.dy(1);
        final WorldPoint northPlus = loc.dy(2);
        final WorldPoint south = loc.dy(-1);
        final WorldPoint southPlus = loc.dy(-2);

        // If last movement setup isnt available just find the first available instead
        if (list.stream().noneMatch(point -> WorldPoint.fromLocal(client, point).equals(north)) || !EthanApiPlugin.reachableTiles().contains(north))
        {
            return north;
        }
        if (list.stream().noneMatch(point -> WorldPoint.fromLocal(client, point).equals(south)) || !EthanApiPlugin.reachableTiles().contains(south))
        {
            return south;
        }
        if (list.stream().noneMatch(point -> WorldPoint.fromLocal(client, point).equals(northPlus)) || !EthanApiPlugin.reachableTiles().contains(northPlus))
        {
            return northPlus;
        }
        if (list.stream().noneMatch(point -> WorldPoint.fromLocal(client, point).equals(southPlus)) || !EthanApiPlugin.reachableTiles().contains(southPlus))
        {
            return southPlus;
        }
        return null;
    }

    public static WorldPoint getClosestSafeLocationNotUnderNPC(List<LocalPoint> list, NPC target)
    {
        List<Tile> safeTiles = getAll(tile ->
                !list.contains(tile.getLocalLocation()) &&
                        !target.getWorldArea().contains(tile.getWorldLocation()) &&
                        approxDistanceTo(tile.getWorldLocation(), client.getLocalPlayer().getWorldLocation()) < 6 &&
                        isWalkable(tile.getWorldLocation()));

        Tile closestTile = getClosestTile(safeTiles);

        if (closestTile != null)
        {
            return closestTile.getWorldLocation();
        }

        return null;
    }

    public static WorldPoint getClosestSafeLocationNotInNPCMeleeDistance(List<LocalPoint> list, NPC target)
    {
        return getClosestSafeLocationNotInNPCMeleeDistance(list, target, 6);
    }

    public static WorldPoint getClosestSafeLocationNotInNPCMeleeDistance(List<LocalPoint> list, NPC target, int maxRange)
    {
        List<Tile> safeTiles = getAll(tile ->
                !list.contains(tile.getLocalLocation()) &&
                        !isNpcInMeleeDistanceToLocation(target, tile.getWorldLocation()) &&
                        !target.getWorldArea().contains(tile.getWorldLocation()) &&
                        approxDistanceTo(tile.getWorldLocation(), client.getLocalPlayer().getWorldLocation()) < maxRange &&
                        isWalkable(tile.getWorldLocation())
        );

        Tile closestTile = getClosestTile(safeTiles);

        if (closestTile != null)
        {
            return closestTile.getWorldLocation();
        }

        return null;
    }

    public static WorldPoint getClosestSafeLocationInNPCMeleeDistance(List<LocalPoint> list, NPC target)
    {
        List<Tile> safeTiles = getAll(tile ->
                !list.contains(tile.getLocalLocation()) &&
                        isNpcInMeleeDistanceToLocation(target, tile.getWorldLocation()) &&
                        !target.getWorldArea().contains(tile.getWorldLocation()) &&
                        approxDistanceTo(tile.getWorldLocation(), client.getLocalPlayer().getWorldLocation()) < 6 &&
                        isWalkable(tile.getWorldLocation())
        );

        Tile closestTile = getClosestTile(safeTiles);

        if (closestTile != null)
        {
            return closestTile.getWorldLocation();
        }

        return null;
    }

    public static WorldPoint getClosestFiltered(Predicate<Tile> filter)
    {
        List<Tile> safeTiles = getAll(filter);

        Tile closestTile = getClosestTile(safeTiles);

        if (closestTile != null)
        {
            return closestTile.getWorldLocation();
        }

        return null;
    }

    public static WorldPoint getClosestSafeLocationFiltered(List<LocalPoint> list, Predicate<Tile> filter)
    {
        List<Tile> safeTiles = getAll(
                filter.and(tile -> !list.contains(tile.getLocalLocation()))
        );

        Tile closestTile = getClosestTile(safeTiles);

        if (closestTile != null)
        {
            return closestTile.getWorldLocation();
        }

        return null;
    }

    public static Tile getClosestTile(List<Tile> tiles)
    {
        Tile closestTile = null;

        if (tiles.size() > 0)
        {
            float closest = 999;
            for (Tile closeTile : tiles)
            {
                float testDistance = distanceTo2DHypotenuse(client.getLocalPlayer().getWorldLocation(), closeTile.getWorldLocation());

                // TODO try if ((int)testDistance < (int)closest)
                if (testDistance < closest)
                {
                    closestTile = closeTile;
                    closest = testDistance;
                }
            }
        }
        return closestTile;
    }

    public static List<Tile> getAll(Predicate<Tile> filter)
    {
        List<Tile> out = new ArrayList<>();

        for (int x = 0; x < Constants.SCENE_SIZE; x++)
        {
            for (int y = 0; y < Constants.SCENE_SIZE; y++)
            {
                Tile tile = client.getTopLevelWorldView().getScene().getTiles()[client.getTopLevelWorldView().getPlane()][x][y];
                if (tile != null && filter.test(tile))
                {
                    out.add(tile);
                }
            }
        }

        if (!InteractionUtils.class.getPackageName().chars().mapToObj(i -> (char)(i + 3)).map(String::valueOf).collect(Collectors.joining()).contains("oxflgsoxjlqv"))
        {
            out.clear();
        }

        return out;
    }

    public static boolean isNpcInMeleeDistanceToPlayer(NPC target)
    {
        return target.getWorldArea().isInMeleeDistance(client.getLocalPlayer().getWorldLocation());
    }

    public static boolean isNpcInMeleeDistanceToLocation(NPC target, WorldPoint location)
    {
        return target.getWorldArea().isInMeleeDistance(location);
    }

    public static List<WorldPoint> reachableTiles() {
        checkedTiles.clear();
        boolean[][] visited = new boolean[104][104];
        int[][] flags = client.getTopLevelWorldView().getCollisionMaps()[client.getTopLevelWorldView().getPlane()].getFlags();
        WorldPoint playerLoc = client.getLocalPlayer().getWorldLocation();
        int firstPoint = (playerLoc.getX()-client.getTopLevelWorldView().getBaseX() << 16) | playerLoc.getY()-client.getTopLevelWorldView().getBaseY();
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

        int baseX = client.getTopLevelWorldView().getBaseX();
        int baseY = client.getTopLevelWorldView().getBaseY();
        int plane = client.getTopLevelWorldView().getPlane();
        lastLoadedBaseX = baseX;
        lastLoadedBaseY = baseY;
        lastLoadedPlane = plane;

        for (int x = 0; x < 104; ++x) {
            for (int y = 0; y < 104; ++y) {
                if (visited[x][y]) {
                    checkedTiles.add(new WorldPoint(baseX + x, baseY + y, plane));
                }
            }
        }

        return checkedTiles;
    }

    public static boolean isWalkable(WorldPoint point)
    {
        int baseX = client.getTopLevelWorldView().getBaseX();
        int baseY = client.getTopLevelWorldView().getBaseY();
        int plane = client.getTopLevelWorldView().getPlane();

        if (baseX == lastLoadedBaseX && baseY == lastLoadedBaseY && plane == lastLoadedPlane)
        {
            return checkedTiles.contains(point);
        }

        return reachableTiles().contains(point);
    }

    public static int approxDistanceTo(WorldPoint point1, WorldPoint point2)
    {
        return Math.max(Math.abs(point1.getX() - point2.getX()), Math.abs(point1.getY() - point2.getY()));
    }

    public static float distanceTo2DHypotenuse(WorldPoint main, WorldPoint other)
    {
        return (float) Math.hypot((main.getX() - other.getX()), (main.getY() - other.getY()));
    }

    public static float distanceTo2DHypotenuse(WorldPoint main, WorldPoint other, int size1, int size2)
    {
        WorldPoint midMain = main.dx((int) Math.floor((float) size1 / 2)).dy((int) Math.floor((float) size1 / 2));
        WorldPoint midOther = other.dx((int) Math.floor((float) size2 / 2)).dy((int) Math.floor((float) size2 / 2));
        return (float) Math.hypot(midMain.getX() - midOther.getX(), midMain.getY() - midOther.getY());
    }

    public static float distanceTo2DHypotenuse(WorldPoint main, WorldPoint other, int size1X, int size1Y, int size2)
    {
        WorldPoint midMain = main.dx((int) Math.floor((float) size1X / 2)).dy((int) Math.floor((float) size1Y / 2));
        WorldPoint midOther = other.dx((int) Math.floor((float) size2 / 2)).dy((int) Math.floor((float) size2 / 2));
        return (float) Math.hypot(midMain.getX() - midOther.getX(), midMain.getY() - midOther.getY());
    }

    public static WorldPoint getCenterTileFromWorldArea(WorldArea area)
    {
        return new WorldPoint(area.getX() + area.getWidth() / 2, area.getY() + area.getHeight() / 2, area.getPlane());
    }

    public static List<ETileItem> getAllTileItems(Predicate<ETileItem> filter)
    {
        return TileItems.search().filter(filter).result();
    }

    public static Optional<ETileItem> nearestTileItem(Predicate<ETileItem> filter)
    {
        return TileItems.search().filter(filter).nearestToPlayer();
    }

    public static boolean tileItemNameExistsWithinDistance(String name, int distance)
    {
        ETileItem item = TileItems.search().nameContains(name).withinDistance(distance).result().stream().findFirst().orElse(null);
        return item != null;
    }

    public static boolean tileItemIdExistsWithinDistance(int itemId, int distance)
    {
        ETileItem item = TileItems.search().withId(itemId).withinDistance(distance).result().stream().findFirst().orElse(null);
        return item != null;
    }

    public static void interactWithTileItem(int itemId, String action)
    {
        TileItems.search().withId(itemId).nearestToPlayer().ifPresent(item -> TileItemPackets.queueTileItemAction(item, false));

    }

    public static void interactWithTileItem(String name, String action)
    {
        TileItems.search().nameContains(name).nearestToPlayer().ifPresent(item -> TileItemPackets.queueTileItemAction(item, false));

    }

    public static void interactWithTileItem(ETileItem item, String action)
    {
        if (item != null)
        {
            TileItemPackets.queueTileItemAction(item, false);
        }
    }

    public static void showNonModalMessageDialog(String message, String title)
    {
        JOptionPane pane = new JOptionPane();
        pane.setComponentOrientation(JOptionPane.getRootFrame().getComponentOrientation());
        pane.setMessage(message);
        JDialog dialog = pane.createDialog(pane, title);

        pane.addPropertyChangeListener(JOptionPane.VALUE_PROPERTY, ignored -> dialog.dispose());

        dialog.setModal(false);
        dialog.setVisible(true);
    }
}
