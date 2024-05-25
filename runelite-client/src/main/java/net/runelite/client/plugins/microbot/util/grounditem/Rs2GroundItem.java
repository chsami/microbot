package net.runelite.client.plugins.microbot.util.grounditem;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.grounditems.GroundItem;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.MicrobotOverlay;
import net.runelite.client.plugins.microbot.util.Global;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.models.RS2Item;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.reflection.Rs2Reflection;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static net.runelite.client.plugins.microbot.util.Global.sleep;
import static net.runelite.client.plugins.microbot.util.Global.sleepUntil;

public class Rs2GroundItem {

    private static boolean interact(RS2Item rs2Item, String action) {
        if (rs2Item == null) return false;
        try {
            interact(new InteractModel(rs2Item.getTileItem().getId(), rs2Item.getTile().getWorldLocation(), rs2Item.getItem().getName()), action);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return true;
    }

    /**
     * Interacts with a ground item by performing a specified action.
     *
     * @param groundItem The ground item to interact with.
     * @param action     The action to perform on the ground item.
     * @return true if the interaction was successful, false otherwise.
     */
    private static boolean interact(InteractModel groundItem, String action) {
        if (groundItem == null) return false;
        try {

            int param0;
            int param1;
            int identifier;
            String target;
            String option;
            MenuAction menuAction = MenuAction.CANCEL;
            ItemComposition item;

            item = Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getItemDefinition(groundItem.getId()));
            identifier = groundItem.getId();

            LocalPoint localPoint = LocalPoint.fromWorld(Microbot.getClient(), groundItem.getLocation());
            if (localPoint == null) return false;

            param0 = localPoint.getSceneX();
            target = "<col=ff9040>" + groundItem.getName();
            param1 = localPoint.getSceneY();
            option = action;

            String[] groundActions = Rs2Reflection.getGroundItemActions(item);

            int index = -1;
            for (int i = 0; i < groundActions.length; i++) {
                String groundAction = groundActions[i];
                if (groundAction == null || !groundAction.equalsIgnoreCase(action)) continue;
                index = i;
            }

            if (Microbot.getClient().isWidgetSelected()) {
                menuAction = MenuAction.WIDGET_TARGET_ON_GROUND_ITEM;
            } else if (index == 0) {
                menuAction = MenuAction.GROUND_ITEM_FIRST_OPTION;
            } else if (index == 1) {
                menuAction = MenuAction.GROUND_ITEM_SECOND_OPTION;
            } else if (index == 2) {
                menuAction = MenuAction.GROUND_ITEM_THIRD_OPTION;
            } else if (index == 3) {
                menuAction = MenuAction.GROUND_ITEM_FOURTH_OPTION;
            } else if (index == 4) {
                menuAction = MenuAction.GROUND_ITEM_FIFTH_OPTION;
            }
            LocalPoint localPoint1 =  LocalPoint.fromWorld(Microbot.getClient(), groundItem.location);
            if (localPoint1 != null) {
                Microbot.doInvoke(new NewMenuEntry(param0, param1, menuAction.getId(), identifier, -1, target),
                        Perspective.getCanvasTilePoly(Microbot.getClient(), localPoint1).getBounds());
            } else {
                Microbot.doInvoke(new NewMenuEntry(param0, param1, menuAction.getId(), identifier, -1, target),
                         new Rectangle(1, 1, Microbot.getClient().getCanvasWidth(), Microbot.getClient().getCanvasHeight()));

            }
            //Rs2Reflection.invokeMenu(param0, param1, menuAction.getId(), identifier, -1, option, target, -1, -1);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return true;
    }

    private static boolean interact(GroundItem groundItem) {
        return interact(new InteractModel(groundItem.getId(), groundItem.getLocation(), groundItem.getName()), "Take");
    }

    /**
     * Returns all the ground items at a tile on the current plane.
     *
     * @param x The x position of the tile in the world.
     * @param y The y position of the tile in the world.
     * @return An array of the ground items on the specified tile.
     */
    public static RS2Item[] getAllAt(int x, int y) {
        return Microbot.getClientThread().runOnClientThread(() -> {
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
        });
    }

    public static Tile getTile(int x, int y) {
        WorldPoint worldPoint = new WorldPoint(x, y, Microbot.getClient().getPlane());
        if (worldPoint.isInScene(Microbot.getClient())) {
            LocalPoint localPoint = LocalPoint.fromWorld(Microbot.getClient(), worldPoint);
            if (localPoint == null) return null;
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
        if (Rs2Inventory.isFull(lootItem)) return false;
        RS2Item[] groundItems = Microbot.getClientThread().runOnClientThread(() ->
                Rs2GroundItem.getAll(range)
        );
        for (RS2Item rs2Item : groundItems) {
            if (rs2Item.getItem().getName().equalsIgnoreCase(lootItem) && rs2Item.getTileItem().getQuantity() >= minQuantity) {
                interact(rs2Item);
                return true;
            }
        }
        return false;
    }

    public static boolean lootItemBasedOnValue(int value, int range) {
        RS2Item[] groundItems = Microbot.getClientThread().runOnClientThread(() ->
                Rs2GroundItem.getAll(range)
        );
        final int invSize = Rs2Inventory.size();
        for (RS2Item rs2Item : groundItems) {
            if (!hasLineOfSight(rs2Item.getTile())) continue;
            long totalPrice = (long) Microbot.getClientThread().runOnClientThread(() ->
                    Microbot.getItemManager().getItemPrice(rs2Item.getItem().getId()) * rs2Item.getTileItem().getQuantity());
            if (totalPrice >= value) {
                if (Rs2Inventory.isFull()) {
                    if (Rs2Player.eatAt(100)) {
                        Rs2Player.waitForAnimation();
                        boolean result = interact(rs2Item);
                        if (result) {
                            sleepUntil(() -> invSize != Rs2Inventory.size());
                        }
                        return result;
                    }
                }
                boolean result = interact(rs2Item);
                if (result) {
                    sleepUntil(() -> invSize != Rs2Inventory.size());
                }
                return result;
            }
        }
        return false;
    }

    public static boolean lootItemBasedOnValue(int minValue, int maxValue, int range) {
        RS2Item[] groundItems = Microbot.getClientThread().runOnClientThread(() ->
                Rs2GroundItem.getAll(range)
        );
        final int invSize = Rs2Inventory.size();
        for (RS2Item rs2Item : groundItems) {
            if (Rs2Inventory.isFull(rs2Item.getItem().getName())) continue;
            if (!hasLineOfSight(rs2Item.getTile())) continue;
            long totalPrice = (long) Microbot.getClientThread().runOnClientThread(() ->
                    Microbot.getItemManager().getItemPrice(rs2Item.getItem().getId()) * rs2Item.getTileItem().getQuantity());
            if (totalPrice >= minValue && totalPrice <= maxValue) {
                if (Rs2Inventory.isFull()) {
                    if (Rs2Player.eatAt(100)) {
                        Rs2Player.waitForAnimation();
                        boolean result = interact(rs2Item);
                        if (result) {
                            sleepUntil(() -> invSize != Rs2Inventory.size());
                        }
                        return result;
                    }
                }
                boolean result = interact(rs2Item);
                if (result) {
                    sleepUntil(() -> invSize != Rs2Inventory.size());
                }
                return result;
            }
        }
        return false;
    }

    public static boolean isItemBasedOnValueOnGround(int value, int range) {
        RS2Item[] groundItems = Microbot.getClientThread().runOnClientThread(() ->
                Rs2GroundItem.getAll(range)
        );
        for (RS2Item rs2Item : groundItems) {
            long totalPrice = (long) Microbot.getClientThread().runOnClientThread(() ->
                    Microbot.getItemManager().getItemPrice(rs2Item.getItem().getId()) * rs2Item.getTileItem().getQuantity());
            if (totalPrice >= value) {
                return true;
            }
        }
        return false;
    }

    public static boolean lootAllItemBasedOnValue(int value, int range) {
        RS2Item[] groundItems = Microbot.getClientThread().runOnClientThread(() ->
                Rs2GroundItem.getAll(range)
        );
        for (RS2Item rs2Item : groundItems) {
            if (Rs2Inventory.isFull(rs2Item.getItem().getName())) continue;
            long totalPrice = (long) Microbot.getClientThread().runOnClientThread(() ->
                    Microbot.getItemManager().getItemPrice(rs2Item.getItem().getId()) * rs2Item.getTileItem().getQuantity());
            if (totalPrice >= value) {
                return interact(rs2Item);
            }
        }
        return false;
    }

    public static boolean loot(int itemId) {
        if (Rs2Inventory.isFull(itemId)) return false;
        RS2Item[] groundItems = Microbot.getClientThread().runOnClientThread(() ->
                Rs2GroundItem.getAll(50)
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
        return lootItemBasedOnValue(minGePrice, 14);
    }

    public static boolean pickup(int itemId) {
        return loot(itemId);
    }

    public static boolean take(int itemId) {
        return loot(itemId);
    }

    public static boolean interact(RS2Item rs2Item) {
        return interact(rs2Item, "Take");
    }

    public static boolean interact(String itemName, String action) {
        return interact(itemName, action, 255);
    }

    public static boolean interact(String itemName, String action, int range) {
        RS2Item[] groundItems = Microbot.getClientThread().runOnClientThread(() -> Rs2GroundItem.getAll(range));
        for (RS2Item rs2Item : groundItems) {
            if (rs2Item.getItem().getName().equalsIgnoreCase(itemName)) {
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

    @Deprecated(since="1.0.0")
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

    @Deprecated(since="1.0.0")
    public static boolean interact(int itemId, String action, int x, int y) {
        RS2Item[] itemsAtTile = getAllAt(x, y);
        if (itemsAtTile != null) {
            for (RS2Item item : itemsAtTile) {
                if (item.getItem().getId() == itemId) {
                    interact(item, action);
                    Global.sleepUntilOnClientThread(() -> Microbot.getClient().getLocalPlayer().getLocalLocation().equals(item.getTile().getLocalLocation()), 5000);
                }
            }
        }
        return false;
    }

    public static boolean exists(String itemName, int range) {
        RS2Item[] groundItems = Microbot.getClientThread().runOnClientThread(() -> Rs2GroundItem.getAll(range));
        for (RS2Item rs2Item : groundItems) {
            if (rs2Item.getItem().getName().equalsIgnoreCase(itemName)) {
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

    @Deprecated(since="1.0.0")
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

    public static boolean hasLineOfSight(Tile tile) {
        if (tile == null) return false;
        return new WorldArea(
                tile.getWorldLocation(),
                1,
                1)
                .hasLineOfSightTo(Microbot.getClient().getTopLevelWorldView(), Microbot.getClient().getLocalPlayer().getWorldLocation().toWorldArea());
    }
}
