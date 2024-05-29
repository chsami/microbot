package net.runelite.client.plugins.microbot.barrows.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PRAYSTYLE {
    OFF ("No prayer needed"),
    MAGE("Pray mage"),
    RANGED("Pray ranged"),
    MELEE("Pray melee");

    private final String action;

    @Override
    public String toString() {
        return action;
    }
}
