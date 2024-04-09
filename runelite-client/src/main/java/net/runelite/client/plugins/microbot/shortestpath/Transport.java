package net.runelite.client.plugins.microbot.shortestpath;

import com.google.common.base.Strings;
import lombok.Getter;
import net.runelite.api.Quest;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * This class represents a travel point between two WorldPoints.
 */
public class Transport {

    /**
     * The starting point of this transport
     */
    @Getter
    private final WorldPoint origin;

    /**
     * The ending point of this transport
     */
    @Getter
    private final WorldPoint destination;

    /**
     * The skill levels required to use this transport
     */
    private final int[] skillLevels = new int[Skill.values().length];

    /**
     * The quests required to use this transport
     */
    @Getter
    private List<Quest> quests = new ArrayList<>();

    /**
     * Whether the transport is an agility shortcut
     */
    @Getter
    private boolean isAgilityShortcut;

    /**
     * Whether the transport is a crossbow grapple shortcut
     */
    @Getter
    private boolean isGrappleShortcut;

    /**
     * Whether the transport is a boat
     */
    @Getter
    private boolean isBoat;

    /**
     * Whether the transport is a canoe
     */
    @Getter
    private boolean isCanoe;

    /**
     * Whether the transport is a charter ship
     */
    @Getter
    private boolean isCharterShip;

    /**
     * Whether the transport is a ship
     */
    @Getter
    private boolean isShip;

    /**
     * Whether the transport is a fairy ring
     */
    @Getter
    private boolean isFairyRing;

    /**
     * Whether the transport is a gnome glider
     */
    @Getter
    private boolean isGnomeGlider;

    /**
     * Whether the transport is a spirit tree
     */
    @Getter
    private boolean isSpiritTree;

    /**
     * Whether the transport is a teleportation lever
     */
    @Getter
    private boolean isTeleportationLever;

    /**
     * Whether the transport is a teleportation portal
     */
    @Getter
    private boolean isTeleportationPortal;

    /**
     * The additional travel time
     */
    @Getter
    private int wait;

    /**
     * Info to display for this transport. For spirit trees, fairy rings, and others, this is the destination option to pick.
     */
    @Getter
    private String displayInfo;

    Transport(final WorldPoint origin, final WorldPoint destination) {
        this.origin = origin;
        this.destination = destination;
    }

    Transport(final String line, TransportType transportType) {
        final String DELIM = " ";

        String[] parts = line.split("\t");

        String[] parts_origin = parts[0].split(DELIM);
        String[] parts_destination = parts[1].split(DELIM);

        origin = new WorldPoint(
                Integer.parseInt(parts_origin[0]),
                Integer.parseInt(parts_origin[1]),
                Integer.parseInt(parts_origin[2]));
        destination = new WorldPoint(
                Integer.parseInt(parts_destination[0]),
                Integer.parseInt(parts_destination[1]),
                Integer.parseInt(parts_destination[2]));

        // Skill requirements
        if (parts.length >= 4 && !parts[3].isEmpty()) {
            String[] skillRequirements = parts[3].split(";");

            for (String requirement : skillRequirements) {
                String[] levelAndSkill = requirement.split(DELIM);

                int level = Integer.parseInt(levelAndSkill[0]);
                String skillName = levelAndSkill[1];

                Skill[] skills = Skill.values();
                for (int i = 0; i < skills.length; i++) {
                    if (skills[i].getName().equals(skillName)) {
                        skillLevels[i] = level;
                        break;
                    }
                }
            }
        }

        // Quest requirements
        if (parts.length >= 6 && !parts[5].isEmpty()) {
            this.quests = findQuests(parts[5]);
        }

        // Additional travel time
        if (parts.length >= 7 && !parts[6].isEmpty()) {
            this.wait = Integer.parseInt(parts[6]);
        }

        // Destination
        if (parts.length >= 8 && !parts[7].isEmpty()) {
            this.displayInfo = parts[7];
        }

        isAgilityShortcut = TransportType.AGILITY_SHORTCUT.equals(transportType);
        isGrappleShortcut = isAgilityShortcut && (getRequiredLevel(Skill.RANGED) > 1 || getRequiredLevel(Skill.STRENGTH) > 1);
        isBoat = TransportType.BOAT.equals(transportType);
        isCanoe = TransportType.CANOE.equals(transportType);
        isCharterShip = TransportType.CHARTER_SHIP.equals(transportType);
        isShip = TransportType.SHIP.equals(transportType);
        isGnomeGlider = TransportType.GNOME_GLIDER.equals(transportType);
        isSpiritTree = TransportType.SPIRIT_TREE.equals(transportType);
        isTeleportationLever = TransportType.TELEPORTATION_LEVER.equals(transportType);
        isTeleportationPortal = TransportType.TELEPORTATION_PORTAL.equals(transportType);
    }

