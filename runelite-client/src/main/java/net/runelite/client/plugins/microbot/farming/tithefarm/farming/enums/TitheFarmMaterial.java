package net.runelite.client.plugins.microbot.farming.tithefarm.farming.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;

@Getter
@RequiredArgsConstructor
public enum TitheFarmMaterial {
    GOLOVANOVA_SEED("Golovanova seed", 34, '1'),
    BOLOGANO_SEED("Bologano seed", 54, '2'),
    LOGAVANO_SEED("Logavano seed", 74, '3');

    final String name;
    final int levelRequired;
    final char option;

    public static TitheFarmMaterial getSeedForLevel() {
        if (Microbot.getClient().getRealSkillLevel(Skill.FARMING) >= LOGAVANO_SEED.levelRequired)
            return LOGAVANO_SEED;
        if (Microbot.getClient().getRealSkillLevel(Skill.FARMING) >= BOLOGANO_SEED.levelRequired)
            return BOLOGANO_SEED;
        if (Microbot.getClient().getRealSkillLevel(Skill.FARMING) >= GOLOVANOVA_SEED.levelRequired)
            return GOLOVANOVA_SEED;

        return null;
    }
}
