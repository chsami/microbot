package net.runelite.client.plugins.jrPlugins.autoZMIAltar

import lombok.Getter
import lombok.RequiredArgsConstructor

@Getter
@RequiredArgsConstructor
enum class TELEPORT(val teleportName: String) {
    CONSTRUCT_CAPE_T("Construct. cape(t)"),
    CONSTRUCT_CAPE("Construct. cape"),
    HOUSE_TAB("Teleport to house");

    override fun toString(): String { return teleportName }
}
