package net.runelite.client.plugins.caleblite.construction;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("clconstruction")
public interface CLConstructionConfig extends Config {

    @ConfigItem(
            keyName = "enablePlugin",
            name = "Enable Plugin",
            description = "Enable or disable the CL Construction plugin",
            position = 0
    )
    default boolean enablePlugin() {
        return false;
    }

    @ConfigItem(
            keyName = "furnitureType",
            name = "Furniture Type",
            description = "Select the type of furniture to build",
            position = 1
    )
    default FurnitureType furnitureType() {
        return FurnitureType.OAK_LARDER;
    }

    @ConfigItem(
            keyName = "useButler",
            name = "Use Butler",
            description = "Enable to use a butler for fetching supplies",
            position = 2
    )
    default boolean useButler() {
        return true;
    }

    @ConfigItem(
            keyName = "butlerType",
            name = "Butler Type",
            description = "Select the type of butler to use",
            position = 3
    )
    default ButlerType butlerType() {
        return ButlerType.DEMON_BUTLER;
    }

    @Range(
            min = 1,
            max = 10
    )
    @ConfigItem(
            keyName = "ticksBetweenActions",
            name = "Ticks Between Actions",
            description = "Number of game ticks to wait between build and remove actions",
            position = 4
    )
    default int ticksBetweenActions() {
        return 3;
    }

    @ConfigItem(
            keyName = "randomizeActions",
            name = "Randomize Actions",
            description = "Adds a small random delay to actions to appear more human-like",
            position = 5
    )
    default boolean randomizeActions() {
        return true;
    }

    enum FurnitureType {
        OAK_LARDER("Oak larder", "Oak plank", 8, 4, 15403, 13565),
        OAK_DUNGEON_DOOR("Oak dungeon door", "Oak plank", 10, 6, 15328, 13344),
        MAHOGANY_TABLE("Mahogany table", "Mahogany plank", 6, 4, 15298, 13298);

        private final String name;
        private final String requiredPlank;
        private final int requiredAmount;
        private final int menuOption;
        private final int buildSpaceId;
        private final int builtId;

        FurnitureType(String name, String requiredPlank, int requiredAmount, int menuOption, int buildSpaceId, int builtId) {
            this.name = name;
            this.requiredPlank = requiredPlank;
            this.requiredAmount = requiredAmount;
            this.menuOption = menuOption;
            this.buildSpaceId = buildSpaceId;
            this.builtId = builtId;
        }

        public String getName() { return name; }
        public String getRequiredPlank() { return requiredPlank; }
        public int getRequiredAmount() { return requiredAmount; }
        public int getMenuOption() { return menuOption; }
        public int getBuildSpaceId() { return buildSpaceId; }
        public int getBuiltId() { return builtId; }
    }

    enum ButlerType {
        BUTLER("Butler"),
        DEMON_BUTLER("Demon butler");

        private final String name;

        ButlerType(String name) {
            this.name = name;
        }

        public String getName() { return name; }
    }
}