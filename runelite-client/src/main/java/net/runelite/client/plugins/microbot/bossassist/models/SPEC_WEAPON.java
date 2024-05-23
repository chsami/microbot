package net.runelite.client.plugins.microbot.bossassist.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SPEC_WEAPON {
    NONE("No spec", 101),
    DRAGON_DAGGER ("Dragon dagger", 25),
    BANDOS_GODSWORD("Bandos godsword", 50);

    private final String name;

    private final int specEnergy;


    public int GetNeededEnergy () {return  specEnergy;}
    @Override
    public String toString() {
        return name;
    }
}
