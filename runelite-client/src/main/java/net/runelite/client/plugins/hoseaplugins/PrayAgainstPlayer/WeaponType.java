package net.runelite.client.plugins.hoseaplugins.PrayAgainstPlayer;

import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.Player;
import net.runelite.api.kit.KitType;

public enum WeaponType {

    WEAPON_MELEE,
    WEAPON_RANGED,
    WEAPON_MAGIC,
    WEAPON_UNKNOWN;

    /**
     * im fully aware this could of been done better!!!
     *
     * @param client
     * @param attacker
     * @return
     */
    public static WeaponType checkWeaponOnPlayer(Client client, Player attacker)
    {
        int itemId = attacker.getPlayerComposition().getEquipmentId(KitType.WEAPON);
        ItemComposition itemComposition = client.getItemDefinition(itemId);
        String weaponNameGivenLowerCase = itemComposition.getName().toLowerCase();

        if (itemId == -1)
        {
            return WEAPON_MELEE;
        }
        if (weaponNameGivenLowerCase.toLowerCase().contains("null"))
        {
            return WEAPON_MELEE;
        }

        for (String meleeWeaponName : meleeWeaponNames)
        {
            if (weaponNameGivenLowerCase.contains(meleeWeaponName) && !weaponNameGivenLowerCase.contains("thrownaxe"))
            {
                return WEAPON_MELEE;
            }
        }

        for (String rangedWeaponName : rangedWeaponNames)
        {
            if (weaponNameGivenLowerCase.contains(rangedWeaponName))
            {
                return WEAPON_RANGED;
            }
        }

        for (String magicWeaponName : magicWeaponNames)
        {
            if (weaponNameGivenLowerCase.contains(magicWeaponName))
            {
                return WEAPON_MAGIC;
            }
        }

        return WEAPON_UNKNOWN;

    }

    private static final String[] meleeWeaponNames = {
            "sword",
            "scimitar",
            "dagger",
            "spear",
            "mace",
            "axe",
            "whip",
            "tentacle",
            "-ket-",
            "-xil-",
            "warhammer",
            "halberd",
            "claws",
            "hasta",
            "scythe",
            "maul",
            "anchor",
            "sabre",
            "excalibur",
            "machete",
            "dragon hunter lance",
            "event rpg",
            "silverlight",
            "darklight",
            "arclight",
            "flail",
            "granite hammer",
            "rapier",
            "bulwark",
            "osmumten's fang"
    };

    private static final String[] rangedWeaponNames = {
            "bow",
            "blowpipe",
            "xil-ul",
            "knife",
            "dart",
            "thrownaxe",
            "chinchompa",
            "ballista"
    };

    private static final String[] magicWeaponNames = {
            "staff",
            "trident",
            "wand",
            "dawnbringer",
            "voidwaker",
            "sceptre"
    };

}
