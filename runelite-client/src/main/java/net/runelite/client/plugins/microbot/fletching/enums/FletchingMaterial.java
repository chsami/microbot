package net.runelite.client.plugins.microbot.fletching.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FletchingMaterial
{
    LOG("Log"),
    OAK("Oak logs"),
    WILLOW("Willow logs"),
    MAPLE("Maple logs"),
    YEW("Yew logs"),
    MAGIC("Magic logs"),
    REDWOOD("Redwood logs");

    private final String name;

    @Override
    public String toString()
    {
        return name;
    }
}
