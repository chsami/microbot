package net.runelite.client.plugins.jrPlugins.autoZMIAltar

import lombok.Getter
import lombok.RequiredArgsConstructor

@Getter
@RequiredArgsConstructor
enum class BANK(val bankName: String) {
    ZMIBANK("ZMI Bank"),
    EDGEVILLE("Edgeville");

    override fun toString(): String { return bankName }
}