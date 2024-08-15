package net.runelite.client.plugins.microbot.GeoffPlugins.lunarplankmake.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Logs {

    LOGS("Logs", "Plank"),
    OAK_LOGS("Oak logs", "Oak plank"),
    TEAK_LOGS("Teak logs", "Teak plank"),
    MAHOGANY_LOGS("Mahogany logs", "Mahogany plank");

    private final String name;
    @Getter
    private final String finished;

    @Override
    public String toString() {
        return name;
    }
}
