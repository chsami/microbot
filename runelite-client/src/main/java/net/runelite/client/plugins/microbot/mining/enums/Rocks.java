package net.runelite.client.plugins.microbot.mining.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

@Getter
@RequiredArgsConstructor
public enum Rocks {
    TIN("tin rocks", 1),
    COPPER("copper rocks", 1),
    CLAY("clay rocks", 1),
    IRON("iron rocks", 15),
    SILVER("silver rocks", 20),
    COAL("coal rocks", 30),
    GOLD("gold rocks", 40),
    GEM("gem rocks", 40),
    CALCIFIED_ROCKS("calcified rocks", 41),
    MITHRIL("mithril rocks", 55),
    ADAMANTITE("adamantite rocks", 70),
    RUNITE("runite rocks", 85);


    private final String name;
    private final int miningLevel;

    @Override
    public String toString() {
        return name;
    }
    
    public boolean hasRequiredLevel() {
        return Rs2Player.getSkillRequirement(Skill.MINING, this.miningLevel);
    }
}
