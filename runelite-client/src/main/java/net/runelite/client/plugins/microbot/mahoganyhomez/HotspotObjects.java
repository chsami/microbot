package net.runelite.client.plugins.microbot.mahoganyhomez;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

public enum HotspotObjects
{
    // East Ardy
    JESS(new HotspotObject(40171, HotspotType.B2),
            new HotspotObject(40172, HotspotType.B2),
            new HotspotObject(40173, HotspotType.B2),
            new HotspotObject(40174, HotspotType.B2),
            new HotspotObject(40175, HotspotType.B3),
            new HotspotObject(40176, HotspotType.B3),
            new HotspotObject(40177, HotspotType.RP),
            new HotspotObject(40299, HotspotType.SB)),
    NOELLA(new HotspotObject(40156, HotspotType.B2),
            new HotspotObject(40157, HotspotType.B2),
            new HotspotObject(40158, HotspotType.RP),
            new HotspotObject(40159, HotspotType.RP),
            new HotspotObject(40160, HotspotType.B2),
            new HotspotObject(40161, HotspotType.B3),
            new HotspotObject(40162, HotspotType.B3),
            new HotspotObject(40163, HotspotType.RP)),
    ROSS(new HotspotObject(40164, HotspotType.SB),
            new HotspotObject(40165, HotspotType.B2),
            new HotspotObject(40166, HotspotType.B2),
            new HotspotObject(40167, HotspotType.B3),
            new HotspotObject(40168, HotspotType.RP),
            new HotspotObject(40169, HotspotType.B2),
            new HotspotObject(40170, HotspotType.RP)),

    // Falador
    LARRY(new HotspotObject(40297, HotspotType.SB),
            new HotspotObject(40095, HotspotType.B2),
            new HotspotObject(40096, HotspotType.B2),
            new HotspotObject(40097, HotspotType.B3),
            new HotspotObject(40298, HotspotType.RP),
            new HotspotObject(40098, HotspotType.B3),
            new HotspotObject(40099, HotspotType.RP)),
    NORMAN(new HotspotObject(40296, HotspotType.SB),
            new HotspotObject(40089, HotspotType.RP),
            new HotspotObject(40090, HotspotType.B3),
            new HotspotObject(40091, HotspotType.B3),
            new HotspotObject(40092, HotspotType.B2),
            new HotspotObject(40093, HotspotType.B2),
            new HotspotObject(40094, HotspotType.B2)),
    TAU(new HotspotObject(40083, HotspotType.SB),
            new HotspotObject(40084, HotspotType.B3),
            new HotspotObject(40085, HotspotType.B3),
            new HotspotObject(40086, HotspotType.B2),
            new HotspotObject(40087, HotspotType.B2),
            new HotspotObject(40088, HotspotType.B2),
            new HotspotObject(40295, HotspotType.RP)),

    // Hosidius
    BARBARA(new HotspotObject(40011, HotspotType.RP),
            new HotspotObject(40293, HotspotType.SB),
            new HotspotObject(40012, HotspotType.B3),
            new HotspotObject(40294, HotspotType.B2),
            new HotspotObject(40013, HotspotType.B2),
            new HotspotObject(40014, HotspotType.B1),
            new HotspotObject(40015, HotspotType.B1)),
    LEELA(new HotspotObject(40007, HotspotType.B2),
            new HotspotObject(40008, HotspotType.B2),
            new HotspotObject(40290, HotspotType.SB),
            new HotspotObject(40291, HotspotType.B3),
            new HotspotObject(40009, HotspotType.B3),
            new HotspotObject(40010, HotspotType.RP),
            new HotspotObject(40292, HotspotType.B2)),
    MARIAH(new HotspotObject(40002, HotspotType.B3),
            new HotspotObject(40287, HotspotType.SB),
            new HotspotObject(40003, HotspotType.B2),
            new HotspotObject(40288, HotspotType.B2),
            new HotspotObject(40004, HotspotType.B2),
            new HotspotObject(40005, HotspotType.B2),
            new HotspotObject(40006, HotspotType.B2),
            new HotspotObject(40289, HotspotType.RP)),


    // Varrock
    BOB(new HotspotObject(39981, HotspotType.B4),
            new HotspotObject(39982, HotspotType.RP),
            new HotspotObject(39983, HotspotType.B2),
            new HotspotObject(39984, HotspotType.B2),
            new HotspotObject(39985, HotspotType.B2),
            new HotspotObject(39986, HotspotType.B2),
            new HotspotObject(39987, HotspotType.B2),
            new HotspotObject(39988, HotspotType.B2)),
    JEFF(new HotspotObject(39989, HotspotType.B3),
            new HotspotObject(39990, HotspotType.B2),
            new HotspotObject(39991, HotspotType.B2),
            new HotspotObject(39992, HotspotType.B3),
            new HotspotObject(39993, HotspotType.B2),
            new HotspotObject(39994, HotspotType.B2),
            new HotspotObject(39995, HotspotType.RP),
            new HotspotObject(39996, HotspotType.B1)),
    SARAH(new HotspotObject(39997, HotspotType.B3),
            new HotspotObject(39998, HotspotType.B2),
            new HotspotObject(39999, HotspotType.B2),
            new HotspotObject(40000, HotspotType.B2),
            new HotspotObject(40286, HotspotType.SB),
            new HotspotObject(40001, HotspotType.B2))
    ;

    public final HotspotObject[] objects;

    HotspotObjects(HotspotObject... objects)
    {
        this.objects = objects;
    }

    RequiredMaterials getRequiredMaterialsForVarbs(Set<Integer> repairableVarbs)
    {
        final RequiredMaterials required = new RequiredMaterials(0, 0, 0, 0);

        final int startingVarb = Hotspot.MAHOGANY_HOMES_HOTSPOT_1.getVarb();
        for (int i = 0; i < this.objects.length; i++)
        {
            // hotspotObjects are added in order where it's index in the array is the offset from the starting varb
            if (!repairableVarbs.contains(startingVarb + i))
            {
                continue;
            }

            final HotspotType type = this.objects[i].getType();
            switch (type.getMaterial())
            {
                case PLANK:
                    required.setMinPlanks(required.MinPlanks + type.getNumOfMaterial());
                    break;
                case STEEL_BAR:
                    required.setMinSteelBars(required.MinSteelBars + type.getNumOfMaterial());
                    break;
            }
        }

        return required;
    }

    @AllArgsConstructor
    @Getter
    public static class HotspotObject
    {
        private final int objectId;
        private final HotspotType type;
    }

    public enum MaterialType
    {
        PLANK,
        STEEL_BAR
    }

    @AllArgsConstructor
    @Getter
    public enum HotspotType
    {
        //Remove & Build Furniture (1 plank)
        B1(MaterialType.PLANK, 1),
        //Remove & Build Furniture (2 plank)
        B2(MaterialType.PLANK, 2),
        //Remove & Build Furniture (3 plank)
        B3(MaterialType.PLANK, 3),
        //Remove & Build Furniture (4 plank)
        B4(MaterialType.PLANK, 4),
        //Repair Furniture (1 plank)
        RP(MaterialType.PLANK, 1),
        //Repair Furniture (1 steel bar)
        SB(MaterialType.STEEL_BAR, 1);

        private final MaterialType material;
        private final int numOfMaterial;
    }
}
