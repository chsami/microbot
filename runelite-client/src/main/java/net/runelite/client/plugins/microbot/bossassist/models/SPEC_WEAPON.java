package net.runelite.client.plugins.microbot.bossassist.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SPEC_WEAPON {
    NONE("No spec", 1001),
    DRAGON_DAGGER ("Dragon dagger", 250),
    BANDOS_GODSWORD("Bandos godsword", 500);

    private final String name;

    private final int specEnergy;


    public int GetNeededEnergy () {return  specEnergy;}
    @Override
    public String toString() {
        return name;
    }
}
