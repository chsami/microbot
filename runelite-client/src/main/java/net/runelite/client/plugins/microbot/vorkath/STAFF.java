package net.runelite.client.plugins.microbot.vorkath;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum STAFF {
    CAST("Cast");

    private final String staffName;

    @Override
    public String toString() {
        return staffName;
    }

}
