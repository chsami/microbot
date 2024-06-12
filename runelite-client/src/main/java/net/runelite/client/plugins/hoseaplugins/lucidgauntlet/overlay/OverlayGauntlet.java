package net.runelite.client.plugins.hoseaplugins.lucidgauntlet.overlay;

import net.runelite.client.plugins.hoseaplugins.lucidgauntlet.LucidGauntletConfig;
import net.runelite.client.plugins.hoseaplugins.lucidgauntlet.LucidGauntletPlugin;
import net.runelite.client.plugins.hoseaplugins.lucidgauntlet.entity.Demiboss;
import net.runelite.client.plugins.hoseaplugins.lucidgauntlet.entity.Resource;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;

@Singleton
public class OverlayGauntlet extends Overlay
{

    private final Client client;
    private final LucidGauntletPlugin plugin;
    private final LucidGauntletConfig config;
    private final ModelOutlineRenderer modelOutlineRenderer;

    private Player player;

    @Inject
    private OverlayGauntlet(final Client client, final LucidGauntletPlugin plugin, final LucidGauntletConfig config, final ModelOutlineRenderer modelOutlineRenderer)
    {
        super(plugin);

        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.modelOutlineRenderer = modelOutlineRenderer;

        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
        determineLayer();
    }

    @Override
    public void determineLayer()
    {
        setLayer(OverlayLayer.UNDER_WIDGETS);
    }

    @Override
    public Dimension render(final Graphics2D graphics2D)
    {
        player = client.getLocalPlayer();

        if (player == null)
        {
            return null;
        }

        renderResources(graphics2D);
        renderUtilities();
        renderDemibosses();
        renderStrongNpcs();
        renderWeakNpcs();

        return null;
    }

    private void renderResources(final Graphics2D graphics2D)
    {
        if ((!config.resourceOverlay() && !config.resourceOutline()) || plugin.getResources().isEmpty())
        {
            return;
        }

        final LocalPoint localPointPlayer = player.getLocalLocation();

        for (final Resource resource : plugin.getResources())
        {
            final GameObject gameObject = resource.getGameObject();

            final LocalPoint localPointGameObject = gameObject.getLocalLocation();

            if (isOutsideRenderDistance(localPointGameObject, localPointPlayer))
            {
                continue;
            }

            if (config.resourceOverlay())
            {
                final Polygon polygon = Perspective.getCanvasTilePoly(client, localPointGameObject);

                if (polygon == null)
                {
                    continue;
                }

                drawOutlineAndFill(graphics2D, config.resourceTileOutlineColor(), config.resourceTileFillColor(),
                        config.resourceTileOutlineWidth(), polygon);

                OverlayUtil.renderImageLocation(client, graphics2D, localPointGameObject, resource.getIcon(), 0);
            }

            if (config.resourceOutline())
            {
                final Shape shape = gameObject.getConvexHull();

                if (shape == null)
                {
                    continue;
                }

                modelOutlineRenderer.drawOutline(gameObject, config.resourceOutlineWidth(),
                        config.resourceOutlineColor(), 0);
            }
        }
    }

    private void renderUtilities()
    {
        if (!config.utilitiesOutline() || plugin.getUtilities().isEmpty())
        {
            return;
        }

        final LocalPoint localPointPlayer = player.getLocalLocation();

        for (final GameObject gameObject : plugin.getUtilities())
        {
            if (isOutsideRenderDistance(gameObject.getLocalLocation(), localPointPlayer))
            {
                continue;
            }

            final Shape shape = gameObject.getConvexHull();

            if (shape == null)
            {
                continue;
            }

            modelOutlineRenderer.drawOutline(gameObject, config.utilitiesOutlineWidth(),
                    config.utilitiesOutlineColor(), 0);
        }
    }

    private void renderDemibosses()
    {
        if (!config.demibossOutline() || plugin.getDemibosses().isEmpty())
        {
            return;
        }

        final LocalPoint localPointPlayer = player.getLocalLocation();

        for (final Demiboss demiboss : plugin.getDemibosses())
        {
            final NPC npc = demiboss.getNpc();

            final LocalPoint localPointNpc = npc.getLocalLocation();

            if (localPointNpc == null || npc.isDead() || isOutsideRenderDistance(localPointNpc, localPointPlayer))
            {
                continue;
            }

            modelOutlineRenderer.drawOutline(npc, config.demibossOutlineWidth(),
                    demiboss.getType().getOutlineColor(), 0);
        }
    }

    private void renderStrongNpcs()
    {
        if (!config.strongNpcOutline() || plugin.getStrongNpcs().isEmpty())
        {
            return;
        }

        final LocalPoint localPointPLayer = player.getLocalLocation();

        for (final NPC npc : plugin.getStrongNpcs())
        {
            final LocalPoint localPointNpc = npc.getLocalLocation();

            if (localPointNpc == null || npc.isDead() || isOutsideRenderDistance(localPointNpc, localPointPLayer))
            {
                continue;
            }

            modelOutlineRenderer.drawOutline(npc, config.strongNpcOutlineWidth(), config.strongNpcOutlineColor(),
                    0);
        }
    }

    private void renderWeakNpcs()
    {
        if (!config.weakNpcOutline() || plugin.getWeakNpcs().isEmpty())
        {
            return;
        }

        final LocalPoint localPointPlayer = player.getLocalLocation();

        for (final NPC npc : plugin.getWeakNpcs())
        {
            final LocalPoint localPointNpc = npc.getLocalLocation();

            if (localPointNpc == null || npc.isDead() || isOutsideRenderDistance(localPointNpc, localPointPlayer))
            {
                continue;
            }

            modelOutlineRenderer.drawOutline(npc, config.weakNpcOutlineWidth(), config.weakNpcOutlineColor(),
                    0);
        }
    }

    private boolean isOutsideRenderDistance(final LocalPoint localPoint, final LocalPoint playerLocation)
    {
        final int maxDistance = config.resourceRenderDistance().getDistance();

        if (maxDistance == 0)
        {
            return false;
        }

        return localPoint.distanceTo(playerLocation) >= maxDistance;
    }
}