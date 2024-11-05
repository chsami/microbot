package net.runelite.client.plugins.microbot.shortestpath.pathfinder;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.shortestpath.*;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.magic.Rs2Magic;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.runelite.client.plugins.microbot.shortestpath.TransportType.*;



public class PathfinderConfig {
    private static final WorldArea WILDERNESS_ABOVE_GROUND = new WorldArea(2944, 3523, 448, 448, 0);
    private static final WorldArea WILDERNESS_ABOVE_GROUND_LEVEL_20 = new WorldArea(2944, 3680, 448, 448, 0);
    private static final WorldArea WILDERNESS_ABOVE_GROUND_LEVEL_30 = new WorldArea(2944, 3760, 448, 448, 0);
    private static final WorldArea WILDERNESS_UNDERGROUND = new WorldArea(2944, 9918, 320, 442, 0);
    private static final WorldArea WILDERNESS_UNDERGROUND_LEVEL_20 = new WorldArea(2944, 10075, 320, 442, 0);
    private static final WorldArea WILDERNESS_UNDERGROUND_LEVEL_30 = new WorldArea(2944, 10155, 320, 442, 0);

    private final SplitFlagMap mapData;
    private final ThreadLocal<CollisionMap> map;
    /** All transports by origin {@link WorldPoint}. The null key is used for transports centered on the player. */
    private final Map<WorldPoint, Set<Transport>> allTransports;
    @Setter
    private Set<Transport> usableTeleports;

    @Getter
    private ConcurrentHashMap<WorldPoint, Set<Transport>> transports;
    // Copy of transports with packed positions for the hotpath; lists are not copied and are the same reference in both maps
    @Getter
    @Setter
    private PrimitiveIntHashMap<Set<Transport>> transportsPacked;

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
            useMinecarts,
            useSpiritTrees,
            useTeleportationLevers,
            useTeleportationPortals,
            useTeleportationSpells,
            useWildernessObelisks;
    //START microbot variables
    @Getter
    private int distanceBeforeUsingTeleport;
    @Getter
    private final List<Restriction> resourceRestrictions;
    @Getter
    private List<Restriction> customRestrictions;
    @Getter
    private Set<Integer> restrictedPointsPacked;
    private boolean useNpcs;
    //END microbot variables
    private TeleportationItem useTeleportationItems;
    private final int[] boostedLevels = new int[Skill.values().length];
    private Map<Quest, QuestState> questStates = new HashMap<>();
    private Map<Integer, Integer> varbitValues = new HashMap<>();

    @Getter
    @Setter
    // Used for manual calculating paths without teleport & items in caves
    private boolean ignoreTeleportAndItems = false;

    public PathfinderConfig(SplitFlagMap mapData, Map<WorldPoint, Set<Transport>> transports,
                            List<Restriction> restrictions,
                            Client client, ShortestPathConfig config) {
        this.mapData = mapData;
        this.map = ThreadLocal.withInitial(() -> new CollisionMap(this.mapData));
        this.allTransports = transports;
        this.usableTeleports = new HashSet<>(allTransports.size() / 20);
        this.transports = new ConcurrentHashMap<>(allTransports.size() / 2);
        this.transportsPacked = new PrimitiveIntHashMap<>(allTransports.size() / 2);
        this.client = client;
        this.config = config;
        //START microbot variables
        this.resourceRestrictions = restrictions;
        this.customRestrictions = new ArrayList<>();
        this.restrictedPointsPacked = new HashSet<>();
        //END microbot variables
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
        useGnomeGliders = config.useGnomeGliders();
        useMinecarts = config.useMinecarts();
        useSpiritTrees = config.useSpiritTrees();
        useTeleportationItems = config.useTeleportationItems();
        useTeleportationLevers = config.useTeleportationLevers();
        useTeleportationPortals = config.useTeleportationPortals();
        useTeleportationSpells = config.useTeleportationSpells();
        useWildernessObelisks = config.useWildernessObelisks();
        distanceBeforeUsingTeleport = config.distanceBeforeUsingTeleport();

        //START microbot variables
        useNpcs = config.useNpcs();
        //END microbot variables

        if (GameState.LOGGED_IN.equals(client.getGameState())) {
            for (int i = 0; i < Skill.values().length; i++) {
                boostedLevels[i] = client.getBoostedSkillLevel(Skill.values()[i]);
            }

            refreshTransports();
            //START microbot variables
            refreshRestrictionData();
            Rs2Tab.switchToInventoryTab();
            //END microbot variables
        }
    }

