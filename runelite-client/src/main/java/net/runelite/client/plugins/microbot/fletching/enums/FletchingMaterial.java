package net.runelite.client.plugins.microbot.fletching.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.Microbot;

@Getter
@RequiredArgsConstructor
public enum FletchingMaterial
{
    LOG(""),
    OAK("Oak"),
    WILLOW("Willow"),
    MAPLE("Maple"),
    YEW("Yew"),
    MAGIC("Magic"),
    REDWOOD("Redwood");

    private final String name;


    @Override
    public String toString()
    {
        return name;
    }
}
