package net.runelite.client.plugins.microbot.tanner.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Location {
    AL_KHARID("Al-kharid");

    private final String name;

    @Override
    public String toString() {
        return name;
    }
}
