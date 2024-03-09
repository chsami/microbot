package net.runelite.client.plugins.griffinplugins.griffintrickortreat;

import java.awt.*;
import java.util.List;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;

public class GriffinTrickOrTreatOverlay extends Overlay {

    @Inject
    private GriffinTrickOrTreatOverlay() {
        setPosition(OverlayPosition.DYNAMIC);
        setNaughty();
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            drawNpcs(graphics);
            drawMouse(graphics);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

    private void drawNpcs(Graphics2D graphics) {
        List<Integer> trackedNpcs = GriffinTrickOrTreatScript.Companion.getTrackedNpcs();
        int currentNpcId = GriffinTrickOrTreatScript.Companion.getCurrentNpcId();

        List<NPC> npcs = Rs2Npc.getNpcs("Guard");
        if (npcs == null) return;

        for (NPC npc : npcs) {
            if (npc == null) continue;

            if (!trackedNpcs.contains(npc.getId()) && npc.getId() != currentNpcId) continue;

            Shape polygon = npc.getConvexHull();
            if (polygon == null) continue;

            if (npc.getId() == currentNpcId) {
                graphics.setColor(new Color(30, 210, 30, 70));
            } else {
                graphics.setColor(new Color(255, 200, 0, 70));
            }
            graphics.fill(polygon);
        }
    }

    private void drawMouse(Graphics2D graphics) {
        // Mocrosoft uncommented this as we no longer use getLastMousePosition
        // The new api basically uses invokes instead of mouse
        // therefor this is not really needed anymore
       /* Point cursorPosition = Microbot.getMouse().getLastMousePosition();
        if (cursorPosition == null) return;
        graphics.setColor(Color.RED);
        graphics.fillOval(cursorPosition.getX() - 5, cursorPosition.getY() - 5, 10, 10);*/
    }
}
