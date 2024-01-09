/*
 * Copyright (c) 2024. By Jrod7938
 *
 */
package net.runelite.client.plugins.jrPlugins.autoVorkath.enums

import lombok.Getter
import lombok.RequiredArgsConstructor

@Getter
@RequiredArgsConstructor
enum class RANGE_POTION(private val potionName: String, private val time: Int) {
    DIVINE_RANGING_POTION("Divine ranging potion", 5),
    DIVINE_BASTION_POTION("Divine bastion potion", 5),
    RANGING_POTION("Ranging potion", 3);

    override fun toString(): String {
        return potionName
    }

    fun time(): Int = ((time * .3) * 60 * 1000).toInt() // time to drink ( 30% of the Range effect )
}
