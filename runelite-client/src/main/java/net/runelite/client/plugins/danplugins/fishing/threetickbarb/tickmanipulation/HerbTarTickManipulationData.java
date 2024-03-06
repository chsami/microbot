package net.runelite.client.plugins.danplugins.fishing.threetickbarb.tickmanipulation;

import net.runelite.api.ItemID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;

public class HerbTarTickManipulationData extends TickManipulationData {

    @Override
    public Runnable getFirstTickRunnable() {
        return () ->
        {
            Rs2Item guamLeafWidget = Rs2Inventory.get(ItemID.GUAM_LEAF);
            Rs2Inventory.use(guamLeafWidget);
        };
    }

    @Override
    public Runnable getSecondTickRunnable() {
        return () -> Rs2Inventory.interact(ItemID.SWAMP_TAR, "use");
    }
}
