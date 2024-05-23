package net.runelite.client.plugins.microbot.bossassist.models;

import net.runelite.api.NPC;
import net.runelite.client.plugins.microbot.util.npc.Rs2NpcManager;
import net.runelite.client.plugins.microbot.util.npc.Rs2NpcStats;
public class BossMonster {
    public int id;
    public int attackAnimMelee;
    public int attackAnimRange;
    public int attackAnimMage;
    public NPC npc;
    public Rs2NpcStats rs2NpcStats;
    public boolean delete;
    public int lastAttack = 0;

    // Mostly used for bosses that dont require multiple prayer uses
    public BossMonster(int id) {
        this.id = id;
    }

    // Constructor with ID and Melee attack animation
    public BossMonster(int id, int attackAnimMelee) {
        this.id = id;
        this.attackAnimMelee = attackAnimMelee;
    }

    // Constructor with NPC and Rs2NpcStats
    public BossMonster(NPC npc, Rs2NpcStats rs2NpcStats) {
        this.npc = npc;
        this.rs2NpcStats = rs2NpcStats;
        this.lastAttack = Rs2NpcManager.getAttackSpeed(npc.getId());
    }

    // Constructor with ID, Melee, and Range attack animations
    public BossMonster(int id, int attackAnimMelee, int attackAnimRange) {
        this.id = id;
        this.attackAnimMelee = attackAnimMelee;
        this.attackAnimRange = attackAnimRange;
    }

    // Constructor with ID, Melee, Range, and Mage attack animations
    public BossMonster(int id, int attackAnimMelee, int attackAnimRange, int attackAnimMage) {
        this.id = id;
        this.attackAnimMelee = attackAnimMelee;
        this.attackAnimRange = attackAnimRange;
        this.attackAnimMage = attackAnimMage;
    }

    // Constructor with NPC, Rs2NpcStats, and Melee attack animation
    public BossMonster(NPC npc, Rs2NpcStats rs2NpcStats, int attackAnimMelee) {
        this.npc = npc;
        this.rs2NpcStats = rs2NpcStats;
        this.attackAnimMelee = attackAnimMelee;
        this.lastAttack = Rs2NpcManager.getAttackSpeed(npc.getId());
    }

    // Constructor with NPC, Rs2NpcStats, Melee, and Range attack animations
    public BossMonster(NPC npc, Rs2NpcStats rs2NpcStats, int attackAnimMelee, int attackAnimRange) {
        this.npc = npc;
        this.rs2NpcStats = rs2NpcStats;
        this.attackAnimMelee = attackAnimMelee;
        this.attackAnimRange = attackAnimRange;
        this.lastAttack = Rs2NpcManager.getAttackSpeed(npc.getId());
    }

    // Constructor with NPC, Rs2NpcStats, Melee, Range, and Mage attack animations
    public BossMonster(NPC npc, Rs2NpcStats rs2NpcStats, int attackAnimMelee, int attackAnimRange, int attackAnimMage) {
        this.npc = npc;
        this.rs2NpcStats = rs2NpcStats;
        this.attackAnimMelee = attackAnimMelee;
        this.attackAnimRange = attackAnimRange;
        this.attackAnimMage = attackAnimMage;
        this.lastAttack = Rs2NpcManager.getAttackSpeed(npc.getId());
    }
}
