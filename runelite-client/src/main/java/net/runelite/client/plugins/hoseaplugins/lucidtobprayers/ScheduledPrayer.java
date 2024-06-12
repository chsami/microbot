package net.runelite.client.plugins.hoseaplugins.lucidtobprayers;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.runelite.api.Prayer;

@Data
@AllArgsConstructor
public class ScheduledPrayer
{

    private Prayer prayer;

    private int activationTick;

    private boolean toggle;
}
