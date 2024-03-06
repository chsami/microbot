package net.runelite.client.plugins.danplugins.fishing.threetickbarb.tickmanipulation;

import net.runelite.api.ItemID;

import java.util.Collection;
import java.util.List;

public abstract class TickManipulationData {
    public abstract Runnable getFirstTickRunnable();

    public abstract Runnable getSecondTickRunnable();
    public Collection<Integer> getItemIdsToDrop()
    {
        return List.of(ItemID.LEAPING_STURGEON, ItemID.LEAPING_SALMON, ItemID.LEAPING_TROUT);
    }
}
