package net.runelite.client.plugins.microbot.util.grounditem;

import net.runelite.api.Perspective;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.camera.Camera;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.menu.Menu;
import net.runelite.client.plugins.microbot.util.models.RS2Item;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GroundItem {
    /**
     * Returns all the ground items at a tile on the current plane.
     *
     * @param x The x position of the tile in the world.
     * @param y The y position of the tile in the world.
     * @return An array of the ground items on the specified tile.
     */
    public static RS2Item[] getAllAt(int x, int y) {
        if (!Microbot.isLoggedIn()) {
            return null;
        }
        List<RS2Item> list = new ArrayList<>();

        Tile tile = getTile(x, y);
        if (tile == null) {
            return null;
        }

        List<TileItem> groundItems = tile.getGroundItems();

        if (groundItems != null && !groundItems.isEmpty()) {
            for (TileItem groundItem : groundItems) {
                RS2Item rs2Item = new RS2Item(Microbot.getItemManager().getItemComposition(groundItem.getId()), tile, groundItem);
                list.add(rs2Item);
            }
        }

        return list.toArray(new RS2Item[list.size()]);
    }

    public static Tile getTile(int x, int y) {
        WorldPoint worldPoint = new WorldPoint(x, y, Microbot.getClient().getPlane());
        if (worldPoint.isInScene(Microbot.getClient())) {
            LocalPoint localPoint = LocalPoint.fromWorld(Microbot.getClient(), worldPoint);
            return Microbot.getClient().getScene().getTiles()[worldPoint.getPlane()][localPoint.getSceneX()][localPoint.getSceneY()];
        }
        return null;
    }

    public static RS2Item[] getAll(int range) {
        ArrayList<RS2Item> temp = new ArrayList<>();
        int pX = Microbot.getClient().getLocalPlayer().getWorldLocation().getX();
        int pY = Microbot.getClient().getLocalPlayer().getWorldLocation().getY();
        int minX = pX - range, minY = pY - range;
        int maxX = pX + range, maxY = pY + range;
        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                RS2Item[] items = getAllAt(x, y);
                if (items != null)
                    for (RS2Item item : items) {
                        if (item == null) {
                            continue;
                        }
                        temp.add(item);
                    }
            }
        }
        return temp.toArray(new RS2Item[temp.size()]);
    }

    public static boolean loot(String lootItem, int range) {
        if (Inventory.isInventoryFull(lootItem)) return false;
        RS2Item[] groundItems = Microbot.getClientThread().runOnClientThread(() ->
                GroundItem.getAll(range)
        );
        for (RS2Item rs2Item : groundItems) {
            if (rs2Item.getItem().getName().toLowerCase().equals(lootItem.toLowerCase())) {
                LocalPoint groundPoint = LocalPoint.fromWorld(Microbot.getClient(), rs2Item.getTile().getWorldLocation());
                Polygon poly = Perspective.getCanvasTilePoly(Microbot.getClient(), groundPoint, rs2Item.getTile().getItemLayer().getHeight());
                if (Camera.isTileOnScreen(rs2Item.getTile().getLocalLocation())) {
                    if (Menu.doAction("Take", poly, new String[]{lootItem.toLowerCase()})) {
                        Microbot.isBussy = true;
                        Global.sleepUntilOnClientThread(() -> Microbot.getClient().getLocalPlayer().getLocalLocation().equals(rs2Item.getTile().getLocalLocation()), 5000);
                        return true;
                    }
                } else {
                    Camera.turnTo(rs2Item.getTile().getLocalLocation());
                }
            }
        }
        return false;
    }
}
