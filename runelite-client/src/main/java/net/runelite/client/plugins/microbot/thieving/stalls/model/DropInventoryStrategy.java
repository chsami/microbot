package net.runelite.client.plugins.microbot.thieving.stalls.model;

import lombok.AllArgsConstructor;

import javax.inject.Inject;

@AllArgsConstructor(onConstructor_ = @Inject)
public class DropInventoryStrategy implements IInventoryStrategy {
    private BotApi botApi;

    @Override
    public void execute(final IStallThievingSpot stallThievingSpot) {
        if (!botApi.isInventoryFull())
        {
            return;
        }

        botApi.dropAll(stallThievingSpot.getItemIdsToDrop());
    }
}
