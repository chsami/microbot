package net.runelite.client.plugins.microbot.storm.plugins.actionHotkey.enums;

public enum sRs2Npc implements Actionable {
    ATTACK("attack"),
    NPC_INTERACT("npcinteract");

    private final String actions;
    sRs2Npc(String actions) {
        this.actions = actions;
    }

    @Override
    public String getAction() {
        return actions;
    }
}
