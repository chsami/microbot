package net.runelite.client.plugins.microbot.bankjs.development.BanksSlayer;

import com.google.common.annotations.VisibleForTesting;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.bankjs.development.BanksShopper.Actions;
import net.runelite.client.plugins.microbot.bankjs.development.BanksShopper.Quantities;
import net.runelite.client.plugins.microbot.bankjs.development.BanksSlayer.Utils.Rs2Teleport;
import net.runelite.client.plugins.microbot.bankjs.development.BanksSlayer.Utils.SlayerMasterLocations;
import net.runelite.client.plugins.microbot.bankjs.development.BanksSlayer.Utils.SlayerMonsterLocations;
import net.runelite.client.plugins.microbot.bankjs.development.BanksSlayer.enums.SlayerMasters;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.shop.Rs2Shop;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.slayer.SlayerConfig;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BanksSlayerScript extends Script {
    @Inject
    private BanksSlayerConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private Client client;
    @Inject
    private ConfigManager configManager;
    @Inject
    private ClientThread clientThread;

    private String taskName;
    private int taskId;
    private int amount;
    private int areaId;
    private String taskLocation;
    private boolean loginFlag;

    public static List<NPC> attackableNpcs = new ArrayList();
    public static Actor currentNpc = null;

    // Map to store the mapping between task names and NPC names
    private static final Map<String, String> TASK_NPC_MAPPING = new HashMap<>();

    static {
        TASK_NPC_MAPPING.put("Dwarves", "Dwarf");
        TASK_NPC_MAPPING.put("Goblins", "Goblin");
        TASK_NPC_MAPPING.put("Bats", "Bat");
        TASK_NPC_MAPPING.put("Wolves", "Wolf");
        TASK_NPC_MAPPING.put("Icefiends", "Icefiend");
        // Add more mappings as needed
    }

    public static double version = 1.0;

    public boolean run(BanksSlayerConfig config) {
        this.config = config;

        SlayerMasters selectedSlayerMaster = config.slayerMaster();
        String slayerMaster = selectedSlayerMaster.toString();

        Microbot.enableAutoRunOn = true;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (client.getGameState() == GameState.LOGGED_IN) {
                    loginFlag = true;
                    clientThread.invoke(this::storeCurrentTaskInMemory);
                }

                if (!hasTask()) {
                    // Get a New Task
                    if (!isCloseToSlayerMaster()) {
                        System.out.println("Travelling to Slayer Master");
                        if (travelToSlayerMaster("Turael")) {
                            System.out.println("Arrived at Turael");
                        } else {
                            System.out.println("Failed to reach Turael!");
                        }
                    }
                    System.out.println("Getting new Assignment");
                    getNewAssignment("Turael");
                } else {
                    // We have a task
                    if (!isCloseToMonster(taskName) && travelToMonster(taskName)) {
                        // If we haven't arrived at the monster's location, travel to it
                        System.out.println("Travelling to " + taskName);
                    } else {
                        System.out.println("Arrived at: " + taskName);
                        // If we've arrived at the monster's location, attack it
                        attackMonster(taskName);
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace(); // Print the full stack trace for debugging
                System.out.println("Error: " + ex.getMessage());
            }
        }, 0, 2000, TimeUnit.MILLISECONDS);
        return true;
    }


    private void storeCurrentTaskInMemory() {
        try {
            this.amount = client.getVarpValue(VarPlayer.SLAYER_TASK_SIZE);
            if (amount > 0) {
                this.taskId = client.getVarpValue(VarPlayer.SLAYER_TASK_CREATURE);
                if (taskId == 98) /* Bosses */ {
                    int structId = client.getEnum(EnumID.SLAYER_TASK)
                            .getIntValue(client.getVarbitValue(Varbits.SLAYER_TASK_BOSS));
                    this.taskName = client.getStructComposition(structId)
                            .getStringValue(ParamID.SLAYER_TASK_NAME);
                } else {
                    this.taskName = client.getEnum(EnumID.SLAYER_TASK_CREATURE)
                            .getStringValue(taskId);
                }

                this.areaId = client.getVarpValue(VarPlayer.SLAYER_TASK_LOCATION);
                this.taskLocation = null;
                if (areaId > 0) {
                    taskLocation = client.getEnum(EnumID.SLAYER_TASK_LOCATION)
                            .getStringValue(areaId);
                }

                System.out.println("Current Task: " + taskName);
                System.out.println("Amount Left: " + amount);
                if (taskLocation != null) {
                    System.out.println("Task Location: " + taskLocation);
                }
            } else {
                System.out.println("No Task, Need one.");
            }
        } catch (Exception ex) {
            ex.printStackTrace(); // Print the full stack trace for debugging
            System.out.println("Error storing current task: " + ex.getMessage());
        }
    }

    private boolean hasTask() {
        try {
            return amount > 0;
        } catch (Exception ex) {
            ex.printStackTrace(); // Print the full stack trace for debugging
            System.out.println("Error checking if has task: " + ex.getMessage());
            return false;
        }
    }

    private boolean isCloseToSlayerMaster() {
        try {
            WorldPoint slayerMasterLocation = SlayerMasterLocations.Turael;
            // Get the player's current location
            WorldPoint playerLocation = getPlayerLocation();

            // Calculate the distance between the player's location and the Slayer Master's location
            int distance = playerLocation.distanceTo2D(slayerMasterLocation);
            System.out.println("Distance: " + distance);

            // Check if the distance is within a certain threshold (e.g., 5 tiles)
            int threshold = 2; // Change this value as needed
            return distance <= threshold;
        } catch (Exception ex) {
            ex.printStackTrace(); // Print the full stack trace for debugging
            System.out.println("Error checking if close to Slayer Master: " + ex.getMessage());
            return false;
        }
    }


    private WorldPoint getPlayerLocation() {
        try {
            System.out.println(Microbot.getClient().getLocalPlayer().getWorldLocation());
            return Microbot.getClient().getLocalPlayer().getWorldLocation();
        } catch (Exception ex) {
            ex.printStackTrace(); // Print the full stack trace for debugging
            System.out.println("Error getting player location: " + ex.getMessage());
            return null;
        }
    }

    private boolean travelToSlayerMaster(String slayerMaster) {
        try {
            switch (slayerMaster) {
                case "Turael":
                    Rs2Walker.walkTo(SlayerMasterLocations.Turael);
                    System.out.println("Walking to Turael!");
                    break;
                default:
                    System.out.println("Invalid Slayer Master Provided.");
                    break;
            }

            return true;
        } catch (Exception ex) {
            ex.printStackTrace(); // Print the full stack trace for debugging
            System.out.println("Error traveling to Slayer Master: " + ex.getMessage());
            return false;
        }
    }

    private boolean getNewAssignment(String slayerMaster) {
        try {
            NPC npc = Rs2Npc.getNpc(slayerMaster);
            System.out.println("NPC: " + npc);

            if (npc == null) {
                System.out.println("No Slayer Master in sight.");
            } else {
                Rs2Npc.interact(npc, "Assignment");
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace(); // Print the full stack trace for debugging
            System.out.println("Error getting new assignment: " + ex.getMessage());
            return false;
        }
    }

    private boolean isCloseToMonster(String taskName) {
        try {
            WorldPoint monsterLocation;
            switch (taskName) {
                case "Dwarves":
                    monsterLocation = SlayerMonsterLocations.Dwarves;
                    break;
                case "Goblins":
                    monsterLocation = SlayerMonsterLocations.Goblins;
                    break;
                case "Bats":
                    monsterLocation = SlayerMonsterLocations.Bats;
                    break;
                case "Wolves":
                    monsterLocation = SlayerMonsterLocations.Wolves;
                    break;
                case "Icefiends":
                    monsterLocation = SlayerMonsterLocations.Icefiends;
                    break;
                // Add more cases for other monster locations as needed
                default:
                    // If taskName is not recognized, assume not close to monster
                    System.out.println("Task not recognised.");
                    return false;
            }

            // Get the player's current location
            WorldPoint playerLocation = getPlayerLocation();

            // Calculate the distance between the player's location and the monster's location
            int distance = playerLocation.distanceTo(monsterLocation);

            // Check if the distance is within a certain threshold (e.g., 5 tiles)
            int threshold = 15; // Change this value as needed
            return distance <= threshold;
        } catch (Exception ex) {
            ex.printStackTrace(); // Print the full stack trace for debugging
            System.out.println("Error checking if close to monster: " + ex.getMessage());
            return false;
        }
    }


    private boolean travelToMonster(String taskName) {
        try {
            switch (taskName) {
                case "Dwarves":
                    Rs2Walker.walkTo(SlayerMonsterLocations.Dwarves);
                    break;
                case "Goblins":
                    Rs2Walker.walkTo(SlayerMonsterLocations.Goblins);
                    break;
                case "Bats":
                    Rs2Walker.walkTo(SlayerMonsterLocations.Bats);
                    break;
                case "Wolves":
                    Rs2Walker.walkTo(SlayerMonsterLocations.Wolves);
                    break;
                case "Icefiends":
                    Rs2Walker.walkTo(SlayerMonsterLocations.Icefiends);
                    break;
                default:
                    System.out.println("No Task Name Specified");
                    return false;
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace(); // Print the full stack trace for debugging
            System.out.println("Error traveling to monster: " + ex.getMessage());
            return false;
        }
    }

    private void attackMonster(String taskName) {
        if (!hasTask()) {
            return;
        }

        try {
            double treshHold = (double) (Microbot.getClient().getBoostedSkillLevel(Skill.HITPOINTS) * 100) / Microbot.getClient().getRealSkillLevel(Skill.HITPOINTS);
            if (Rs2Inventory.getInventoryFood().isEmpty() && treshHold < 10) return;

            // Retrieve the corresponding NPC name from the map
            String npcName = TASK_NPC_MAPPING.getOrDefault(taskName, taskName);
            System.out.println("Expected NPC Name: " + npcName); // Debug log

            attackableNpcs = Microbot.getClient().getNpcs().stream()
                    .sorted(Comparator.comparingInt(value -> value.getLocalLocation().distanceTo(Microbot.getClient().getLocalPlayer().getLocalLocation())))
                    .filter(x -> {
                        boolean result = !x.isDead()
                                // && x.getWorldLocation().distanceTo(getInitialPlayerLocation()) < 10
                                && (!x.isInteracting() || x.getInteracting() == Microbot.getClient().getLocalPlayer())
                                && (x.getInteracting() == null || x.getInteracting() == Microbot.getClient().getLocalPlayer())
                                && x.getAnimation() == -1 && x.getName().equalsIgnoreCase(npcName); // Use the NPC name retrieved from the map
                        if (!result) {
                            System.out.println("Filtered out NPC: " + x.getName()); // Debug log
                        }
                        System.out.println("Result: " + result);
                        return result;
                    })
                    .collect(Collectors.toList());

            if (Rs2Combat.inCombat()) {
                return;
            }

            if (attackableNpcs.isEmpty()) {
                System.out.println("No attackable NPCs found!"); // Debug log
                return;
            }
            System.out.println("Attackable NPS: " + attackableNpcs);

            for (NPC npc : attackableNpcs) {
                if (npc == null
                        || npc.getAnimation() != -1
                        || npc.isDead()
                        || (npc.getInteracting() != null && npc.getInteracting() != Microbot.getClient().getLocalPlayer())
                        || (npc.isInteracting() && npc.getInteracting() != Microbot.getClient().getLocalPlayer())
                        || !npc.getName().equalsIgnoreCase(npcName)) // Use the NPC name retrieved from the map
                    break;
                //if (npc.getWorldLocation().distanceTo(getInitialPlayerLocation()) > 8)
                //  break;
                if (!Rs2Camera.isTileOnScreen(npc.getLocalLocation()))
                    Rs2Camera.turnTo(npc);

                if (!Rs2Npc.hasLineOfSight(npc))
                    continue;
                Rs2Npc.interact(npc, "attack");
                sleepUntil(() -> Microbot.getClient().getLocalPlayer().isInteracting() && Microbot.getClient().getLocalPlayer().getInteracting() instanceof NPC);
                break;
            }
        } catch (Exception ex) {
            ex.printStackTrace(); // Print the full stack trace for debugging
            System.out.println("Error attacking monster: " + ex.getMessage());
        }
    }
}
