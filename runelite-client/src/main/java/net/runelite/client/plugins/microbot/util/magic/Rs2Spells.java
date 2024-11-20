package net.runelite.client.plugins.microbot.util.magic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.World;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;
import org.apache.commons.lang3.tuple.Pair;

@Getter
@RequiredArgsConstructor
public enum Rs2Spells {
    VARROCK(25,
            new Pair[] {Pair.of("law rune", 1), Pair.of("air rune", 3), Pair.of("fire rune", 1)},
            new WorldPoint(3213, 3425, 0), "<col=00ff00>Varrock Teleport</col>", "Varrock teleport", MagicAction.VARROCK_TELEPORT),
    LUMBRIDGE(31,
                    new Pair[] {Pair.of("law rune", 1), Pair.of("air rune", 3), Pair.of("earth rune", 1)},
            new WorldPoint(3222, 3218, 0), "<col=00ff00>Lumbridge Teleport</col>", "Lumbridge teleport", MagicAction.LUMBRIDGE_TELEPORT),
    FALADOR(37,
                      new Pair[] {Pair.of("law rune", 1), Pair.of("air rune", 3), Pair.of("water rune", 1)},
            new WorldPoint(3093, 3380, 0), "<col=00ff00>Falador Teleport</col>", "Falador teleport", MagicAction.FALADOR_TELEPORT),
    CAMELOT(45,
                    new Pair[] {Pair.of("law rune", 1), Pair.of("air rune", 5)},
            new WorldPoint(2756, 3476, 0), "<col=00ff00>Camelot Teleport</col>", "Camelot teleport", MagicAction.CAMELOT_TELEPORT),

    ARDOUGNE(51,
                    new Pair[] {Pair.of("law rune", 2), Pair.of("water rune", 2)},
            new WorldPoint(2789, 3306, 0), "<col=00ff00>Ardougne Teleport</col>", "Ardougne teleport", MagicAction.ARDOUGNE_TELEPORT),
    CONFUSE(3,
                     new Pair[] {Pair.of("earth rune", 2), Pair.of("water rune", 3), Pair.of("body rune", 1)},
            new WorldPoint(-1, -1, 0), "<col=00ff00>Confuse</col>", "", MagicAction.CONFUSE),
    WEAKEN(11,
            new Pair[] {Pair.of("earth rune", 2), Pair.of("water rune", 3), Pair.of("body rune", 1)},
            new WorldPoint(-1, -1, 0), "<col=00ff00>Weaken</col>", "", MagicAction.WEAKEN),
    CURSE(19,
            new Pair[] {Pair.of("earth rune", 3), Pair.of("water rune", 2), Pair.of("body rune", 1)},
            new WorldPoint(-1, -1, 0), "<col=00ff00>Weaken</col>", "", MagicAction.CURSE),
    VULNERABILITY(66,
            new Pair[] {Pair.of("earth rune", 5), Pair.of("water rune", 5), Pair.of("soul rune", 1)},
            new WorldPoint(-1, -1, 0), "<col=00ff00>Weaken</col>", "", MagicAction.VULNERABILITY),
    ENFEEBLE(73,
                          new Pair[] {Pair.of("earth rune", 8), Pair.of("water rune", 8), Pair.of("soul rune", 1)},
            new WorldPoint(-1, -1, 0), "<col=00ff00>Weaken</col>", "", MagicAction.ENFEEBLE),
    STUN(80, new Pair[] {Pair.of("earth", 12), Pair.of("water rune", 12), Pair.of("soul rune", 1)},
            new WorldPoint(-1, -1, 0), "<col=00ff00>Weaken</col>", "", MagicAction.STUN);


    private final int level;
    private final Pair[] itemsRequired;
    private final WorldPoint destination;
    private final String widgetText;
    private final String tabletName;
    private final MagicAction spell;
}
