package net.runelite.client.plugins.microbot.mixology;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;
import java.util.Iterator;

public class MixologyOverlay extends Overlay {
    private final MixologyPlugin plugin;
    private final ModelOutlineRenderer modelOutlineRenderer;

    @Inject
    MixologyOverlay(MixologyPlugin plugin, ModelOutlineRenderer modelOutlineRenderer) {
        this.plugin = plugin;
        this.modelOutlineRenderer = modelOutlineRenderer;
        this.setPosition(OverlayPosition.DYNAMIC);
        this.setLayer(OverlayLayer.ABOVE_SCENE);
    }

    public Dimension render(Graphics2D graphics) {
        Iterator var2 = this.plugin.highlightedObjects().values().iterator();

        while(var2.hasNext()) {
            MixologyPlugin.HighlightedObject highlightedObject = (MixologyPlugin.HighlightedObject)var2.next();
            this.modelOutlineRenderer.drawOutline(highlightedObject.object(), highlightedObject.outlineWidth(), highlightedObject.color(), highlightedObject.feather());
        }

        return null;
    }
}
