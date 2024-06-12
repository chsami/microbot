package net.runelite.client.plugins.hoseaplugins.lucidcannonreloader;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("lucid-cannon-reloader")
public interface LucidCannonReloaderConfig extends Config
{

    @ConfigItem(
            keyName = "minCannonballAmount",
            name = "Min. Cannonballs",
            description = "The absolute minimum amount of cannonballs left before a reload is needed",
            position = 0
    )
    @Range(min = 1, max = 50)
    default int minCannonballAmount()
    {
        return 3;
    }

    @ConfigItem(
            keyName = "maxCannonballAmount",
            name = "Max. Cannonballs",
            description = "The absolute maximum amount of cannonballs left before a reload is needed",
            position = 1
    )
    @Range(min = 1, max = 50)
    default int maxCannonballAmount()
    {
        return 30;
    }

    @ConfigItem(
            keyName = "minReloadDelay",
            name = "Min. Reload Delay",
            description = "Minimum amount of game ticks plugin will wait to reload the cannon when below reload amount (Each game tick is ~600ms)",
            position = 2
    )
    @Range(min = 1, max = 20)
    default int minReloadDelay()
    {
        return 3;
    }

    @ConfigItem(
            keyName = "maxReloadDelay",
            name = "Max. Reload Delay",
            description = "Maximum amount of game ticks plugin will wait to reload the cannon when below reload amount (Each game tick is ~600ms)",
            position = 3
    )
    @Range(min = 1, max = 20)
    default int maxReloadDelay()
    {
        return 5;
    }
}