    /** Specialized method for only updating player-held item and spell transports */
    public void refreshTeleports(int packedLocation, int wildernessLevel) {
        Set<Transport> usableWildyTeleports = new HashSet<>(usableTeleports.size());
        if (ignoreTeleportAndItems) return;

        for (Transport teleport : usableTeleports) {
            if (wildernessLevel <= teleport.getMaxWildernessLevel()) {
                usableWildyTeleports.add(teleport);
            }
        }

        if (!usableWildyTeleports.isEmpty()) {
            // Added extra code to check if the key already exists
            // if the key already exists we append instead of overwriting
            // The issue was that the transport list would contain a transport object on the same
            // tile as the player, this would then be overwritten by the usableWildyTeleports
            // therefor losing the original transport object
            WorldPoint key = WorldPointUtil.unpackWorldPoint(packedLocation);
            Set<Transport> existingTeleports = transports.get(key);
            if (existingTeleports != null) {
                existingTeleports.addAll(usableWildyTeleports);
            } else {
                transports.put(key, usableWildyTeleports);
            }
            transportsPacked.put(packedLocation, usableWildyTeleports);
        }
    }

    private void refreshTransports() {
        useFairyRings &= !QuestState.NOT_STARTED.equals(Microbot.getQuestState(Quest.FAIRYTALE_II__CURE_A_QUEEN))
                && (Rs2Inventory.contains(ItemID.DRAMEN_STAFF, ItemID.LUNAR_STAFF)
                || Rs2Equipment.isWearing(ItemID.DRAMEN_STAFF)
                || Rs2Equipment.isWearing(ItemID.LUNAR_STAFF)
                || Microbot.getVarbitValue(Varbits.DIARY_LUMBRIDGE_ELITE)  == 1);
        useGnomeGliders &= QuestState.FINISHED.equals(Microbot.getQuestState(Quest.THE_GRAND_TREE));
        useSpiritTrees &= QuestState.FINISHED.equals(Microbot.getQuestState(Quest.TREE_GNOME_VILLAGE));

        transports.clear();
        transportsPacked.clear();
        usableTeleports.clear();
         Microbot.getClientThread().runOnClientThread(() -> {
            for (Map.Entry<WorldPoint, Set<Transport>> entry : allTransports.entrySet()) {
                for (Transport transport : entry.getValue()) {
                    for (Quest quest : transport.getQuests()) {
                        try {
                            questStates.put(quest, Microbot.getQuestState(quest));
                        } catch (NullPointerException ignored) {
                            System.out.println(ignored.getMessage());
                        }
                    }
                    for (TransportVarbit varbitCheck : transport.getVarbits()) {
                        varbitValues.put(varbitCheck.getVarbitId(), Microbot.getVarbitValue(varbitCheck.getVarbitId()));
                    }
                }
            }
            return true;
        });

        for (Map.Entry<WorldPoint, Set<Transport>> entry : allTransports.entrySet()) {
            WorldPoint point = entry.getKey();
            Set<Transport> usableTransports = new HashSet<>(entry.getValue().size());
            for (Transport transport : entry.getValue()) {

                if (point == null && useTransport(transport) && hasRequiredItems(transport)) {
                    usableTeleports.add(transport);
                } else if (useTransport(transport)) {
                    usableTransports.add(transport);
                }
            }

            if (point != null && !usableTransports.isEmpty()) {
                transports.put(point, usableTransports);
                transportsPacked.put(WorldPointUtil.packWorldPoint(point), usableTransports);
            }
        }
    }

