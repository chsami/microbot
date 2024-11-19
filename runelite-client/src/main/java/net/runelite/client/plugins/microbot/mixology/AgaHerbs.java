package net.runelite.client.plugins.microbot.mixology;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
public enum AgaHerbs {
    Irit("irit leaf"),
    Cadantine("cadantine"),
    Lantadyme("Lantadyme"),
    Dwarf_Weed("dwarf weed"),
    Torstol("torstol");

    private final String itemName;

    @Override
    public String toString() {
        return itemName;
    }
}
