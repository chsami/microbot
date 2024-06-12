package net.runelite.client.plugins.hoseaplugins.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Prayer;

@Getter
@RequiredArgsConstructor
public enum WeaponType
{
    MAGIC(Prayer.PROTECT_FROM_MAGIC, Prayer.AUGURY), MELEE(Prayer.PROTECT_FROM_MELEE, Prayer.PIETY), RANGED(Prayer.PROTECT_FROM_MISSILES, Prayer.RIGOUR), OTHER(null, null);


    final Prayer protectionPrayer;
    final Prayer offensivePrayer;
}
