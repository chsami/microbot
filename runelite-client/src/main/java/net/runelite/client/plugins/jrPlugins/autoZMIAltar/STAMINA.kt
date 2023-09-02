package net.runelite.client.plugins.jrPlugins.autoZMIAltar

import lombok.Getter
import lombok.RequiredArgsConstructor

@Getter
@RequiredArgsConstructor
enum class STAMINA(val staminaName: String) {
    ORNATEPOOL("Construct. cape(t)"),
    STAMINAPOTION("Stamina Potion");

    override fun toString(): String { return staminaName }
}