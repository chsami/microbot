package net.runelite.client.plugins.microbot.roguesden;


import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Obstacles {
    static final Map<WorldPoint, Obstacle> TILE_MAP = new HashMap();
    static Obstacle[] OBSTACLES;

    Obstacles() {
    }

    public static class TipObstacle extends Obstacle {
        private final Color tileColor;

        public TipObstacle(int x, int y, String hint) {
            super(x, y, hint);
            this.tileColor = Color.ORANGE;
        }

        public Color getTileColor() {
            return this.tileColor;
        }
    }

    private static class AvoidObstacle extends Obstacle {
        private final Color tileColor;

        private AvoidObstacle(int x, int y) {
            super(x, y, "AVOID");
            this.tileColor = Color.RED;
        }

        public Color getTileColor() {
            return this.tileColor;
        }
    }

    static class Obstacle {
        @Getter
        private final WorldPoint tile;
        @Getter
        private final String hint;
        @Getter
        private final int objectId;
        @Getter
        private final Color tileColor;
        @Getter
        private final int wait;

        public Obstacle(int x, int y, String hint) {
            this(x, y, hint, -1);
        }

        public Obstacle(int x, int y, String hint, int wait) {
            this(x, y, hint, -1, wait);
        }

        public Obstacle(int x, int y, int objectId) {
            this(x, y, "", objectId, 0);
        }

        public Obstacle(int x, int y, String hint, int objectId, int wait) {
            this.tileColor = Color.GREEN;
            this.tile = new WorldPoint(x, y, 1);
            this.hint = hint;
            this.objectId = objectId;
            this.wait = wait;
            if (objectId != -1) {
                Obstacles.TILE_MAP.put(new WorldPoint(x, y, 1), this);
            }

        }

    }
}
