package net.runelite.client.plugins.microbot.playerassist.model;

import net.runelite.api.NPC;
import net.runelite.client.plugins.microbot.playerassist.enums.AttackStyle;

public class Monster {
    public int id;
    public int attackSpeed;
    public int attackAnimation;

    public NPC npc;
    public int adjustableAttackSpeed;

    public AttackStyle attackStyle;

    public boolean delete;


    public Monster(int id, int attackSpeed, int attackAnimation, AttackStyle attackStyle) {
        this.id = id;
        this.attackSpeed = attackSpeed;
        this.adjustableAttackSpeed = attackSpeed;
        this.attackAnimation = attackAnimation;
        this.attackStyle = attackStyle;
    }
}
