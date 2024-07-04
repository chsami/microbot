package net.runelite.client.plugins.microbot.shortestpath;

import com.google.common.base.Strings;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grandexchange.Rs2GrandExchange;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static net.runelite.client.plugins.microbot.util.Global.sleep;
import static net.runelite.client.plugins.microbot.util.Global.sleepUntil;

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

    @Getter
    private final HashMap<String, Integer> items = new HashMap<>();

    /**
     * The quests required to use this transport
     */
    @Getter
    private List<Quest> quests = new ArrayList<>();

    /** The ids of items required to use this transport. If the player has **any** matching item, this transport is valid */
    @Getter
    private List<Integer> itemRequirements = new ArrayList<>();

    /** Whether the transport is an agility shortcut */
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

    /** Whether the transport is a player-held item */
    @Getter
    private boolean isPlayerItem;

    /** The additional travel time */
    @Setter
    @Getter
    private int wait;

    /**
     * Info to display for this transport. For spirit trees, fairy rings, and others, this is the destination option to pick.
     */
    @Getter
    private String displayInfo;

    @Getter
    private String action;
    @Getter
    private String objectName;
    @Getter
    private int objectId;
    @Getter
    private boolean isMember;
    
    @Getter
    private String npcName;

    /** If this is an item transport, this tracks if it is consumable (as opposed to having infinite uses) */
    @Getter
    private final boolean isConsumable;

    /** If this is an item transport, this is the maximum wilderness level that it can be used in */
    @Getter
    private final int maxWildernessLevel;

    /** Any varbits to check for the transport to be valid. All must pass for a transport to be valid */
    @Getter
    private final List<TransportVarbit> varbits;

    Transport(final WorldPoint origin, final WorldPoint destination) {
        this.origin = origin;
        this.destination = destination;
        this.isConsumable = false;
        this.maxWildernessLevel = -1;
        this.varbits = new ArrayList<>();
    }

    Transport(final String line, TransportType transportType) {
        final String DELIM = " ";

        String[] parts = line.split("\t");

        String[] parts_origin = parts[0].split(DELIM);
        String[] parts_destination = parts[1].split(DELIM);

        if (!TransportType.PLAYER_ITEM.equals(transportType)) {
            origin = new WorldPoint(
                Integer.parseInt(parts_origin[0]),
                Integer.parseInt(parts_origin[1]),
                Integer.parseInt(parts_origin[2]));
        } else {
            origin = null;
        }
        destination = new WorldPoint(
                Integer.parseInt(parts_destination[0]),
                Integer.parseInt(parts_destination[1]),
                Integer.parseInt(parts_destination[2]));

        try {
            if (!parts[2].isBlank()){
                action = parts[2].split(DELIM)[0];
                String npcAndObjectId = parts[2].substring(parts[2].indexOf(action) + action.length()).trim();
                npcName = npcAndObjectId.replaceAll("\\d", "").trim();  // Remove the numbers to get the NPC name
                objectId = Integer.parseInt(parts[2].split(DELIM)[parts[2].split(DELIM).length - 1]);
            }
        } catch(Exception ex) {
            System.out.println(ex.getMessage());
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

        itemRequirements = new ArrayList<>();
        // Item requirements are currently only implemented for player-held item transports
        if (TransportType.PLAYER_ITEM.equals(transportType)) {
            String[] itemIds = parts[4].split(";");
            for (String item : itemIds) {
                int itemId = Integer.parseInt(item);
                itemRequirements.add(itemId);
            }
        } else if (parts.length >= 5 && !parts[4].isEmpty()) {
            String[] itemRequirements = parts[4].split(";");

            for (String requirement : itemRequirements) {
                if (requirement.isBlank())
                    continue;

                int splitIndex = requirement.indexOf(DELIM);
                int amount;
                String item;

                try {
                    amount = Integer.parseInt(requirement.substring(0, splitIndex));
                    item = requirement.substring(splitIndex + 1);
                } catch (NumberFormatException e) {
                    amount = 1;
                    item = requirement;
                }

                items.put(item, amount);
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
        if (TransportType.PLAYER_ITEM.equals(transportType)) {
            // Item transports should always have a non-zero wait, so the pathfinder doesn't calculate the cost by distance
            this.wait = Math.max(this.wait, 1);
        }

        // Destination
        if (parts.length >= 8 && !parts[7].isEmpty()) {
            this.displayInfo = parts[7];
        }

        // Consumable - for item transports
        this.isConsumable = parts.length >= 9 && parts[8].equals("T");

        // Wilderness level - for item transports
        if (parts.length >= 10 && !parts[9].isEmpty()) {
            this.maxWildernessLevel = Integer.parseInt(parts[9]);
        } else {
            this.maxWildernessLevel = -1;
        }

        this.varbits = new ArrayList<>();
        // Varbit check - all must evaluate to true
        if (parts.length >= 11 && !parts[10].isEmpty()) {
            for (String varbitCheck : parts[10].split(DELIM)) {
                var varbitParts = varbitCheck.split("=");
                int varbitId = Integer.parseInt(varbitParts[0]);
                int varbitValue = Integer.parseInt(varbitParts[1]);
                varbits.add(new TransportVarbit(varbitId, varbitValue));
            }
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
        isPlayerItem = TransportType.PLAYER_ITEM.equals(transportType);
        isMember = TransportType.TELEPORTATION_LEVER.equals(transportType)
                ||  TransportType.SPIRIT_TREE.equals(transportType)
                || TransportType.GNOME_GLIDER.equals(transportType)
                || TransportType.CANOE.equals(transportType)
                || isAgilityShortcut
                || isGrappleShortcut;
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
                    WorldPoint origin = transportType.equals(TransportType.PLAYER_ITEM) ? null : transport.getOrigin();
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
            scanner.close();
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
        addTransports(transports, "items.tsv", TransportType.PLAYER_ITEM);

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
        TELEPORTATION_PORTAL,
        PLAYER_ITEM,
    }

    private static boolean completedQuests(Transport transport) {
        for (Quest quest : transport.getQuests()) {
            if (!QuestState.FINISHED.equals(quest.getState(Microbot.getClient()))) {
                return false;
            }
        }
        return true;
    }

    public boolean handleSpiritTree() {
        int spiritTreeMenu = 12255232;

        // Get Transport Information
        String displayInfo = this.getDisplayInfo();
        String objectName = this.getObjectName();
        int objectId = this.getObjectId();
        String action = this.getAction();
        WorldPoint origin = this.getOrigin();
        WorldPoint destination = this.getDestination();

        System.out.println("Display info: " + displayInfo);
        System.out.println("Object Name: " + objectName);
        System.out.println("Object ID: " + objectId);
        System.out.println("Action: " + action);
        System.out.println("Origin: " + origin);
        System.out.println("Destination: " + destination);

        // Check if the widget is already visible
        if (!Rs2Widget.isHidden(spiritTreeMenu)) {
            System.out.println("Widget is already visible. Skipping interaction.");
            char key = displayInfo.charAt(0);
            System.out.println(key);
            Rs2Keyboard.keyPress(key);
            System.out.println("Pressing: " + key);
            return true;
        }

        // Find the spirit tree object
        TileObject spiritTree = Rs2GameObject.findObjectByImposter(objectId, "Travel");
        if (spiritTree == null) {
            System.out.println("Spirit tree not found.");
            return false;
        }

        // Interact with the spirit tree
        Rs2GameObject.interact(spiritTree);

        // Wait for the widget to become visible
        boolean widgetVisible = !Rs2Widget.isHidden(spiritTreeMenu);
        if (!widgetVisible) {
            System.out.println("Widget did not become visible within the timeout.");
            return false;
        }

        System.out.println("Widget is now visible.");
        char key = displayInfo.charAt(0);
        Rs2Keyboard.keyPress(key);
        System.out.println("Pressing: " + key);
        return true;
    }

    public boolean handleGlider() {
        int gliderMenu = 9043968;
        int TA_QUIR_PRIW = 9043972;
        int SINDARPOS = 9043975;
        int LEMANTO_ANDRA = 9043978;
        int KAR_HEWO = 9043981;
        int GANDIUS = 9043984;
        int OOKOOKOLLY_UNDRI = 9043993;

        // Get Transport Information
        String displayInfo = this.getDisplayInfo();
        String objectName = this.getObjectName();
        String npcName = this.getNpcName();
        int objectId = this.getObjectId();
        String action = this.getAction();
        WorldPoint origin = this.getOrigin();
        WorldPoint destination = this.getDestination();

        System.out.println("Display info: " + displayInfo);
        System.out.println("Object Name: " + objectName);
        System.out.println("NPC Name: " + npcName);
        System.out.println("Object ID: " + objectId);
        System.out.println("Action: " + action);
        System.out.println("Origin: " + origin);
        System.out.println("Destination: " + destination);

        // Check if the widget is already visible
        if (!Rs2Widget.isHidden(gliderMenu)) {
            System.out.println("Widget is already visible. Skipping interaction.");
            return true;
        }

        // Find the glider NPC
        NPC gnome = Rs2Npc.getNpc(npcName);  // Use the NPC name to find the NPC
        if (gnome == null) {
            System.out.println("Gnome not found.");
            return false;
        }

        // Interact with the gnome glider NPC
        Rs2Npc.interact(gnome, action);

        sleep(1200,2400);

        // Wait for the widget to become visible
        boolean widgetVisible = !Rs2Widget.isHidden(gliderMenu);
        if (!widgetVisible) {
            System.out.println("Widget did not become visible within the timeout.");
            return false;
        }

        System.out.println("Widget is now visible.");

        switch(displayInfo) {
            case "Kar-Hewo":
                Rs2Widget.clickWidget(KAR_HEWO);
            case "Gnome Stronghold":
                Rs2Widget.clickWidget(TA_QUIR_PRIW);
            case "Sindarpos":
                Rs2Widget.clickWidget(SINDARPOS);
            case "Lemanto Andra":
                Rs2Widget.clickWidget(LEMANTO_ANDRA);
            case "Gandius":
                Rs2Widget.clickWidget(GANDIUS);
            case "Ookookolly Undri":
                Rs2Widget.clickWidget(OOKOOKOLLY_UNDRI);
        }
        return true;
    }

    // Constants for widget IDs
    private static final int FAIRY_RING_ID = 29495;
    private static final int FAIRY_RING_MENU = 26083328;

    private static final int SLOT_ONE = 26083331;
    private static final int SLOT_TWO = 26083332;
    private static final int SLOT_THREE = 26083333;
    private static final int TELEPORT_BUTTON = 26083354;

    private static final int SLOT_ONE_CW_ROTATION = 26083347;
    private static final int SLOT_ONE_ACW_ROTATION = 26083348;
    private static final int SLOT_TWO_CW_ROTATION = 26083349;
    private static final int SLOT_TWO_ACW_ROTATION = 26083350;
    private static final int SLOT_THREE_CW_ROTATION = 26083351;
    private static final int SLOT_THREE_ACW_ROTATION = 26083352;
    private static Rs2Item startingWeapon = null;
    private static int startingWeaponId;

    public boolean handleFairyRing() {
        // Get Transport Information
        String displayInfo = getDisplayInfo();
        String objectName = getObjectName();
        int objectId = getObjectId();
        String action = getAction();
        WorldPoint origin = getOrigin();
        WorldPoint destination = getDestination();

        if (startingWeapon == null) {


        startingWeapon = Rs2Equipment.get(EquipmentInventorySlot.WEAPON);
            System.out.println(startingWeapon);
            startingWeaponId = startingWeapon.getId();
        }

        System.out.println("Display info: " + displayInfo);
        System.out.println("Object Name: " + objectName);
        System.out.println("Object ID: " + objectId);
        System.out.println("Action: " + action);
        System.out.println("Origin: " + origin);
        System.out.println("Destination: " + destination);
        System.out.println("Starting Weapon ID: " + startingWeaponId);

        // Check if the widget is already visible
        if (!Rs2Widget.isHidden(FAIRY_RING_MENU)) {
            System.out.println("Widget is already visible. Skipping interaction.");
            rotateSlotToDesiredRotation(SLOT_ONE, Rs2Widget.getWidget(SLOT_ONE).getRotationY(), getDesiredRotation(getDisplayInfo().charAt(0)), SLOT_ONE_ACW_ROTATION, SLOT_ONE_CW_ROTATION);
            rotateSlotToDesiredRotation(SLOT_TWO, Rs2Widget.getWidget(SLOT_TWO).getRotationY(), getDesiredRotation(getDisplayInfo().charAt(1)), SLOT_TWO_ACW_ROTATION, SLOT_TWO_CW_ROTATION);
            rotateSlotToDesiredRotation(SLOT_THREE, Rs2Widget.getWidget(SLOT_THREE).getRotationY(), getDesiredRotation(getDisplayInfo().charAt(2)), SLOT_THREE_ACW_ROTATION, SLOT_THREE_CW_ROTATION);
            Rs2Widget.clickWidget(TELEPORT_BUTTON);
            sleep(1200,1800);
            if (!Rs2Equipment.isWearing(startingWeaponId)) {
                sleep(3000,3600); // Required due to long animation time
                System.out.println("Equipping Starting Weapon: " + startingWeaponId);
                Rs2Inventory.equip(startingWeaponId);
            }
            return true;
        }

        if (Rs2Equipment.isWearing("Dramen staff") || Rs2Equipment.isWearing("Lunar staff")) {
            System.out.println("Interacting with the fairy ring directly.");
            Rs2GameObject.interact(FAIRY_RING_ID, "Configure");
        } else if (Rs2Inventory.contains("Dramen staff")) {
            Rs2Inventory.equip("Dramen staff");
            sleep(600);
        } else if (Rs2Inventory.contains("Lunar staff")) {
            Rs2Inventory.equip("Lunar staff");
            sleep(600);
        }
        return true;
    }

    private boolean rotateSlotToDesiredRotation(int slotId, int currentRotation, int desiredRotation, int slotAcwRotationId, int slotCwRotationId) {
        int anticlockwiseTurns = (desiredRotation - currentRotation + 2048) % 2048;
        int clockwiseTurns = (currentRotation - desiredRotation + 2048) % 2048;

        if (clockwiseTurns <= anticlockwiseTurns) {
            System.out.println("Rotating slot " + slotId + " clockwise " + (clockwiseTurns / 512) + " times.");
            for (int i = 0; i < clockwiseTurns / 512; i++) {
                Rs2Widget.clickWidget(slotCwRotationId);
                sleep(600, 1200);
            }
            return true;
        } else {
                System.out.println("Rotating slot " + slotId + " anticlockwise " + (anticlockwiseTurns / 512) + " times.");
                for (int i = 0; i < anticlockwiseTurns / 512; i++) {
                    Rs2Widget.clickWidget(slotAcwRotationId);
                    sleep(600, 1200);
                }
                return true;
            }

    }

    public int getDesiredRotation(char letter) {
        switch (letter) {
            case 'A':
            case 'I':
            case 'P':
                return 0;
            case 'B':
            case 'J':
            case 'Q':
                return 512;
            case 'C':
            case 'K':
            case 'R':
                return 1024;
            case 'D':
            case 'L':
            case 'S':
                return 1536;
            default:
                return -1;
        }
    }

    public boolean handleItemTeleport(){
        for (var itemId : itemRequirements){
            Rs2Item item;
            boolean isWearing = Rs2Equipment.isWearing(itemId);

            if (isWearing)
                item = Rs2Equipment.get(itemId);
            else if (Rs2Inventory.hasItem(itemId))
                item = Rs2Inventory.get(itemId);
            else
                continue;

            if (item == null)
                continue;

            // Close blocking interfaces
            if (Rs2Bank.isOpen()){
                Rs2Bank.closeBank();
                sleepUntil(() -> !Rs2Bank.isOpen(), 1000);
                if (Rs2Bank.isOpen())
                    return false;
            } else if (Rs2GrandExchange.isOpen()){
                Rs2GrandExchange.closeExchange();
                sleepUntil(() -> !Rs2GrandExchange.isOpen(), 1000);
                if (Rs2GrandExchange.isOpen())
                    return false;
            }

            String itemAction = "";

            // House teleports should target outside for following pathing
            if (itemId == ItemID.TELEPORT_TO_HOUSE)
                itemAction = "outside";

            // Match item action based on the teleport destination
            if (itemAction.isEmpty()){
                var actions = isWearing ? item.getEquipmentActions().toArray(new String[0]) : item.getInventoryActions();
                for (var action : actions){
                    if (action == null)
                        continue;

                    var actionSplits = action.toLowerCase().split(":");
                    if (hasTeleportMatch(actionSplits[actionSplits.length - 1])){
                        itemAction = action;
                        break;
                    }
                }
            }

            // Choose default action if no match found
            if (itemAction.isEmpty()){
                var actions = isWearing ? item.getEquipmentActions().toArray(new String[0]) : item.getInventoryActions();
                for (var action : actions){
                    if (action == null)
                        continue;

                    if (action.equalsIgnoreCase("rub")
                            || action.equalsIgnoreCase("break")
                            || action.equalsIgnoreCase("teleport")) {
                        itemAction = action;
                        break;
                    }
                }
            }

            if (!itemAction.isEmpty()){
                if (isWearing)
                    Rs2Equipment.interact(itemId, itemAction);
                else
                    Rs2Inventory.interact(itemId, itemAction);

                if (itemAction.equalsIgnoreCase("rub")){
                    sleepUntil(() -> Rs2Widget.isWidgetVisible(WidgetInfo.DIALOG_OPTION_OPTIONS), 1000);
                    if (!Rs2Widget.isWidgetVisible(WidgetInfo.DIALOG_OPTION_OPTIONS))
                        return false;

                    var options = Rs2Widget.getWidget(WidgetInfo.DIALOG_OPTION_OPTIONS).getDynamicChildren();
                    for (var option : options){
                        if (hasTeleportMatch(StringUtils.strip(option.getText(), ".").toLowerCase())){
                            Rs2Keyboard.keyPress(option.getOnKeyListener()[7].toString().charAt(0));
                            break;
                        }
                    }
                }

                Rs2Player.waitForAnimation();
                return true;
            }
        }

        return false;
    }

    private boolean hasTeleportMatch(String text){
        return Arrays.stream(text.split(" "))
                .filter(x -> !x.isBlank()
                        && !x.equalsIgnoreCase("xeric's")
                        && !x.equalsIgnoreCase("guild")
                        && !x.equalsIgnoreCase("the"))
                .anyMatch(x -> displayInfo.toLowerCase().contains(x));
    }
}
