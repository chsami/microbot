package net.runelite.client.plugins.hoseaplugins.OneTickSwitcher;

import com.google.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import java.awt.*;

public class PvpHelperTargetOverlay extends Overlay {

    private final Client client;
    private final PvpHelperPlugin plugin;
    private final PvpHelperConfig config;
    private final ModelOutlineRenderer modelOutlineRenderer;

    @Inject
    private PvpHelperTargetOverlay(Client client, PvpHelperPlugin plugin,
                                   PvpHelperConfig config,
                                   ModelOutlineRenderer modelOutlineRenderer) {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.modelOutlineRenderer = modelOutlineRenderer;
        this.setPriority(OverlayPriority.HIGH);
        this.setLayer(OverlayLayer.ABOVE_SCENE);
        this.setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!client.getPlayers().contains((Player) plugin.getTarget())
            || !config.targetOverlay()) {
            return null;
        }

        if (plugin.getTarget() != null) {
            modelOutlineRenderer.drawOutline((Player) plugin.getTarget(), config.overlayWidth(), config.overlayColor(), 1);
        }

        return null;
    }
}
