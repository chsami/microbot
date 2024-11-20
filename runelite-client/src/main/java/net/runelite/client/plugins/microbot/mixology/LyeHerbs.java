package net.runelite.client.plugins.microbot.mixology;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
public enum LyeHerbs {
    Ranarr("ranarr weed"),
    Toadflax("toadflax"),
    Avantoe("avantoe"),
    Kwuarm("kwuarm"),
    Snapdragon("snapdragon");

    private final String itemName;

    @Override
    public String toString() {
        return itemName;
    }
}
