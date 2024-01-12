/*
 * Copyright (c) 2024. By Jrod7938
 *
 */
package net.runelite.client.plugins.jrPlugins.autoVorkath.enums

import lombok.Getter
import lombok.RequiredArgsConstructor

@Getter
@RequiredArgsConstructor
enum class STAFF(private val staffName: String) {
    SLAYER_STAFF("Slayer's staff"),
    SLAYER_STAFF_E("Slayer's staff (e)");

    override fun toString(): String {
        return staffName
    }
}
