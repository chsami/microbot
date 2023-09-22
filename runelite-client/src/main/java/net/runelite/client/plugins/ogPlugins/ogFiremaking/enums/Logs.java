package net.runelite.client.plugins.ogPlugins.ogFiremaking.enums;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;

@Getter
@RequiredArgsConstructor
public enum Logs {
    NORMAL("Normal logs",1, ItemID.LOGS),
    RED_LOGS("Red logs logs",1, ItemID.REDWOOD_LOGS),
    OAK("Oak logs",15, ItemID.OAK_LOGS),
    WILLOW("Willow logs",30, ItemID.WILLOW_LOGS),
    TEAK("Teak logs",35, ItemID.TEAK_LOGS),
    ARCTIC_PINE("Arctic pine logs",42, ItemID.ARCTIC_PINE_LOGS),
    MAPLE("Maple logs",45, ItemID.MAPLE_LOGS),
    MAHOGANY("Mahogany logs",50, ItemID.MAHOGANY_LOGS),
    YEW("Yew logs",60, ItemID.YEW_LOGS),
    MAGIC("Magic logs",75, ItemID.MAGIC_LOGS),
    REDWOOD("Redwood logs",90, ItemID.REDWOOD_LOGS);

    private final String name;
    private final Integer levelRequired;
    private final Integer itemID;

    public String getName() {return name;}
    public Integer getLevelRequired() {return levelRequired;}
    public Integer getItemID() {return itemID;}
}
