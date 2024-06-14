package net.runelite.client.plugins.microbot.mining.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Rocks {
    TIN("tin rocks", 1),
    COPPER("copper rocks", 1),
    IRON("iron rocks", 15),
    SILVER("silver rocks", 20),
    COAL("coal rocks", 30),
    GOLD("gold rocks", 40),
    MITHRIL("mithril rocks", 55),
    ADAMANTITE("adamantite rocks", 70),
    RUNITE("runite rocks", 85);

    private final String name;
    private final int miningLevel;

    @Override
    public String toString() {
        return name;
    }
}
