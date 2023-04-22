package net.runelite.client.plugins.microbot.giantsfoundry.enums;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;

import java.util.Map;

import static net.runelite.client.plugins.microbot.giantsfoundry.enums.CommissionType.*;
import static net.runelite.client.plugins.microbot.giantsfoundry.enums.MouldType.*;

@AllArgsConstructor
public enum Mould {
    CHOPPER_FORTE("Chopper Forte", FORTE, ImmutableMap.of(BROAD, 4, LIGHT, 4, FLAT, 4)),
    GALDIUS_RICASSO("Galdius Ricasso", FORTE, ImmutableMap.of(BROAD, 4, HEAVY, 4, FLAT, 4)),
    DISARMING_FORTE("Disarming Forte", FORTE, ImmutableMap.of(NARROW, 4, LIGHT, 4, SPIKED, 4)),
    MEDUSA_RICASSO("Medusa Ricasso", FORTE, ImmutableMap.of(BROAD, 8, HEAVY, 6, FLAT, 8)),
    SERPENT_RICASSO("Serpent Ricasso", FORTE, ImmutableMap.of(NARROW, 6, LIGHT, 8, FLAT, 8)),
    SERRATED_FORTE("Serrated Forte", FORTE, ImmutableMap.of(NARROW, 8, HEAVY, 8, SPIKED, 6)),
    STILETTO_FORTE("Stiletto Forte", FORTE, ImmutableMap.of(NARROW, 8, LIGHT, 10, FLAT, 8)),
    DEFENDER_BASE("Defender Base", FORTE, ImmutableMap.of(BROAD, 8, HEAVY, 10, FLAT, 8)),
    JUGGERNAUT_FORTE("Juggernaut Forte", FORTE, ImmutableMap.of(BROAD, 4, HEAVY, 4, SPIKED, 16)),
    CHOPPER_FORTE_1("Chopper Forte +1", FORTE, ImmutableMap.of(BROAD, 3, LIGHT, 4, FLAT, 18)),
    SPIKER("Spiker!", FORTE, ImmutableMap.of(NARROW, 1, HEAVY, 2, SPIKED, 22)),
    SAW_BLADE("Saw Blade", BLADE, ImmutableMap.of(BROAD, 4, LIGHT, 4, SPIKED, 4)),
    DEFENDERS_EDGE("Defenders Edge", BLADE, ImmutableMap.of(BROAD, 4, HEAVY, 4, SPIKED, 4)),
    FISH_BLADE("Fish Blade", BLADE, ImmutableMap.of(NARROW, 4, LIGHT, 4, FLAT, 4)),
    MEDUSA_BLADE("Medusa Blade", BLADE, ImmutableMap.of(BROAD, 8, HEAVY, 8, FLAT, 6)),
    STILETTO_BLADE("Stiletto Blade", BLADE, ImmutableMap.of(NARROW, 8, LIGHT, 6, FLAT, 8)),
    GLADIUS_EDGE("Gladius Edge", BLADE, ImmutableMap.of(NARROW, 6, HEAVY, 8, FLAT, 8)),
    FLAMBERGE_BLADE("Flamberge Blade", BLADE, ImmutableMap.of(NARROW, 8, LIGHT, 8, SPIKED, 10)),
    SERPENT_BLADE("Serpent Blade", BLADE, ImmutableMap.of(NARROW, 10, LIGHT, 8, FLAT, 8)),
    CLAYMORE_BLADE("Claymore Blade", BLADE, ImmutableMap.of(BROAD, 16, HEAVY, 4, FLAT, 4)),
    FLEUR_DE_BLADE("Fleur de Blade", BLADE, ImmutableMap.of(BROAD, 4, HEAVY, 18, SPIKED, 1)),
    CHOPPA("Choppa!", BLADE, ImmutableMap.of(BROAD, 1, LIGHT, 22, FLAT, 2)),
    PEOPLE_POKER_POINT("People Poker Point", TIP, ImmutableMap.of(NARROW, 4, HEAVY, 4, FLAT, 4)),
    CHOPPER_TIP("Chopper Tip", TIP, ImmutableMap.of(BROAD, 4, LIGHT, 4, SPIKED, 4)),
    MEDUSAS_HEAD("Medusa's Head", TIP, ImmutableMap.of(BROAD, 4, HEAVY, 4, SPIKED, 4)),
    SERPENTS_FANG("Serpent's Fang", TIP, ImmutableMap.of(NARROW, 8, LIGHT, 6, SPIKED, 8)),
    GLADIUS_POINT("Gladius Point", TIP, ImmutableMap.of(NARROW, 8, HEAVY, 8, FLAT, 6)),
    SAW_TIP("Saw Tip", TIP, ImmutableMap.of(BROAD, 6, HEAVY, 8, SPIKED, 8)),
    CORRUPTED_POINT("Corrupted Point", TIP, ImmutableMap.of(NARROW, 8, LIGHT, 10, SPIKED, 8)),
    DEFENDERS_TIP("Defenders Tip", TIP, ImmutableMap.of(BROAD, 10, HEAVY, 8, SPIKED, 8)),
    SERRATED_TIP("Serrated Tip", TIP, ImmutableMap.of(NARROW, 4, LIGHT, 16, SPIKED, 4)),
    NEEDLE_POINT("Needle Point", TIP, ImmutableMap.of(NARROW, 18, LIGHT, 3, FLAT, 4)),
    THE_POINT("The Point!", TIP, ImmutableMap.of(BROAD, 2, LIGHT, 1, FLAT, 22)),
    ;


    private final String name;
    private final MouldType mouldType;
    private final Map<CommissionType, Integer> typeToScore;

    public static final Mould[] values = Mould.values();

    public static Mould forName(String text) {
        for (Mould mould : values) {
            if (mould.name.equalsIgnoreCase(text)) {
                return mould;
            }
        }
        return null;
    }

    public int getScore(CommissionType type1, CommissionType type2) {
        int score = 0;
        score += typeToScore.getOrDefault(type1, 0);
        score += typeToScore.getOrDefault(type2, 0);
        return score;
    }
}