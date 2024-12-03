package net.runelite.client.plugins.microbot.AgilityPyramid.data;

// Simple Obstacle class with public fields
public class Obstacle {
    public String obstacleName;
    public String interactOption;
    public int x1, y1, width, height, obstacleID;

    public Obstacle(String obstacleName, String interactOption, int x1, int y1, int width, int height, int obstacleID) {
        this.obstacleName = obstacleName;
        this.interactOption = interactOption;
        this.x1 = x1;
        this.y1 = y1;
        this.width = width;
        this.height = height;
        this.obstacleID = obstacleID;
    }

    @Override
    public String toString() {
        return "Obstacle{" +
                "obstacleName='" + obstacleName + '\'' +
                ", interactOption='" + interactOption + '\'' +
                ", x1=" + x1 +
                ", y1=" + y1 +
                ", width=" + width +
                ", height=" + height +
                ", obstacleID=" + obstacleID +
                '}';
    }
}
