package net.runelite.client.plugins.microbot.GirdyScripts.cannonballsmelter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigInformation;

@ConfigGroup("CannonballSmelter")
@ConfigInformation ("<div style='background-color:black;color:yellow;padding:20px;'>" +
                    "<center><h2>Instructions</h2>" +
                    " <p>Start script in Edgeville bank <br /><br />" +
                    " <b style='color:red;'>MUST</b> have Ammo mould in inventory <br />" +
                    " and Steel bars in bank</center></div>")
public interface CannonballSmelterConfig extends Config {
}
