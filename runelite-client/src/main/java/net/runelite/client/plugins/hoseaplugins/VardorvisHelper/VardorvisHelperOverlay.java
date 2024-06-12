package net.runelite.client.plugins.hoseaplugins.VardorvisHelper;

import net.runelite.api.Client;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.ImageComponent;

import com.google.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class VardorvisHelperOverlay extends OverlayPanel {

    private final Client client;
    private final SpriteManager spriteManager;
    private final VardorvisHelperPlugin plugin;


    @Inject
    private VardorvisHelperOverlay(Client client, SpriteManager spriteManager, VardorvisHelperPlugin plugin) {
        super(plugin);
        this.client = client;
        this.spriteManager = spriteManager;
        this.plugin = plugin;
        setPosition(OverlayPosition.BOTTOM_RIGHT);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(OverlayPriority.HIGH);
    }

    @Override
    public Dimension render(Graphics2D graphics2D) {
        if (!plugin.isInFight()) {
            return null;
        }

        panelComponent.getChildren().clear();
        BufferedImage prayerImage;
        prayerImage = getPrayerImage(plugin.getPrayerSprite());

        panelComponent.setBackgroundColor(client.isPrayerActive(plugin.getCorrectPrayer()) ? Color.GREEN : Color.RED);
        panelComponent.getChildren().add(new ImageComponent(prayerImage));

        return super.render(graphics2D);
    }

    private BufferedImage getPrayerImage(int spriteId) {
        return spriteManager.getSprite(spriteId, 0);
    }
}
