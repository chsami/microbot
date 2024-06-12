package net.runelite.client.plugins.hoseaplugins.OneTickSwitcher;

import com.google.inject.Inject;
import lombok.Setter;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

public class PvpHelperValueOverlay extends OverlayPanel {

    private final PvpHelperPlugin plugin;
    private final PvpHelperConfig config;
    @Setter
    private int itemValue;
    @Setter
    private boolean hidden = true;

    @Inject
    private PvpHelperValueOverlay(PvpHelperPlugin plugin, PvpHelperConfig config) {
        super(plugin);
        this.plugin = plugin;
        this.config = config;
        setPosition(OverlayPosition.BOTTOM_LEFT);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!config.showOverlay()) {
            return null;
        }

        panelComponent.getChildren().add(TitleComponent.builder()
                .text("PVP Helper")
                .color(Color.WHITE)
                .build());

        if (plugin.getTarget() != null) {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Expected Value: ")
                    .leftColor(Color.WHITE)
                    .right(NumberFormat.getNumberInstance(Locale.US).format(itemValue) + " gp")
                    .rightColor(Color.YELLOW)
                    .build());
        }

        return super.render(graphics);
    }
}
