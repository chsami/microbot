package net.runelite.client.plugins.griffinplugins.transporthelper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransportTypes {
    DOOR("Door", "door"),
    STAIR("Stair", "stair");

    public final String name;
    public final String value;

    @Override
    public String toString() {
        return name;
    }
}
