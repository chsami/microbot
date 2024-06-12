package net.runelite.client.plugins.hoseaplugins.api.utils;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.TileObjects;
import net.runelite.client.plugins.hoseaplugins.ethanapi.InteractionApi.TileObjectInteraction;
import net.runelite.api.*;
import net.runelite.client.RuneLite;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class GameObjectUtils
{
    static Client client = RuneLite.getInjector().getInstance(Client.class);

    public static TileObject getFirstTileObjectAt(Tile tile, int... ids)
    {
        return Arrays.stream(tile.getGameObjects()).filter(gameObject -> gameObject != null && Arrays.asList(ids).contains(gameObject.getId())).findFirst().orElse(null);
    }

    public static void interact(GameObject object, String action)
    {
        TileObjectInteraction.interact(object, action);
    }

    public static void interact(TileObject object, String action)
    {
        TileObjectInteraction.interact(object, action);
    }

    public static void interact(WallObject object, String action)
    {
        TileObjectInteraction.interact(object, action);
    }

    public static boolean hasAction(int objectId, String action)
    {
        ObjectComposition composition = client.getObjectDefinition(objectId);
        if (composition == null)
        {
            return false;
        }

        if (composition.getActions() == null)
        {
            return false;
        }

        return Arrays.stream(composition.getActions()).anyMatch(s -> s != null && s.equals(action));
    }

    public static TileObject nearest(String name)
    {
        return TileObjects.search().nameContains(name).nearestToPlayer().orElse(null);
    }

    public static TileObject nearest(int id)
    {
        return TileObjects.search().withId(id).nearestToPlayer().orElse(null);
    }

    public static TileObject nearest(Predicate<TileObject> filter)
    {
        return TileObjects.search().filter(filter).nearestToPlayer().orElse(null);
    }

    public static List<TileObject> getAll(Predicate<TileObject> filter)
    {
        return TileObjects.search().filter(filter).result();
    }
}
