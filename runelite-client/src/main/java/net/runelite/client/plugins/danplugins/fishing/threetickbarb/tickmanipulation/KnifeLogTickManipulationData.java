package net.runelite.client.plugins.danplugins.fishing.threetickbarb.tickmanipulation;

import net.runelite.api.ItemID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;

import java.util.Optional;

public class KnifeLogTickManipulationData extends TickManipulationData
{

    @Override
    public Runnable getFirstTickRunnable()
    {
        return () ->
        {
            Widget logs = Optional.ofNullable(Inventory.findItem(ItemID.TEAK_LOGS))
                                  .orElse(Inventory.findItem(ItemID.MAHOGANY_LOGS));
            Microbot.getMouse().click(logs.getBounds());
        };
    }

    @Override
    public Runnable getSecondTickRunnable()
    {
        return () -> Inventory.useItemFast(ItemID.KNIFE, "use");
    }
}
