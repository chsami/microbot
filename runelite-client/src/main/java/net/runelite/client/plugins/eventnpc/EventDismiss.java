package net.runelite.client.plugins.eventnpc;

import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.NPC;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;

@PluginDescriptor(
        name = "<html>[<font color=#FF7F7F>F</font>] Event Dismiss</html>"",
        description = "F126's Random Event Dismisser",
        tags = {"random", "events", "microbot", "f126"},
        enabledByDefault = false
)
@Slf4j
public class EventDismiss extends Plugin
{
    @Inject
    private Client client;

    private final String[] npcNamesToDismiss = {"Bee keeper", "Capt' Arnav", "Niles", "Miles", "Giles", "Count Check", "Sergeant Damien", "Drunken dwarf", "Evil Bob", "Servant", "Postie Pete", "Molly", "Freaky Forester", "Genie", "Leo", "Dr Jekyll", "Frogs", "Frog Prince", "Frog Princess", "Mysterious Old Man", "Pillory Guard", "Flippa", "Tilt", "Evil Bob", "Prison Pete", "Quiz Master", "Rick Turpentine", "Sandwich lady", "Strange plant", "Dunce", "Mr. Mordaut"};

    @Subscribe
    public void onNpcSpawned(NpcSpawned event)
    {
        NPC npc = event.getNpc();
        if (shouldDismissNpc(npc))
        {
            dismissNpc(npc);
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event)
    {
        if (event.getMenuAction() == MenuAction.RUNELITE && event.getMenuOption().equals("Dismiss"))
        {
            // Prevent accidentally dismissing NPCs that we didn't intend to
            event.consume();
        }
    }

    private boolean shouldDismissNpc(NPC npc)
    {
        String npcName = npc.getName();
        for (String name : npcNamesToDismiss)
        {
            if (npcName.equals(name))
            {
                return true;
            }
        }
        return false;
    }

    private void dismissNpc(NPC npc)
    {
        // Interact with NPC to dismiss it
        Rs2Npc.interact(npc, "Dismiss");
    }
}
