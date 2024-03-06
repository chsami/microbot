package net.runelite.client.plugins.danplugins.fishing.threetickbarb.tickmanipulation;

import net.runelite.api.ItemID;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;

import java.util.Optional;

public class KnifeLogTickManipulationData extends TickManipulationData
{

    @Override
    public Runnable getFirstTickRunnable()
    {
        return () ->
        {
            Rs2Item logs = Optional.ofNullable(Rs2Inventory.get(ItemID.TEAK_LOGS))
                                  .orElse(Rs2Inventory.get(ItemID.MAHOGANY_LOGS));
            Rs2Inventory.use(logs);
        };
    }

    @Override
    public Runnable getSecondTickRunnable()
    {
        return () -> Rs2Inventory.interact(ItemID.KNIFE, "use");
    }
}
