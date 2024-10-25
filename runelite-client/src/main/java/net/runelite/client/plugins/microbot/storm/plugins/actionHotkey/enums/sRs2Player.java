package net.runelite.client.plugins.microbot.storm.plugins.actionHotkey.enums;

public enum sRs2Player implements Actionable {
    LOGOUT("logout"),
    USE_FOOD("useFood");

    private final String actions;
    sRs2Player(String actions) {
        this.actions = actions;
    }

    @Override
    public String getAction() {
        return actions;
    }
}
