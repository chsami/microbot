package net.runelite.client.plugins.microbot.derangedarchaeologist;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.plugins.microbot.playerassist.combat.FlickerScript;
@Getter
@RequiredArgsConstructor
public enum PRAY_MODE {
    NONE ("No prayers"),
    AUTO("Auto pray"),
    FLICK("Flicking");


    private final String action;

    @Override
    public String toString() {
        return action;
    }
}
