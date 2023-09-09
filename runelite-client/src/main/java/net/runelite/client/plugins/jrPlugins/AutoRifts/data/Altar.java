package net.runelite.client.plugins.jrPlugins.AutoRifts.data;

import lombok.Getter;

@Getter
public enum Altar {
    AIR(43701, Constants.AIR_SPRITE),
    MIND(43705, Constants.MIND_SPRITE),
    WATER(43702, Constants.WATER_SPRITE),
    EARTH(43703, Constants.EARTH_SPRITE),
    FIRE(43704, Constants.FIRE_SPRITE),
    BODY(43709, Constants.BODY_SPRITE),
    COSMIC(43710, Constants.COSMIC_SPRITE),
    CHAOS(43706, Constants.CHAOS_SPRITE),
    NATURE(43711, Constants.NATURE_SPRITE),
    LAW(43712, Constants.LAW_SPRITE),
    DEATH(43707, Constants.DEATH_SPRITE),
    BLOOD(43708, Constants.BLOOD_SPRITE);

    final int id;
    final int spriteId;

    Altar(int id, int spriteId) {
        this.id = id;
        this.spriteId = spriteId;
    }

    public static Altar getAltarBySpriteId(int spriteId) {
        for (Altar altar : Altar.values()) {
            if (altar.getSpriteId() == spriteId) {
                return altar;
            }
        }
        return null;
    }

    public static Altar getAltarByObjectId(int id) {
        for (Altar altar : Altar.values()) {
            if (altar.getId() == id) {
                return altar;
            }
        }
        return null;
    }
}
