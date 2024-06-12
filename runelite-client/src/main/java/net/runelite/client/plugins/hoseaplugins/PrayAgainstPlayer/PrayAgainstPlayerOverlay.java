package net.runelite.client.plugins.hoseaplugins.PrayAgainstPlayer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.ConcurrentModificationException;
import com.google.inject.Inject;
import javax.inject.Singleton;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.Prayer;
import net.runelite.api.kit.KitType;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.Text;

@Singleton
class PrayAgainstPlayerOverlay extends Overlay
{

    private final PrayAgainstPlayerPlugin plugin;
    private final PrayAgainstPlayerConfig config;
    private final Client client;
    @Getter
    private Prayer prayerToActivate;

    @Inject
    private PrayAgainstPlayerOverlay(final PrayAgainstPlayerPlugin plugin, final PrayAgainstPlayerConfig config, final Client client)
    {
        this.plugin = plugin;
        this.config = config;
        this.client = client;
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        renderPotentialPlayers(graphics);
        renderAttackingPlayers(graphics);
        return null;
    }

    private void renderPotentialPlayers(Graphics2D graphics)
    {
        if (plugin.getPotentialPlayersAttackingMe() == null || !plugin.getPotentialPlayersAttackingMe().isEmpty())
        {
            try
            {
                if (plugin.getPotentialPlayersAttackingMe() != null)
                {
                    for (PlayerContainer container : plugin.getPotentialPlayersAttackingMe())
                    {
                        if ((System.currentTimeMillis() > (container.getWhenTheyAttackedMe() + container.getMillisToExpireHighlight())) && (container.getPlayer().getInteracting() != client.getLocalPlayer()))
                        {
                            plugin.removePlayerFromPotentialContainer(container);
                        }
                        if (config.drawPotentialTargetsName())
                        {
                            renderNameAboveHead(graphics, container.getPlayer(), config.potentialPlayerColor());
                        }
                        if (config.drawPotentialTargetHighlight())
                        {
                            renderHighlightedPlayer(graphics, container.getPlayer(), config.potentialPlayerColor());
                        }
                        if (config.drawPotentialTargetTile())
                        {
                            renderTileUnderPlayer(graphics, container.getPlayer(), config.potentialPlayerColor());
                        }
                        if (config.drawPotentialTargetPrayAgainst())
                        {
                            renderPrayAgainstOnPlayer(graphics, container.getPlayer(), config.potentialPlayerColor());
                        }
                    }
                }
            }
            catch (ConcurrentModificationException ignored)
            {
            }
        }
    }

    private void renderAttackingPlayers(Graphics2D graphics)
    {
        if (plugin.getPlayersAttackingMe() == null || !plugin.getPlayersAttackingMe().isEmpty())
        {
            try
            {
                if (plugin.getPlayersAttackingMe() != null)
                {
                    for (PlayerContainer container : plugin.getPlayersAttackingMe())
                    {
                        if ((System.currentTimeMillis() > (container.getWhenTheyAttackedMe() + container.getMillisToExpireHighlight())) && (container.getPlayer().getInteracting() != client.getLocalPlayer()))
                        {
                            plugin.removePlayerFromAttackerContainer(container);
                        }

                        if (config.drawTargetsName())
                        {
                            renderNameAboveHead(graphics, container.getPlayer(), config.attackerPlayerColor());
                        }
                        if (config.drawTargetHighlight())
                        {
                            renderHighlightedPlayer(graphics, container.getPlayer(), config.attackerPlayerColor());
                        }
                        if (config.drawTargetTile())
                        {
                            renderTileUnderPlayer(graphics, container.getPlayer(), config.attackerPlayerColor());
                        }
                        if (config.drawTargetPrayAgainst())
                        {
                            renderPrayAgainstOnPlayer(graphics, container.getPlayer(), config.attackerPlayerColor());
                        }
                    }
                }
            }
            catch (ConcurrentModificationException ignored)
            {
            }
        }
    }

    private void renderNameAboveHead(Graphics2D graphics, Player player, Color color)
    {
        final String name = Text.sanitize(player.getName());
        final int offset = player.getLogicalHeight() + 40;
        Point textLocation = player.getCanvasTextLocation(graphics, name, offset);
        if (textLocation != null)
        {
            OverlayUtil.renderTextLocation(graphics, textLocation, name, color);
        }
    }

    private void renderHighlightedPlayer(Graphics2D graphics, Player player, Color color)
    {
        try
        {
            OverlayUtil.renderPolygon(graphics, player.getConvexHull(), color);
        }
        catch (NullPointerException ignored)
        {
        }
    }

    private void renderTileUnderPlayer(Graphics2D graphics, Player player, Color color)
    {
        Polygon poly = player.getCanvasTilePoly();
        OverlayUtil.renderPolygon(graphics, poly, color);
    }

    private void renderPrayAgainstOnPlayer(Graphics2D graphics, Player player, Color color)
    {
        final int offset = (player.getLogicalHeight() / 2) + 75;
        BufferedImage icon;

        switch (WeaponType.checkWeaponOnPlayer(client, player))
        {
            case WEAPON_MELEE:
                icon = plugin.getProtectionIcon(WeaponType.WEAPON_MELEE);
                prayerToActivate = Prayer.PROTECT_FROM_MELEE;
                break;
            case WEAPON_MAGIC:
                icon = plugin.getProtectionIcon(WeaponType.WEAPON_MAGIC);
                prayerToActivate = Prayer.PROTECT_FROM_MAGIC;
                break;
            case WEAPON_RANGED:
                icon = plugin.getProtectionIcon(WeaponType.WEAPON_RANGED);
                prayerToActivate = Prayer.PROTECT_FROM_MISSILES;
                break;
            default:
                icon = null;
                break;
        }
        try
        {
            if (icon != null)
            {
                Point point = player.getCanvasImageLocation(icon, offset);
                OverlayUtil.renderImageLocation(graphics, point, icon);
            }
            else
            {
                if (config.drawUnknownWeapons())
                {
                    int itemId = player.getPlayerComposition().getEquipmentId(KitType.WEAPON);
                    ItemComposition itemComposition = client.getItemDefinition(itemId);

                    final String str = itemComposition.getName().toUpperCase();
                    Point point = player.getCanvasTextLocation(graphics, str, offset);
                    OverlayUtil.renderTextLocation(graphics, point, str, color);
                }
            }
        }
        catch (Exception ignored)
        {
        }
    }
}
