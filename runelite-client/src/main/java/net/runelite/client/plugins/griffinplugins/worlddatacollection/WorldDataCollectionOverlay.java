package net.runelite.client.plugins.griffinplugins.worlddatacollection;

import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class WorldDataCollectionOverlay extends OverlayPanel {

    @Inject
    private WorldDataCollectionOverlay() {
        setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(200, 300));
            panelComponent.getChildren().add(TitleComponent.builder().text("Static Walker World Data Collection").color(Color.GREEN).build());
            panelComponent.getChildren().add(LineComponent.builder().build());
            panelComponent.getChildren().add(LineComponent.builder().left("Started: " + WorldDataCollectionThread.started).build());
            panelComponent.getChildren().add(LineComponent.builder().left("Completed: " + WorldDataCollectionThread.completed).build());

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}
