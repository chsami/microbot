/*
 * Copyright (c) 2024. By Jrod7938
 *
 */
package net.runelite.client.plugins.jrPlugins.autoVorkath.enums

import lombok.Getter
import lombok.RequiredArgsConstructor

@Getter
@RequiredArgsConstructor
enum class CROSSBOW(private val crossbowName: String) {
    DRAGON_HUNTER_CROSSBOW("Dragon hunter crossbow"),
    RUNE_CROSSBOW("Rune crossbow"),
    ARMADYL_CROSSBOW("Armadyl crossbow"),
    DRAGON_CROSSBOW("Dragon crossbow");

    override fun toString(): String {
        return crossbowName
    }
}
