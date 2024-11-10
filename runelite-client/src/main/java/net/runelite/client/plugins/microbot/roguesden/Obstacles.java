package net.runelite.client.plugins.microbot.roguesden;


import java.awt.*;

public class TipObstacle extends Obstacles.Obstacle {
    private final Color tileColor;

    private TipObstacle(int x, int y, String hint) {
        super(x, y, hint);
        this.tileColor = Color.ORANGE;
    }

    public Color getTileColor() {
        return this.tileColor;
    }
}
