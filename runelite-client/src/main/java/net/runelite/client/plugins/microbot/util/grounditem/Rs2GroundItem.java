package net.runelite.client.plugins.microbot.util.grounditem;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.grounditems.GroundItem;
import net.runelite.client.plugins.grounditems.GroundItemsPlugin;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.camera.Camera;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.menu.Rs2Menu;
import net.runelite.client.plugins.microbot.util.models.RS2Item;
import net.runelite.client.plugins.microbot.util.reflection.Rs2Reflection;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static net.runelite.client.plugins.microbot.util.Global.*;
import static net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory.isItemSelected;

public class Rs2GroundItem {

    public static RS2Item itemInteraction;
    public static String itemAction;
    public static GroundItem groundItemInteraction;

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
        List<RS2Item> temp = new ArrayList<>();
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
        //sort on closest item first
        temp = temp.stream().sorted(Comparator
                        .comparingInt(value -> value.getTile().getLocalLocation()
                                .distanceTo(Microbot.getClient().getLocalPlayer().getLocalLocation())))
                .collect(Collectors.toList());

        return temp.toArray(new RS2Item[temp.size()]);
    }

    public static boolean loot(String lootItem, int range) {
        return loot(lootItem, 1, range);
    }

    public static boolean pickup(String lootItem, int range) {
        return loot(lootItem, 1, range);
    }

    public static boolean take(String lootItem, int range) {
        return loot(lootItem, 1, range);
    }

    public static boolean loot(String lootItem, int minQuantity, int range) {
        if (Inventory.isInventoryFull(lootItem)) return false;
        RS2Item[] groundItems = Microbot.getClientThread().runOnClientThread(() ->
                Rs2GroundItem.getAll(range)
        );
        for (RS2Item rs2Item : groundItems) {
            if (rs2Item.getItem().getName().toLowerCase().equals(lootItem.toLowerCase()) && rs2Item.getTileItem().getQuantity() >= minQuantity) {
                interact(rs2Item);
                return true;
            }
        }
        return false;
    }

    public static boolean loot(int itemId) {
        if (Inventory.isInventoryFull(itemId)) return false;
        RS2Item[] groundItems = Microbot.getClientThread().runOnClientThread(() ->
                Rs2GroundItem.getAll(255)
        );
        for (RS2Item rs2Item : groundItems) {
            if (rs2Item.getItem().getId() == itemId) {
                interact(rs2Item);
                return true;
            }
        }
        return false;
    }

    public static boolean lootAtGePrice(int minGePrice) {
        Collection<GroundItem> groundItemList = GroundItemsPlugin.getCollectedGroundItems().values();
        groundItemList = groundItemList.stream().filter(x -> x.getGePrice() >= minGePrice)
                .sorted(Comparator.comparingInt(value -> value.getLocation().distanceTo(Microbot.getClient().getLocalPlayer().getWorldLocation())))
                .collect(Collectors.toList());
        for (GroundItem groundItem : groundItemList) {
            if (Inventory.isInventoryFull(groundItem.getName())) return false;
            interact(groundItem);
            sleepUntilOnClientThread(() -> Microbot.getClient().getLocalPlayer().getWorldLocation().equals(groundItem.getLocation()));
            return true;
        }
        return false;
    }

    public static boolean pickup(int itemId) {
        return loot(itemId);
    }

    public static boolean take(int itemId) {
        return loot(itemId);
    }

    private static boolean interact(RS2Item rs2Item, String action) {
        if (rs2Item == null) return false;
        try {
            itemInteraction = rs2Item;
            itemAction = action;
            Microbot.getMouse().clickFast(Random.random(0, Microbot.getClient().getCanvasWidth()), Random.random(0, Microbot.getClient().getCanvasHeight()));
            sleep(100);
            itemInteraction = null;
            itemAction = "";
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return true;
    }

    private static boolean interact(GroundItem groundItem, String action) {
        if (groundItem == null) return false;
        try {
            groundItemInteraction = groundItem;
            itemAction = action;
            Microbot.getMouse().clickFast(Random.random(0, Microbot.getClient().getCanvasWidth()), Random.random(0, Microbot.getClient().getCanvasHeight()));
            sleep(100);
            groundItemInteraction = null;
            itemAction = "";
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return true;
    }

    private static boolean interact(RS2Item rs2Item) {
        return interact(rs2Item, "Take");
    }

    private static boolean interact(GroundItem groundItem) {
        return interact(groundItem, "Take");
    }

    public static boolean interact(String itemName, String action) {
        return interact(itemName, action, 255);
    }

    public static boolean interact(String itemName, String action, int range) {
        RS2Item[] groundItems = Microbot.getClientThread().runOnClientThread(() -> Rs2GroundItem.getAll(range));
        for (RS2Item rs2Item : groundItems) {
            if (rs2Item.getItem().getName().toLowerCase().equals(itemName.toLowerCase())) {
                interact(rs2Item, action);
                return true;
            }
        }
        return false;
    }

    public static boolean interact(int itemId, String action, int range) {
        RS2Item[] groundItems = Microbot.getClientThread().runOnClientThread(() -> Rs2GroundItem.getAll(range));
        for (RS2Item rs2Item : groundItems) {
            if (rs2Item.getItem().getId() == itemId) {
                interact(rs2Item, action);
                return true;
            }
        }
        return false;
    }

    public static boolean interact(String itemName, String action, int x, int y) {
        RS2Item[] itemsAtTile = getAllAt(x, y);
        if (itemsAtTile != null) {
            for (RS2Item item : itemsAtTile) {
                if (item.getItem().getName().equalsIgnoreCase(itemName)) {
                    interact(item, action);
                }
            }
        }
        return false;
    }

    public static boolean interact(int itemId, String action, int x, int y) {
        RS2Item[] itemsAtTile = getAllAt(x, y);
        if (itemsAtTile != null) {
            for (RS2Item item : itemsAtTile) {
                if (item.getItem().getId() == itemId) {
                    LocalPoint groundPoint = LocalPoint.fromWorld(Microbot.getClient(), item.getTile().getWorldLocation());
                    Polygon poly = Perspective.getCanvasTilePoly(Microbot.getClient(), groundPoint, item.getTile().getItemLayer().getHeight());
                    if (Camera.isTileOnScreen(item.getTile().getLocalLocation())) {
                        if (Rs2Menu.doAction(action, poly)) {
                            Microbot.pauseAllScripts = true;
                            Global.sleepUntilOnClientThread(() -> Microbot.getClient().getLocalPlayer().getLocalLocation().equals(item.getTile().getLocalLocation()), 5000);
                            return true;
                        }
                    } else {
                        Camera.turnTo(item.getTile().getLocalLocation());
                    }
                }
            }
        }
        return false;
    }

    public static boolean exists(String itemName, int range) {
        RS2Item[] groundItems = Microbot.getClientThread().runOnClientThread(() -> Rs2GroundItem.getAll(range));
        for (RS2Item rs2Item : groundItems) {
            if (rs2Item.getItem().getName().toLowerCase().equals(itemName.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static boolean exists(int itemId, int range) {
        RS2Item[] groundItems = Microbot.getClientThread().runOnClientThread(() -> Rs2GroundItem.getAll(range));
        for (RS2Item rs2Item : groundItems) {
            if (rs2Item.getItem().getId() == itemId) {
                return true;
            }
        }
        return false;
    }

    public static boolean exists(String itemName, int x, int y) {
        RS2Item[] itemsAtTile = getAllAt(x, y);
        if (itemsAtTile != null) {
            for (RS2Item item : itemsAtTile) {
                if (item.getItem().getName().equalsIgnoreCase(itemName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void handleMenuSwapper(MenuEntry menuEntry) {
        if (itemInteraction == null && groundItemInteraction == null) return;

        ItemComposition item = null;

        if (itemInteraction != null) {
            item = Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getItemDefinition(itemInteraction.getTileItem().getId()));
            menuEntry.setIdentifier(itemInteraction.getTileItem().getId());
            menuEntry.setParam0(itemInteraction.getTile().getLocalLocation().getSceneX());
            menuEntry.setTarget("<col=ff9040>" + itemInteraction.getItem().getName());
            menuEntry.setParam1(itemInteraction.getTile().getLocalLocation().getSceneY());
        }
        if (groundItemInteraction != null) {
            item = Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getItemDefinition(groundItemInteraction.getId()));
            menuEntry.setIdentifier(groundItemInteraction.getId());
            LocalPoint localPoint = LocalPoint.fromWorld(Microbot.getClient(), groundItemInteraction.getLocation());
            menuEntry.setParam0(localPoint.getSceneX());
            menuEntry.setTarget("<col=ff9040>" + groundItemInteraction.getName());
            menuEntry.setParam1(localPoint.getSceneY());
        }
        menuEntry.setOption(itemAction);

        String[] groundActions = Rs2Reflection.getGroundItemActions(item);
        int index = -1;
        for (int i = 0; i < groundActions.length; i++) {
            String groundAction = groundActions[i];
            if (groundAction == null || !groundAction.equalsIgnoreCase(itemAction)) continue;
            index = i;
        }

        if (isItemSelected()) {
            menuEntry.setType(MenuAction.WIDGET_TARGET_ON_GROUND_ITEM);
        } else if (index == 0) {
            menuEntry.setType(MenuAction.GROUND_ITEM_SECOND_OPTION);
        } else if (index == 1) {
            menuEntry.setType(MenuAction.GROUND_ITEM_SECOND_OPTION);
        } else if (index == 2) {
            menuEntry.setType(MenuAction.GROUND_ITEM_THIRD_OPTION);
        } else if (index == 3) {
            menuEntry.setType(MenuAction.GROUND_ITEM_FOURTH_OPTION);
        } else if (index == 4) {
            menuEntry.setType(MenuAction.GROUND_ITEM_FIFTH_OPTION);
        }
    }
}
