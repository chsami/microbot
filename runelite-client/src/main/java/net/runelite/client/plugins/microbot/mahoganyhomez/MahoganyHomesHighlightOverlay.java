package net.runelite.client.plugins.microbot.mahoganyhomez;

import net.runelite.api.Player;
import net.runelite.api.TileObject;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;

class MahoganyHomesHighlightOverlay extends Overlay
{
    public static final Color REPAIR_COLOR = new Color(248,187,208, 100);
    public static final Color REMOVE_COLOR = new Color(255, 0, 0, 100);
    public static final Color BUILD_COLOR = new Color(0, 0, 255, 100);
    public static final Color CLICKBOX_BORDER_COLOR = Color.ORANGE;
    public static final Color CLICKBOX_HOVER_BORDER_COLOR = CLICKBOX_BORDER_COLOR.darker();

    private final MahoganyHomesPlugin plugin;
    private final MahoganyHomesConfig config;

    @Inject
    MahoganyHomesHighlightOverlay(MahoganyHomesPlugin plugin, MahoganyHomesConfig config)
    {
        super(plugin);
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        final Home home = plugin.getCurrentHome();
        final Player player = plugin.getClient().getLocalPlayer();
        if (plugin.isPluginTimedOut() || home == null || player == null)
        {
            return null;
        }

        for (TileObject gameObject : plugin.getObjectsToMark())
        {
            if (gameObject.getPlane() != plugin.getClient().getPlane())
            {
                continue;
            }

            if (plugin.distanceBetween(home.getArea(), gameObject.getWorldLocation()) > 0)
            {
                // Object not inside area for this house.
                continue;
            }

            Color fillColor = REMOVE_COLOR;
            final Hotspot spot = Hotspot.getByObjectId(gameObject.getId());
            if (spot == null)
            {
                // Ladders aren't hotspots so handle them after this check
                if (!Home.isLadder(gameObject.getId()) || !config.highlightStairs())
                {
                    continue;
                }

                fillColor = config.highlightStairsColor();
            }
            else
            {
                // Do not highlight the hotspot if the config is disabled or it doesn't require any attention
                if (!config.highlightHotspots() || !plugin.doesHotspotRequireAttention(spot.getVarb()))
                {
                    continue;
                }
                String action = spot.getRequiredAction();
                if (action.equals("Build"))
                {
                    fillColor = BUILD_COLOR;
                }
                else if (action.equals("Repair"))
                {
                    fillColor = REPAIR_COLOR;
                }
            }

            final net.runelite.api.Point mousePosition = plugin.getClient().getMouseCanvasPosition();
            OverlayUtil.renderHoverableArea(graphics, gameObject.getClickbox(), mousePosition,
                    fillColor, CLICKBOX_BORDER_COLOR, CLICKBOX_HOVER_BORDER_COLOR);
        }

        return null;
    }
}
