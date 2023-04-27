package net.runelite.client.plugins.microbot.fletching.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FletchingMode
{
    UNSTRUNG("Cutting", 0, "knife", 1),
    STRUNG("Stringing", 1, "bow string", 14);

    private final String name;
    private final int mode;
    private final String itemName;
    private final int amount;

    @Override
    public String toString()
    {
        return name;
    }
}