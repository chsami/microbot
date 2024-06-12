package net.runelite.client.plugins.hoseaplugins.PiggyUtils.API;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.TileObjects;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.query.TileObjectQuery;
import net.runelite.api.ObjectComposition;
import net.runelite.api.TileObject;

import java.util.Arrays;
import java.util.Optional;

public class ObjectUtil {

    public static Optional<TileObject> getNearestBank() {
        return TileObjects.search().filter(tileObject -> {
            ObjectComposition comp = TileObjectQuery.getObjectComposition(tileObject);
            if (comp == null)
                return false;
            return comp.getName().toLowerCase().contains("bank") && Arrays.stream(comp.getActions()).anyMatch(action -> action.toLowerCase().contains("bank") || action.toLowerCase().contains("use") || action.contains("open"));
        }).nearestToPlayer();
    }

    public static Optional<TileObject> getNearest(String name, boolean caseSensitive) {
        if (caseSensitive) {
            return TileObjects.search().withName(name).nearestToPlayer();
        } else {
            return withNameNoCase(name).nearestToPlayer();
        }
    }

    public static Optional<TileObject> getNearest(int id) {
        return TileObjects.search().withId(id).nearestToPlayer();
    }

    public static Optional<TileObject> getNearestNameContains(String name) {
        return nameContainsNoCase(name).nearestToPlayer();
    }

    public static TileObjectQuery withNameNoCase(String name) {
        return TileObjects.search().filter(tileObject -> {
            ObjectComposition comp = TileObjectQuery.getObjectComposition(tileObject);
            if (comp == null)
                return false;
            return comp.getName().toLowerCase().equals(name.toLowerCase());
        });
    }

    public static TileObjectQuery nameContainsNoCase(String name) {
        return TileObjects.search().filter(tileObject -> {
                    ObjectComposition comp = TileObjectQuery.getObjectComposition(tileObject);
                    if (comp == null)
                        return false;
                    return comp.getName().toLowerCase().contains(name.toLowerCase());
                });
    }
}
