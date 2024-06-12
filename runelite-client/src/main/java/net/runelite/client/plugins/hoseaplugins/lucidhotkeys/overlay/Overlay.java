package net.runelite.client.plugins.hoseaplugins.lucidhotkeys.overlay;

import net.runelite.client.plugins.Plugin;

import java.awt.*;

public abstract class Overlay extends net.runelite.client.ui.overlay.Overlay
{
    Overlay(final Plugin plugin)
    {
        super(plugin);
    }

    static void drawOutlineAndFill(final Graphics2D graphics2D, final Color outlineColor, final Color fillColor, final float strokeWidth, final Shape shape)
    {
        final Color originalColor = graphics2D.getColor();
        final Stroke originalStroke = graphics2D.getStroke();

        final Color outline = outlineColor != null ? outlineColor : new Color(0, 0, 0, 0);
        final Color fill = fillColor != null ? fillColor : new Color(0, 0, 0, 0);

        graphics2D.setStroke(new BasicStroke(strokeWidth));
        graphics2D.setColor(outline);
        graphics2D.draw(shape);

        graphics2D.setColor(fill);
        graphics2D.fill(shape);

        graphics2D.setColor(originalColor);
        graphics2D.setStroke(originalStroke);
    }
}