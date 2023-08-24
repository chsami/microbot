package net.runelite.client.plugins.microbot.util.gameobject;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.math.Random;

import java.util.*;
import java.util.stream.Collectors;

import static net.runelite.client.plugins.microbot.util.Global.sleep;


public class Rs2GameObject {

    public static TileObject objectToInteract = null;
    public static String objectAction = null;

    public static boolean interact(GameObject gameObject) {
        return clickObject(gameObject);
    }

    public static boolean interact(TileObject tileObject) {
        return clickObject(tileObject, null);
    }

    public static boolean interact(TileObject tileObject, String action) {
        return clickObject(tileObject, action);
    }

    public static boolean interact(GameObject gameObject, String action) {
        return clickObject(gameObject, action);
    }

    public static boolean interact(int id) {
        TileObject object = findObjectById(id);
        return clickObject(object);
    }

    public static boolean interact(int id, String action) {
        TileObject object = findObjectById(id);
        return clickObject(object, action);
    }

    public static boolean interact(int id, String action, int distance) {
        TileObject object = findObjectByIdAndDistance(id, distance);
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

    public static ObjectComposition findObject(int id) {
        ObjectComposition objComp = Microbot.getClientThread().runOnClientThread(() -> Microbot.getClient().getObjectDefinition(id));
        return objComp;
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
            if (wallObject.getId() == id)
                return wallObject;
        }

        List<DecorativeObject> decorationObjects = getDecorationObjects();


        for (DecorativeObject decorativeObject : decorationObjects) {
            if (decorativeObject.getId() == id)
                return decorativeObject;
        }

        return null;
    }

    public static TileObject findObjectByLocation(WorldPoint worldPoint) {

        List<GameObject> gameObjects = getGameObjects();

        if (gameObjects == null) return null;

        for (net.runelite.api.GameObject gameObject : gameObjects) {
            if (gameObject.getWorldLocation().equals(worldPoint))
                return gameObject;
        }

        List<GroundObject> groundObjects = getGroundObjects();

        for (GroundObject groundObject : groundObjects) {
            if (groundObject.getWorldLocation().equals(worldPoint))
                return groundObject;
        }

        List<WallObject> wallObjects = getWallObjects();


        for (WallObject wallObject : wallObjects) {
            if (wallObject.getWorldLocation().equals(worldPoint))
                return wallObject;
        }

        List<DecorativeObject> decorationObjects = getDecorationObjects();


        for (DecorativeObject decorativeObject : decorationObjects) {
            if (decorativeObject.getWorldLocation().equals(worldPoint))
                return decorativeObject;
        }

        return null;
    }

    public static TileObject findObjectByIdAndDistance(int id, int distance) {

        List<GameObject> gameObjects = getGameObjects();

        gameObjects = gameObjects.stream().filter(x -> Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(x.getWorldLocation()) < distance).collect(Collectors.toList());

        if (gameObjects == null) return null;

        for (net.runelite.api.GameObject gameObject : gameObjects) {
            if (gameObject.getId() == id)
                return gameObject;
        }

        List<GroundObject> groundObjects = getGroundObjects();

        groundObjects = groundObjects.stream().filter(x -> Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(x.getWorldLocation()) < distance).collect(Collectors.toList());


        for (GroundObject groundObject : groundObjects) {
            if (groundObject.getId() == id)
                return groundObject;
        }

        List<WallObject> wallObjects = getWallObjects();

        wallObjects = wallObjects.stream().filter(x -> Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(x.getWorldLocation()) < distance).collect(Collectors.toList());


        for (WallObject wallObject : wallObjects) {
            if (wallObject.getId() == id)
                return wallObject;
        }

        List<DecorativeObject> decorationObjects = getDecorationObjects();

        decorationObjects = decorationObjects.stream().filter(x -> Microbot.getClient().getLocalPlayer().getWorldLocation().distanceTo(x.getWorldLocation()) < distance).collect(Collectors.toList());

        for (DecorativeObject decorativeObject : decorationObjects) {
            if (decorativeObject.getId() == id)
                return decorativeObject;
        }

        return null;
    }

