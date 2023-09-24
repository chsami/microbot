package net.runelite.client.plugins.griffinplugins.transporthelper;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;

public class TransportHelperOverlay extends Overlay {

    @Inject
    private TransportHelperOverlay() {
        setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Client client = Microbot.getClient();
        int playerPlane = client.getPlane();

        for (Tile tile : TransportHelperPlugin.Companion.getUnaddedTransportTiles().values()) {
            if (tile.getPlane() != playerPlane) {
                continue;
            }

            Polygon poly = Perspective.getCanvasTilePoly(client, tile.getLocalLocation(), tile.getPlane());
            if (poly != null) {
                OverlayUtil.renderPolygon(graphics, poly, Color.RED);
            }
        }

        for (Tile tile : TransportHelperPlugin.Companion.getNeedsWorkTransportTiles().values()) {
            if (tile.getPlane() != playerPlane) {
                continue;
            }

            Polygon poly = Perspective.getCanvasTilePoly(client, tile.getLocalLocation(), tile.getPlane());
            if (poly != null) {
                OverlayUtil.renderPolygon(graphics, poly, Color.YELLOW);
            }
        }

        for (Tile tile : Rs2GameObject.getTiles()) {
            if (tile.getPlane() != playerPlane) {
                continue;
            }

            if (!TransportHelperPlugin.Companion.getAddedTransportWorldPoints().containsValue(tile.getWorldLocation())) {
                continue;
            }

            final LocalPoint tileLocalLocation = tile.getLocalLocation();
            Polygon poly = Perspective.getCanvasTilePoly(client, tileLocalLocation, tile.getPlane());

            if (poly != null) {
                OverlayUtil.renderPolygon(graphics, poly, Color.GREEN);
            }
        }

        return null;
    }
}
