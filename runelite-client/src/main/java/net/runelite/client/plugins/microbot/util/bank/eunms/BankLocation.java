package net.runelite.client.plugins.microbot.util.bank.eunms;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.coords.WorldPoint;

@Getter
@RequiredArgsConstructor
public enum BankLocation {
    AL_KHARID("AL-Kharid", new WorldPoint(3270, 3166, 0));

    private final String name;
    private final WorldPoint worldPoint;

    @Override
    public String toString() {
        return name;
    }
}
