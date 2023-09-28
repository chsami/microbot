package net.runelite.client.plugins.nateplugins.skilling.nateminer.nateminer.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Rocks {
    TIN("tin rocks", 1),
    IRON("iron rocks", 15),
    COAL("coal rocks", 30);

    private final String name;
    private final int miningLevel;

    @Override
    public String toString() {
        return name;
    }
}
