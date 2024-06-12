package net.runelite.client.plugins.hoseaplugins.lucidgauntlet.overlay;

import com.google.common.base.Strings;
import net.runelite.api.Client;
import net.runelite.api.Prayer;
import net.runelite.api.VarClientInt;
import net.runelite.api.widgets.Widget;
import net.runelite.api.Point;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;


import static net.runelite.client.ui.overlay.OverlayUtil.renderPolygon;

public class OverlayUtil
{
    public static Rectangle renderPrayerOverlay(Graphics2D graphics, Client client, Prayer prayer, Color color)
    {
        Widget widget = client.getWidget(PrayerExtended.getPrayerWidgetId(prayer));

        if (widget == null || client.getVarbitValue(VarClientInt.INVENTORY_TAB) != InterfaceTab.PRAYER.getId())
        {
            return null;
        }

        Rectangle bounds = widget.getBounds();
        renderPolygon(graphics, rectangleToPolygon(bounds), color);
        return bounds;
    }

    private static Polygon rectangleToPolygon(Rectangle rect)
    {
        int[] xpoints = {rect.x, rect.x + rect.width, rect.x + rect.width, rect.x};
        int[] ypoints = {rect.y, rect.y, rect.y + rect.height, rect.y + rect.height};

        return new Polygon(xpoints, ypoints, 4);
    }

    public static void renderTextLocation(Graphics2D graphics, String txtString, int fontSize, int fontStyle, Color fontColor, Point canvasPoint, boolean shadows, int yOffset)
    {
        graphics.setFont(new Font("Arial", fontStyle, fontSize));
        if (canvasPoint != null)
        {
            final net.runelite.api.Point canvasCenterPoint = new net.runelite.api.Point(
                    (int) canvasPoint.getX(),
                    (int) (canvasPoint.getY() + yOffset));
            final net.runelite.api.Point canvasCenterPoint_shadow = new net.runelite.api.Point(
                    (int) (canvasPoint.getX() + 1),
                    (int) (canvasPoint.getY() + 1 + yOffset));
            if (shadows)
            {
                renderTextLocation(graphics, canvasCenterPoint_shadow, txtString, Color.BLACK);
            }
            renderTextLocation(graphics, canvasCenterPoint, txtString, fontColor);
        }
    }

    public static void renderTextLocation(Graphics2D graphics, net.runelite.api.Point txtLoc, String text, Color color)
    {
        if (Strings.isNullOrEmpty(text))
        {
            return;
        }

        int x = (int) txtLoc.getX();
        int y = (int) txtLoc.getY();

        graphics.setColor(Color.BLACK);
        graphics.drawString(text, x + 1, y + 1);

        graphics.setColor(color);
        graphics.drawString(text, x, y);
    }
}
