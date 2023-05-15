package net.runelite.client.plugins.microbot.agility.models;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

public class AgilityObstacleModel {
    @Getter
    int objectID;

    public AgilityObstacleModel(int objectID) {
        this.objectID = objectID;
    }
}
