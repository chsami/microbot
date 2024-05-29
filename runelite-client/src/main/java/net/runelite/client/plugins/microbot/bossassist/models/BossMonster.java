package net.runelite.client.plugins.microbot.bossassist.models;

import net.runelite.api.NPC;
import net.runelite.client.plugins.microbot.util.npc.Rs2NpcManager;
import net.runelite.client.plugins.microbot.util.npc.Rs2NpcStats;
public class BossMonster {
    public int id;
    public int[] attackAnimsMelee;
    public int[] attackAnimsRange;
    public int[] attackAnimsMage;

    public NPC npc;
    public boolean delete;
    // Mostly used for bosses that dont require multiple prayer uses
    public BossMonster(int id) {
        this.id = id;
    }

    public BossMonster(int id, int[] attackAnimsMelee) {
        this.id = id;
        this.attackAnimsMelee = attackAnimsMelee;
    }


    public BossMonster(int id, int[] attackAnimsMelee, int[] attackAnimsRange) {
        this.id = id;
        this.attackAnimsMelee = attackAnimsMelee;
        this.attackAnimsRange = attackAnimsRange;
    }


    public BossMonster(int id, int[] attackAnimsMelee, int[] attackAnimsRange, int[] attackAnimsMage) {
        this.id = id;
        this.attackAnimsMelee = attackAnimsMelee;
        this.attackAnimsRange = attackAnimsRange;
        this.attackAnimsMage = attackAnimsMage;
    }



}
