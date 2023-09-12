package net.runelite.client.plugins.microbot.crafting;

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
import net.runelite.client.plugins.microbot.crafting.enums.Activities;
import net.runelite.client.plugins.microbot.crafting.enums.Gems;
import net.runelite.client.plugins.microbot.crafting.scripts.DefaultScript;
import net.runelite.client.plugins.microbot.crafting.scripts.GemsScript;
import net.runelite.client.plugins.microbot.crafting.scripts.GlassblowingScript;
import net.runelite.client.plugins.microbot.util.mouse.VirtualMouse;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

import static net.runelite.client.plugins.natepainthelper.Info.*;


@PluginDescriptor(
        name = PluginDescriptor.Mocrosoft + "Crafting",
        description = "Microbot crafting plugin",
        tags = {"skilling", "microbot", "crafting"},
        enabledByDefault = false
)
@Slf4j
public class CraftingPlugin extends Plugin {

    @Inject
    private CraftingConfig config;

    @Provides
    CraftingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(CraftingConfig.class);
    }

    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private Notifier notifier;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private CraftingOverlay craftingOverlay;

    private DefaultScript defaultScript = new DefaultScript();
    private GemsScript gemsScript = new GemsScript();
    private GlassblowingScript glassblowingScript = new GlassblowingScript();

    @Override
    protected void startUp() throws AWTException {
        expstarted = Microbot.getClient().getSkillExperience(Skill.CRAFTING);
        startinglevel = Microbot.getClient().getRealSkillLevel(Skill.CRAFTING);
        timeBegan = System.currentTimeMillis();
        Microbot.pauseAllScripts = false;
        Microbot.setClient(client);
        Microbot.setClientThread(clientThread);
        Microbot.setNotifier(notifier);
        Microbot.setMouse(new VirtualMouse());
        if (overlayManager != null) {
            overlayManager.add(craftingOverlay);
        }

        if (config.activityType() == Activities.DEFAULT) {
            defaultScript.run(config);
        } else if (config.activityType() == Activities.GEM_CUTTING) {
            gemsScript.run(config);
        } else if (config.activityType() == Activities.GLASSBLOWING) {
            glassblowingScript.run(config);
        }
    }

    protected void shutDown() {
        glassblowingScript.shutdown();
        gemsScript.shutdown();
        defaultScript.shutdown();
        overlayManager.remove(craftingOverlay);
    }
}
