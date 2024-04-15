package net.runelite.client.plugins.microbot.pestcontrol;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("pestcontrol")
public interface PestControlConfig extends Config {
    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 1
    )
    default String GUIDE() {
        return "Start near a boat of your combat level. Make sure your minimap is zoomed out all the way out. ENABLE THE GPU PLUGIN with render distance > 50 ";
    }

    @ConfigItem(
            keyName = "NPC Priority 1",
            name = "NPC Priority 1",
            description = "What npc to attack as first option",
            position = 2
    )
    default PestControlNpc Priority1() {
        return PestControlNpc.PORTAL;
    }
    @ConfigItem(
            keyName = "NPC Priority 2",
            name = "NPC Priority 2",
            description = "What npc to attack as second option",
            position = 3
    )
    default PestControlNpc Priority2() {
        return PestControlNpc.SPINNER;
    }
    @ConfigItem(
            keyName = "NPC Priority 3",
            name = "NPC Priority 3",
            description = "What npc to attack as third option",
            position = 4
    )
    default PestControlNpc Priority3() {
        return PestControlNpc.BRAWLER;
    }

    @ConfigItem(
            keyName = "Alch in boat",
            name = "Alch while waiting",
            description = "Alch while waiting",
            position = 5
    )
    default boolean alchInBoat() {
        return false;
    }

    @ConfigItem(
            keyName = "itemToAlch",
            name = "Item to alch",
            description = "Item to alch",
            position = 6
    )
    default String alchItem() {
        return "";
    }
}
