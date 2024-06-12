package net.runelite.client.plugins.hoseaplugins.AutoTitheFarm;


import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

import static net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.EthanApiPlugin.getClient;

@Getter
public enum PatchLayout {
    REGULAR_LAYOUT(
            new int[]{62, 64},
            new int[][]{
            {64, 65}, {59, 65}, {64, 62}, {59, 62}, {64, 59}, {59, 59}, {64, 56}, {59, 56}, {59, 50}, {64, 50},
            {59, 47}, {64, 47}, {59, 44}, {64, 44}, {59, 41}, {64, 41}, {69, 41}, {69, 44}, {69, 47}, {69, 50},
            {69, 56}, {69, 59}, {69, 62}, {69, 65}
    }),
    TICK_PERFECT_LAYOUT(
            new int[]{61, 51},
            new int[][]{
                    {59, 50}, {59, 56}, {64, 56}, {59, 59}, {64, 59}, {59, 62}, {64, 62}, {59, 65}, {64, 65}, {69, 65},
                    {69, 62}, {69, 59}, {69, 56}, {64, 50}, {69, 50}, {64, 47}, {69, 47}, {64, 44}, {69, 44}, {64, 41},
                    {69, 41}, {69, 35}, {59, 41}, {59, 44}, {59, 47}
    });

    private final int[] startingPoint;

    private final int[][] layout;

    PatchLayout(int[] startingPoint, int[][] layout) {
        this.startingPoint = startingPoint;
        this.layout = layout;
    }

    public WorldPoint getStartingPoint() {
        if (this.startingPoint.length == 0) {
            return null;
        }
        return WorldPoint.fromScene(getClient().getTopLevelWorldView(), this.startingPoint[0], this.startingPoint[1], 0);
    }
}
