package net.runelite.client.plugins.ogPlugins.ogPrayer.enums;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;

@Getter
@RequiredArgsConstructor
public enum Bones {
    Bones("Bones",ItemID.BONES,527),
    Wolf_bones("Wolf bones",ItemID.WOLF_BONES,2859),
    Burnt_bones("Burnt bones",ItemID.BURNT_BONES,529),
    Monkey_bones("Monkey bones",ItemID.MONKEY_BONES,3184),
    Bat_bones("Bat bones",ItemID.BAT_BONES,531),
    Big_bones("Big bones",ItemID.BIG_BONES,533),
    Jogre_bones("Jogre bones",ItemID.JOGRE_BONES,3126),
    Zogre_bones("Zogre bones",ItemID.ZOGRE_BONES,4813),
    Shaikahan_bones("Shaikahan bones",ItemID.SHAIKAHAN_BONES,3124),
    Babydragon_bones("Babydragon bones",ItemID.BABYDRAGON_BONES,535),
    Wyrm_bones("Wyrm bones",ItemID.WYRM_BONES,22781),
    Wyvern_bones("Wyvern bones",ItemID.WYVERN_BONES,6813),
    Dragon_bones("Dragon bones",ItemID.DRAGON_BONES,537),
    Drake_bones("Drake bones",ItemID.DRAKE_BONES,22784),
    Fayrg_bones("Fayrg bones",ItemID.FAYRG_BONES,4830),
    Lava_dragon_bones("Lava dragon bones",ItemID.LAVA_DRAGON_BONES,1194),
    Raurg_bones("Raurg bones",ItemID.RAURG_BONES,4833),
    Hydra_bones("Hydra bones",ItemID.HYDRA_BONES,22787),
    Dagannoth_bones("Dagannoth bones",ItemID.DAGANNOTH_BONES,6730),
    Ourg_bones("Ourg bones",ItemID.OURG_BONES, 4835),
    Superior_dragon_bones("Superior dragon bones",ItemID.SUPERIOR_DRAGON_BONES,22125);
    private final String name;
    private final int itemID;
    private final int notedID;

    public String getName() {
        return name;
    }
    public int getItemID() {
        return itemID;
    }
    public int getNotedID() {
        return notedID;
    }
}
