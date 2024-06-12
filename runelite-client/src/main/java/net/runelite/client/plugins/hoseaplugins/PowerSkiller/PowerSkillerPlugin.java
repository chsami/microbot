package net.runelite.client.plugins.hoseaplugins.PowerSkiller;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.*;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.query.TileObjectQuery;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.EthanApiPlugin;
import net.runelite.client.plugins.hoseaplugins.ethanapi.InteractionApi.BankInventoryInteraction;
import net.runelite.client.plugins.hoseaplugins.ethanapi.InteractionApi.InventoryInteraction;
import net.runelite.client.plugins.hoseaplugins.ethanapi.InteractionApi.NPCInteraction;
import net.runelite.client.plugins.hoseaplugins.ethanapi.InteractionApi.TileObjectInteraction;
import com.google.inject.Provides;
import net.runelite.client.plugins.hoseaplugins.PiggyUtils.BreakHandler.ReflectBreakHandler;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

import com.google.inject.Inject;
import net.runelite.client.util.Text;
import org.apache.commons.lang3.RandomUtils;

import java.util.*;


@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> Power Skiller</html>",
        description = "Will interact with an object and drop or bank all items when inventory is full",
        tags = {"ethan", "piggy", "skilling"}
)
public class PowerSkillerPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    PowerSkillerConfig config;
    @Inject
    private KeyManager keyManager;
    @Inject
    private ReflectBreakHandler breakHandler;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private PowerSkillerOverlay overlay;
    State state;
    boolean started;

    boolean bankPin = false;

    private int timeout;

    @Override
    protected void startUp() throws Exception {
        bankPin = false;
        breakHandler.registerPlugin(this);
        keyManager.registerKeyListener(toggle);
        this.overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() throws Exception {
        bankPin = false;
        breakHandler.unregisterPlugin(this);
        keyManager.unregisterKeyListener(toggle);
        this.overlayManager.remove(overlay);
    }

    @Provides
    private PowerSkillerConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(PowerSkillerConfig.class);
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        if (!EthanApiPlugin.loggedIn() || !started || breakHandler.isBreakActive(this)) {
            // We do an early return if the user isn't logged in
            return;
        }
        state = getNextState();
        handleState();
    }

    private void handleState() {
        switch (state) {
            case BANK:
                if (Widgets.search().withId(13959169).first().isPresent()) {
                    bankPin = true;
                    return;
                }
                if (Widgets.search().withId(786445).first().isEmpty()) {
                    TileObjects.search().withAction("Bank").nearestToPlayer().ifPresent(tileObject -> {
                        TileObjectInteraction.interact(tileObject, "Bank");
                        return;
                    });
                    NPCs.search().withAction("Bank").nearestToPlayer().ifPresent(npc -> {
                        if (EthanApiPlugin.pathToGoalFromPlayerUsingCustomDangerous(npc.getWorldLocation(), new HashSet<>()) != null) {
                            NPCInteraction.interact(npc, "Bank");
                        }
                        return;
                    });
                    TileObjects.search().withName("Bank chest").nearestToPlayer().ifPresent(tileObject -> {
                        TileObjectInteraction.interact(tileObject, "Use");
                        return;
                    });
                    if (TileObjects.search().withAction("Bank").nearestToPlayer().isEmpty() && NPCs.search().withAction("Bank").nearestToPlayer().isEmpty()) {
                        EthanApiPlugin.sendClientMessage("Bank is not found, move to an area with a bank.");
                    }

                    return;

                }
                List<Widget> items = BankInventory.search().result();
                for (Widget item : items) {
                    if (!isTool(item.getName().toLowerCase()) && !shouldKeep(item.getName().toLowerCase())) {
                        BankInventoryInteraction.useItem(item, "Deposit-All");
                        return;
                    }
                }
                break;
            case TIMEOUT:
                timeout--;
                break;
            case FIND_OBJECT:
                if (config.searchNpc()) {
                    findNpc();
                } else {
                    findObject();
                }
                setTimeout();
                break;
            case DROP_ITEMS:
                dropItems();
                break;
        }
    }

    private State getNextState() {
        // self-explanatory, we just return a State if the conditions are met.
        if (EthanApiPlugin.isMoving() || client.getLocalPlayer().getAnimation() != -1) {
            // this is to prevent clicks while animating/moving.
            return State.ANIMATING;
        }

        if (!hasTools()) {
            // if the user doesn't have tools we don't want it to do anything at all lol, maybe stop the plugin if you want.
            return State.MISSING_TOOLS;
        }
        if (timeout > 0) {
            return State.TIMEOUT;
        }

        if (shouldBank() && Inventory.full() || (Bank.isOpen() && !isInventoryReset())) {
            if (shouldBank() && !isInventoryReset()) {
                return State.BANK;
            }
        }

        if ((isDroppingItems() && !isInventoryReset()) || !shouldBank() && Inventory.full()) {
            // if the user should be dropping items, we'll check if they're done
            // should sit at this state til it's finished.
            return State.DROP_ITEMS;
        }


        // default it'll look for an object.
        return State.FIND_OBJECT;
    }

    private void findObject() {
        String objectName = config.objectToInteract();
        if (config.useForestryTreeNotClosest() && config.expectedAction().equalsIgnoreCase("chop")) {
            TileObjects.search().withName(objectName).nearestToPoint(getObjectWMostPlayers()).ifPresent(tileObject -> {
                ObjectComposition comp = TileObjectQuery.getObjectComposition(tileObject);
                TileObjectInteraction.interact(tileObject, comp.getActions()[0]);
            });
        } else {
            TileObjects.search().withName(objectName).nearestToPlayer().ifPresent(tileObject -> {
                ObjectComposition comp = TileObjectQuery.getObjectComposition(tileObject);
                TileObjectInteraction.interact(tileObject, comp.getActions()[0]); // find the object we're looking for.  this specific example will only work if the first Action the object has is the one that interacts with it.
                // don't *always* do this, you can manually type the possible actions. eg. "Mine", "Chop", "Cook", "Climb".
            });
        }
    }

    /**
     * Tile w most players on it within 2 tiles of the object we're looking for
     *
     * @return That tile or the player's tile if failed(such as doing forestry option when alone by trees)
     */
    public WorldPoint getObjectWMostPlayers() {
        String objectName = config.objectToInteract();
        Map<WorldPoint, Integer> playerCounts = new HashMap<>();
        WorldPoint mostPlayersTile = null;
        int highestCount = 0;
        List<TileObject> objects = TileObjects.search().withName(objectName).result();

        List<Player> players = Players.search().notLocalPlayer().result();

        for (TileObject object : objects) {
            for (Player player : players) {
                if (player.getWorldLocation().distanceTo(object.getWorldLocation()) <= 2) {
                    WorldPoint playerTile = player.getWorldLocation();
                    playerCounts.put(playerTile, playerCounts.getOrDefault(playerTile, 0) + 1);
                    if (playerCounts.get(playerTile) > highestCount) {
                        highestCount = playerCounts.get(playerTile);
                        mostPlayersTile = playerTile;
                    }
                }
            }
        }

        return mostPlayersTile == null ? client.getLocalPlayer().getWorldLocation() : mostPlayersTile;
    }


    private void findNpc() {
        String npcName = config.objectToInteract();
        NPCs.search().withName(npcName).nearestToPlayer().ifPresent(npc -> {
            NPCComposition comp = client.getNpcDefinition(npc.getId());
            if (Arrays.stream(comp.getActions())
                    .filter(Objects::nonNull)
                    .anyMatch(action -> action.equalsIgnoreCase(config.expectedAction()))) {
                NPCInteraction.interact(npc, config.expectedAction()); // For fishing spots ?
            } else {
                NPCInteraction.interact(npc, comp.getActions()[0]);
            }

        });
    }

    private void dropItems() {
        List<Widget> itemsToDrop = Inventory.search()
                .filter(item -> !shouldKeep(item.getName()) && !isTool(item.getName())).result(); // filter the inventory to only get the items we want to drop

        for (int i = 0; i < Math.min(itemsToDrop.size(), RandomUtils.nextInt(config.dropPerTickOne(), config.dropPerTickTwo())); i++) {
            InventoryInteraction.useItem(itemsToDrop.get(i), "Drop"); // we'll loop through this at a max of 10 times.  can make this a config options.  drops x items per tick (x = 10 in this example)
        }
    }

    private boolean isInventoryReset() {
        List<Widget> inventory = Inventory.search().result();
        for (Widget item : inventory) {
            if (!shouldKeep(Text.removeTags(item.getName()))) { // using our shouldKeep method, we can filter the items here to only include the ones we want to drop.
                return false;
            }
        }
        return true; // we will know that the inventory is reset because the inventory only contains items we want to keep
    }

    private boolean isDroppingItems() {
        return state == State.DROP_ITEMS; // if the user is dropping items, we don't want it to proceed until they're all dropped.
    }


    private boolean shouldKeep(String name) {
        List<String> itemsToKeep = new ArrayList<>(List.of(config.itemsToKeep().split(","))); // split the items listed by comma. and add them to a list.
        itemsToKeep.addAll(List.of(config.toolsToUse().split(","))); //We must also check if the tools are included in the Inventory, Rather than equipped, so they are added here
        return itemsToKeep.stream()// stream the List using Collection.stream() from java.util
                .anyMatch(i -> Text.removeTags(name.toLowerCase()).contains(i.toLowerCase()));
        // we'll set everything to lowercase as well as remove the html tags that is included (The color of the item in game),
        // and check if the input name contains any of the items in the itemsToKeep list.
        // might seem silly, but this is to allow specific items you want to keep without typing the full name.
        // We also prefer names to ids here, but you can change this if you like.
    }

    private boolean hasTools() {
        //Updated from https://github.com/moneyprinterbrrr/ImpactPlugins/blob/experimental/src/main/java/com/impact/PowerGather/PowerGatherPlugin.java#L196
        //Big thanks hawkkkkkk
        String[] tools = config.toolsToUse().split(","); // split the tools listed by comma, no space.

        int numInventoryTools = Inventory.search()
                .filter(item -> isTool(item.getName())) // filter inventory by using out isTool method
                .result().size();
        int numEquippedTools = Equipment.search()
                .filter(item -> isTool(item.getName())) // filter inventory by using out isTool method
                .result().size();

        return numInventoryTools + numEquippedTools >= tools.length; // if the size of tools and the filtered inventory is the same, we have our tools.
    }

    private void setTimeout() {
        timeout = RandomUtils.nextInt(config.tickdelayMin(), config.tickDelayMax());
    }

    private boolean isTool(String name) {
        String[] tools = config.toolsToUse().split(","); // split the tools listed by comma, no space.

        return Arrays.stream(tools) // stream the array using Arrays.stream() from java.util
                .anyMatch(i -> name.toLowerCase().contains(i.toLowerCase())); // more likely for user error than the shouldKeep option, but we'll follow the same idea as shouldKeep.
    }

    private final HotkeyListener toggle = new HotkeyListener(() -> config.toggle()) {
        @Override
        public void hotkeyPressed() {
            toggle();
        }
    };

    private boolean shouldBank() {
        return config.shouldBank() &&
                (NPCs.search().withAction("Bank").first().isPresent() || TileObjects.search().withAction("Bank").first().isPresent()
                || TileObjects.search().withAction("Collect").first().isPresent() && !bankPin);
    }

    public void toggle() {
        if (!EthanApiPlugin.loggedIn()) {
            return;
        }
        started = !started;
        if(!started){
            breakHandler.stopPlugin(this);
        }else{
            breakHandler.startPlugin(this);
        }
    }
}
