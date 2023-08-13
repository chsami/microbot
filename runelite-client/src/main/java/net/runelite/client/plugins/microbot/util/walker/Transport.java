package net.runelite.client.plugins.microbot.util.walker;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.Quest;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.magic.Teleport;
import net.runelite.client.plugins.microbot.util.walker.pathfinder.Pathfinder;
import net.runelite.client.plugins.microbot.util.walker.pathfinder.PathfinderConfig;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * This class represents a travel point between two WorldPoints.
 */
public class Transport {
    int agilityLevelRequired = 0;
    /**
     * The starting point of this transport
     */
    @Getter
    public WorldPoint origin = null;

    /**
     * The ending point of this transport
     */
    @Getter
    public WorldPoint destination = null;

    /**
     * The action of the object
     */
    @Getter
    public String action;

    /**
     * The id of the object
     */
    @Getter
    public int objectId;

    /**
     * The linked transport
     */
    @Getter
    public Transport linkedTransport;


    /**
     * The skill levels required to use this transport
     */
    public final int[] skillLevels = new int[Skill.values().length];

    /**
     * The quest required to use this transport
     */
    @Getter
    public Quest quest;

    /**
     * Whether the transport is an agility shortcut
     */
    @Getter
    public boolean isAgilityShortcut;

    /**
     * Whether the transport is a crossbow grapple shortcut
     */
    @Getter
    public boolean isGrappleShortcut;

    /**
     * Whether the transport is a boat
     */
    @Getter
    public boolean isBoat;

    /**
     * Whether the transport is a fairy ring
     */
    @Getter
    public boolean isFairyRing;

    /**
     * Whether the transport is a teleport
     */
    @Getter
    public boolean isTeleport;

    /**
     * The additional travel time
     */
    @Getter
    public int wait;

    @Getter
    public int itemRequired;
    @Getter
    public boolean isMember;

    public boolean reverse = false;
    public String reverseAction;


    public Transport() {

    }

    Transport(final WorldPoint origin, final WorldPoint destination) {
        this.origin = origin;
        this.destination = destination;
    }

    Transport(final WorldPoint origin, final WorldPoint destination, final boolean isFairyRing) {
        this(origin, destination);
        this.isFairyRing = isFairyRing;
    }

    Transport(final String line) {
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


        //menu action
        String[] parts_object = parts[2].split(DELIM);
        if (parts_object.length == 3) {
            action = parts_object[0];
            objectId = Integer.parseInt(parts_object[2]);
        }

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
            this.quest = findQuest(parts[5]);
        }

        // Additional travel time
        if (parts.length >= 7 && !parts[6].isEmpty()) {
            this.wait = Integer.parseInt(parts[6]);
        }

