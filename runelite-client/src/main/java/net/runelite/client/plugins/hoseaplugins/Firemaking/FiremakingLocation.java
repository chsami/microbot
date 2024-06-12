package net.runelite.client.plugins.hoseaplugins.Firemaking;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.Arrays;

public enum FiremakingLocation {
    GE("Grand Exchange", new ArrayList<WorldPoint>(Arrays.asList(
            new WorldPoint(3194, 3491, 0), new WorldPoint(3194, 3490, 0),
            new WorldPoint(3194, 3489, 0), new WorldPoint(3194, 3488, 0)))
    ),
    VARROCK_WEST("Varrock West", new ArrayList<WorldPoint>(Arrays.asList(
            new WorldPoint(3199, 3431, 0), new WorldPoint(3199, 3430, 0),
            new WorldPoint(3199, 3429, 0), new WorldPoint(3199, 3428, 0)))
    ),

    VARROCK_EAST("Varrock East", new ArrayList<WorldPoint>(Arrays.asList(
            new WorldPoint(3266, 3429, 0), new WorldPoint(3266, 3428, 0)))
    ),
    FALADOR_EAST("Falador East", new ArrayList<WorldPoint>(Arrays.asList(
            new WorldPoint(3033, 3362, 0), new WorldPoint(3033, 3361, 0),
            new WorldPoint(3033, 3360, 0)))
    ),
    SEERS("Seers' Village", new ArrayList<WorldPoint>(Arrays.asList(
            new WorldPoint(2733, 3485, 0), new WorldPoint(2733, 3484, 0)))
    );


    @Getter
    private String name;
    @Getter
    private ArrayList<WorldPoint> startTiles;

    FiremakingLocation(String name, ArrayList<WorldPoint> startTiles) {
        this.name = name;
        this.startTiles = startTiles;
    }


}
