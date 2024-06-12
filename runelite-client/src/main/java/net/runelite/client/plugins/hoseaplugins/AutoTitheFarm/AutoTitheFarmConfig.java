package net.runelite.client.plugins.hoseaplugins.AutoTitheFarm;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("AutoTitheFarm")
public interface AutoTitheFarmConfig extends Config {

    @ConfigSection(
            name = "Plugin setup",
            description = "",
            position = 0
    )
    String SETUP = "Setup";

    @ConfigSection(
            name = "Misc.",
            description = "",
            position = 1
    )
    String MISC = "Misc";

    @ConfigSection(
            name = "Overlay",
            description = "",
            position = 2
    )
    String OVERLAY = "Overlay";

    @ConfigItem(
            keyName = "layout",
            name = "Patch Layout",
            description = " ",
            position = 0,
            section = SETUP
    )
    default PatchLayout patchLayout() {
        return PatchLayout.REGULAR_LAYOUT;
    }

    @ConfigItem(
            keyName = "minRunEnergyToIdleUnder",
            name = "Idle if run energy is under",
            description = " ",
            position = 1,
            section = SETUP
    )
    default int minRunEnergyToIdleUnder() {
        return 20;
    }

    @ConfigItem(
            keyName = "useStaminaPot",
            name = "Use Stamina Potion",
            description = "Will use stamina potion to restore energy.",
            position = 2,
            section = SETUP
    )
    default boolean useStaminaPot() {
        return false;
    }

    @ConfigItem(
            keyName = "oneTickBankAllHerbBoxes",
            name = "One tick bank-all Herb boxes",
            description = "Will mass bank-all herb boxes rather than just banking them one tick at a time",
            position = 3,
            section = SETUP
    )
    default boolean oneTickBankAllHerbBoxes() {
        return false;
    }

    @ConfigItem(
            keyName = "switchGearDuringHarvestingPhase",
            name = "Switch gear for xp",
            description = "Will switch gear to farmer's equipment (if available) during harvesting phase.",
            position = 4,
            section = SETUP
    )
    default boolean switchGearDuringHarvestingPhase() {
        return false;
    }

    @ConfigItem(
            keyName = "StopIfReachedFruitAmountFarmed",
            name = "Enable stop condition below",
            description = " ",
            position = 2,
            section = MISC
    )
    default boolean stopIfReachedFruitAmountFarmed() {
        return false;
    }

    @ConfigItem(
            keyName = "maxFruitToFarm",
            name = "Maximum amount of fruit to farm",
            description = "How much fruit to farm before the plugin stops",
            position = 3,
            section = MISC
    )
    default int maxFruitToFarm() {
        return 200;
    }

    @ConfigItem(
            keyName = "enableDebug",
            name = "Enable debug",
            description = " ",
            position = 4,
            section = OVERLAY
    )
    default boolean enableDebug() {
        return false;
    }
}
