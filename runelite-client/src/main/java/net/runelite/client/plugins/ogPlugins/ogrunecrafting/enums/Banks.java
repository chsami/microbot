package net.runelite.client.plugins.ogPlugins.ogrunecrafting.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Banks {

    CRAFTING_GUILD("Bank chest",
            14886
    );

    private final String name;
    private final int bankID;

    public String getName() {
        return name;
    }

    public int getBankID() {
        return bankID;
    }
}
