package net.runelite.client.plugins.microbot.fletching.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FletchingMode
{
    UNSTRUNG("Cutting", 0, "knife"),
    STRUNG("Stringing", 1, "bow string");

    private final String name;
    private final int mode;
    private final String itemName;

    @Override
    public String toString()
    {
        return name;
    }
}