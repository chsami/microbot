package net.runelite.client.plugins.microbot.GirdyScripts.cannonballsmelter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("CannonballSmelter")
public interface CannonballSmelterConfig extends Config {
    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "Instructions on how to use this plugin",
            position = 0
    )
    default String GUIDE() {
        return "Start at edgeville or port phasmatys bank " +
                "with ammo mould in inventory and steel bars in bank";
    }
}
