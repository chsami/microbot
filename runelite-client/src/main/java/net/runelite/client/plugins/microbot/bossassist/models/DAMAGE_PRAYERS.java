package net.runelite.client.plugins.microbot.bossassist.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DAMAGE_PRAYERS {
    NONE ("None"),
    RIGOUR("Rigour"),
    AUGURY("Augury"),
    PIETY("Piety");


    private final String prayer;

    @Override
    public String toString() {
        return prayer;
    }
}
