package net.runelite.client.plugins.ogPlugins.ogPrayer.enums;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.NpcID;

@Getter
@RequiredArgsConstructor
public enum RestockMethod {


    //CRAFTING_GUILD(),
    //CASTLE_WARS(),
    //EDGEVILE_BANK(),
    //FEROX_ENCLAVE(),
    ELDER_CHAOS_DRUID(NpcID.ELDER_CHAOS_DRUID_7995, RestockType.NOTING, BankType.NPC),
    PHIALS(NpcID.PHIALS, RestockType.NOTING, BankType.NPC);

    public enum RestockType { NOTING , BANKING }
    public enum BankType { NPC , OBJECT }

    private final Integer ID;
    private final RestockType restockType;
    private final BankType bankType;

}

