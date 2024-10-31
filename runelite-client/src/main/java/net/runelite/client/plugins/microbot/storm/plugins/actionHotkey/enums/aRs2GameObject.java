package net.runelite.client.plugins.microbot.storm.plugins.actionHotkey.enums;

public enum aRs2GameObject implements Actionable {
    OBJ_INTERACT("objinteract");

    private final String actions;
    aRs2GameObject(String actions) {
        this.actions = actions;
    }

    @Override
    public String getAction() {
        return actions;
    }
}
