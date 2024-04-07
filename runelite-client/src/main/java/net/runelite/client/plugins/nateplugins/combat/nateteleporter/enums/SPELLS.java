package net.runelite.client.plugins.nateplugins.combat.nateteleporter.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.plugins.microbot.util.magic.Rs2Spells;

@Getter
@RequiredArgsConstructor
public enum SPELLS {
    NONE(null),
    FALADOR(Rs2Spells.FALADOR),
    LUMBRIDGE(Rs2Spells.LUMBRIDGE),
    VARROCK(Rs2Spells.VARROCK),
    CAMELOT(Rs2Spells.CAMELOT),
    ARDOUGNE(Rs2Spells.ARDOUGNE),
    CONFUSE(Rs2Spells.CONFUSE),
    WEAKEN(Rs2Spells.WEAKEN),
    CURSE(Rs2Spells.CURSE),
    VULNERABILITY(Rs2Spells.VULNERABILITY),
    ENFEEBLE(Rs2Spells.ENFEEBLE),
    STUN(Rs2Spells.STUN);

    private final Rs2Spells spell;

    @Override
    public String toString() {
        return spell != null ? spell.getSpell().getName() : "None";
    }

}
