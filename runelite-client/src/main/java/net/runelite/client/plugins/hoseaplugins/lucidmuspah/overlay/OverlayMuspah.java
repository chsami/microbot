package net.runelite.client.plugins.hoseaplugins.lucidmuspah.overlay;

import net.runelite.client.plugins.hoseaplugins.lucidmuspah.LucidMuspahHelperConfig;
import net.runelite.client.plugins.hoseaplugins.lucidmuspah.LucidMuspahHelperPlugin;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;

public class OverlayMuspah extends Overlay
{

    private final Client client;
    private final LucidMuspahHelperPlugin plugin;
    private final LucidMuspahHelperConfig config;
    private Player player;
    @Inject
    OverlayMuspah(final Client client, final LucidMuspahHelperPlugin plugin, final LucidMuspahHelperConfig config)
    {
        super(plugin);
        this.client = client;
        this.plugin = plugin;
        this.config = config;

        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics2D)
    {

        player = client.getLocalPlayer();

        if (player == null)
        {
            return null;
        }

        renderMeleeTicks(graphics2D);

        return null;
    }

    private void renderMeleeTicks(Graphics2D graphics2D)
    {
        if (!config.stepBackOverlay() || !plugin.inMeleeForm())
        {
            return;
        }

        final Polygon polygon = Perspective.getCanvasTileAreaPoly(client, player.getLocalLocation(), 1);
        if (polygon == null)
        {
            return;
        }

        String text = plugin.getTicksUntilAttack() > 1 ? (plugin.getTicksUntilAttack() - 1) + "" : "MOVE NOW";
        final Point point = Perspective.getCanvasTextLocation(client, graphics2D, player.getLocalLocation(), text, -25);
        if (point == null)
        {
            return;
        }

        final Font originalFont = graphics2D.getFont();
        graphics2D.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

        drawOutlineAndFill(graphics2D, plugin.getTicksUntilAttack() > 1 ? Color.GREEN : Color.RED, null, 1, polygon);
        OverlayUtil.renderTextLocation(graphics2D, point, text, plugin.getTicksUntilAttack() > 1 ? Color.GREEN : Color.RED);
        graphics2D.setFont(originalFont);
    }
}