    private void refreshRestrictionData() {
        restrictedPointsPacked.clear();

        Set<Quest> questsToFetch = new HashSet<>();
        Set<Integer> varbitsToFetch = new HashSet<>();
        List<Restriction> allRestrictions = Stream.concat(resourceRestrictions.stream(), customRestrictions.stream())
                .collect(Collectors.toList());

        for (Restriction entry : allRestrictions) {
            questsToFetch.addAll(entry.getQuests());
            for (TransportVarbit varbitCheck : entry.getVarbits()) {
                varbitsToFetch.add(varbitCheck.getVarbitId());
            }
        }

        // Fetch quest states and varbit values directly
        for (Quest quest : questsToFetch) {
            try {
                questStates.put(quest, Microbot.getQuestState(quest));
            } catch (NullPointerException ignored) {
                // Handle exceptions if necessary
            }
        }
        for (Integer varbitId : varbitsToFetch) {
            varbitValues.put(varbitId, Microbot.getVarbitValue(varbitId));
        }

        for (Restriction entry : allRestrictions) {
            boolean restrictionApplies = false;

            // Check if there are no quests, varbits, or skills, used for explicit restrictions
            if (entry.getQuests().isEmpty() && entry.getVarbits().isEmpty() && Arrays.stream(entry.getSkillLevels()).allMatch(level -> level == 0)) {
                restrictionApplies = true;
            }

            // Quest check
            for (Quest quest : entry.getQuests()) {
                if (questStates.getOrDefault(quest, QuestState.NOT_STARTED) != QuestState.FINISHED) {
                    restrictionApplies = true;
                    break;
                }
            }

            // Varbit check
            if (!restrictionApplies) {
                for (TransportVarbit varbitCheck : entry.getVarbits()) {
                    int varbitId = varbitCheck.getVarbitId();
                    int expectedValue = varbitCheck.getValue();
                    if (varbitValues.getOrDefault(varbitId, -1) != expectedValue) {
                        restrictionApplies = true;
                        break;
                    }
                }
            }

            // Skill level check
            if (!restrictionApplies && !hasRequiredLevels(entry)) {
                restrictionApplies = true;
            }

            if (restrictionApplies) {
                restrictedPointsPacked.add(entry.getPackedWorldPoint());
            }
        }
    }

    public static boolean isInWilderness(WorldPoint p) {
        return WILDERNESS_ABOVE_GROUND.distanceTo(p) == 0 || WILDERNESS_UNDERGROUND.distanceTo(p) == 0;
    }

    public static boolean isInWilderness(int packedPoint) {
        return WorldPointUtil.distanceToArea(packedPoint, WILDERNESS_ABOVE_GROUND) == 0
                || WorldPointUtil.distanceToArea(packedPoint, WILDERNESS_UNDERGROUND) == 0;
    }

    public boolean avoidWilderness(int packedPosition, int packedNeightborPosition, boolean targetInWilderness) {
        return avoidWilderness && !targetInWilderness
                && !isInWilderness(packedPosition) && isInWilderness(packedNeightborPosition);
    }

    public boolean isInLevel20Wilderness(int packedPoint) {
        return WorldPointUtil.distanceToArea(packedPoint, WILDERNESS_ABOVE_GROUND_LEVEL_20) == 0
                || WorldPointUtil.distanceToArea(packedPoint, WILDERNESS_UNDERGROUND_LEVEL_20) == 0;
    }

    public boolean isInLevel30Wilderness(int packedPoint){
        return WorldPointUtil.distanceToArea(packedPoint, WILDERNESS_ABOVE_GROUND_LEVEL_30) == 0
                || WorldPointUtil.distanceToArea(packedPoint, WILDERNESS_UNDERGROUND_LEVEL_30) == 0;

    }

    private boolean completedQuests(Transport transport) {
        for (Quest quest : transport.getQuests()) {
            if (!QuestState.FINISHED.equals(questStates.getOrDefault(quest, QuestState.NOT_STARTED))) {
                return false;
            }
        }
        return true;
    }

    private boolean varbitChecks(Transport transport) {
        if (varbitValues.isEmpty()) return true;
        for (TransportVarbit varbitCheck : transport.getVarbits()) {
            if (!varbitValues.get(varbitCheck.getVarbitId()).equals(varbitCheck.getValue())) {
                return false;
            }
        }
        return true;
    }

