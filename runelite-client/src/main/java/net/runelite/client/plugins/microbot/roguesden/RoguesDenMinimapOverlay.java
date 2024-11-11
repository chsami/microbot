package net.runelite.client.plugins.microbot.roguesden;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

class RoguesDenMinimapOverlay extends Overlay {
    private final Client client;
    private final RoguesDenPlugin plugin;

    @Inject
    public RoguesDenMinimapOverlay(Client client, RoguesDenPlugin plugin) {
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_WIDGETS);
        this.client = client;
        this.plugin = plugin;
    }

    public Dimension render(Graphics2D graphics) {
        if (!this.plugin.isHasGem()) {
            return null;
        } else {
            Obstacles.Obstacle[] var2 = Obstacles.OBSTACLES;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Obstacles.Obstacle obstacle = var2[var4];
                LocalPoint localPoint = LocalPoint.fromWorld(this.client, obstacle.getTile());
                if (localPoint != null && obstacle.getTile().getPlane() == this.client.getPlane()) {
                    Point minimapPoint = Perspective.localToMinimap(this.client, localPoint);
                    if (minimapPoint != null) {
                        OverlayUtil.renderMinimapLocation(graphics, minimapPoint, obstacle.getObjectId() == -1 ? Color.GREEN : Color.RED);
                    }
                }
            }

            return null;
        }
    }
}

