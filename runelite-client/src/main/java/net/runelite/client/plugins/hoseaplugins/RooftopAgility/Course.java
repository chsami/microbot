package net.runelite.client.plugins.hoseaplugins.RooftopAgility;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Course {
    GNOME("Gnome Stronghold", 9781),
    DRAYNOR("Draynor Village", 12338, 12339),
    AL_KHARID("Al Kharid", 13105),
    VARROCK("Varrock", 12853, 12572),
    CANAFIS("Canafis", 13878),
    APE_ATOLL("Ape Atoll", 11050, 10794),
    FALADOR("Falador", 12084),
    SEERS("Seers Village", 10806),
    POLLNIVNEACH("Pollnivneach"),
    PRIFDDINAS("Prifddinas", 12895, 13151, 13152),
    RELLEKKA("Rellekka", 10297, 10553),
    ARDOUGNE("Ardougne", 10547);

    private final String name;
    private final int[] regionIDs;

    Course(String name, int ...regionIDs) {
        this.name = name;
        this.regionIDs = regionIDs;
    }

    public static String getCourseNameByRegionID(int id) {
        for (Course course : Course.values()) {
            for (int regionID : course.regionIDs) {
                if (regionID == id) {
                    return course.name;
                }
            }
        }
        return null;
    }

    public static String getCourseNameFromMapRegions(int[] mapRegions) {
        return Arrays.stream(Course.values())
                .filter(course -> Arrays.stream(course.regionIDs).anyMatch(regionID -> containsRegion(mapRegions, regionID)))
                .map(course -> course.name)
                .findFirst()
                .orElse(null);
    }

    private static boolean containsRegion(int[] regions, int regionID) {
        return Arrays.stream(regions).anyMatch(region -> region == regionID);
    }
}

