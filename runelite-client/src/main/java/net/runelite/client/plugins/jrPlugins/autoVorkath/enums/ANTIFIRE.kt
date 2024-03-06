/*
 * Copyright (c) 2024. By Jrod7938
 *
 */

package net.runelite.client.plugins.jrPlugins.autoVorkath.enums

import lombok.Getter
import lombok.RequiredArgsConstructor

@Getter
@RequiredArgsConstructor
enum class ANTIFIRE(private val antiFireName: String, private val time: Int) {
    EXTENDED_SUPER_ANTIFIRE("Extended super antifire", 6),
    EXTENDED_SUPER_ANTIFIRE_MIX("Extended super antifire mix", 6),
    EXTENDED_ANTIFIRE("Extended antifire", 12),
    EXTENDED_ANTIFIRE_MIX("Extended antifire mix", 12),
    SUPER_ANTIFIRE_POTION("Super antifire potion", 3),
    SUPER_ANTIFIRE_MIX("Super antifire mix", 3),
    ANTIFIRE_POTION("Antifire potion", 6),
    ANTIFIRE_MIX("Antifire mix", 6);

    override fun toString(): String = antiFireName
    fun time(): Int = ((time * .3) * 60 * 1000).toInt() // time to drink ( 30% of the antifire effect )

}
