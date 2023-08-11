package net.runelite.client.plugins.autoVorkath

import net.runelite.client.config.Config
import net.runelite.client.config.ConfigGroup
import net.runelite.client.config.ConfigItem

@ConfigGroup("AutoVorkath")
interface AutoVorkathConfig : Config {
    @ConfigItem(
        keyName = "crossbow",
        name = "Crossbow",
        description = "Choose your crossbow",
        position = 0
    )
    fun CROSSBOW(): CROSSBOW? {
        return CROSSBOW.ARMADYL_CROSSBOW
    }

    @ConfigItem(
        keyName = "slayersStaff",
        name = "Slayers Staff",
        description = "Choose your slayers staff",
        position = 1
    )
    fun SLAYERSTAFF(): STAFF? {
        return STAFF.SLAYER_STAFF
    }

    @ConfigItem(
        keyName = "teleport",
        name = "Teleport",
        description = "Choose your teleport",
        position = 2
    )
    fun TELEPORT(): TELEPORT? {
        return TELEPORT.CONSTRUCT_CAPE_T
    }
}