    private boolean useTransport(Transport transport) {
        final boolean isQuestLocked = transport.isQuestLocked();

        //START microbot variables
        final boolean isNpc = transport.getType() == TransportType.NPC;
        if (isNpc && !useNpcs){
            return false;
        }
        //END microbot variables

        if (!hasRequiredLevels(transport)) {
            return false;
        }

        //ship charters, mine cart will check for coins before using them
        if (transport.getAmtItemRequired() > 0 && !Rs2Inventory.hasItemAmount(transport.getItemRequired(), transport.getAmtItemRequired()))
            return false;

        TransportType type = transport.getType();

        if (AGILITY_SHORTCUT.equals(type) && (!useAgilityShortcuts || !client.getWorldType().contains(WorldType.MEMBERS))) {
            return false;
        } else if (GRAPPLE_SHORTCUT.equals(type) && (!useGrappleShortcuts || !client.getWorldType().contains(WorldType.MEMBERS))) {
            return false;
        } else if (BOAT.equals(type) && (!useBoats || !client.getWorldType().contains(WorldType.MEMBERS))) {
            return false;
        } else if (CANOE.equals(type) && (!useCanoes || !client.getWorldType().contains(WorldType.MEMBERS))) {
            return false;
        } else if (CHARTER_SHIP.equals(type) && (!useCharterShips || !client.getWorldType().contains(WorldType.MEMBERS))) {
            return false;
        } else if (SHIP.equals(type) && !useShips) {
            return false;
        } else if (FAIRY_RING.equals(type) && (!useFairyRings || !client.getWorldType().contains(WorldType.MEMBERS))) {
            return false;
        } else if (GNOME_GLIDER.equals(type) && (!useGnomeGliders || !client.getWorldType().contains(WorldType.MEMBERS))) {
            return false;
        } else if (MINECART.equals(type) && (!useMinecarts || !client.getWorldType().contains(WorldType.MEMBERS))) {
            return false;
        } else if (SPIRIT_TREE.equals(type) && (!useSpiritTrees || !client.getWorldType().contains(WorldType.MEMBERS))) {
            return false;
        } else if (TELEPORTATION_ITEM.equals(type)) {
            switch (useTeleportationItems) {
                case ALL:
                case INVENTORY:
                    break;
                case NONE:
                    return false;
                case INVENTORY_NON_CONSUMABLE:
                case ALL_NON_CONSUMABLE:
                    if (transport.isConsumable()) {
                        return false;
                    }
                    break;
            }
        } else if (TELEPORTATION_LEVER.equals(type) && !useTeleportationLevers) {
            return false;
        } else if (TELEPORTATION_PORTAL.equals(type) && !useTeleportationPortals) {
            return false;
        } else if (TELEPORTATION_SPELL.equals(type) && !useTeleportationSpells) {
            return false;
        } else if (WILDERNESS_OBELISK.equals(type) && !useWildernessObelisks) {
            return false;
        }

        if (isQuestLocked && !completedQuests(transport)) {
            return false;
        }

        if (!varbitChecks(transport)) {
            return false;
        }

        return true;
    }

    /** Checks if the player has all the required skill levels for the transport */
    private boolean hasRequiredLevels(Transport transport) {
        int[] requiredLevels = transport.getSkillLevels();
        for (int i = 0; i < boostedLevels.length; i++) {
            int boostedLevel = boostedLevels[i];
            int requiredLevel = requiredLevels[i];
            if (boostedLevel < requiredLevel) {
                return false;
            }
        }
        return true;
    }

    /** Checks if the player has all the required skill levels for the restriction */
    private boolean hasRequiredLevels(Restriction restriction) {
        int[] requiredLevels = restriction.getSkillLevels();
        for (int i = 0; i < boostedLevels.length; i++) {
            int boostedLevel = boostedLevels[i];
            int requiredLevel = requiredLevels[i];
            if (boostedLevel < requiredLevel) {
                return false;
            }
        }
        return true;
    }

    /** Checks if the player has all the required equipment and inventory items for the transport */
    private boolean hasRequiredItems(Transport transport) {
        if ((TeleportationItem.ALL.equals(useTeleportationItems) ||
                TeleportationItem.ALL_NON_CONSUMABLE.equals(useTeleportationItems)) &&
                TransportType.TELEPORTATION_ITEM.equals(transport.getType())) {
            return true;
        }
        if (TeleportationItem.NONE.equals(useTeleportationItems) &&
                TransportType.TELEPORTATION_ITEM.equals(transport.getType())) {
            return false;
        }

        if (transport.getType() == TELEPORTATION_SPELL) {
            //START microbot variables
            return Rs2Magic.quickCanCast(transport.getDisplayInfo());
            //END microbot variables
        } else {
            //START microbot variables
            if (!Microbot.getClient().getWorldType().contains(WorldType.MEMBERS)) return false;
            //END microbot variables
            // TODO: this does not check quantity
            return transport.getItemIdRequirements().stream().flatMap(Collection::stream).anyMatch(x -> Rs2Equipment.isWearing(x) || Rs2Inventory.hasItem(x));
       /*     List<Integer> inventoryItems = Arrays.stream(new InventoryID[]{InventoryID.INVENTORY, InventoryID.EQUIPMENT})
                    .map(client::getItemContainer)
                    .filter(Objects::nonNull)
                    .map(ItemContainer::getItems)
                    .flatMap(Arrays::stream)
                    .map(Item::getId)
                    .filter(itemId -> itemId != -1)
                    .collect(Collectors.toList());
            return transport.getItemIdRequirements().stream().anyMatch(requirements -> requirements.stream().allMatch(inventoryItems::contains));*/
        }
    }

    //microbot method
    public void setRestrictedTiles(Restriction... restrictions){
        this.customRestrictions = List.of(restrictions);
    }
}
