package net.runelite.client.plugins.microbot;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.StatChanged;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ProfileManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.NPCManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.game.WorldService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.cooking.CookingScript;
import net.runelite.client.plugins.microbot.quest.QuestScript;
import net.runelite.client.plugins.microbot.thieving.ThievingScript;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.math.Calculations;
import net.runelite.client.plugins.microbot.util.menu.Rs2Menu;
import net.runelite.client.plugins.microbot.util.mouse.VirtualMouse;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.walker.Walker;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

import java.awt.AWTException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static net.runelite.client.plugins.microbot.util.Global.sleep;

@PluginDescriptor(
        name = "Microbot - do not turn this off",
        description = "Microbot do not turn this off",
        tags = {"main", "microbot", "parent"}
)
@Slf4j
public class MicrobotPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    Notifier notifier;
    @Inject
    WorldService worldService;
    @Inject
    ProfileManager profileManager;
    @Inject
    ItemManager itemManager;
    @Inject
    NPCManager npcManager;
    @Inject
    private MicrobotOverlay microbotOverlay;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private SpriteManager spriteManager;

    public ThievingScript thievingScript;
    public CookingScript cookingScript;

    QuestScript questScript;
    @Override
    protected void startUp() throws AWTException {
        Microbot.pauseAllScripts = false;
        Microbot.setClient(client);
        Microbot.setClientThread(clientThread);
        Microbot.setNotifier(notifier);
        Microbot.setWorldService(worldService);
        Microbot.setProfileManager(profileManager);
        Microbot.setItemManager(itemManager);
        Microbot.setNpcManager(npcManager);
        Microbot.setWalker(new Walker());
        Microbot.setMouse(new VirtualMouse());
        Microbot.setSpriteManager(spriteManager);
        if (overlayManager != null) {
            overlayManager.add(microbotOverlay);
        }
    }

    protected void shutDown() {
        overlayManager.remove(microbotOverlay);
        Microbot.setWalker(null);
        if (cookingScript != null) {
            cookingScript.shutdown();
            cookingScript = null;
        }
        if (thievingScript != null) {
            thievingScript.shutdown();
            thievingScript = null;
        }
    }


    @Subscribe
    public void onMenuEntryAdded(MenuEntryAdded event) {
        //System.out.println(Arrays.deepToString(Arrays.stream(client.getMenuEntries()).toArray(MenuEntry[]::new)));
        if (Rs2Menu.getOption().length() > 0) {
            final MenuEntry[] menuEntries = client.getMenuEntries();
            client.setMenuEntries(new MenuEntry[]{});
            if (Arrays.stream(menuEntries).anyMatch(x -> x.getOption() != null && x.getOption().toLowerCase().equals(Rs2Menu.getOption().toLowerCase()))) {
                client.setMenuEntries(Arrays.stream(menuEntries).filter(x -> x.getOption().toLowerCase().equals(Rs2Menu.getOption().toLowerCase())).toArray(MenuEntry[]::new));
            }
        }
        if (Rs2Npc.npcInteraction != null) {
            final MenuEntry[] menuEntries = client.getMenuEntries();
            client.setMenuEntries(new MenuEntry[]{});
            if (Arrays.stream(menuEntries).anyMatch(x -> x.getOption() != null && x.getOption().toLowerCase().equals(Rs2Npc.npcAction.toLowerCase()) && x.getIdentifier() == Rs2Npc.npcInteraction.getIndex())) {
                client.setMenuEntries(Arrays.stream(menuEntries).filter(x -> x.getOption().toLowerCase().equals(Rs2Npc.npcAction.toLowerCase()) && x.getIdentifier() == Rs2Npc.npcInteraction.getIndex()).toArray(MenuEntry[]::new));
            }
        }
        if (Rs2Bank.objectToBank != null) {
            final MenuEntry[] menuEntries = client.getMenuEntries();
            client.setMenuEntries(new MenuEntry[]{});
            if (Arrays.stream(menuEntries).anyMatch(x -> x.getOption() != null && x.getOption().toLowerCase().equals("bank".toLowerCase()))) {
                client.setMenuEntries(Arrays.stream(menuEntries).filter(x -> x.getOption().toLowerCase().equals("bank".toLowerCase())).toArray(MenuEntry[]::new));
            }
        }
        if (Rs2GameObject.objectToInteract != null) {
            final MenuEntry[] menuEntries = client.getMenuEntries();
            client.setMenuEntries(new MenuEntry[]{});
            if (Arrays.stream(menuEntries).anyMatch(x -> x.getOption() != null && x.getOption().toLowerCase().equals(Rs2GameObject.objectAction.toLowerCase()))) {
                client.setMenuEntries(Arrays.stream(menuEntries).filter(x -> x.getOption().toLowerCase().equals(Rs2GameObject.objectAction.toLowerCase())).toArray(MenuEntry[]::new));
            }
        }
    }

    @Subscribe
    public void onStatChanged(StatChanged statChanged) {
        Microbot.setIsGainingExp(true);
    }

    private Consumer<MenuEntry> menuActionNpcConsumer(boolean shift, net.runelite.api.NPC npc) {
        return e ->
        {
            if (thievingScript == null) {
                thievingScript = new ThievingScript();
                thievingScript.run(npc);
            } else {
                thievingScript.shutdown();
                thievingScript = null;
            }
        };
    }

    private Consumer<MenuEntry> menuActionGameObjectConsumer(int gameObjectId) {
        return e ->
        {
            if (cookingScript == null) {
                cookingScript = new CookingScript();
                cookingScript.run(gameObjectId);
            } else {
                cookingScript.shutdown();
                cookingScript = null;
            }
        };
    }

    @Subscribe
    public void onMenuOpened(MenuOpened event) {
        MenuEntry[] entries = event.getMenuEntries();
        MenuEntry npcEntry = Arrays.stream(entries).filter(x -> x.getType() == MenuAction.EXAMINE_NPC).findFirst().orElse(null);
        MenuEntry objectEntry = Arrays.stream(entries).filter(x -> x.getType() == MenuAction.EXAMINE_OBJECT).findFirst().orElse(null);
        if (npcEntry != null) {
            net.runelite.api.NPC npc = Rs2Npc.getNpcByIndex(npcEntry.getIdentifier());

            List<MenuEntry> leftClickMenus = new ArrayList<>(entries.length + 2);

            leftClickMenus.add(Microbot.getClient().createMenuEntry(0)
                    .setOption(thievingScript == null ? "Start AutoThiever" : "Stop AutoThiever")
                    .setType(MenuAction.RUNELITE)
                    .onClick(menuActionNpcConsumer(false, npc)));
        }
        if (objectEntry != null) {
            // Currently only supports alkharid furnace
            if (objectEntry.getIdentifier() == ObjectID.RANGE_26181) {

                List<MenuEntry> leftClickMenus = new ArrayList<>(entries.length + 2);

                leftClickMenus.add(Microbot.getClient().createMenuEntry(0)
                        .setOption(cookingScript == null ? "Start AutoCooker" : "Stop AutoCooker")
                        .setType(MenuAction.RUNELITE)
                        .onClick(menuActionGameObjectConsumer( objectEntry.getIdentifier())));
            }
        }
    }
}
