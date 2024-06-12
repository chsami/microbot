package net.runelite.client.plugins.hoseaplugins.strategyexample;

import lombok.Getter;

public enum Bar {
    BRONZE("Bronze bar"),
    IRON("Iron bar"),
    STEEL("Steel bar"),
    MITHRIL("Mithril bar"),
    ADAMANTITE("Adamantite bar"),
    RUNITE("Runite bar");


    @Getter
    private String name;

    Bar(String name) {
        this.name = name;
    }

}
