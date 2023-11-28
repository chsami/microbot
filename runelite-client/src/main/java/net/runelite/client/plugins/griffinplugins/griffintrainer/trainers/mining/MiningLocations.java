package net.runelite.client.plugins.griffinplugins.griffintrainer.trainers.mining;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MiningLocations {
    VARROCK_EAST("Varrock East"),
    VARROCK_WEST("Varrock West");

    public final String name;

    @Override
    public String toString() {
        return name;
    }
}


