package net.runelite.client.plugins.microbot.playerassist;

import net.runelite.client.plugins.microbot.playerassist.model.Monster;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;
import net.runelite.api.*;
import javax.inject.Inject;
import java.awt.*;

import static net.runelite.client.plugins.microbot.playerassist.combat.AttackNpcScript.attackableNpcs;
import static net.runelite.client.plugins.microbot.playerassist.combat.FlickerScript.currentMonstersAttackingUs;

public class PlayerAssistOverlay extends OverlayPanel {

    private final ModelOutlineRenderer modelOutlineRenderer;

    @Inject
    private PlayerAssistOverlay(ModelOutlineRenderer modelOutlineRenderer) {
        this.modelOutlineRenderer = modelOutlineRenderer;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(OverlayPriority.HIGH);
        setNaughty();
    }


    @Override
    public Dimension render(Graphics2D graphics) {
        if (attackableNpcs == null) return null;

        for (net.runelite.api.NPC npc :
                attackableNpcs) {
            if (npc != null && npc.getCanvasTilePoly() != null) {
                try {
                    graphics.setColor(Color.CYAN);
                    modelOutlineRenderer.drawOutline((NPC) npc, 2, Color.RED, 4);
                    graphics.draw(npc.getCanvasTilePoly());
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }

        for (Monster currentMonster: currentMonstersAttackingUs) {
            if (currentMonster != null && currentMonster.npc != null && currentMonster.npc.getCanvasTilePoly() != null) {
                try {
                    graphics.setColor(Color.CYAN);
                    modelOutlineRenderer.drawOutline((NPC) currentMonster.npc, 2, Color.RED, 4);
                    graphics.draw(currentMonster.npc.getCanvasTilePoly());
                    graphics.drawString("" + currentMonster.lastAttack,
                            (int) currentMonster.npc.getCanvasTilePoly().getBounds().getCenterX(),
                            (int) currentMonster.npc.getCanvasTilePoly().getBounds().getCenterY());
                } catch(Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }

        return super.render(graphics);
    }
}