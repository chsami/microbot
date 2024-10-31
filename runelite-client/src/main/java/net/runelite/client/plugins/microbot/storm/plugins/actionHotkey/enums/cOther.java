package net.runelite.client.plugins.microbot.storm.plugins.actionHotkey.enums;

public enum cOther implements Actionable{
    NONE("none");


    private final String conditionals;
    cOther(String conditionals) {
        this.conditionals = conditionals;
    }

    @Override
    public String getAction() {
        return conditionals;
    }
}
