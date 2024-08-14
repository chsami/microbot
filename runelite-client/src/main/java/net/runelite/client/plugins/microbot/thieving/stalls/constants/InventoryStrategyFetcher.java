package net.runelite.client.plugins.microbot.thieving.stalls.constants;

import lombok.AllArgsConstructor;
import net.runelite.client.plugins.microbot.thieving.stalls.StallThievingConfig;
import net.runelite.client.plugins.microbot.thieving.stalls.model.BankInventoryStrategy;
import net.runelite.client.plugins.microbot.thieving.stalls.model.DropInventoryStrategy;
import net.runelite.client.plugins.microbot.thieving.stalls.model.IInventoryStrategy;

import javax.inject.Inject;

@AllArgsConstructor(onConstructor_ = @Inject)
public class InventoryStrategyFetcher {

    public IInventoryStrategy getInventoryStrategy(final StallThievingConfig config)
    {
        if (config.shouldBankWhenPossible())
        {
            return bankInventoryStrategy;
        }

        return dropInventoryStrategy;
    }

    private BankInventoryStrategy bankInventoryStrategy;
    private DropInventoryStrategy dropInventoryStrategy;
}
