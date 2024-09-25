package net.runelite.client.plugins.microbot.qualityoflife.scripts.pouch;

import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.ui.overlay.components.TextComponent;

import javax.inject.Inject;
import java.awt.*;

public class PouchOverlay extends WidgetItemOverlay
{
	@Inject
	PouchOverlay()
	{
		showOnInventory();
	}

	public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem itemWidget)
	{
		final Pouch pouch = Pouch.forItem(itemId);
		if (pouch == null)
		{
			return;
		}

		final Rectangle bounds = itemWidget.getCanvasBounds();
		final TextComponent textComponent = new TextComponent();
		textComponent.setPosition(new Point(bounds.x - 1, bounds.y + 8));
		textComponent.setColor(Color.CYAN);
		if (pouch.isUnknown())
		{
			textComponent.setText("?");
		}
		else
		{
			textComponent.setText(Integer.toString(pouch.getHolding()));
		}
		textComponent.render(graphics);
	}
}