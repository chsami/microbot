package net.runelite.client.plugins.microbot.DesolesticePlugins.Smelting.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Ores {
    TIN("tin ore"),
    COPPER("copper ore"),
    CLAY("clay"),
    IRON("iron ore"),
    SILVER("silver ore"),
    COAL("coal"),
    GOLD("gold ore"),
    MITHRIL("mithril ore"),
    ADAMANTITE("adamantite ore"),
    RUNITE("runite ore");

    private final String name;
    @Override
    public String toString() {
        return name;
    }
}
