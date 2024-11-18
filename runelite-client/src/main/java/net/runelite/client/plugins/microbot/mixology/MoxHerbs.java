package net.runelite.client.plugins.microbot.mixology;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MoxHerbs {
    GUAM("guam leaf"),
    Marrentill("marrentill"),
    Tarromin("tarromin"),
    Harralander("harralander");

    private final String itemName;

    @Override
    public String toString() {
        return itemName;
    }
}
