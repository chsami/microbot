package net.runelite.client.plugins.microbot.playerassist;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.client.plugins.interacthighlight.InteractHighlightConfig;
import net.runelite.client.plugins.interacthighlight.InteractHighlightPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;

import static net.runelite.client.plugins.microbot.playerassist.combat.AttackNpcScript.attackableNpcs;

public class PlayerAssistOverlay extends Overlay {

    private final ModelOutlineRenderer modelOutlineRenderer;

    @Inject
    private PlayerAssistOverlay(ModelOutlineRenderer modelOutlineRenderer)
    {
        this.modelOutlineRenderer = modelOutlineRenderer;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(OverlayPriority.HIGH);
    }


    @Override
    public Dimension render(Graphics2D graphics) {
        if (attackableNpcs == null) return null;

        for (net.runelite.api.NPC npc:
                attackableNpcs) {
            if (npc != null) {
                try {
                    graphics.setColor(Color.CYAN);
                    modelOutlineRenderer.drawOutline((NPC) npc, 2, Color.RED, 4);
                    graphics.draw(npc.getCanvasTilePoly());
                } catch(Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        return null;
    }
}