package net.runelite.client.plugins.microbot.bossassist;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BOSS {
    NONE("None"),
    SCURRIUS("Scurrius");

    private final String bossName;

    @Override
    public String toString() {
        return bossName;
    }
}
