package net.runelite.client.plugins.microbot.hunter;

import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import java.util.Objects;

import static net.runelite.client.plugins.microbot.hunter.AutoHunterScript.hunterArea;

public class AutoHunterOverlay extends Overlay {

    @Inject
    AutoHunterOverlay(AutoHunterPlugin plugin)
    {
        super(plugin);
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(PRIORITY_HIGHEST);
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        try {

            // Get walkable tiles
            // Highlight each walkable tile
            for (WorldPoint tile : hunterArea) {
                drawTile(graphics, tile, Color.GREEN);
            }


        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    /**
     * Draws a single tile on the game screen.
     *
     * @param graphics The Graphics2D object to render with.
     * @param tile     The WorldPoint to highlight.
     * @param color    The color to use for highlighting.
     */
    private void drawTile(Graphics2D graphics, WorldPoint tile, Color color) {
        Polygon poly = tileToCanvasPolygon(tile);
        if (poly != null) {
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50)); // Semi-transparent fill
            graphics.fill(poly);
            graphics.setColor(color);
            graphics.draw(poly);
        }
    }

    /**
     * Converts a WorldPoint to a canvas polygon for rendering.
     *
     * @param tile The WorldPoint to convert.
     * @return A Polygon representing the tile, or null if not visible.
     */
    private Polygon tileToCanvasPolygon(WorldPoint tile) {
        if (tile.getPlane() != Microbot.getClient().getTopLevelWorldView().getPlane()) {
            return null; // Tile is on a different plane
        }

        return Perspective.getCanvasTilePoly(Microbot.getClient(), Objects.requireNonNull(LocalPoint.fromWorld(Microbot.getClient().getTopLevelWorldView(), tile)));
    }
}
