package net.runelite.client.plugins.hoseaplugins.PrayAgainstPlayer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ConcurrentModificationException;
import com.google.inject.Inject;
import javax.inject.Singleton;

import net.runelite.client.plugins.hoseaplugins.PiggyUtils.API.PrayerUtil;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Prayer;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

@Singleton
class PrayAgainstPlayerOverlayPrayerTab extends Overlay
{

    private final PrayAgainstPlayerPlugin plugin;
    private final PrayAgainstPlayerConfig config;
    private final Client client;

    @Inject
    private PrayAgainstPlayerOverlayPrayerTab(final PrayAgainstPlayerPlugin plugin, final PrayAgainstPlayerConfig config, final Client client)
    {
        this.plugin = plugin;
        this.config = config;
        this.client = client;
        setPosition(OverlayPosition.DETACHED);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        setPriority(OverlayPriority.MED);
    }


    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (plugin.getPlayersAttackingMe() == null || !plugin.getPlayersAttackingMe().isEmpty())
        {
            try
            {
                if (plugin.getPlayersAttackingMe() != null)
                {
                    for (PlayerContainer container : plugin.getPlayersAttackingMe())
                    {
                        if (plugin.getPlayersAttackingMe() != null && plugin.getPlayersAttackingMe().size() == 1 &&
                                config.drawTargetPrayAgainstPrayerTab())
                        {
                            renderPrayerToClick(graphics, container.getPlayer());
                        }
                    }
                }
            }
            catch (ConcurrentModificationException ignored)
            {
            }
        }
        return null;
    }

    private void renderPrayerToClick(Graphics2D graphics, Player player)
    {
        Widget PROTECT_FROM_MAGIC = client.getWidget(PrayerUtil.getPrayerWidgetId(Prayer.PROTECT_FROM_MAGIC));
        Widget PROTECT_FROM_RANGED = client.getWidget(PrayerUtil.getPrayerWidgetId(Prayer.PROTECT_FROM_MISSILES));
        Widget PROTECT_FROM_MELEE = client.getWidget(PrayerUtil.getPrayerWidgetId(Prayer.PROTECT_FROM_MELEE));
        Color color = Color.RED;
        if (PROTECT_FROM_MELEE.isHidden())
        {
            return;
        }
        switch (WeaponType.checkWeaponOnPlayer(client, player))
        {
            case WEAPON_MAGIC:
                OverlayUtil.renderPolygon(graphics, rectangleToPolygon(PROTECT_FROM_MAGIC.getBounds()), color);
                break;
            case WEAPON_MELEE:
                OverlayUtil.renderPolygon(graphics, rectangleToPolygon(PROTECT_FROM_MELEE.getBounds()), color);
                break;
            case WEAPON_RANGED:
                OverlayUtil.renderPolygon(graphics, rectangleToPolygon(PROTECT_FROM_RANGED.getBounds()), color);
                break;
            default:
                break;
        }
    }

    private static Polygon rectangleToPolygon(Rectangle rect)
    {
        int[] xpoints = {rect.x, rect.x + rect.width, rect.x + rect.width, rect.x};
        int[] ypoints = {rect.y, rect.y, rect.y + rect.height, rect.y + rect.height};

        return new Polygon(xpoints, ypoints, 4);
    }
}