        isAgilityShortcut = getRequiredLevel(Skill.AGILITY) > 1;
        isGrappleShortcut = isAgilityShortcut && (getRequiredLevel(Skill.RANGED) > 1 || getRequiredLevel(Skill.STRENGTH) > 1);
    }

    /**
     * The skill level required to use this transport
     */
    public int getRequiredLevel(Skill skill) {
        return skillLevels[skill.ordinal()];
    }

    /**
     * Whether the transport has a quest requirement
     */
    public boolean isQuestLocked() {
        return quest != null;
    }

    private static Quest findQuest(String questName) {
        for (Quest quest : Quest.values()) {
            if (quest.getName().equals(questName)) {
                return quest;
            }
        }
        return null;
    }

    private static void addTransports(Map<WorldPoint, List<Transport>> transports, String path, TransportType transportType) {
        try {
            String s = new String(Util.readAllBytes(Microbot.class.getResourceAsStream(path)), StandardCharsets.UTF_8);
            Scanner scanner = new Scanner(s);
            List<WorldPoint> fairyRings = new ArrayList<>();
            List<String> fairyRingsQuestNames = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (line.startsWith("#") || line.isEmpty()) {
                    continue;
                }

                if (TransportType.FAIRY_RING.equals(transportType)) {
                    String[] p = line.split("\t");
                    fairyRings.add(new WorldPoint(Integer.parseInt(p[0]), Integer.parseInt(p[1]), Integer.parseInt(p[2])));
                    fairyRingsQuestNames.add(p.length >= 7 ? p[6] : "");
                } else {
                    Transport transport = new Transport(line);
                    transport.isBoat = TransportType.BOAT.equals(transportType);
                    transport.isTeleport = TransportType.TELEPORT.equals(transportType);
                    if (transport.isAgilityShortcut) {
                        continue;
                    }
                    if (transport.isGrappleShortcut) {
                        continue;
                    }
                    WorldPoint origin = transport.getOrigin();
                    transports.computeIfAbsent(origin, k -> new ArrayList<>()).add(transport);
                }
            }
            for (WorldPoint origin : fairyRings) {
                for (int i = 0; i < fairyRings.size(); i++) {
                    WorldPoint destination = fairyRings.get(i);
                    String questName = fairyRingsQuestNames.get(i);
                    if (origin.equals(destination)) {
                        continue;
                    }
                    Transport transport = new Transport(origin, destination, true);
                    transport.wait = 5;
                    transports.computeIfAbsent(origin, k -> new ArrayList<>()).add(transport);
                    if (!Strings.isNullOrEmpty(questName)) {
                        transport.quest = findQuest(questName);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

  /*  public HashMap<WorldPoint, List<Transport>> fromResources() {


       //HILL GIANT PATH - Edgeville Dungeon
        addDoor(new WorldPoint(3115, 3449, 0), new WorldPoint(3115, 3450, 0), ObjectID.DOOR_1804, "Open", ItemID.BRASS_KEY, 2000);

        Transport varrock_hillgiant_ladder = new Transport();

        varrock_hillgiant_ladder.origin = new WorldPoint(3116, 3452, 0);
        varrock_hillgiant_ladder.destination =  new WorldPoint(3116, 9851, 0);
        varrock_hillgiant_ladder.objectId = 17384;
        varrock_hillgiant_ladder.action = "climb-down";


        Transport varrock_hillgiant_ladder_dungeon = new Transport();

        varrock_hillgiant_ladder_dungeon.origin = new WorldPoint(3116, 9852, 0);
        varrock_hillgiant_ladder_dungeon.destination =  new WorldPoint(3116, 3451, 0);
        varrock_hillgiant_ladder_dungeon.objectId = 17385;
        varrock_hillgiant_ladder_dungeon.action = "climb-up";

        transports.computeIfAbsent(varrock_hillgiant_ladder.origin, k -> new ArrayList<>()).add(varrock_hillgiant_ladder);
        transports.computeIfAbsent(varrock_hillgiant_ladder_dungeon.origin, k -> new ArrayList<>()).add(varrock_hillgiant_ladder_dungeon);

        //AIR ORBS PATH - Edgeville Dungeon
        Transport edgeville_dungeon_trapdoor_closed = new Transport();

        edgeville_dungeon_trapdoor_closed.origin = new WorldPoint(3097, 3468, 0);
        edgeville_dungeon_trapdoor_closed.destination =  new WorldPoint(3097, 3468, 0);
        edgeville_dungeon_trapdoor_closed.objectId = 1579;
        edgeville_dungeon_trapdoor_closed.action = "Open";

        Transport edgeville_dungeon_trapdoor_open = new Transport();

        edgeville_dungeon_trapdoor_open.origin = new WorldPoint(3097, 3468, 0);
        edgeville_dungeon_trapdoor_open.destination =  new WorldPoint(3096, 9867, 0);
        edgeville_dungeon_trapdoor_open.objectId = 1581;
        edgeville_dungeon_trapdoor_open.action = "Climb-down";

        Transport edgeville_dungeon_ladder_up = new Transport();

        edgeville_dungeon_ladder_up.origin = new WorldPoint(3097, 9867, 0);
        edgeville_dungeon_ladder_up.destination =  new WorldPoint(3096, 3468, 0);
        edgeville_dungeon_ladder_up.objectId = 17385;
        edgeville_dungeon_ladder_up.action = "Climb-up";

        addDoor(new WorldPoint(3103, 9909, 0), new WorldPoint(3103, 9909, 0), 1727, "Open", -1, 0);
        addDoor(new WorldPoint(3131, 9917, 0), new WorldPoint(3131, 9918, 0), 1727, "Open", -1, 0);
        addDoor(new WorldPoint(3106, 9944, 0), new WorldPoint(3106, 9944, 0), 1569, "Open", -1, 0);

        Transport air_orb_ladder_up = new Transport();

        air_orb_ladder_up.origin = new WorldPoint(3088, 9971, 0);
        air_orb_ladder_up.destination =  new WorldPoint(3088, 3570, 0);
        air_orb_ladder_up.objectId = 17385;
        air_orb_ladder_up.action = "Climb-up";

        Transport air_orb_ladder_down = new Transport();

        air_orb_ladder_down.origin = new WorldPoint(3088, 3570, 0);
        air_orb_ladder_down.destination =  new WorldPoint(3088, 9970, 0);
        air_orb_ladder_down.objectId = 16680;
        air_orb_ladder_down.action = "Climb-down";

        transports.computeIfAbsent(edgeville_dungeon_trapdoor_closed.origin, k -> new ArrayList<>()).add(edgeville_dungeon_trapdoor_closed);
        transports.computeIfAbsent(edgeville_dungeon_trapdoor_open.origin, k -> new ArrayList<>()).add(edgeville_dungeon_trapdoor_open);
        transports.computeIfAbsent(edgeville_dungeon_ladder_up.origin, k -> new ArrayList<>()).add(edgeville_dungeon_ladder_up);
        transports.computeIfAbsent(air_orb_ladder_up.origin, k -> new ArrayList<>()).add(air_orb_ladder_up);
        transports.computeIfAbsent(air_orb_ladder_down.origin, k -> new ArrayList<>()).add(air_orb_ladder_down);


        //Lumbridge staircase
        Transport lumbridge_staircase = new Transport();

        lumbridge_staircase.origin = new WorldPoint(3205, 3208, 0);
        lumbridge_staircase.destination =  new WorldPoint(3205, 3209, 1);
        lumbridge_staircase.objectId = ObjectID.STAIRCASE_16671;
        lumbridge_staircase.action = "Climb-up";

        Transport lumbridge_staircase_1_up = new Transport();

        lumbridge_staircase_1_up.origin = new WorldPoint(3204, 3207, 1);
        lumbridge_staircase_1_up.destination =  new WorldPoint(3205, 3209, 2);
        lumbridge_staircase_1_up.objectId = ObjectID.STAIRCASE_16672;
        lumbridge_staircase_1_up.action = "Climb-up";

        Transport lumbridge_staircase_1_down = new Transport();

        lumbridge_staircase_1_down.origin = new WorldPoint(3204, 3207, 1);
        lumbridge_staircase_1_down.destination =  new WorldPoint(3206, 3208, 1);
        lumbridge_staircase_1_down.objectId = ObjectID.STAIRCASE_16672;
        lumbridge_staircase_1_down.action = "Climb-down";

        Transport lumbridge_staircase_2 = new Transport();

        lumbridge_staircase_2.origin = new WorldPoint(3205, 3208, 2);
        lumbridge_staircase_2.destination =  new WorldPoint(3205, 3209, 1);
        lumbridge_staircase_2.objectId = ObjectID.STAIRCASE_16673;
        lumbridge_staircase_2.action = "Climb-down";

        transports.computeIfAbsent(lumbridge_staircase.origin, k -> new ArrayList<>()).add(lumbridge_staircase);
        transports.computeIfAbsent(lumbridge_staircase_1_up.origin, k -> new ArrayList<>()).add(lumbridge_staircase_1_up);
        transports.computeIfAbsent(lumbridge_staircase_1_down.origin, k -> new ArrayList<>()).add(lumbridge_staircase_1_down);
        transports.computeIfAbsent(lumbridge_staircase_2.origin, k -> new ArrayList<>()).add(lumbridge_staircase_2);

    }*/

    public Transport addObstacle(WorldPoint origin, WorldPoint destination) {
        return addObstacle(origin, destination, null, false);
    }

    public Transport addObstacle(WorldPoint origin, WorldPoint destination, String action) {
        return addObstacle(origin,destination,action,false);
    }

    public Transport addObstacle(WorldPoint origin, WorldPoint destination, String action, boolean isMember) {

        this.origin = origin;
        this.destination =  destination;
        this.action = action;
        this.isMember = isMember;


        return this;
    }

    public Transport addReverse() {
        return addReverse(null);
    }

    public Transport addReverse(String action) {

        reverse = true;
        reverseAction = action;

        return this;
    }

    public Transport chain(Transport linkedTransport) {

        this.linkedTransport = linkedTransport;

        return this;
    }

    public Transport addAgilityRequirement(int level) {
        agilityLevelRequired = level;
        return this;
    }

    public Transport build() {
        if (Microbot.getClient().getRealSkillLevel(Skill.AGILITY) < agilityLevelRequired)
            return this;

        if (reverse) {
            Transport obstacle_reverse = new Transport();

            obstacle_reverse.destination = this.origin;
            obstacle_reverse.origin =  this.destination;
            obstacle_reverse.linkedTransport = this.linkedTransport;
            obstacle_reverse.action = reverseAction;
            obstacle_reverse.isMember = this.isMember;

            PathfinderConfig.transports.computeIfAbsent(obstacle_reverse.origin, k -> new ArrayList<>()).add(obstacle_reverse);
        }

        PathfinderConfig.transports.computeIfAbsent(this.origin, k -> new ArrayList<>()).add(this);

        return this;
    }

    private enum TransportType {
        TRANSPORT,
        BOAT,
        FAIRY_RING,
        TELEPORT
    }
}
