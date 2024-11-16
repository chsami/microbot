package net.runelite.client.plugins.microbot.mixology;

import com.google.common.collect.ImmutableMap;

import java.util.Arrays;
import java.util.Map;

public enum PotionType {
    MAMMOTH_MIGHT_MIX(30011, 1900, new PotionComponent[]{PotionComponent.MOX, PotionComponent.MOX, PotionComponent.MOX}),
    MYSTIC_MANA_AMALGAM(30012, 2150, new PotionComponent[]{PotionComponent.MOX, PotionComponent.MOX, PotionComponent.AGA}),
    MARLEYS_MOONLIGHT(30013, 2400, new PotionComponent[]{PotionComponent.MOX, PotionComponent.MOX, PotionComponent.LYE}),
    ALCO_AUGMENTATOR(30014, 1900, new PotionComponent[]{PotionComponent.AGA, PotionComponent.AGA, PotionComponent.AGA}),
    AZURE_AURA_MIX(30016, 2650, new PotionComponent[]{PotionComponent.AGA, PotionComponent.AGA, PotionComponent.MOX}),
    AQUALUX_AMALGAM(30015, 2900, new PotionComponent[]{PotionComponent.AGA, PotionComponent.LYE, PotionComponent.AGA}),
    LIPLACK_LIQUOR(30017, 1900, new PotionComponent[]{PotionComponent.LYE, PotionComponent.LYE, PotionComponent.LYE}),
    MEGALITE_LIQUID(30019, 3150, new PotionComponent[]{PotionComponent.MOX, PotionComponent.LYE, PotionComponent.LYE}),
    ANTI_LEECH_LOTION(30018, 3400, new PotionComponent[]{PotionComponent.AGA, PotionComponent.LYE, PotionComponent.LYE}),
    MIXALOT(30020, 3650, new PotionComponent[]{PotionComponent.MOX, PotionComponent.AGA, PotionComponent.LYE});

    public static final PotionType[] TYPES = values();
    private static final Map<Integer, PotionType> ITEM_MAP;
    private final int itemId;
    private final String recipe;
    private final String abbreviation;
    private final int experience;
    private final PotionComponent[] components;

    private PotionType(int itemId, int experience, PotionComponent... components) {
        this.itemId = itemId;
        this.recipe = colorizeRecipe(components);
        this.experience = experience;
        this.components = components;
        char var10001 = components[0].character();
        this.abbreviation = "" + var10001 + components[1].character() + components[2].character();
    }

    public static PotionType fromItemId(int itemId) {
        return (PotionType)ITEM_MAP.get(itemId);
    }

    public static PotionType fromIdx(int potionTypeId) {
        return potionTypeId >= 0 && potionTypeId < TYPES.length ? TYPES[potionTypeId] : null;
    }

    private static String colorizeRecipe(PotionComponent[] components) {
        if (components.length != 3) {
            throw new IllegalArgumentException("Invalid potion components: " + Arrays.toString(components));
        } else {
            String var10000 = colorizeRecipeComponent(components[0]);
            return var10000 + colorizeRecipeComponent(components[1]) + colorizeRecipeComponent(components[2]);
        }
    }

    private static String colorizeRecipeComponent(PotionComponent component) {
        String var10000 = component.color();
        return "<col=" + var10000 + ">" + component.character() + "</col>";
    }

    public int itemId() {
        return this.itemId;
    }

    public String recipe() {
        return this.recipe;
    }

    public int experience() {
        return this.experience;
    }

    public PotionComponent[] components() {
        return this.components;
    }

    public String abbreviation() {
        return this.abbreviation;
    }

    static {
        ImmutableMap.Builder<Integer, PotionType> builder = new ImmutableMap.Builder();
        PotionType[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            PotionType p = var1[var3];
            builder.put(p.itemId(), p);
        }

        ITEM_MAP = builder.build();
    }
}

