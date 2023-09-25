package net.runelite.client.plugins.microbot.playerassist;

import net.runelite.api.NPC;
import net.runelite.client.plugins.microbot.playerassist.model.Monster;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;

import static net.runelite.client.plugins.microbot.playerassist.combat.AttackNpcScript.attackableNpcs;
import static net.runelite.client.plugins.microbot.playerassist.combat.FlickerScript.currentMonstersAttackingUs;

public class PlayerAssistOverlay extends Overlay {

    private final ModelOutlineRenderer modelOutlineRenderer;

    @Inject
    private PlayerAssistOverlay(ModelOutlineRenderer modelOutlineRenderer) {
        this.modelOutlineRenderer = modelOutlineRenderer;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(OverlayPriority.HIGH);
    }


    @Override
    public Dimension render(Graphics2D graphics) {
        if (attackableNpcs == null) return null;

        for (net.runelite.api.NPC npc :
                attackableNpcs) {
            if (npc != null) {
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
            if (currentMonster != null && currentMonster.npc != null) {
                try {
                    graphics.setColor(Color.CYAN);
                    modelOutlineRenderer.drawOutline((NPC) currentMonster.npc, 2, Color.RED, 4);
                    graphics.draw(currentMonster.npc.getCanvasTilePoly());
                    graphics.drawString("" + currentMonster.adjustableAttackSpeed,
                            (int) currentMonster.npc.getCanvasTilePoly().getBounds().getCenterX(),
                            (int) currentMonster.npc.getCanvasTilePoly().getBounds().getCenterY());
                } catch(Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }

        return null;
    }
}