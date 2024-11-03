package net.runelite.client.plugins.microbot.mahoganyhomez;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.coords.Rs2WorldPoint;
import net.runelite.client.plugins.microbot.util.dialogues.Rs2Dialogue;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tile.Rs2Tile;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.walker.WalkerState;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class MahoganyHomesScript extends Script {

    @Inject
    MahoganyHomesPlugin plugin;

    public boolean run(MahoganyHomesConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) return;
                fix();
                finish();
                getNewContract();
                bank();
                walkToHome();


            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }

    private List<GameObject> getFixableObjects() {
        List<GameObject> objects = plugin.getObjectsToMark();
        List<Hotspot> fixableHotspots = Hotspot.getBrokenHotspots();
        HotspotObjects hotspotObjects = plugin.getCurrentHome().getHotspotObjects();

        // Precompute the set of IDs
        Set<Integer> ids = fixableHotspots.stream()
                .map(hotspot -> hotspotObjects.objects[hotspot.ordinal()].getObjectId())
                .collect(Collectors.toSet());

        // Filter using the precomputed set
        return objects.stream()
                .filter(Objects::nonNull)
                .filter(o -> ids.contains(o.getId()))
                .collect(Collectors.toList());
    }

    // Tasks section

    private void fix() {
        if (plugin.getCurrentHome() == null
                || !plugin.getCurrentHome().isInside(Rs2Player.getWorldLocation())
                || Hotspot.isEverythingFixed()) {
            return;
        }

        Rs2WorldPoint playerLocation = new Rs2WorldPoint(Rs2Player.getWorldLocation());
        MahoganyHomesOverlay.setFixableObjects(getFixableObjects());

        // Sort fixable objects by plane and distance
        List<GameObject> sortedObjects = getFixableObjects().stream()
                .sorted(Comparator.comparingInt(TileObject::getPlane))
                .collect(Collectors.toList());


        GameObject object = sortedObjects.get(0);

        // Find the closest walkable tile around the object
        WorldPoint closestWalkableObjectTile = Rs2Tile.getWalkableTilesAroundTile(object.getWorldLocation(), 2).stream()
                .min(Comparator.comparingInt(wp -> Rs2Player.getWorldLocation().distanceTo(wp))
                )
                .orElse(null);
            Rs2WorldPoint objectLocation = new Rs2WorldPoint(closestWalkableObjectTile != null ? closestWalkableObjectTile : object.getWorldLocation());

        int pathDistance = objectLocation.distanceToPath(Microbot.getClient(), Rs2Player.getWorldLocation());
        Microbot.log("Local Path Distance: " + pathDistance);

        if (pathDistance > 20) {
            if (openDoorToObject(object, objectLocation)) {
                return;
            }
            Microbot.log("Local Path Distance is too far or unreachable, switching to WebWalker.");
            WalkerState state = Rs2Walker.walkWithState(object.getWorldLocation(), 2);
            if (state == WalkerState.UNREACHABLE) {
                if (Rs2Player.getWorldLocation().getPlane() != object.getWorldLocation().getPlane()) {
                    tryToUseLadder();
                    return;
                } else {
                    Microbot.log("All pathing failed, trying to interact anyways.");
                    interactWithObject(object);
                }
            } else if (state == WalkerState.ARRIVED) {
                sleep(1200, 2200);
            }
            return;
        }

        Rs2Walker.setTarget(null);
        interactWithObject(object);
    }

    private void interactWithObject(GameObject object) {
        String action = Objects.requireNonNull(Hotspot.getByObjectId(object.getId())).getRequiredAction();
        if (Rs2GameObject.interact(object, action)) {
            sleepUntil(() -> !action.equals(Objects.requireNonNull(Hotspot.getByObjectId(object.getId())).getRequiredAction()), 5000);
        }
        sleep(800, 1200);
    }

    private boolean openDoorToObject(GameObject object, Rs2WorldPoint objectLocation) {
        if (Rs2Player.getWorldLocation().getPlane() != object.getWorldLocation().getPlane()) {
            return false;
        }
        Microbot.log("Local Path seems to be blocked, checking for doors to open.");
        List<TileObject> doors = Rs2GameObject.getAll().stream()
                .filter(Objects::nonNull)
                .filter(w -> w.getWorldLocation().distanceTo2D(Rs2Player.getWorldLocation()) < 10)
                .filter(w -> "Door".equals(Objects.requireNonNull(Rs2GameObject.getObjectComposition(w.getId())).getName()))
                .collect(Collectors.toList());

        log.info("Found {} doors", doors.size());
        log.info("Doors: {}", doors);

        for (TileObject door : doors) {
            ObjectComposition doorComp = Rs2GameObject.getObjectComposition(door.getId());
            List<String> actions = null;
            if (doorComp != null) {
                actions = Arrays.asList(doorComp.getActions());
            }
            if (actions != null && actions.contains("Open")) {
                List<WorldPoint> walkableTiles = Rs2Tile.getWalkableTilesAroundTile(door.getWorldLocation(), 2);
                WorldPoint closestTile = walkableTiles.stream()
                        .min(Comparator.comparingInt(wp -> objectLocation.distanceToPath(Microbot.getClient(), wp)))
                        .orElse(null);
                if (closestTile != null && objectLocation.distanceToPath(Microbot.getClient(), closestTile) < 10) {
                    Microbot.log("Opening door to get to object");
                    if (Rs2GameObject.interact(door, "Open")) {
                        Rs2Player.waitForWalking();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void tryToUseLadder() {
        Microbot.log("Walker missing transport, trying to find ladder manually.");
        TileObject closestLadder = Rs2GameObject.findObject(plugin.getCurrentHome().getLadders());
        Rs2GameObject.interact(closestLadder);
        sleep(600, 1200);
    }


    // Finish by talking to the NPC
    private void finish() {
        if(plugin.getCurrentHome() != null
        && plugin.getCurrentHome().isInside(Rs2Player.getWorldLocation())
        && Hotspot.isEverythingFixed()) {
            NPC npc = Rs2Npc.getNpc(plugin.getCurrentHome().getNpcId());
            if(npc == null && Rs2Player.getWorldLocation().getPlane()>0) {
                Microbot.log("We are on the wrong floor, Trying to find ladder to go down");
                TileObject closestLadder = Rs2GameObject.findObject(plugin.getCurrentHome().getLadders());
                if(Rs2GameObject.interact(closestLadder))
                    sleepUntil(
                            () -> Rs2Player.getWorldLocation().getPlane() == 0
                            , 5000);
                sleep(600,1200);
                return;
            }
            if(npc != null) {
                Rs2WorldPoint npcLocation = new Rs2WorldPoint(npc.getWorldLocation());
                Microbot.log("Local NPC path distance: " + npcLocation.distanceToPath(Microbot.getClient(), Rs2Player.getWorldLocation()));
                if(npcLocation.distanceToPath(Microbot.getClient(), Rs2Player.getWorldLocation()) < 10){
                    if(Rs2Npc.interact(npc, "Talk-to")) {
                        Microbot.log("Getting reward from NPC");
                        sleepUntil(Rs2Dialogue::hasContinue, 10000);
                        sleepUntil(() -> !Rs2Player.isInteracting() , 5000);
                        sleep(1200,2200);

                    }
                } else {
                    Microbot.log("Local NPC path distance is too far, switching to WebWalker.");
                    Rs2Walker.walkTo(npc.getWorldLocation());
                    sleep(1200,2200);
                }
            }
        }
    }

    // Get new contract
    private void getNewContract() {
        if(plugin.getCurrentHome() == null) {
            WorldPoint contractLocation = getClosestContractLocation();
            if(contractLocation.distanceTo2D(Rs2Player.getWorldLocation()) > 10) {
                Microbot.log("Walking to contract NPC");
                Rs2Walker.walkWithState(contractLocation,5);

            }
            else {
                Microbot.log("Getting new contract");
                List<NPC> allNpcs = Rs2Npc.getNpcs().filter(n -> n.getWorldLocation().distanceTo(Rs2Player.getWorldLocation())<10).collect(Collectors.toList());
                NPC finalNpc = null;
                for(NPC npc : allNpcs) {
                    // Mahogany Homes NPCs require both composition and transform to get correct actions
                    if(Arrays.toString(npc.getComposition().transform().getActions()).contains("Contract")) {
                        finalNpc = npc;
                        break;
                    }
                }


                NPC npc = finalNpc;
                Microbot.log("NPC found: " + (npc != null ? npc.getComposition().transform().getName() : null));
                if (Rs2Npc.interact(npc, "Contract")) {
                    sleepUntil(Rs2Dialogue::hasSelectAnOption, 5000);
                    sleep(1200, 2200);
                    Rs2Dialogue.keyPressForDialogueOption(plugin.getConfig().currentTier().getPlankSelection().getChatOption());
                    sleepUntil(() -> !Rs2Dialogue.hasSelectAnOption(), 5000);
                    sleep(1200, 2200);
                }

            }

        }

    }

    // Bank if we need to
    private void bank() {
        if(plugin.getCurrentHome() != null
        && !plugin.getCurrentHome().isInside(Rs2Player.getWorldLocation())
        && isMissingItems()) {
            if(Rs2Bank.walkToBankAndUseBank()){
                sleep(600,1200);
                if(Rs2Inventory.getEmptySlots()-steelBarsNeeded() > 0)
                    Rs2Bank.withdrawX(plugin.getConfig().currentTier().getPlankSelection().getPlankId(),Rs2Inventory.getEmptySlots()-steelBarsNeeded());
                sleep(600,1200);
                if(steelBarsNeeded()>steelBarsInInventory()) {
                    Rs2Bank.withdrawX(ItemID.STEEL_BAR,steelBarsNeeded());
                    sleep(600,1200);
                }
                Rs2Bank.closeBank();
                return;
            }
            sleep(600,1200);
        }
    }

    // Walk to current home
    private void walkToHome() {
        if(plugin.getCurrentHome() != null
        && !plugin.getCurrentHome().isInside(Rs2Player.getWorldLocation())
        && !isMissingItems()) {
            Rs2Walker.walkWithState(plugin.getCurrentHome().getLocation(),3);
        }
    }

    private boolean isMissingItems() {
        return planksInInventory() < planksNeeded()
                || steelBarsInInventory() < steelBarsNeeded();
    }

    private int planksNeeded() {
        return plugin.getCurrentHome().getRequiredPlanks(plugin.getContractTier());
    }

    private int steelBarsNeeded() {
        return plugin.getCurrentHome().getRequiredSteelBars(plugin.getContractTier());
    }

    private int planksInInventory() {
        return Rs2Inventory.count(plugin.getConfig().currentTier().getPlankSelection().getPlankId());
    }

    private int steelBarsInInventory() {
        return Rs2Inventory.count(ItemID.STEEL_BAR);
    }

    // Get closest contract location
    private WorldPoint getClosestContractLocation() {
        List<WorldPoint> contractLocations = new ArrayList<>();
        contractLocations.add(ContractLocation.MAHOGANY_HOMES_ARDOUGNE.getLocation());
        contractLocations.add(ContractLocation.MAHOGANY_HOMES_FALADOR.getLocation());
        contractLocations.add(ContractLocation.MAHOGANY_HOMES_HOSIDIUS.getLocation());
        contractLocations.add(ContractLocation.MAHOGANY_HOMES_VARROCK.getLocation());

        return contractLocations.stream()
                .min(Comparator.comparingInt(wp -> wp.distanceTo2D(Rs2Player.getWorldLocation())))
                .orElse(null);
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }
}
