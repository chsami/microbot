package net.runelite.client.plugins.autoVorkath

import lombok.Getter
import lombok.RequiredArgsConstructor

@Getter
@RequiredArgsConstructor
enum class TELEPORT(val teleportName: String) {
    CONSTRUCT_CAPE_T("Construct. Cape (t)"),
    CONSTRUCT_CAPE("Construct. Cape"),
    HOUSE_TAB("Teleport to house");

    override fun toString(): String { return teleportName }
}
