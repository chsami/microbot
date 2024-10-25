package net.runelite.client.plugins.microbot.storm.plugins.actionHotkey.enums;

public enum sRs2GameObject implements Actionable {
    OBJ_INTERACT("objinteract");

    private final String actions;
    sRs2GameObject(String actions) {
        this.actions = actions;
    }

    @Override
    public String getAction() {
        return actions;
    }
}
