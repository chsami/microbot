package net.runelite.client.plugins.microbot.runecrafting.gotr;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GotrState {
    ENTER_GAME,
    WAITING,
    MINE_LARGE_GUARDIAN_REMAINS,
    LEAVING_LARGE_MINE,
    ENTER_ALTAR,
    CRAFTING_RUNES,
    LEAVING_ALTAR,
    POWERING_UP,
    CRAFT_GUARDIAN_ESSENCE;
}
