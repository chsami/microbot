package net.runelite.client.plugins.microbot.farming.herbrun;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("example")
public interface HerbrunConfig extends Config {

    @ConfigSection(
            name = "Guide",
            description = "Guide",
            position = 1
    )
    String guideSection = "Guide";

    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 1,
            section = guideSection
    )
    default String GUIDE() {
        return "Start next to a bank\n" +
                "Have the following in your bank:\n" +
                "1. Ardougne cloak\n" +
                "2. Ultracompost or Bottomless compost bucket (filled)\n" +
                "3. Magic Secatuers\n" +
                "4. Seed dibber\n" +
                "5. Spade\n" +
                "6. Rake\n" +
                "7. Ectophial \n" +
                "8. Any Quetzal whistle (WITH CHARGES)\n" +
                "9. Stony Basalt/Trollheim tab/runes\n" +
                "10. Icy Basalt\n" +
                "11. Skills necklace \n" +
                "12. Explorer's Ring \n" +
                "13. Herb seeds \n" +
                "14. Camelot teleport tab/runes\n" +
                "15. Xeric's talisman";

    }

    @ConfigItem(
            keyName = "fastHerb",
            name = "Fast Herb Picking?",
            description = "Enable quick herb picking",
            position = 4,
            section = settingsSection
    )
    default boolean FAST_HERB() {
        return false;
    }

    @ConfigItem(
            keyName = "bottomless",
            name = "Use bottomless bucket?",
            description = "Should  bottomless bucket be withdrawn from the bank?",
            position = 4,
            section = settingsSection
    )
    default boolean COMPOST() {
        return true;
    }

    @ConfigItem(
            keyName = "ardougne_teleport",
            name = "Use Ardougne cloak?",
            description = "Does your adougne cloak still have charges for today? If not the script will use Ardy tab",
            position = 4,
            section = settingsSection
    )
    default boolean ARDOUGNE_TELEPORT_OPTION() {
        return true;
    }

    @ConfigItem(
            keyName = "falador_teleport",
            name = "Use Explorer's ring?",
            description = "Does your ring still have charges for today? If not the script will use falador tab",
            position = 4,
            section = settingsSection
    )
    default boolean FALADOR_TELEPORT_OPTION() {
        return true;
    }

    @ConfigItem(
            keyName = "graceful",
            name = "Equip graceful?",
            description = "Should graceful be equipped from bank?",
            position = 4,
            section = settingsSection
    )
    default boolean GRACEFUL() {
        return true;
    }


    @ConfigItem(
            keyName = "seedTypes",
            name = "Seeds to use",
            description = "Which seeds to use for the herb run?",
            position = 3,
            section = settingsSection
    )
    default HerbrunInfo.seedType SEED() {
        return HerbrunInfo.seedType.KWUARM_SEED;
    }

    @ConfigItem(
            keyName = "cloakType",
            name = "Cloak to use",
            description = "Which cloak to use for the herb run?",
            position = 3,
            section = settingsSection
    )
    default HerbrunInfo.cloak CLOAK() {
        return HerbrunInfo.cloak.ARDOUGNE_CLOAK_3;
    }

    @ConfigItem(
            keyName = "ringType",
            name = "Explorers ring to use",
            description = "Which explorers ring to use for the herb run?",
            position = 3,
            section = settingsSection
    )
    default HerbrunInfo.ring RING() {
        return HerbrunInfo.ring.EXPLORERS_RING_3;
    }

    @ConfigItem(
            keyName = "trollHeim teleport Type",
            name = "Trollheim teleport to use?",
            description = "Which trollheim teleport to use??",
            position = 3,
            section = settingsSection
    )
    default HerbrunInfo.trollheimTeleport TROLLHEIMTELEPORT() { return HerbrunInfo.trollheimTeleport.STONY_BASALT;
    }


    @ConfigSection(
            name = "Settings",
            description = "Settings",
            position = 2
    )
    String settingsSection = "Settings";
/*    @ConfigItem(
            keyName = "Ore",
            name = "Ore",
            description = "Choose the ore",
            position = 0
    )
    default List<String> ORE()
    {
        return Rocks.TIN;
    }*/
}
