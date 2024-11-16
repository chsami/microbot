package net.runelite.client.plugins.microbot.mixology;

import com.google.inject.Inject;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

import java.awt.*;

public class InventoryPotionOverlay extends WidgetItemOverlay {
    private final MixologyPlugin plugin;
    private final MasteringMixologyConfig config;

    @Inject
    InventoryPotionOverlay(MixologyPlugin plugin, MasteringMixologyConfig config) {
        this.plugin = plugin;
        this.config = config;
        this.showOnInventory();
    }

    public void renderItemOverlay(Graphics2D graphics2D, int itemId, WidgetItem widgetItem) {
        if (this.plugin.isInLab() && this.config.identifyPotions()) {
            PotionType potion = PotionType.fromItemId(itemId);
            if (potion != null) {
                Rectangle bounds = widgetItem.getCanvasBounds();
                graphics2D.setFont(FontManager.getRunescapeSmallFont());
                graphics2D.setColor(Color.WHITE);
                graphics2D.drawString(potion.abbreviation(), bounds.x - 1, bounds.y + 15);
            }
        }
    }
}
