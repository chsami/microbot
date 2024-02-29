package net.runelite.client.plugins.microbot.vorkath;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Teleport {
    CONSTRUCTION_CAPE("Construction cape (t)", "teleport"),
    HOUSE_TAB("Teleport to house", "break"),
    VARROCK_TAB("Varrock teleport", "break");

    private final String itemName;
    private final String action;

    @Override
    public String toString()
    {
        return itemName;
    }
}
