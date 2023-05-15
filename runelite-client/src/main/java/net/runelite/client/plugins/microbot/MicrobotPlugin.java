package net.runelite.client.plugins.microbot;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.StatChanged;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ProfileManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.NPCManager;
import net.runelite.client.game.WorldService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.menu.Rs2Menu;
import net.runelite.client.plugins.microbot.util.mouse.VirtualMouse;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.walker.Walker;

import javax.inject.Inject;
import java.awt.*;
import java.util.Arrays;

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
    }

    protected void shutDown() {
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
    }

    @Subscribe
    public void onStatChanged(StatChanged statChanged) {
        Microbot.setIsGainingExp(true);
    }
}
// MenuEntryImpl(getOption=Pickpocket, getTarget=<col=ffff00>Master Farmer, getIdentifier=1859, getType=NPC_THIRD_OPTION, getParam0=0, getParam1=0, getItemId=-1, isForceLeftClick=false, isDeprioritized=false)