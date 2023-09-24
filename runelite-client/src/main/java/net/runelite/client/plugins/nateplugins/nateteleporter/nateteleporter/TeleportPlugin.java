package net.runelite.client.plugins.nateplugins.nateteleporter.nateteleporter;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.mouse.VirtualMouse;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

import static net.runelite.client.plugins.natepainthelper.Info.*;

@PluginDescriptor(
        name = PluginDescriptor.Nate +"Power Teleporter",
        description = "Nate's Teleporter plugin",
        tags = {"Magic", "nate", "combat"},
        enabledByDefault = false
)
@Slf4j
public class TeleportPlugin extends Plugin {
    @Inject
    private TeleporterConfig config;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    Notifier notifier;

    @Provides
    TeleporterConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(TeleporterConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private TeleportOverlay teleportOverlay;

    @Inject
    TeleportScript teleportScript;


    public static int teleportamount = 0;

    @Override
    protected void startUp() throws AWTException {
        Microbot.pauseAllScripts = false;
        Microbot.setClient(client);
        Microbot.setClientThread(clientThread);
        Microbot.setNotifier(notifier);
        Microbot.setMouse(new VirtualMouse());
        expstarted = Microbot.getClient().getSkillExperience(Skill.MAGIC);
        startinglevel = Microbot.getClient().getRealSkillLevel(Skill.MAGIC);
        timeBegan = System.currentTimeMillis();
       // teleportamount = getAmount("law rune");
        if (overlayManager != null) {
            overlayManager.add(teleportOverlay);
        }
        teleportScript.run(config);
    }

    /*public int getAmount(String itemname) {
       return Microbot.getClientThread().runOnClientThread(() -> {
        ItemContainer container = client.getItemContainer(InventoryID.INVENTORY);
        int inventoryAmount = container.count(Rs2Inventory.getInventoryItem(itemname).getId());
        return inventoryAmount;
    });
    }*/

    protected void shutDown() {
        teleportScript.shutdown();
        overlayManager.remove(teleportOverlay);
    }
}
