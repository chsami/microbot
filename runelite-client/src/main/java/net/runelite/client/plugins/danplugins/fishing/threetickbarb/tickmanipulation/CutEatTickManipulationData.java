package net.runelite.client.plugins.danplugins.fishing.threetickbarb.tickmanipulation;

import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;

import java.util.Collections;
import java.util.List;

public class CutEatTickManipulationData extends TickManipulationData {

    public CutEatTickManipulationData(final TickManipulationData tickManipulationData)
    {
        this.normalMethod = tickManipulationData;
    }

    @Override
    public Runnable getFirstTickRunnable() {
        return shouldUseNormalManipulation() ? normalMethod.getFirstTickRunnable() : DO_NOTHING;
    }

    @Override
    public Runnable getSecondTickRunnable() {
        final Runnable method = shouldUseNormalManipulation() ? normalMethod.getSecondTickRunnable() : eatFood();

        return alsoCutItem(method);
    }

    @Override
    public List<Integer> getItemIdsToDrop()
    {
        return Collections.emptyList();
    }

    private boolean shouldUseNormalManipulation()
    {
        return foodId <= 0;
    }

    private Runnable alsoCutItem(final Runnable runnable)
    {
        return () ->
        {
            runnable.run();
            cutItem();
        };
    }

    private Runnable eatFood()
    {
        return () -> Inventory.useItemFast(foodId, "eat");
    }

    private void cutItem()
    {
        if (cutId <= 0)
        {
            return;
        }

        Inventory.useItemFast(ItemID.KNIFE, "Use");
        Inventory.useItemFast(cutId, "Use");
    }

    @Subscribe
    public void onItemContainerChanged(final ItemContainerChanged event)
    {
        final ItemContainer inventory = event.getItemContainer();
        if (inventory != Microbot.getClient().getItemContainer(InventoryID.INVENTORY))
        {
            return;
        }

        setFoodId(inventory);
        setCutId(inventory);
    }

    private void setFoodId(final ItemContainer inventory)
    {
        for (Integer foodId : itemIdsToEat)
        {
            if (inventory.contains(foodId))
            {
                this.foodId = foodId;
                return;
            }
        }

        foodId = -1;
    }

    private void setCutId(final ItemContainer inventory)
    {
        for (Integer cutId : itemIdsToCut)
        {
            if (inventory.contains(cutId))
            {
                this.cutId = cutId;
                return;
            }
        }

        cutId = -1;
    }

    private int foodId;
    private int cutId;
    private final TickManipulationData normalMethod;

    private static final List<Integer> itemIdsToCut = List.of(ItemID.LEAPING_STURGEON, ItemID.LEAPING_SALMON, ItemID.LEAPING_TROUT);
    private static final List<Integer> itemIdsToEat = List.of(ItemID.CAVIAR, ItemID.ROE);
    private static final Runnable DO_NOTHING = () -> {};
}
