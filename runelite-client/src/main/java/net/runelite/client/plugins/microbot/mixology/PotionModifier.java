package net.runelite.client.plugins.microbot.mixology;

public enum PotionModifier {
    HOMOGENOUS(AlchemyObject.AGITATOR, 21),
    CONCENTRATED(AlchemyObject.RETORT, 20),
    CRYSTALISED(AlchemyObject.ALEMBIC, 14);

    private static final PotionModifier[] TYPES = values();
    private final AlchemyObject alchemyObject;
    private final int quickActionExperience;

    private PotionModifier(AlchemyObject alchemyObject, int quickActionExperience) {
        this.alchemyObject = alchemyObject;
        this.quickActionExperience = quickActionExperience;
    }

    public static PotionModifier from(int potionModifierId) {
        return potionModifierId >= 0 && potionModifierId < TYPES.length ? TYPES[potionModifierId] : null;
    }

    public AlchemyObject alchemyObject() {
        return this.alchemyObject;
    }

    public int quickActionExperience() {
        return this.quickActionExperience;
    }
}