    /**
     * The skill level required to use this transport
     */
    public int getRequiredLevel(Skill skill) {
        return skillLevels[skill.ordinal()];
    }

    /**
     * Whether the transport has one or more quest requirements
     */
    public boolean isQuestLocked() {
        return !quests.isEmpty();
    }

    private static List<Quest> findQuests(String questNamesCombined) {
        String[] questNames = questNamesCombined.split(";");
        List<Quest> quests = new ArrayList<>();
        for (String questName : questNames) {
            for (Quest quest : Quest.values()) {
                if (quest.getName().equals(questName)) {
                    quests.add(quest);
                    break;
                }
            }
        }
        return quests;
    }

    private static void addTransports(Map<WorldPoint, List<Transport>> transports, String path, TransportType transportType) {
        try {
            String s = new String(Util.readAllBytes(ShortestPathPlugin.class.getResourceAsStream(path)), StandardCharsets.UTF_8);
            Scanner scanner = new Scanner(s);
            List<String> fairyRingsQuestNames = new ArrayList<>();
            List<WorldPoint> fairyRings = new ArrayList<>();
            List<String> fairyRingCodes = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (line.startsWith("#") || line.isBlank()) {
                    continue;
                }

                if (TransportType.FAIRY_RING.equals(transportType)) {
                    String[] p = line.split("\t");
                    fairyRings.add(new WorldPoint(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2])));
                    fairyRingCodes.add(p.length >= 4 ? p[3].replaceAll("_", " ") : null);
                    fairyRingsQuestNames.add(p.length >= 7 ? p[6] : "");
                } else {
                    Transport transport = new Transport(line, transportType);
                    WorldPoint origin = transport.getOrigin();
                    transports.computeIfAbsent(origin, k -> new ArrayList<>()).add(transport);
                }
            }
            if (TransportType.FAIRY_RING.equals(transportType)) {
                for (WorldPoint origin : fairyRings) {
                    for (int i = 0; i < fairyRings.size(); i++) {
                        WorldPoint destination = fairyRings.get(i);
                        String questName = fairyRingsQuestNames.get(i);
                        if (origin.equals(destination)) {
                            continue;
                        }
                        Transport transport = new Transport(origin, destination);
                        transport.isFairyRing = true;
                        transport.wait = 5;
                        transport.displayInfo = fairyRingCodes.get(i);
                        transports.computeIfAbsent(origin, k -> new ArrayList<>()).add(transport);
                        if (!Strings.isNullOrEmpty(questName)) {
                            transport.quests = findQuests(questName);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap<WorldPoint, List<Transport>> loadAllFromResources() {
        HashMap<WorldPoint, List<Transport>> transports = new HashMap<>();
        addTransports(transports, "transports.tsv", TransportType.TRANSPORT);
        addTransports(transports, "agility_shortcuts.tsv", TransportType.AGILITY_SHORTCUT);
        addTransports(transports, "boats.tsv", TransportType.BOAT);
        addTransports(transports, "canoes.tsv", TransportType.CANOE);
        addTransports(transports, "charter_ships.tsv", TransportType.CHARTER_SHIP);
        addTransports(transports, "ships.tsv", TransportType.SHIP);
        addTransports(transports, "fairy_rings.tsv", TransportType.FAIRY_RING);
        addTransports(transports, "gnome_gliders.tsv", TransportType.GNOME_GLIDER);
        addTransports(transports, "spirit_trees.tsv", TransportType.SPIRIT_TREE);
        addTransports(transports, "levers.tsv", TransportType.TELEPORTATION_LEVER);
        addTransports(transports, "portals.tsv", TransportType.TELEPORTATION_PORTAL);

        return transports;
    }

    private enum TransportType {
        TRANSPORT,
        AGILITY_SHORTCUT,
        BOAT,
        CANOE,
        CHARTER_SHIP,
        SHIP,
        FAIRY_RING,
        GNOME_GLIDER,
        SPIRIT_TREE,
        TELEPORTATION_LEVER,
        TELEPORTATION_PORTAL
    }
}
