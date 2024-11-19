package net.runelite.client.plugins.microbot.nateplugins.moneymaking.natehumidifier.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Items {
    JUG("jug", "Jug of water"),
    CLAY("clay", "Soft clay");

    private final String name;
    @Getter
    private final String finished;

    @Override
    public String toString() {
        return name;
    }
}
