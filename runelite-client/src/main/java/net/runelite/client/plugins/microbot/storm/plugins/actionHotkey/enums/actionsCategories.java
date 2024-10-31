package net.runelite.client.plugins.microbot.storm.plugins.actionHotkey.enums;

public enum actionsCategories implements Actionable {
    RS2NPC("Rs2Npc"),
    RS2PLAYER("Rs2Player"),
    RS2INVENTORY("Rs2Inventory"),
    RS2WALKER("Rs2Walker"),
    RS2GAMEOBJECT("Rs2GameObject"),
    RS2WIDGET("Rs2Widget"),
    RS2BANK("Rs2Bank"),
    RS2MAGIC("Rs2Magic"),
    OTHER("aOther");

    private final String actionscategories;
    actionsCategories(String actionscategories) {
        this.actionscategories = actionscategories;
    }

    @Override
    public String getAction() {
        return actionscategories;
    }
}