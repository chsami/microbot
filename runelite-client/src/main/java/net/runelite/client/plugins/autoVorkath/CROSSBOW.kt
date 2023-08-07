package net.runelite.client.plugins.autoVorkath

import lombok.Getter
import lombok.RequiredArgsConstructor

@Getter
@RequiredArgsConstructor
enum class CROSSBOW(val crossbowName: String) {
    ARMADYL_CROSSBOW("Armadyl Crossbow"),
    DRAGON_HUNTER_CROSSBOW("Dragon Hunter Crossbow"),
    RUNE_CROSSBOW("Rune Crossbow"),
    DRAGON_CROSSBOW("Dragon Crossbow"),
    TOXIC_BLOWPIPE("Toxic Blowpipe");

    override fun toString(): String { return crossbowName }
}
