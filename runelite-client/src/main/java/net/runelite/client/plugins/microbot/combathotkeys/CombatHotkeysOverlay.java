package net.runelite.client.plugins.microbot.combathotkeys;

import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class CombatHotkeysOverlay extends OverlayPanel {

    @Inject
    CombatHotkeysOverlay(CombatHotkeysPlugin plugin)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(150, 250));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Combat Hotkeys V0.0.1")
                    .color(Color.GREEN)
                    .build());

        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}
