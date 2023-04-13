package net.runelite.client.plugins.microbot.scripts.magic.housetabs;

import net.runelite.api.Point;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.scripts.Scripts;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.inventory.Inventory;
import net.runelite.client.plugins.microbot.util.keyboard.VirtualKeyboard;
import net.runelite.client.plugins.microbot.util.math.Random;
import net.runelite.client.plugins.microbot.util.npc.Npc;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HouseTabs extends Scripts {
    private final int RIMMINGTON_PORTAL_OBJECT = 15478;
    private final int HOUSE_PORTAL_OBJECT = 4525;

    private final int HOUSE_ADVERTISEMENT_OBJECT = 29091;

    private final int HOUSE_ADVERTISEMENT_NAME_PARENT_INTERFACE = 3407881;

    private final int HOUSE_TABLET_INTERFACE = 5177359;

    private final HOUSETABS_CONFIG houseTabConfig;
    private final String[] playerHouses;

    private final ScheduledExecutorService scheduledExecutorService;


    private boolean hasSoftClay() {
        boolean f = Microbot.getClientThread().runOnClientThread(() -> Inventory.findItem(1761) != null);
        return f;
    }

    public HouseTabs(HOUSETABS_CONFIG houseTabConfig, String[] playerHouses) {
        this.houseTabConfig = houseTabConfig;
        this.playerHouses = playerHouses;
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
    }

    private void lookForHouseAdvertisementObject() {
        Widget houseAdvertisementPanel = Microbot.getClient().getWidget(HOUSE_ADVERTISEMENT_NAME_PARENT_INTERFACE);
        if (!hasSoftClay() || houseAdvertisementPanel != null || Rs2GameObject.findGameObject(HOUSE_PORTAL_OBJECT) != null)
            return;


        boolean success = Rs2GameObject
                .interact(29091, "View");


        if (success) {
            sleepUntilOnClientThread(() -> Microbot.getClient().getWidget(HOUSE_ADVERTISEMENT_NAME_PARENT_INTERFACE) != null);
        }
    }

    private void lookForPlayerHouse() {
        Widget houseAdvertisementNameWidget = Microbot.getClient().getWidget(HOUSE_ADVERTISEMENT_NAME_PARENT_INTERFACE);
        if (houseAdvertisementNameWidget == null || houseAdvertisementNameWidget.getChildren() == null) return;
        if (!hasSoftClay())
            return;

        int enterHouseButtonHeight = 21;
        int houseIndexToJoin = 0;

        for (int i = 0; i < houseAdvertisementNameWidget.getChildren().length; i++) {
            Widget child = houseAdvertisementNameWidget.getChild(i);
            if (child == null) continue;
            if (Arrays.stream(this.playerHouses).anyMatch(x -> child.getText().equalsIgnoreCase(x))) {
                houseIndexToJoin = i;
                break;
            }
        }

        Widget mainWindow = Microbot.getClient().getWidget(3407879);
        if (mainWindow == null) return;
        int HOUSE_ADVERTISEMENT_ENTER_HOUSE_PARENT_INTERFACE = 3407891;
        Widget houseAdvertisementEnterHouseWidget = Microbot.getClient().getWidget(HOUSE_ADVERTISEMENT_ENTER_HOUSE_PARENT_INTERFACE);
        if (houseAdvertisementEnterHouseWidget == null) return;
        Widget enterHouseButton = houseAdvertisementEnterHouseWidget.getChild(houseIndexToJoin);
        int buttonRelativeY = houseAdvertisementEnterHouseWidget.getChild(houseIndexToJoin).getRelativeY() + enterHouseButtonHeight;
        if (buttonRelativeY > (mainWindow.getScrollY() + mainWindow.getHeight())) {
            keepExecuteUntil(() -> {
                int x = (int) mainWindow.getBounds().getCenterX() + Random.random(-50, 50);
                int y = (int) mainWindow.getBounds().getCenterY() + Random.random(-50, 50);
                Microbot.getMouse().scrollDown(new Point(x, y));
            }, () -> buttonRelativeY <= (mainWindow.getScrollY() + mainWindow.getHeight()), 500);
        } else {
            Microbot.getMouse()
                    .click(enterHouseButton.getCanvasLocation());
            sleepUntilOnClientThread(() -> Rs2GameObject.findGameObject(HOUSE_PORTAL_OBJECT) != null);
        }
    }

    public void lookForLectern() {
        if (!hasSoftClay() || Rs2GameObject.findGameObject(HOUSE_ADVERTISEMENT_OBJECT) != null || Microbot.isGainingExp)
            return;

        Widget houseTabInterface = Microbot.getClient().getWidget(HOUSE_TABLET_INTERFACE);
        if (houseTabInterface != null || Rs2GameObject.findGameObject(HOUSE_PORTAL_OBJECT) == null) return;

        boolean success = Rs2GameObject.interact(new int[]{13647, 37349}, "Study");
        if (success) {
            sleepUntilOnClientThread(() -> Microbot.getClient().getWidget(HOUSE_TABLET_INTERFACE) != null);
        }
    }

    public void createHouseTablet() {
        Widget houseTabInterface = Microbot.getClient().getWidget(HOUSE_TABLET_INTERFACE);
        if (houseTabInterface == null) return;
        if (!hasSoftClay() || Rs2GameObject.findGameObject(HOUSE_PORTAL_OBJECT) == null)
            return;

        while (Microbot.getClient().getWidget(HOUSE_TABLET_INTERFACE) != null) {
            Microbot.getMouse()
                    .click(houseTabInterface.getCanvasLocation());
            sleep(200, 400);
        }

        sleepUntilOnClientThread(() -> !hasSoftClay()
                || Microbot.getClient().getWidget(HOUSE_TABLET_INTERFACE) != null
                || Microbot.isGainingExp, 35000);
    }

    public void leaveHouse() {
        if (hasSoftClay() || Rs2GameObject.findGameObject(HOUSE_PORTAL_OBJECT) == null)
            return;

        boolean success = Rs2GameObject.interact(HOUSE_PORTAL_OBJECT, "Enter");
        if (success)
            sleepUntil(() -> Rs2GameObject.findGameObject(HOUSE_PORTAL_OBJECT) == null);
    }

    public void unnoteClay() {
        if (hasSoftClay() || Rs2GameObject.findGameObject(HOUSE_ADVERTISEMENT_OBJECT) == null)
            return;
        if (Microbot.getClient().getWidget(14352385) == null) {
            do {
                Microbot.getClientThread().invoke(() -> {
                    Inventory.useItemSafe("Soft clay");
                });
                sleep(300, 380);
            } while (!Npc.interact("Phials", "Use"));
        }

        sleep(2500, 5000);
        if (Microbot.getClient().getWidget(14352385) != null) {
            VirtualKeyboard.keyPress('3');
            sleep(300, 380);
        }
    }

    long currentInventoryCount = 0;

    public boolean run() {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                super.run();
                System.out.println("Main loop of your script every 600ms");

                if (Rs2GameObject.findGameObject(new int[]{13647, 37349}) != null) {
                    currentInventoryCount = Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(Inventory.getInventoryItems()).count());
                    Thread.sleep(3000);
                    if (currentInventoryCount != Microbot.getClientThread().runOnClientThread(() -> Arrays.stream(Inventory.getInventoryItems()).count()))
                        return;
                }

                if (houseTabConfig == HOUSETABS_CONFIG.HOUSE_ADVERTISEMENT) {
                    lookForHouseAdvertisementObject();
                    lookForPlayerHouse();
                    lookForLectern();
                    createHouseTablet();
                    leaveHouse();
                    unnoteClay();
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 600, TimeUnit.MILLISECONDS);
        return true;
    }
}