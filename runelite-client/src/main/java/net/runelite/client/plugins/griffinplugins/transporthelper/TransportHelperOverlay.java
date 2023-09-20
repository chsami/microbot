package net.runelite.client.plugins.griffinplugins.transporthelper;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;

public class TransportHelperOverlay extends Overlay {

    @Inject
    private TransportHelperOverlay() {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        setPriority(OverlayPriority.HIGHEST);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Client client = Microbot.getClient();
        for (Tile tile : Rs2GameObject.getTiles()) {
            final LocalPoint tileLocalLocation = tile.getLocalLocation();
            Polygon poly = Perspective.getCanvasTilePoly(client, tileLocalLocation);

            if (poly != null && poly.contains(client.getMouseCanvasPosition().getX(), client.getMouseCanvasPosition().getY())) {
                OverlayUtil.renderPolygon(graphics, poly, Color.GREEN);
            }
        }
        return null;
    }
}
