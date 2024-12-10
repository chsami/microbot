package net.runelite.client.plugins.microbot.magic.orbcharger.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;


@Getter
@RequiredArgsConstructor
public enum Teleport {
    
    AMULET_OF_GLORY(BankLocation.EDGEVILLE, "^Amulet of glory\\(\\d\\)$"),
    RING_OF_DUELING(BankLocation.FEROX_ENCLAVE, "^Ring of dueling\\(\\d\\)$");
    
    private final BankLocation bankLocation;
    private final String regexPattern;
}
