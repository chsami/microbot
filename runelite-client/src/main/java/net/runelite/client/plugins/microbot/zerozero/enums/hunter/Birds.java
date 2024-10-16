package net.runelite.client.plugins.microbot.zerozero.enums.hunter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

@Getter
@RequiredArgsConstructor
public enum Birds {
    CRIMSON(1, "Crimson swift", 1),
    GOLDEN(2, "Golden warbler", 5),
    COPPER(3, "Copper longtail", 9),
    CERULEAN(4, "Cerulean twitch", 11),
    TROPICAL(5, "Tropical wagtail", 15);

    @Getter
    private final int id;
    private final String name;
    private final int hunterLevel;

    public boolean hasRequiredLevel() {
        return Rs2Player.getSkillRequirement(Skill.HUNTER, this.hunterLevel);
    }
}
