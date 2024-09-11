package net.runelite.client.plugins.microbot.mining.shootingstar.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.mining.shootingstar.enums.ShootingStarLocation;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldType;

@Data
@AllArgsConstructor
public class Star {
    private long calledAt;
    @SerializedName("estimatedEnd")
    private long endsAt;
    private int world;
    private World worldObject;
    private Object locationKey;
    private String rawLocation;
    private ShootingStarLocation shootingStarLocation;
    private int tier;
    private transient boolean selected;

    private int objectID;
    private int miningLevel;

    public boolean hasRequirements() {
        return this.hasLocationRequirements() && this.hasMiningLevel();
    }

    public boolean hasMiningLevel() {
        return Rs2Player.getSkillRequirement(Skill.MINING, this.miningLevel, true);
    }

    public boolean hasLocationRequirements() {
        return this.shootingStarLocation.hasRequirements();
    }

    public boolean isInWilderness() {
        return this.shootingStarLocation.isInWilderness();
    }

    public int getRequiredMiningLevel() {
        switch (this.tier) {
            case 1: return 10;
            case 2: return 20;
            case 3: return 30;
            case 4: return 40;
            case 5: return 50;
            case 6: return 60;
            case 7: return 70;
            case 8: return 80;
            case 9: return 90;
            default: return -1;
        }
    }

    public int getObjectIDBasedOnTier() {
        switch (this.tier) {
            case 1: return ObjectID.CRASHED_STAR_41229;
            case 2: return ObjectID.CRASHED_STAR_41228;
            case 3: return ObjectID.CRASHED_STAR_41227;
            case 4: return ObjectID.CRASHED_STAR_41226;
            case 5: return ObjectID.CRASHED_STAR_41225;
            case 6: return ObjectID.CRASHED_STAR_41224;
            case 7: return ObjectID.CRASHED_STAR_41223;
            case 8: return ObjectID.CRASHED_STAR_41021;
            case 9: return ObjectID.CRASHED_STAR;
            default: return -1;
        }
    }

    public int getTierBasedOnObjectID() {
        switch (this.objectID) {
            case ObjectID.CRASHED_STAR_41229: return 1;
            case ObjectID.CRASHED_STAR_41228: return 2;
            case ObjectID.CRASHED_STAR_41227: return 3;
            case ObjectID.CRASHED_STAR_41226: return 4;
            case ObjectID.CRASHED_STAR_41225: return 5;
            case ObjectID.CRASHED_STAR_41224: return 6;
            case ObjectID.CRASHED_STAR_41223: return 7;
            case ObjectID.CRASHED_STAR_41021: return 8;
            case ObjectID.CRASHED_STAR: return 9;
            default: return -1;
        }
    }

    public boolean isGameModeWorld() {
        return this.getWorldObject().getTypes().contains(WorldType.PVP) ||
                this.getWorldObject().getTypes().contains(WorldType.HIGH_RISK) ||
                this.getWorldObject().getTypes().contains(WorldType.BOUNTY) ||
                this.getWorldObject().getTypes().contains(WorldType.SKILL_TOTAL) ||
                this.getWorldObject().getTypes().contains(WorldType.LAST_MAN_STANDING) ||
                this.getWorldObject().getTypes().contains(WorldType.QUEST_SPEEDRUNNING) ||
                this.getWorldObject().getTypes().contains(WorldType.BETA_WORLD) ||
                this.getWorldObject().getTypes().contains(WorldType.DEADMAN) ||
                this.getWorldObject().getTypes().contains(WorldType.PVP_ARENA) ||
                this.getWorldObject().getTypes().contains(WorldType.TOURNAMENT) ||
                this.getWorldObject().getTypes().contains(WorldType.FRESH_START_WORLD);
    }

    public boolean isMemberWorld() {
        return !this.isGameModeWorld() && this.getWorldObject().getTypes().contains(WorldType.MEMBERS);
    }
    public boolean isF2PWorld() {
        return !this.isGameModeWorld() && !this.getWorldObject().getTypes().contains(WorldType.MEMBERS);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Star other = (Star) obj;
        return this.getWorld() == other.getWorld() && this.getShootingStarLocation().equals(other.getShootingStarLocation());
    }
}
