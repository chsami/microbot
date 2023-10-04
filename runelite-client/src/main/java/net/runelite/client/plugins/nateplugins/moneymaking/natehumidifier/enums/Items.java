package net.runelite.client.plugins.nateplugins.moneymaking.natehumidifier.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Items {
    JUG("jug", "jug of water"),
    CLAY("clay", "soft clay");

    private final String name;
    private final String finished;

    @Override
    public String toString() {
        return name;
    }
    public String getFinished(){
        return finished;
    }
}
