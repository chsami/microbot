package net.runelite.client.plugins.microbot.construction2;

import net.runelite.api.ItemID;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(Construction2Config.GROUP)
public interface Construction2Config extends Config {

    String GROUP = "Construction2";

    enum ConstructionMode {
        OAK_LARDER("Oak Larder", ItemID.OAK_PLANK),
        OAK_DUNGEON_DOOR("Oak Dungeon Door", ItemID.OAK_PLANK),
        MAHOGANY_TABLE("Mahogany Table", ItemID.MAHOGANY_PLANK);
        //MYTHICAL_CAPE("Mythical Cape Mount", ItemID.MYTHICAL_CAPE); broken, keeps trying to remove the guild trophy space instead of build shits weird idk

        private final String name;
        private final int plankItemId;

        ConstructionMode(String name, int plankItemId) {
            this.name = name;
            this.plankItemId = plankItemId;
        }

        @Override
        public String toString() {
            return name;
        }

        public int getPlankItemId() {
            return plankItemId;
        }
    }

    @ConfigSection(
            name = "General",
            description = "General configuration",
            position = 0
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "Guide",
            name = "How to use",
            description = "How to use the script",
            position = 1,
            section = generalSection
    )
    default String GUIDE() {
        return "This script supports oak larder, oak dungeon doors, mahogany table with a demon butler. " +
                "Call the butler and clear any previous conversations you've had." +
                " Then start the plugin next to the build space with " +
                "coins, a saw, a hammer and your noted planks and the rest of your inventory un-noted planks.";
    }

    @ConfigItem(
            keyName = "selectedMode",
            name = "Mode",
            description = "Select the construction mode",
            position = 2,
            section = generalSection
    )
    default ConstructionMode selectedMode() {
        return ConstructionMode.OAK_DUNGEON_DOOR;
    }
}
