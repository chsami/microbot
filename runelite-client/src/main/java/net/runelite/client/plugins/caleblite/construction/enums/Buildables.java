package net.runelite.client.plugins.caleblite.construction.enums;

public enum Buildables {
    OAK_LARDER(
            "Larder space",
            "Oak larder",
            "Build",
            15403,
            13565,
            "Oak plank",
            8,
            480,
            33,
            1
    ),
    TEAK_LARDER(
            "Larder space",
            "Teak larder",
            "Build",
            15403,
            13566,
            "Teak plank",
            8,
            750,
            43,
            2
    ),
    MAHOGANY_TABLE(
            "Dining table space",
            "Mahogany table",
            "Build",
            15298,
            13298,
            "Mahogany plank",
            6,
            840,
            52,
            3
    ),
    OAK_DINING_TABLE(
            "Dining table space",
            "Oak dining table",
            "Build",
            15298,
            13293,
            "Oak plank",
            4,
            240,
            22,
            1
    ),
    TEAK_DINING_TABLE(
            "Dining table space",
            "Teak dining table",
            "Build",
            15298,
            13295,
            "Teak plank",
            4,
            360,
            38,
            2
    ),
    OAK_DOOR(
            "Door hotspot",
            "Oak door",
            "Build",
            15313,
            13345,
            "Oak plank",
            10,
            600,
            74,
            1
    ),
    MYTHICAL_CAPE(
            "Guild trophy space",
            "Mythical cape",
            "Build",
            15382,
            31989,
            "Teak plank",
            3,
            370,
            82,
            1
    ),
    DUNGEON_DOOR(
            "Door space",
            "Dungeon door",
            "Build",
            15323,
            13344,
            "Oak plank",
            10,
            600,
            74,
            1
    ),
    MAHOGANY_BENCH(
            "Seating space",
            "Mahogany bench",
            "Build",
            15301,
            13305,
            "Mahogany plank",
            4,
            560,
            52,
            3
    );

    private final String spotName;
    private final String builtName;
    private final String action;
    private final int hotspotId;
    private final int builtId;
    private final String materialName;
    private final int materialAmount;
    private final int experienceGained;
    private final int levelRequired;
    private final int buildOption;

    Buildables(String spotName, String builtName, String action, int hotspotId, int builtId,
               String materialName, int materialAmount, int experienceGained,
               int levelRequired, int buildOption) {
        this.spotName = spotName;
        this.builtName = builtName;
        this.action = action;
        this.hotspotId = hotspotId;
        this.builtId = builtId;
        this.materialName = materialName;
        this.materialAmount = materialAmount;
        this.experienceGained = experienceGained;
        this.levelRequired = levelRequired;
        this.buildOption = buildOption;
    }

    public String getSpotName() { return spotName; }
    public String getBuiltName() { return builtName; }
    public String getAction() { return action; }
    public int getHotspotId() { return hotspotId; }
    public int getBuiltId() { return builtId; }
    public String getMaterialName() { return materialName; }
    public int getMaterialAmount() { return materialAmount; }
    public int getExperienceGained() { return experienceGained; }
    public int getLevelRequired() { return levelRequired; }
    public int getBuildOption() { return buildOption; }
}