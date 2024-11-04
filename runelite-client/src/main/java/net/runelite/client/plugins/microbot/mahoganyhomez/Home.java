package net.runelite.client.plugins.microbot.mahoganyhomez;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import org.apache.commons.text.WordUtils;

@Getter
enum Home
{
    // area is based on bounds of house not area at which stuff loads in for the homes
// Ardy
    JESS(new WorldArea(2611, 3290, 14, 7, 0), "Upstairs of the building south of the church in East Ardougne",
            NpcID.JESS, new WorldPoint(2621, 3292, 0), RequiredMaterialsByTier.JESS, HotspotObjects.JESS, 17026, 16685),
    NOELLA(new WorldArea(2652, 3317, 15, 8, 0), "North of East Ardougne market",
            NpcID.NOELLA, new WorldPoint(2659, 3322, 0), RequiredMaterialsByTier.NOELLA, HotspotObjects.NOELLA, 17026, 16685, 15645, 15648),
    ROSS(new WorldArea(2609, 3313, 11, 9, 0), "North of the church in East Ardougne",
            NpcID.ROSS, new WorldPoint(2613, 3316, 0), RequiredMaterialsByTier.ROSS, HotspotObjects.ROSS, 16683, 16679),

    // Falador
    LARRY(new WorldArea(3033, 3360, 10, 9, 0), "North of the fountain in Falador",
            NpcID.LARRY_10418, new WorldPoint(3038, 3364, 0), RequiredMaterialsByTier.LARRY, HotspotObjects.LARRY, 24075, 24076),
    NORMAN(new WorldArea(3034, 3341, 8, 8, 0), "South of the fountain in Falador",
            NpcID.NORMAN, new WorldPoint(3038, 3344, 0), RequiredMaterialsByTier.NORMAN, HotspotObjects.NORMAN, 24082, 24085),
    TAU(new WorldArea(3043, 3340, 10, 11, 0), "South east of the fountain in Falador",
            NpcID.TAU, new WorldPoint(3047, 3345, 0), RequiredMaterialsByTier.TAU, HotspotObjects.TAU),

    // Hosidius
    BARBARA(new WorldArea(1746, 3531, 10, 11, 0), "South of Hosidius, near the mill",
            NpcID.BARBARA, new WorldPoint(1750, 3534, 0), RequiredMaterialsByTier.BARBARA, HotspotObjects.BARBARA),
    LEELA(new WorldArea(1781, 3589, 9, 8, 0), "East of the town market in Hosidius",
            NpcID.LEELA_10423, new WorldPoint(1785, 3592, 0), RequiredMaterialsByTier.LEELA, HotspotObjects.LEELA, 11794, 11802),
    MARIAH(new WorldArea(1762, 3618, 10, 7, 0), "West of the estate agents in Hosidius",
            NpcID.MARIAH, new WorldPoint(1766, 3621, 0), RequiredMaterialsByTier.MARIAH, HotspotObjects.MARIAH, 11794, 11802),

    // Varrock
    BOB(new WorldArea(3234, 3482, 10, 10, 0), "North-east Varrock, opposite the church",
            NpcID.BOB_10414, new WorldPoint(3238, 3486, 0), RequiredMaterialsByTier.BOB, HotspotObjects.BOB, 11797, 11799),
    JEFF(new WorldArea(3235, 3445, 10, 12, 0), "Middle of Varrock, west of the museum",
            NpcID.JEFF_10415, new WorldPoint(3239, 3450, 0), RequiredMaterialsByTier.JEFF, HotspotObjects.JEFF, 11789, 11793),
    SARAH(new WorldArea(3232, 3381, 8, 7, 0), "Along the south wall of Varrock",
            NpcID.SARAH_10416, new WorldPoint(3235, 3384, 0), RequiredMaterialsByTier.SARAH, HotspotObjects.SARAH);


    private final WorldArea area;
    private final String hint;
    private final int npcId;
    private final WorldPoint location;
    private final Integer[] ladders;

    private final RequiredMaterialsByTier requiredMaterialsByTier;
    private final HotspotObjects hotspotObjects;

    Home(final WorldArea area, final String hint, final int npcId, final WorldPoint location,
         final RequiredMaterialsByTier requiredMaterials, final HotspotObjects hotspotObjects, final Integer... ladders)
    {
        this.area = area;
        this.hint = hint;
        this.npcId = npcId;
        this.location = location;
        this.ladders = ladders;
        this.hotspotObjects = hotspotObjects;
        this.requiredMaterialsByTier = requiredMaterials;
    }

    String getName()
    {
        return WordUtils.capitalize(name().toLowerCase());
    }

    String getRequiredPlanksFormated(int tier)
    {
        RequiredMaterials requiredMaterials = this.requiredMaterialsByTier.getByTier(tier);
        if (requiredMaterials == null)
        {
            return null;
        }

        if (requiredMaterials.MinPlanks == requiredMaterials.MaxPlanks)
        {
            return String.format("%d planks", requiredMaterials.MinPlanks);
        }

        return String.format("%d - %d planks", requiredMaterials.MinPlanks, requiredMaterials.MaxPlanks);
    }

    int getRequiredPlanks(int tier)
    {
        RequiredMaterials requiredMaterials = this.requiredMaterialsByTier.getByTier(tier);
        if (requiredMaterials == null)
        {
            return 0;
        }

        return requiredMaterials.MaxPlanks;
    }

    String getRequiredSteelBarsFormated(int tier)
    {
        RequiredMaterials requiredMaterials = this.requiredMaterialsByTier.getByTier(tier);
        if (requiredMaterials == null)
        {
            return null;
        }

        if (requiredMaterials.MinSteelBars + requiredMaterials.MaxSteelBars == 0)
        {
            return null;
        }

        if (requiredMaterials.MinSteelBars == requiredMaterials.MaxSteelBars)
        {
            return String.format("%d steel bar", requiredMaterials.MinSteelBars);
        }

        return String.format("%d - %d steel bars", requiredMaterials.MinSteelBars, requiredMaterials.MaxSteelBars);
    }

    int getRequiredSteelBars(int tier)
    {
        RequiredMaterials requiredMaterials = this.requiredMaterialsByTier.getByTier(tier);
        if (requiredMaterials == null)
        {
            return 0;
        }

        return requiredMaterials.MaxSteelBars;
    }

    private static final ImmutableSet<Integer> LADDERS;
    static
    {
        final ImmutableSet.Builder<Integer> b = new ImmutableSet.Builder<>();
        for (final Home h : values())
        {
            b.add(h.getLadders());
        }
        LADDERS = b.build();
    }

    static boolean isLadder(final int objID)
    {
        return LADDERS.contains(objID);
    }

    public boolean isInside(final WorldPoint point)
    {
        for (final Home h : values())
        {
            if (h.getArea().contains2D(point))
            {
                return true;
            }
        }
        return false;
    }
}