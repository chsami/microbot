package net.runelite.client.plugins.microbot.bossassist.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PRAY_MODE {
    NONE ("No prayers"),
    VISUAL("Visual"),
    AUTO("Auto pray"),
    FLICK("Flicking");


    private final String action;

    @Override
    public String toString() {
        return action;
    }
}
