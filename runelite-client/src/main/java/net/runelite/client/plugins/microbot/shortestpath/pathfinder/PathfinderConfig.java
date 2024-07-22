package net.runelite.client.plugins.microbot.shortestpath.pathfinder;

import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.shortestpath.*;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathfinderConfig {
    private static final WorldArea WILDERNESS_ABOVE_GROUND = new WorldArea(2944, 3523, 448, 448, 0);
    private static final WorldArea WILDERNESS_UNDERGROUND = new WorldArea(2944, 9918, 320, 442, 0);

    private final SplitFlagMap mapData;
    private final ThreadLocal<CollisionMap> map;
    private final Map<WorldPoint, List<Transport>> allTransports;
    @Getter
    private Map<WorldPoint, List<Transport>> transports;

    private final List<Restriction> resourceRestrictions;
    private List<Restriction> customRestrictions;
    @Getter
    private Set<Integer> restrictedPointsPacked;

    // Copy of transports with packed positions for the hotpath; lists are not copied and are the same reference in both maps
    @Getter
    private PrimitiveIntHashMap<List<Transport>> transportsPacked;

    private final Client client;
    private final ShortestPathConfig config;

    @Getter
    private long calculationCutoffMillis;
    @Getter
    private boolean avoidWilderness;
    private boolean useAgilityShortcuts,
        useGrappleShortcuts,
        useBoats,
        useCanoes,
        useCharterShips,
        useShips,
        useFairyRings,
        useGnomeGliders,
        useSpiritTrees,
        useTeleportationLevers,
        useTeleportationPortals,
        useNpcs;
    private int agilityLevel;
    private int rangedLevel;
    private int strengthLevel;
    private int prayerLevel;
    private int woodcuttingLevel;
    private Map<Quest, QuestState> questStates = new HashMap<>();

    public PathfinderConfig(SplitFlagMap mapData, Map<WorldPoint, List<Transport>> transports, List<Restriction> restrictions, Client client,
                            ShortestPathConfig config) {
        this.mapData = mapData;
        this.map = ThreadLocal.withInitial(() -> new CollisionMap(this.mapData));
        this.allTransports = transports;
        this.transports = new HashMap<>(allTransports.size());
        this.resourceRestrictions = restrictions;
        this.customRestrictions = new ArrayList<>();
        this.restrictedPointsPacked = new HashSet<>();
        this.transportsPacked = new PrimitiveIntHashMap<>(allTransports.size());
        this.client = client;
        this.config = config;
    }

    public CollisionMap getMap() {
        return map.get();
    }

    public void refresh() {
        calculationCutoffMillis = config.calculationCutoff() * Constants.GAME_TICK_LENGTH;
        avoidWilderness = config.avoidWilderness();
        useAgilityShortcuts = config.useAgilityShortcuts();
        useGrappleShortcuts = config.useGrappleShortcuts();
        useBoats = config.useBoats();
        useCanoes = config.useCanoes();
        useCharterShips = config.useCharterShips();
        useShips = config.useShips();
        useFairyRings = config.useFairyRings();
        useSpiritTrees = config.useSpiritTrees();
        useGnomeGliders = config.useGnomeGliders();
        useTeleportationLevers = config.useTeleportationLevers();
        useTeleportationPortals = config.useTeleportationPortals();
        useNpcs = config.useNpcs();

        if (GameState.LOGGED_IN.equals(client.getGameState())) {
            agilityLevel = client.getBoostedSkillLevel(Skill.AGILITY);
            rangedLevel = client.getBoostedSkillLevel(Skill.RANGED);
            strengthLevel = client.getBoostedSkillLevel(Skill.STRENGTH);
            prayerLevel = client.getBoostedSkillLevel(Skill.PRAYER);
            woodcuttingLevel = client.getBoostedSkillLevel(Skill.WOODCUTTING);

            questStates.clear();
            refreshTransportData();
            refreshRestrictionData();
        }
    }

    private void refreshTransportData() {
        if (!Thread.currentThread().equals(client.getClientThread())) {
            return; // Has to run on the client thread; data will be refreshed when path finding commences
        }

        useFairyRings &= !QuestState.NOT_STARTED.equals(getQuestState(Quest.FAIRYTALE_II__CURE_A_QUEEN))
                        && (Rs2Inventory.contains(ItemID.DRAMEN_STAFF, ItemID.LUNAR_STAFF)
                            || Rs2Equipment.isWearing(ItemID.DRAMEN_STAFF)
                            || Rs2Equipment.isWearing(ItemID.LUNAR_STAFF)
                            || client.getVarbitValue(Varbits.DIARY_LUMBRIDGE_ELITE)  == 1);
        useGnomeGliders &= QuestState.FINISHED.equals(getQuestState(Quest.THE_GRAND_TREE));
        useSpiritTrees &= QuestState.FINISHED.equals(getQuestState(Quest.TREE_GNOME_VILLAGE));

        transports.clear();
        transportsPacked.clear();
        for (Map.Entry<WorldPoint, List<Transport>> entry : allTransports.entrySet()) {
            List<Transport> usableTransports = new ArrayList<>(entry.getValue().size());
            for (Transport transport : entry.getValue()) {
                for (Quest quest : transport.getQuests()) {
                    if (!questStates.containsKey(quest)) {
                        try {
                            questStates.put(quest, getQuestState(quest));
                        } catch (NullPointerException ignored) {
                        }
                    }
                }

                if (useTransport(transport)) {
                    usableTransports.add(transport);
                }
            }

            WorldPoint point = entry.getKey();
            transports.put(point, usableTransports);
            transportsPacked.put(WorldPointUtil.packWorldPoint(point), usableTransports);
        }
    }

    private void refreshRestrictionData() {
        if (!Thread.currentThread().equals(client.getClientThread())) {
            return;
        }

        restrictedPointsPacked.clear();
        for (var entry : Stream.concat(resourceRestrictions.stream(), customRestrictions.stream()).collect(Collectors.toList())){
            for (Quest quest : entry.getQuests()) {
                if (!questStates.containsKey(quest)){
                    try {
                        questStates.put(quest, getQuestState(quest));
                    } catch (NullPointerException ignored) {
                    }
                }
            }

            if (entry.getQuests().isEmpty() || entry.getQuests().stream().anyMatch(x -> questStates.get(x) != QuestState.FINISHED))
                restrictedPointsPacked.add(entry.getPackedWorldPoint());
        }
    }

    public static boolean isInWilderness(WorldPoint p) {
        return WILDERNESS_ABOVE_GROUND.distanceTo(p) == 0 || WILDERNESS_UNDERGROUND.distanceTo(p) == 0;
    }

    public static boolean isInWilderness(int packedPoint) {
        return WorldPointUtil.distanceToArea(packedPoint, WILDERNESS_ABOVE_GROUND) == 0 || WorldPointUtil.distanceToArea(packedPoint, WILDERNESS_UNDERGROUND) == 0;
    }

    public boolean avoidWilderness(int packedPosition, int packedNeightborPosition, boolean targetInWilderness) {
        return avoidWilderness && !isInWilderness(packedPosition) && isInWilderness(packedNeightborPosition) && !targetInWilderness;
    }

    public QuestState getQuestState(Quest quest) {
        return quest.getState(client);
    }

    private boolean completedQuests(Transport transport) {
        for (Quest quest : transport.getQuests()) {
            if (!QuestState.FINISHED.equals(questStates.getOrDefault(quest, QuestState.NOT_STARTED))) {
                return false;
            }
        }
        return true;
    }

    private boolean useTransport(Transport transport) {
        final int transportAgilityLevel = transport.getRequiredLevel(Skill.AGILITY);
        final int transportRangedLevel = transport.getRequiredLevel(Skill.RANGED);
        final int transportStrengthLevel = transport.getRequiredLevel(Skill.STRENGTH);
        final int transportPrayerLevel = transport.getRequiredLevel(Skill.PRAYER);
        final int transportWoodcuttingLevel = transport.getRequiredLevel(Skill.WOODCUTTING);

        final boolean isAgilityShortcut = transport.isAgilityShortcut();
        final boolean isGrappleShortcut = transport.isGrappleShortcut();
        final boolean isBoat = transport.isBoat();
        final boolean isCanoe = transport.isCanoe();
        final boolean isCharterShip = transport.isCharterShip();
        final boolean isShip = transport.isShip();
        final boolean isFairyRing = transport.isFairyRing();
        final boolean isGnomeGlider = transport.isGnomeGlider();
        final boolean isSpiritTree = transport.isSpiritTree();
        final boolean isTeleportationLever = transport.isTeleportationLever();
        final boolean isTeleportationPortal = transport.isTeleportationPortal();
        final boolean isPrayerLocked = transportPrayerLevel > 1;
        final boolean isQuestLocked = transport.isQuestLocked();
        final boolean isNpc = transport.isNpc();

        if (isAgilityShortcut) {
            if (!useAgilityShortcuts || agilityLevel < transportAgilityLevel) {
                return false;
            }

            if (isGrappleShortcut && (!useGrappleShortcuts || rangedLevel < transportRangedLevel || strengthLevel < transportStrengthLevel)) {
                return false;
            }
        }

        if (isBoat && !useBoats) {
            return false;
        }

        if (isCanoe && (!useCanoes || woodcuttingLevel < transportWoodcuttingLevel)) {
            return false;
        }

        if (isCharterShip && !useCharterShips) {
            return false;
        }

        if (isShip && !useShips) {
            return false;
        }

        if (isFairyRing && !useFairyRings) {
            return false;
        }

        if (isGnomeGlider && !useGnomeGliders) {
            return false;
        }

        if (isSpiritTree && !useSpiritTrees) {
            return false;
        }

        if (isTeleportationLever && !useTeleportationLevers) {
            return false;
        }

        if (isTeleportationPortal && !useTeleportationPortals) {
            return false;
        }

        if (isPrayerLocked && prayerLevel < transportPrayerLevel) {
            return false;
        }

        if (isQuestLocked && !completedQuests(transport)) {
            return false;
        }

        if (isNpc && !useNpcs){
            return false;
        }

        if (transport.getItems().entrySet().stream().anyMatch(x -> !Rs2Inventory.hasItemAmount(x.getKey(), x.getValue())))
            return false;

        return true;
    }

    public void setRestrictedTiles(Restriction... restrictions){
        this.customRestrictions = List.of(restrictions);
    }
}
