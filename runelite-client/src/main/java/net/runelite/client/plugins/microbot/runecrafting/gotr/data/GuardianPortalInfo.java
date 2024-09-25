package net.runelite.client.plugins.microbot.runecrafting.gotr.data;


import lombok.Getter;
import net.runelite.api.QuestState;

public class GuardianPortalInfo {
    @Getter
    private String name;
    @Getter
    private int requiredLevel;
    private int runeId;
    private int talismanId;
    @Getter
    private int spriteId;
    private RuneType runeType;
    private CellType cellType;

    @Getter
    private QuestState questState;

    public GuardianPortalInfo(String name, int requiredLevel, int runeId, int talismanId, int spriteId, RuneType runeType, CellType cellType, QuestState questState) {
        this.name = name;
        this.requiredLevel = requiredLevel;
        this.runeId = runeId;
        this.talismanId = talismanId;
        this.spriteId = spriteId;
        this.runeType = runeType;
        this.cellType = cellType;
        this.questState = questState;
    }
}