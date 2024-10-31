package net.runelite.client.plugins.microbot.storm.plugins.actionHotkey.enums;

public enum aRs2Magic implements Actionable {
    ALCH("alch");

    private final String actions;
    aRs2Magic(String actions) {
        this.actions = actions;
    }

    @Override
    public String getAction() {
        return actions;
    }
}
