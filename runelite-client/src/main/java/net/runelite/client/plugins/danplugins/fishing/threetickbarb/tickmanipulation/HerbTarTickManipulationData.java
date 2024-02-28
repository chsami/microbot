package net.runelite.client.plugins.danplugins.fishing.threetickbarb.tickmanipulation;

import net.runelite.api.ItemID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;

public class HerbTarTickManipulationData extends TickManipulationData {

    @Override
    public Runnable getFirstTickRunnable() {
        return () ->
        {
            Widget guamLeafWidget = Inventory.findItem(ItemID.GUAM_LEAF);
            Microbot.getMouse().click(guamLeafWidget.getBounds());
        };
    }

    @Override
    public Runnable getSecondTickRunnable() {
        return () -> Inventory.useItemFast(ItemID.SWAMP_TAR, "use");
    }
}
