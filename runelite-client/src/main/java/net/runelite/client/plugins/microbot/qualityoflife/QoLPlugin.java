package net.runelite.client.plugins.microbot.qualityoflife;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.BeforeRender;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.qualityoflife.scripts.NeverLogoutScript;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@PluginDescriptor(
        name = PluginDescriptor.See1Duck + "QoL",
        description = "Quality of Life Plugin",
        tags = {"QoL", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class QoLPlugin extends Plugin {
    public static String version = "1.0.0";
    public static String LOADOUT_TO_LOAD = "";
    public static List<NewMenuEntry> bankMenuEntries = new LinkedList<>();
    public static List<NewMenuEntry> furnaceMenuEntries = new LinkedList<>();
    public static List<NewMenuEntry> anvilMenuEntries = new LinkedList<>();
    public static NewMenuEntry workbenchMenuEntry;
    public static boolean recordActions = false;
    public static boolean executeBankActions = false;
    public static boolean executeFurnaceActions = false;
    public static boolean executeAnvilActions = false;
    public static boolean executeWorkbenchActions = false;
    public static boolean executeLoadoutActions = false;
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

    private final int HALF_ROTATION = 1024;
    private final int FULL_ROTATION = 2048;

    private final int PITCH_INDEX = 0;
    private final int YAW_INDEX = 1;
    private final int SCROLL_INDEX = 2;
    //Made an array just in case a setter for camera pitch is ever added
    private final int[] deltaCamera = new int[3];
    private final int[] previousCamera = new int[3];

    private int lerp(int x, int y, float alpha) {
        return x + (int) ((y - x) * alpha);
    }

    private int getSmallestAngle(int x, int y) {
        return mod(((y - x) + HALF_ROTATION), FULL_ROTATION) - HALF_ROTATION;
    }

    //https://stackoverflow.com/questions/1878907/how-can-i-find-the-difference-between-two-angles
    //This function was tested to produce results that were much more reasonable than the modulo operator
    public int mod(int a, int n) {
        return (int) (a - Math.floor(a / (float) n) * n);
    }

    private void applySmoothingToAngle(int index) {
        int deltaChange;
        int changed;
        int newDeltaAngle;
        newDeltaAngle = getSmallestAngle(previousCamera[index], index == YAW_INDEX ? Microbot.getClient().getMapAngle() : 0/*No pitch method in RL*/);
        deltaCamera[index] += newDeltaAngle;

        deltaChange = lerp(deltaCamera[index], 0, (80 / 100.0f));
        changed = previousCamera[index] + deltaChange;

        deltaCamera[index] -= deltaChange;
        if (index == YAW_INDEX) {
            Microbot.getClient().setCameraYawTarget(changed);
        }/* else if(index == PITCH_INDEX) {
			//No pitch method in RL yet
		}*/
        previousCamera[index] += deltaChange;
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
    public void onGameTick(GameTick event)
    {
        if (!Microbot.isLoggedIn()) return;
        if (config.neverLogout()) {
            NeverLogoutScript.onGameTick(event);
        }
    }

    @Subscribe
    private void onMenuOptionClicked(MenuOptionClicked event) {
        if (Rs2Bank.isOpen() && config.useDoLastBank() && recordActions) {
            MenuEntry menuEntry = event.getMenuEntry();
            if (menuEntry.getOption().contains("Withdraw")) {
            }
            NewMenuEntry cachedMenuEntry = new NewMenuEntry(menuEntry.getOption(), menuEntry.getTarget(), menuEntry.getIdentifier(), menuEntry.getType(), menuEntry.getParam0(), menuEntry.getParam1(), menuEntry.isForceLeftClick());
            cachedMenuEntry.setItemId(menuEntry.getItemId());
            cachedMenuEntry.setWidget(menuEntry.getWidget());
            bankMenuEntries.add(cachedMenuEntry);
            if (menuEntry.getOption().equals("Close")) {
                recordActions = false;
                Microbot.log("<col=5F1515>Stopped recording actions</col>");
            }
        }
        if ((Rs2Widget.isProductionWidgetOpen() || Rs2Widget.isGoldCraftingWidgetOpen()) && config.useDoLastFurnace() && recordActions) {
            MenuEntry menuEntry = event.getMenuEntry();
            NewMenuEntry cachedMenuEntry = new NewMenuEntry(menuEntry.getOption(), menuEntry.getTarget(), menuEntry.getIdentifier(), menuEntry.getType(), menuEntry.getParam0(), menuEntry.getParam1(), menuEntry.isForceLeftClick());
            cachedMenuEntry.setItemId(menuEntry.getItemId());
            cachedMenuEntry.setWidget(menuEntry.getWidget());
            furnaceMenuEntries.add(cachedMenuEntry);
            if (menuEntry.getOption().equals("Make sets:") || menuEntry.getOption().equals("Smelt") || menuEntry.getOption().contains("Make")) {
                recordActions = false;
                Microbot.log("<col=5F1515>Stopped recording actions</col>");
            }
        }
        if (Rs2Widget.isSmithingWidgetOpen() && config.useDoLastAnvil() && recordActions) {
            MenuEntry menuEntry = event.getMenuEntry();
            NewMenuEntry cachedMenuEntry = new NewMenuEntry(menuEntry.getOption(), menuEntry.getTarget(), menuEntry.getIdentifier(), menuEntry.getType(), menuEntry.getParam0(), menuEntry.getParam1(), menuEntry.isForceLeftClick());
            cachedMenuEntry.setItemId(menuEntry.getItemId());
            cachedMenuEntry.setWidget(menuEntry.getWidget());
            anvilMenuEntries.add(cachedMenuEntry);
            if (menuEntry.getOption().equals("Smith set") || menuEntry.getOption().equals("Smith")) {
                recordActions = false;
                Microbot.log("<col=5F1515>Stopped recording actions</col>");
            }
        }
        if (event.getMenuOption().equals("Track")) {
            event.consume();
        }

        if (event.getMenuOption().equals("Work-at")) {
            MenuEntry menuEntry = event.getMenuEntry();
            workbenchMenuEntry = new NewMenuEntry(menuEntry.getOption(), menuEntry.getTarget(), menuEntry.getIdentifier(), menuEntry.getType(), menuEntry.getParam0(), menuEntry.getParam1(), menuEntry.isForceLeftClick());
        }

    }

    @Subscribe
    private void onMenuEntryAdded(MenuEntryAdded event) {
        if (config.rightClickCameraTracking()) {
            if (event.getMenuEntry().getNpc() != null && event.getMenuEntry().getNpc().getId() > 0) {
                addMenuEntry(event, "Track", event.getTarget(), this::customTrackOnClicked);
            }
        }

        if (config.useDoLastBank()) {
            if (event.getOption().equals("Talk-to")) {
                List<MenuEntry> entries = new LinkedList<>(Arrays.asList(Microbot.getClient().getMenuEntries()));
                if (entries.stream().anyMatch(e -> e.getOption().equals("Bank") && e.getTarget().equals(event.getTarget()))) {
                    event.getMenuEntry().setDeprioritized(true);
                }
            }
            if (event.getOption().equals("Bank")) {
                event.getMenuEntry().onClick(this::recordNewActions);

                addMenuEntry(event, "<col=FFA500>Do-Last</col>", event.getTarget(), this::customBankingOnClicked);
            }
        }
        if (config.useDoLastFurnace()) {
            if (event.getOption().equals("Smelt")) {
                event.getMenuEntry().onClick(this::recordNewActions);
                addMenuEntry(event, "<col=FFA500>Do-Last</col>", event.getTarget(), this::customFurnaceOnClicked);
            }
        }
        if (config.useDoLastAnvil()) {
            if (event.getOption().equals("Smith") && event.getTarget().contains("Anvil")) {
                event.getMenuEntry().onClick(this::recordNewActions);
                addMenuEntry(event, "<col=FFA500>Do-Last</col>", event.getTarget(), this::customAnvilOnClicked);
            }
        }

        if (config.useDoLastWorkbench()) {
            if (event.getOption().equals("Work-at")) {
                event.getMenuEntry().onClick(this::customWorkbenchOnClicked);
                //addMenuEntry(event, "<col=FFA500>Do-Last</col>", event.getTarget(), this::customWorkbenchOnClicked);
            }
        }
        if (config.displayInventorySetups()) {
            if (event.getOption().equals("Bank")) {
                if (config.displaySetup1()) {
                    addLoadoutMenuEntry(event, "<col=FFA500>Equip: " + config.Setup1() + "</col>", event.getTarget(), (menuEntry) -> customLoadoutOnClicked(menuEntry, config.Setup1()));
                }
                if (config.displaySetup2()) {
                    addLoadoutMenuEntry(event, "<col=FFA500>Equip: " + config.Setup2() + "</col>", event.getTarget(), (menuEntry) -> customLoadoutOnClicked(menuEntry, config.Setup2()));
                }
                if (config.displaySetup3()) {
                    addLoadoutMenuEntry(event, "<col=FFA500>Equip: " + config.Setup3() + "</col>", event.getTarget(), (menuEntry) -> customLoadoutOnClicked(menuEntry, config.Setup3()));
                }
                if (config.displaySetup4()) {
                    addLoadoutMenuEntry(event, "<col=FFA500>Equip: " + config.Setup4() + "</col>", event.getTarget(), (menuEntry) -> customLoadoutOnClicked(menuEntry, config.Setup4()));
                }
            }
        }

    }

    @Subscribe
    public void onConfigChanged(ConfigChanged ev) {
        if (ev.getKey().equals("autoEatFood")) {
            if (config.autoEatFood()) {
            } else {
                qoLScript.shutdown();
            }
        }
        if (ev.getKey().equals("smoothRotation")) {
            if (config.smoothCameraTracking()) {
                previousCamera[YAW_INDEX] = Microbot.getClient().getMapAngle();
            }
        }
    }

    @Subscribe
    public void onBeforeRender(BeforeRender render) {
        if (!Microbot.isLoggedIn()) {
            return;
        }

        if (config.smoothCameraTracking()) {
            //Pitch stuff to be added if runelite ever decides to add a Client.setCameraPitchTarget method
            //Until then, yaw going to have to stick with yaw!
            //applySmoothingToAngle(PITCH_INDEX);
            applySmoothingToAngle(YAW_INDEX);
        }


    }

    private void customLoadoutOnClicked(MenuEntry event, String loadoutName) {
        recordActions = false;
        LOADOUT_TO_LOAD = loadoutName;
        executeLoadoutActions = true;

    }

    private void customBankingOnClicked(MenuEntry event) {
        recordActions = false;
        if (bankMenuEntries.isEmpty()) {
            Microbot.log("<col=5F1515>No actions recorded</col>");
            return;
        }
        Microbot.log("<col=245C2D>Banking</col>");
        executeBankActions();

    }

    private void customFurnaceOnClicked(MenuEntry event) {
        recordActions = false;
        if (furnaceMenuEntries.isEmpty()) {
            Microbot.log("<col=5F1515>No actions recorded</col>");
            return;
        }
        Microbot.log("<col=245C2D>Furnace</col>");
        executeFurnaceActions();

    }

    private void customAnvilOnClicked(MenuEntry event) {
        recordActions = false;
        if (anvilMenuEntries.isEmpty()) {
            Microbot.log("<col=5F1515>No actions recorded</col>");
            return;
        }
        Microbot.log("<col=245C2D>Anvil</col>");
        executeAnvilActions();

    }

    private void customTrackOnClicked(MenuEntry event) {
        if (Rs2Camera.isTrackingNpc()) {
            Rs2Camera.stopTrackingNpc();
            Microbot.log("<col=5F1515>Stopped tracking old NPC, try again to track new NPC</col>");
            return;
        }
        Rs2Camera.trackNpc(Objects.requireNonNull(event.getNpc()).getId());
    }

    private void customWorkbenchOnClicked(MenuEntry event) {
        Microbot.log("<col=245C2D>Workbench</col>");
        // get all pouches in inventory except for the "Rune pouch"
        executeWorkbenchActions();
    }

    private void recordNewActions(MenuEntry event) {
        recordActions = true;
        if (event.getOption().equals("Bank")) {
            bankMenuEntries.clear();
        }
        if (event.getOption().equals("Smelt")) {
            furnaceMenuEntries.clear();
        }
        if (event.getOption().equals("Smith")) {
            anvilMenuEntries.clear();
        }
        Microbot.log("<col=245C2D>Recording actions</col>");

    }

    private void executeBankActions() {
        executeBankActions = true;
    }

    private void executeFurnaceActions() {
        executeFurnaceActions = true;
    }

    private void executeAnvilActions() {
        executeAnvilActions = true;
    }

    private void executeWorkbenchActions() {
        executeWorkbenchActions = true;
    }

    private void addMenuEntry(MenuEntryAdded event, String option, String target, Consumer<MenuEntry> callback) {
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
                .onClick(callback);

    }

    private void addLoadoutMenuEntry(MenuEntryAdded event, String option, String target, Consumer<MenuEntry> callback) {
        Microbot.getClient().createMenuEntry(1)
                .setOption(option)
                .setTarget(target)
                .setParam0(event.getActionParam0())
                .setParam1(event.getActionParam1())
                .setIdentifier(event.getIdentifier())
                .setType(event.getMenuEntry().getType())
                .onClick(callback);

    }
}
