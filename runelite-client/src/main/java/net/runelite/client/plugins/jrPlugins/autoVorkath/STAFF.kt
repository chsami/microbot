package net.runelite.client.plugins.jrPlugins.autoVorkath

import lombok.Getter
import lombok.RequiredArgsConstructor

@Getter
@RequiredArgsConstructor
enum class STAFF(val staffName: String) {
    CAST("Cast"),
    SLAYER_STAFF("Slayer's Staff"),
    SLAYER_STAFF_E("Slayer's Staff (e)");

    override fun toString(): String { return staffName }
}
