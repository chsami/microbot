package net.runelite.client.plugins.hoseaplugins.AutoTitheFarm;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class AutoTitheFarmOverlay extends Overlay {

    private final AutoTitheFarmPlugin plugin;

    private final Client client;

    private final AutoTitheFarmConfig config;

    @Inject
    AutoTitheFarmOverlay(Client client, AutoTitheFarmPlugin plugin, AutoTitheFarmConfig config) {
        this.plugin = plugin;
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(OverlayPriority.HIGHEST);

    }

    private void renderTextLocation(Graphics2D graphics, String text, WorldPoint worldPoint, Color color) {
        LocalPoint point = LocalPoint.fromWorld(client, worldPoint);
        if (point == null) {
            return;
        }
        Point textLocation = Perspective.getCanvasTextLocation(client, graphics, point, text, 0);
        if (textLocation != null) {
            OverlayUtil.renderTextLocation(graphics, textLocation, text, color);
        }
    }

    private void renderTextOnActor(Graphics2D graphics, Actor actor, String text, Color color, int zOffSet) {
        Point textLocation = actor.getCanvasTextLocation(graphics, text, actor.getLogicalHeight() + zOffSet);
        if (textLocation != null) {
            OverlayUtil.renderTextLocation(graphics, textLocation, text, color);
        }
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        graphics.setFont(FontManager.getRunescapeFont());

        if (config.enableDebug()) {
            List<TileObject> patches = new ArrayList<>(plugin.emptyPatches);
            for (TileObject tileObject : patches) {
                renderTextLocation(graphics, String.valueOf(patches.indexOf(tileObject) + 1), tileObject.getWorldLocation(), Color.WHITE);
            }

            renderTextOnActor(graphics, client.getLocalPlayer(), "Wait for action: " + plugin.actionDelayHandler.isWaitForAction(), Color.RED, 40);
        }

        if (plugin.isNeedToRestoreRunEnergy() && plugin.startingNewRun()) {
            renderTextLocation(graphics, "Idling until 100% energy", client.getLocalPlayer().getWorldLocation(), Color.RED);
        }

        return null;
    }
}