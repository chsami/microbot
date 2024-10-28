package net.runelite.client.plugins.microbot.storm.plugins.actionHotkey.enums;

public enum Conditionals implements Actionable{
    HAS_ITEM("hasItem"),
    NONE("none");


    private final String conditionals;
    Conditionals(String conditionals) {
        this.conditionals = conditionals;
    }

    @Override
    public String getAction() {
        return conditionals;
    }
}
