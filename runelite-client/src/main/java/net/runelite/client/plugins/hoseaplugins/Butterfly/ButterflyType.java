package net.runelite.client.plugins.hoseaplugins.Butterfly;

import lombok.Getter;

@Getter
public enum ButterflyType {

    RUBY_HARVEST("Ruby harvest"),
    SAPPHIRE_GLACIALIS("Sapphire glacialis"),
    SNOWY_KNIGHT("Snowy knight"),
    BLACK_WARLOCK("Black warlock");

    private final String name;

    ButterflyType(String name) {
        this.name = name;
    }


}
