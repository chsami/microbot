package net.runelite.client.plugins.microbot.storm.plugins.actionHotkey.enums;

public enum aRs2Bank implements Actionable {
    OPEN_BANK("openBank"),
    WITHDRAW_ALL("withdrawAll"),
    DEPOSIT_ALL("depositAll"),
    WITHDRAW_ONE("withdrawOne");

    private final String actions;
    aRs2Bank(String actions) {
        this.actions = actions;
    }

    @Override
    public String getAction() {
        return actions;
    }
}