    private static List<DecorativeObject> getDecorationObjects() {
        Scene scene = Microbot.getClient().getScene();
        Tile[][][] tiles = scene.getTiles();

        int z = Microbot.getClient().getPlane();
        List<DecorativeObject> tileObjects = new ArrayList<>();
        for (int x = 0; x < Constants.SCENE_SIZE; ++x) {
            for (int y = 0; y < Constants.SCENE_SIZE; ++y) {
                Tile tile = tiles[z][x][y];

                if (tile == null) {
                    continue;
                }

                tileObjects.add(tile.getDecorativeObject());
            }
        }


        List<DecorativeObject> decorativeObjects = Arrays.stream(tileObjects.toArray(new DecorativeObject[tileObjects.size()]))
                .filter(value -> value != null)
                .sorted(Comparator.comparingInt(value -> value.getLocalLocation().distanceTo(Microbot.getClient().getLocalPlayer().getLocalLocation())))
                .collect(Collectors.toList());

        return decorativeObjects;
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

    public static GameObject findObject(int id, WorldPoint worldPoint) {

        List<GameObject> gameObjects = getGameObjects();

        if (gameObjects == null) return null;

        for (net.runelite.api.GameObject gameObject : gameObjects) {
            if (gameObject.getId() == id && gameObject.getWorldLocation().equals(worldPoint))
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

    public static boolean hasAction(ObjectComposition objComp, String action) {
        boolean result = false;

        result = Arrays.stream(objComp.getActions()).anyMatch(x -> x != null && x.equals(action));
        if (!result) {
            try {
                result = Arrays.stream(objComp.getImpostor().getActions()).anyMatch(x -> x != null && x.toLowerCase().equals(action.toLowerCase()));
            } catch (Exception ex) {
                //do nothing
            }
        }
        return result;
    }

    public static boolean hasAction(GameObject gameObject, String action) {
        boolean result = false;
        ObjectComposition objComp = convertGameObjectToObjectComposition(gameObject);

        result = Arrays.stream(objComp.getActions()).anyMatch(x -> x != null && x.equals(action));
        if (!result) {
            try {
                result = Arrays.stream(objComp.getImpostor().getActions()).anyMatch(x -> x != null && x.toLowerCase().equals(action.toLowerCase()));
            } catch (Exception ex) {
                //do nothing
            }
        }
        return result;
    }

    /**
     * Imposter objects are objects that have their menu action changed but still remain the same object.
     * for example: farming patches
     *
     * @param action
     * @return
     */
    public static GameObject findObjectByImposter(int id, String action) {
        return findObjectByImposter(id, action, true);
    }

    public static GameObject findObjectByImposter(int id, String optionName, boolean exact) {
        List<GameObject> gameObjects = getGameObjects();

        if (gameObjects == null) return null;

        for (net.runelite.api.GameObject gameObject : gameObjects) {

            if (gameObject.getId() != id) continue;

            ObjectComposition objComp = convertGameObjectToObjectComposition(gameObject);

            if (objComp == null) continue;

            try {
                if (objComp.getImpostor() == null) continue;
                if (exact) {
                    if (Arrays.stream(objComp.getImpostor().getActions()).filter(action -> action != null)
                            .anyMatch((action) -> action.toLowerCase().equals(optionName.toLowerCase()))) {
                        return gameObject;
                    }
                } else {
                    if (Arrays.stream(objComp.getImpostor().getActions()).filter(action -> action != null)
                            .anyMatch((action) -> action.toLowerCase().contains(optionName.toLowerCase()))) {
                        return gameObject;
                    }
                }
            } catch (Exception ex) {
                // do nothing
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
    public static GameObject getGameObject(LocalPoint localPoint) {
        Scene scene = Microbot.getClient().getScene();
        Tile[][][] tiles = scene.getTiles();

        int z = Microbot.getClient().getPlane();
        List<GameObject> tileObjects = new ArrayList<>();
        Tile tile = tiles[z][localPoint.getSceneX()][localPoint.getSceneY()];

        for (GameObject tileObject :
                tile.getGameObjects()) {
            if (tileObject != null
                    && tileObject.getSceneMinLocation().equals(tile.getSceneLocation()))
                tileObjects.add(tileObject);
        }

        return Arrays.stream(tile.getGameObjects()).filter(Objects::nonNull).findFirst().orElse(null);
    }

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
                    if (Arrays.stream(tile.getGameObjects()).anyMatch(c -> c != null && c.getId() == 11797)) {
                        System.out.println(tile.getGameObjects());
                    }
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
                .filter(value -> value != null && value.getConfig() >= 0)
                .sorted(Comparator.comparingInt(value -> value.getLocalLocation().distanceTo(Microbot.getClient().getLocalPlayer().getLocalLocation())))
                .collect(Collectors.toList());

        return wallObjects;
    }

    private static boolean clickObject(TileObject object) {
        return clickObject(object, "");
    }

    private static boolean clickObject(TileObject object, String action) {
        if (object == null) return false;
        try {
            objectToInteract = object;
            objectAction = action;
            Microbot.getMouse().clickFast(Random.random(0, Microbot.getClient().getCanvasWidth()), Random.random(0, Microbot.getClient().getCanvasHeight()));
            sleep(100);
            objectToInteract = null;
            objectAction = null;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return true;
    }

    public static void handleMenuSwapper(MenuEntry menuEntry) {
        if (objectToInteract == null) return;

        menuEntry.setIdentifier(objectToInteract.getId());
        try {
            GameObject gameObject = (GameObject) objectToInteract;

            if ((gameObject).sizeX() > 1) {
                int offset = gameObject.sizeX() / 2;
                menuEntry.setParam0(gameObject.getLocalLocation().getSceneX() - offset);
            } else {
                menuEntry.setParam0(gameObject.getLocalLocation().getSceneX());
            }
            if (gameObject.sizeY() > 1) {
                int offset = gameObject.sizeY() / 2;
                menuEntry.setParam1(gameObject.getLocalLocation().getSceneY() - offset);
            } else {
                menuEntry.setParam1(gameObject.getLocalLocation().getSceneY());
            }
        } catch (Exception ex) {
            //default objects like walls, groundobjects, decorationobjects etc...
            menuEntry.setParam0(objectToInteract.getLocalLocation().getSceneX());
            menuEntry.setParam1(objectToInteract.getLocalLocation().getSceneY());
        }

        menuEntry.setTarget("");
        menuEntry.setOption(objectAction);
        if (objectAction.equalsIgnoreCase("bank") || objectAction.equalsIgnoreCase("take")) {
            menuEntry.setType(MenuAction.GAME_OBJECT_SECOND_OPTION);
        } else if (objectAction.equalsIgnoreCase("collect") || objectAction.equalsIgnoreCase("store") || objectAction.equalsIgnoreCase("Nets")) {
            menuEntry.setType(MenuAction.GAME_OBJECT_THIRD_OPTION);
        } else if (objectAction.toLowerCase().equals("reset")) {
            menuEntry.setType(MenuAction.GAME_OBJECT_SECOND_OPTION);
        } else {
            menuEntry.setType(MenuAction.GAME_OBJECT_FIRST_OPTION);
        }
    }
}
