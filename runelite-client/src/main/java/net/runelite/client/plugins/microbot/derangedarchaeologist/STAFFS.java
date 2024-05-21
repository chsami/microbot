package net.runelite.client.plugins.microbot.derangedarchaeologist;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum STAFFS {
    IBANS("Cast");


    private final String staffName;

    @Override
    public String toString() {
        return staffName;
    }
}
