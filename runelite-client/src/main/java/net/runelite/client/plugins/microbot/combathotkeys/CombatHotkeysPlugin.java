package net.runelite.client.plugins.microbot.combathotkeys;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Item;
import net.runelite.client.plugins.microbot.util.prayer.Rs2Prayer;
import net.runelite.client.plugins.microbot.util.prayer.Rs2PrayerEnum;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Comparator;

@PluginDescriptor(
        name = PluginDescriptor.Cicire + "Combat hotkeys",
        description = "A plugin to bind hotkeys to combat stuff",
        tags = {"combat", "hotkeys", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class CombatHotkeysPlugin extends Plugin implements KeyListener {
    @Inject
    private CombatHotkeysConfig config;

    @Provides
    CombatHotkeysConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(CombatHotkeysConfig.class);
    }

    @Inject
    private KeyManager keyManager;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private CombatHotkeysOverlay overlay;

    @Inject
    private CombatHotkeysScript script;


    @Override
    protected void startUp() throws AWTException {
        keyManager.registerKeyListener(this);

        if (overlayManager != null) {
            overlayManager.add(overlay);
        }
        script.run(config);
    }

    protected void shutDown() {
        script.shutdown();
        keyManager.unregisterKeyListener(this);
        overlayManager.remove(overlay);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!Microbot.isLoggedIn()){
            return;
        }

        if (config.protectFromMagic().matches(e)) {
            e.consume();
            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MAGIC);
        }

        if (config.protectFromMissles().matches(e)) {
            e.consume();
            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_RANGE);
        }

        if (config.protectFromMelee().matches(e)) {
            e.consume();
            Rs2Prayer.toggle(Rs2PrayerEnum.PROTECT_MELEE);
        }

        if (config.gear1().matches(e)) {
            script.gearToSwitch = processGearList(config.gearList1());
            script.isSwitchingGear = true;
        }

        if (config.gear2().matches(e)) {
            script.gearToSwitch = processGearList(config.gearList2());
            script.isSwitchingGear = true;
        }

        if (config.gear3().matches(e)) {
            script.gearToSwitch = processGearList(config.gearList3());
            script.isSwitchingGear = true;
        }

        if (config.gear4().matches(e)) {
            script.gearToSwitch = processGearList(config.gearList4());
            script.isSwitchingGear = true;
        }

        if (config.gear5().matches(e)) {
            script.gearToSwitch = processGearList(config.gearList5());
            script.isSwitchingGear = true;
        }
    }

    private static ArrayList<Rs2Item> processGearList(String gearListConfig) {
        String[] itemIDs = gearListConfig.split(",");
        ArrayList<Rs2Item> gearList = new ArrayList<>();

        for (String value : itemIDs) {
            int itemId = Integer.parseInt(value);
            if (Rs2Inventory.hasItem(itemId)) {
                Rs2Item item = Rs2Inventory.get(itemId);
                gearList.add(item);
            }
        }

        // Sort the list based on the item slot
        gearList.sort(Comparator.comparingInt(item -> item.slot));

        return gearList;
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
