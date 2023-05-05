package net.runelite.client.plugins.microbot.util.gameobject;

import net.runelite.api.*;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.menu.Rs2Menu;
import net.runelite.client.plugins.microbot.util.walker.Walker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class Rs2GameObject {

    public static boolean interact(GameObject gameObject) {
        return clickObject(gameObject);
    }

    public static boolean interact(int id) {
        TileObject object = findObjectById(id);
        return clickObject(object);
    }

    public static boolean interact(int id, String action) {
        TileObject object = findObjectById(id);
        return clickObject(object, action);
    }

    public static TileObject interactAndGetObject(int id) {
        TileObject object = findObjectById(id);
        clickObject(object);
        return object;
    }

    public static boolean interact(int[] objectIds, String action) {
        for (int i = 0; i < objectIds.length; i++) {
            if (interact(objectIds[i], action))
                return true;
        }
        return false;
    }

    public static boolean interact(String objectName) {
        GameObject object = findObject(objectName, true);
        return clickObject(object);
    }

    public static boolean interact(String objectName, String action) {
        GameObject object = findObject(objectName, true);
        return Rs2Menu.doAction(action, object.getClickbox().getBounds());
    }

    public static boolean interactByOptionName(String action) {
        GameObject object = findObjectByOption(action);
        return clickObject(object);
    }

    public static boolean interact(String objectName, boolean exact) {
        GameObject object = findObject(objectName, exact);
        return clickObject(object);
    }

    public static boolean interact(String objectName, String action, boolean exact) {
        GameObject object = findObject(objectName, exact);
        return clickObject(object, action);
    }

    public static GameObject findObject(String objectName) {
        return findObject(objectName, true);
    }

    public static TileObject findObjectById(int id) {

        List<GameObject> gameObjects = getGameObjects();

        if (gameObjects == null) return null;

        for (net.runelite.api.GameObject gameObject : gameObjects) {
            if (gameObject.getId() == id)
                return gameObject;
        }

        List<GroundObject> groundObjects = getGroundObjects();

        for (GroundObject groundObject : groundObjects) {
            if (groundObject.getId() == id)
                return groundObject;
        }

        List<WallObject> wallObjects = getWallObjects();


        for (WallObject wallObject : wallObjects) {
            if (wallObject.getId() == id && wallObject.getConfig() == 128)
                return wallObject;
        }

        return null;
    }

    public static GameObject findObjectById(int id, int x) {

        List<GameObject> gameObjects = getGameObjects();

        if (gameObjects == null) return null;

        for (net.runelite.api.GameObject gameObject : gameObjects) {
            if (gameObject.getId() == id && gameObject.getWorldLocation().getX() == x)
                return gameObject;
        }

        return null;
    }

    public static ObjectComposition findObjectComposition(int id) {

        List<GameObject> gameObjects = getGameObjects();

        if (gameObjects == null) return null;

        for (net.runelite.api.GameObject gameObject : gameObjects) {
            if (gameObject.getId() == id) {
                ObjectComposition objComp = convertGameObjectToObjectComposition(gameObject);
                return objComp;
            }
        }
        return null;
    }


    public static GameObject findObject(String objectName, boolean exact) {

        List<GameObject> gameObjects = getGameObjects();

        if (gameObjects == null) return null;

        for (net.runelite.api.GameObject gameObject : gameObjects) {

            ObjectComposition objComp = convertGameObjectToObjectComposition(gameObject);

            if (objComp == null) continue;

            if (exact) {
                if (objComp.getName().toLowerCase().equals(objectName.toLowerCase())) {
                    return gameObject;
                }
            } else {
                if (objComp.getName().toLowerCase().contains(objectName.toLowerCase())) {
                    return gameObject;
                }
            }
        }

        return null;
    }

    public static GameObject findObjectByOption(String action) {
        return findObjectByOption(action, true);
    }

    public static GameObject findObjectByOption(String optionName, boolean exact) {
        List<GameObject> gameObjects = getGameObjects();

        if (gameObjects == null) return null;

        for (net.runelite.api.GameObject gameObject : gameObjects) {

            ObjectComposition objComp = convertGameObjectToObjectComposition(gameObject);

            if (objComp == null) continue;

            if (exact) {
                if (Arrays.stream(objComp.getActions()).filter(action -> action != null).anyMatch((action) -> action.toLowerCase().equals(optionName.toLowerCase()))) {
                    return gameObject;
                }
            } else {
                if (Arrays.stream(objComp.getActions()).filter(action -> action != null).anyMatch((action) -> action.toLowerCase().contains(optionName.toLowerCase()))) {
                    return gameObject;
                }
            }
        }

        return null;
    }


    public static GameObject findBank() {
        GameObject bank = findObjectByOption("bank", false);
        if (bank == null) {
            bank = findObjectByOption("collect", false);
        }
        return bank;
    }

    public static GameObject findBank(String action) {
        GameObject bank = findObjectByOption(action, false);
        return bank;
    }

    public static TileObject findObject(int[] ids) {
        TileObject tileObject = null;
        for (int id :
                ids) {
            tileObject = findObjectById(id);
        }
        return tileObject;
    }

    public static ObjectComposition convertGameObjectToObjectComposition(GameObject gameObject) {
        Player player = Microbot.getClient().getLocalPlayer();
        if (player.getLocalLocation().distanceTo(gameObject.getLocalLocation()) > 2400) return null;
        ObjectComposition objComp = Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getObjectDefinition(gameObject.getId()));
        return objComp;
    }

    public static WallObject findDoor(int id) {
        Scene scene = Microbot.getClient().getScene();
        Tile[][][] tiles = scene.getTiles();

        int z = Microbot.getClient().getPlane();
        for (int x = 0; x < Constants.SCENE_SIZE; ++x) {
            for (int y = 0; y < Constants.SCENE_SIZE; ++y) {
                Tile tile = tiles[z][x][y];

                if (tile == null) {
                    continue;
                }
                WallObject wall = tile.getWallObject();
                if (wall != null && wall.getId() == id)
                    return wall;
            }
        }
        return null;
    }


    //private methods
    private static List<GameObject> getGameObjects() {
        Scene scene = Microbot.getClient().getScene();
        Tile[][][] tiles = scene.getTiles();

        int z = Microbot.getClient().getPlane();
        List<GameObject> tileObjects = new ArrayList<>();
        for (int x = 0; x < Constants.SCENE_SIZE; ++x) {
            for (int y = 0; y < Constants.SCENE_SIZE; ++y) {
                Tile tile = tiles[z][x][y];

                if (tile == null) {
                    continue;
                }
                for (GameObject tileObject :
                        tile.getGameObjects()) {
                    if (tileObject != null
                            && tileObject.getSceneMinLocation().equals(tile.getSceneLocation()))
                        tileObjects.add(tileObject);
                }
            }
        }


        List<GameObject> gameObjects = Arrays.stream(tileObjects.toArray(new GameObject[tileObjects.size()]))
                .filter(value -> value != null)
                .sorted(Comparator.comparingInt(value -> value.getLocalLocation().distanceTo(Microbot.getClient().getLocalPlayer().getLocalLocation())))
                .collect(Collectors.toList());

        return gameObjects;
    }

    private static List<GroundObject> getGroundObjects() {
        Scene scene = Microbot.getClient().getScene();
        Tile[][][] tiles = scene.getTiles();

        int z = Microbot.getClient().getPlane();
        List<GroundObject> tileObjects = new ArrayList<>();
        for (int x = 0; x < Constants.SCENE_SIZE; ++x) {
            for (int y = 0; y < Constants.SCENE_SIZE; ++y) {
                Tile tile = tiles[z][x][y];

                if (tile == null) {
                    continue;
                }

                tileObjects.add(tile.getGroundObject());
            }
        }


        List<GroundObject> groundObjects = Arrays.stream(tileObjects.toArray(new GroundObject[tileObjects.size()]))
                .filter(value -> value != null)
                .sorted(Comparator.comparingInt(value -> value.getLocalLocation().distanceTo(Microbot.getClient().getLocalPlayer().getLocalLocation())))
                .collect(Collectors.toList());

        return groundObjects;
    }

    private static List<WallObject> getWallObjects() {
        Scene scene = Microbot.getClient().getScene();
        Tile[][][] tiles = scene.getTiles();

        int z = Microbot.getClient().getPlane();
        List<WallObject> tileObjects = new ArrayList<>();
        for (int x = 0; x < Constants.SCENE_SIZE; ++x) {
            for (int y = 0; y < Constants.SCENE_SIZE; ++y) {
                Tile tile = tiles[z][x][y];

                if (tile == null) {
                    continue;
                }

                tileObjects.add(tile.getWallObject());
            }
        }


        List<WallObject> wallObjects = Arrays.stream(tileObjects.toArray(new WallObject[tileObjects.size()]))
                .filter(value -> value != null)
                .sorted(Comparator.comparingInt(value -> value.getLocalLocation().distanceTo(Microbot.getClient().getLocalPlayer().getLocalLocation())))
                .collect(Collectors.toList());

        return wallObjects;
    }

    private static boolean clickObject(TileObject object) {
        if (object != null && object.getClickbox() != null) {
            Microbot.getMouse().click(object.getClickbox().getBounds());
            return true;
        }
        return false;
    }

    private static boolean clickObject(TileObject object, String optionName) {
        if (object != null && object.getClickbox() != null) {
            if (optionName != null && optionName.length() > 0) {
                return Rs2Menu.doAction(optionName, object.getClickbox());
            } else {
                Microbot.getMouse().click(object.getClickbox().getBounds());
                return true;
            }
        }
        return false;
    }
}
