package net.runelite.client.plugins.microbot.storm.plugins.actionHotkey.enums;

public enum conditionsCategories implements Actionable {
    RS2INVENTORY("Rs2Inventory"),
    NONE("none");

    private final String conditionscategories;
    conditionsCategories(String conditionscategories) {
        this.conditionscategories = conditionscategories;
    }

    @Override
    public String getAction() {
        return conditionscategories;
    }
}