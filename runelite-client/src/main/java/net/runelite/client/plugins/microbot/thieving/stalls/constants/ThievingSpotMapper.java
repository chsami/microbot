package net.runelite.client.plugins.microbot.thieving.stalls.constants;

import lombok.AllArgsConstructor;
import net.runelite.client.plugins.microbot.thieving.stalls.model.ArdyBakerThievingSpot;
import net.runelite.client.plugins.microbot.thieving.stalls.model.ArdySilkThievingSpot;
import net.runelite.client.plugins.microbot.thieving.stalls.model.HosidiusFruitThievingSpot;
import net.runelite.client.plugins.microbot.thieving.stalls.model.IStallThievingSpot;

import javax.inject.Inject;
import java.util.Map;

@AllArgsConstructor(onConstructor_ = @Inject)
public class ThievingSpotMapper {

    public IStallThievingSpot getThievingSpot(final ThievingSpot thievingSpot)
    {
        final Map<ThievingSpot, IStallThievingSpot> map = Map.of(
                ThievingSpot.ARDY_BAKER, ardyBakerThievingSpot,
                ThievingSpot.ARDY_SILK, ardySilkThievingSpot,
                ThievingSpot.HOSIDIUS_FRUIT, hosidiusFruitThievingSpot
        );

        return map.get(thievingSpot);
    }

    private ArdyBakerThievingSpot ardyBakerThievingSpot;
    private ArdySilkThievingSpot ardySilkThievingSpot;
    private HosidiusFruitThievingSpot hosidiusFruitThievingSpot;
}
