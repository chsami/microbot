package net.runelite.client.plugins.microbot.qualityoflife;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@PluginDescriptor(
        name = PluginDescriptor.See1Duck + "QoL",
        description = "Quality of Life Plugin",
        tags = {"QoL", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class QoLPlugin extends Plugin {
    public static String version = "1.0.0";
    public static List<NewMenuEntry> menuEntries = new LinkedList<>();
    public static boolean recordActions = false;
    public static boolean executeActions = false;
    @Inject
    QoLScript qoLScript;
    @Inject
    private QoLConfig config;
    @Inject
    private OverlayManager overlayManager;
    @Inject
    private QoLOverlay qoLOverlay;

    @Provides
    QoLConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(QoLConfig.class);
    }

    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(qoLOverlay);
        }
        qoLScript.run(config);
    }

    protected void shutDown() {
        qoLScript.shutdown();
        overlayManager.remove(qoLOverlay);
    }

    @Subscribe
    private void onMenuOptionClicked(MenuOptionClicked event) {
        if (Rs2Bank.isOpen() && recordActions) {
            MenuEntry menuEntry = event.getMenuEntry();
            if (menuEntry.getOption().contains("Withdraw")) {
            }
            NewMenuEntry cachedMenuEntry = new NewMenuEntry(menuEntry.getOption(), menuEntry.getTarget(), menuEntry.getIdentifier(), menuEntry.getType(), menuEntry.getParam0(), menuEntry.getParam1(), menuEntry.isForceLeftClick());
            cachedMenuEntry.setItemId(menuEntry.getItemId());
            cachedMenuEntry.setWidget(menuEntry.getWidget());
            menuEntries.add(cachedMenuEntry);
            if (menuEntry.getOption().equals("Close")) {
                recordActions = false;
                Microbot.log("<col=C3352B>Stopped recording actions</col>");
            }
        }

    }

    @Subscribe
    private void onMenuEntryAdded(MenuEntryAdded event) {
        if (!config.useWithdrawLast()) return;
        if (event.getOption().equals("Talk-to")) {
            List<MenuEntry> entries = new LinkedList<>(Arrays.asList(Microbot.getClient().getMenuEntries()));
            if (entries.stream().anyMatch(e -> e.getOption().equals("Bank") && e.getTarget().equals(event.getTarget()))) {
                event.getMenuEntry().setDeprioritized(true);
            }
        }
        if (event.getOption().equals("Bank")) {
            event.getMenuEntry().onClick(this::recordNewActions);

            addMenuEntry(event, "<col=FFA500>Withdraw-Last</col>", event.getTarget());
        }


    }

    private void customOnClicked(MenuEntry event) {
        recordActions = false;
        Microbot.log("<col=337C12>Banking</col>");
        executeActions();

    }

    private void recordNewActions(MenuEntry event) {
        recordActions = true;
        menuEntries.clear();
        Microbot.log("<col=337C12>Recording actions</col>");

    }

    private void executeActions() {
        executeActions = true;

    }

    private void addMenuEntry(MenuEntryAdded event, String option, String target) {
        List<MenuEntry> entries = new LinkedList<>(Arrays.asList(Microbot.getClient().getMenuEntries()));

        if (entries.stream().anyMatch(e -> e.getOption().equals(option) && e.getTarget().equals(target))) {
            return;
        }

        Microbot.getClient().createMenuEntry(entries.size())
                .setOption(option)
                .setTarget(target)
                .setParam0(event.getActionParam0())
                .setParam1(event.getActionParam1())
                .setIdentifier(event.getIdentifier())
                .setType(event.getMenuEntry().getType())
                .onClick(this::customOnClicked);

    }

}
