package net.runelite.client.plugins.microbot.bossassist;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BOSS {
    NONE("None"),
    DERANGED_ARCHEOLGIST("Deranged Archaeologist"),
    OBOR("Obor"),
    SCURRIUS("Scurrius");

    private final String bossName;

    @Override
    public String toString() {
        return bossName;
    }
}
