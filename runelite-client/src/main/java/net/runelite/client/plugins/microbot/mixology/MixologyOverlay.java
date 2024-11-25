package net.runelite.client.plugins.microbot.mixology;

import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;
import java.time.Duration;

public class MixologyOverlay extends OverlayPanel {
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

        panelComponent.setPreferredLocation(new Point(200, 20));
        panelComponent.setPreferredSize(new Dimension(300, 300));
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Micro Mixology V" + MixologyScript.version)
                .color(Color.GREEN)
                .build());

        panelComponent.getChildren().add(LineComponent.builder().build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Mixology state")
                .right(MixologyScript.mixologyState.toString())
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Mox/Aga/Lye paste")
                .right(String.valueOf(MixologyScript.moxPasteAmount) + "/" + String.valueOf(MixologyScript.agaPasteAmount) + "/" + String.valueOf(MixologyScript.lyePasteAmount))
                .build());

        Duration runtime = plugin.mixologyScript.getRunTime();

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Mox/Aga/Lye points per hour")
                .rightColor(Color.YELLOW)
                .right(calculatePointsPerHour(runtime.getSeconds()))
                .build());

        panelComponent.getChildren().add(LineComponent.builder()
                .left("Runtime")
                .rightColor(Color.GREEN)
                .right(String.format("%02d:%02d:%02d", runtime.toHours(), runtime.toMinutesPart(), runtime.toSecondsPart()))
                .build());

        for (MixologyPlugin.HighlightedObject highlightedObject : this.plugin.highlightedObjects().values()) {
            this.modelOutlineRenderer.drawOutline(highlightedObject.object(), highlightedObject.outlineWidth(), highlightedObject.color(), highlightedObject.feather());
        }

        return super.render(graphics);
    }

    private String calculatePointsPerHour(long seconds) {
        // int elapsedTimeInSeconds = 3600; // Time elapsed (e.g., 1 hour = 3600 seconds)

        // Convert time to hours
        double elapsedTimeInHours = seconds / 3600.0;

        int gainedMoxPoints = MixologyScript.currentMoxPoints - MixologyScript.startMoxPoints;
        int gainedLyePoints = MixologyScript.currentLyePoints - MixologyScript.startLyePoints;
        int gainedAgaPoints = MixologyScript.currentAgaPoints - MixologyScript.startAgaPoints;

        // Calculate experience per hour
        int moxPointsPerHour = (int) (gainedMoxPoints / elapsedTimeInHours);
        int lyePointsPerHour = (int) (gainedLyePoints / elapsedTimeInHours);
        int agaPointsPerHour = (int) (gainedAgaPoints / elapsedTimeInHours);

        if (moxPointsPerHour < 0)
            moxPointsPerHour = 0;
        if (lyePointsPerHour < 0)
            lyePointsPerHour = 0;
        if (agaPointsPerHour < 0)
            agaPointsPerHour = 0;

        return "" + moxPointsPerHour + "/" + agaPointsPerHour + "/" + lyePointsPerHour;
    }
}
