package net.runelite.client.plugins.microbot.barrows.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum STATE {
    IDLE,
    BANKING,
    ERROR,
    WALKING,
    CALCULATING,
    SEARCHING_GRAVE,
    DIGGING,

    LEAVING_CRYPT,

    FIGHTHING;

}
