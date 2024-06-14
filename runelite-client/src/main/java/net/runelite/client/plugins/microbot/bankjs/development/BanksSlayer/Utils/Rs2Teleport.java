package net.runelite.client.plugins.microbot.bankjs.development.BanksSlayer.Utils;

import net.runelite.api.Client;
import net.runelite.api.VarPlayer;
import net.runelite.api.annotations.Varp;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.MicrobotPlugin;
import net.runelite.client.plugins.microbot.util.menu.NewMenuEntry;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import javax.inject.Inject;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

import static net.runelite.client.plugins.microbot.util.Global.sleep;
import static net.runelite.client.plugins.microbot.util.Global.sleepUntil;


public class Rs2Teleport {
    @Inject
    private static Client client;

    private static final int GROUP_TAB_WIDGET_ID = 10551339;
    private static final int GROUPING_ICON_WIDGET_ID = 46333958;
    private static final int MINIGAME_DROPDOWN_WIDGET_PARENT_ID = 4980742;
    private static final int MINIGAME_DROPDOWN_WIDGET_TEXT_ID = 4980747;

    private static final int TELEPORT_LIST_WIDGET_ID = 4980758;
    private static final int MINIGAME_TELEPORT_BUTTON_WIDGET_ID = 4980766;

    private static final int LAST_MINIGAME_TELEPORT = 892;
    private static final int LAST_HOME_TELEPORT = 888;

    private static final int MAGE_SPELLBOOK = 10551368;
    public static final int SPELLBOOK_LUMBRIDGE_HOME_TELEPORT = 14286855;

    // Define the teleport durations here
    private static final Duration HOME_TELEPORT_DURATION = Duration.ofMinutes(30);
    private static final Duration MINIGAME_TELEPORT_DURATION = Duration.ofMinutes(20);

    private static boolean isTeleportCooldownExpired(@Varp int varPlayer) {
        final Duration teleportDuration;
        switch (varPlayer) {
            case LAST_HOME_TELEPORT:
                teleportDuration = HOME_TELEPORT_DURATION;
                break;
            case LAST_MINIGAME_TELEPORT:
                teleportDuration = MINIGAME_TELEPORT_DURATION;
                break;
            default:
                System.out.println("Other var changes are not handled as teleports, so cooldown is considered expired");
                return true;
        }

        int lastTeleport = Microbot.getVarbitPlayerValue(varPlayer);
        System.out.println("Last Teleport: " + lastTeleport);
        long lastTeleportSeconds = (long) lastTeleport * 60;
        Instant teleportExpireInstant = Instant.ofEpochSecond(lastTeleportSeconds).plus(teleportDuration);
        Duration remainingTime = Duration.between(Instant.now(), teleportExpireInstant);

        if (remainingTime.getSeconds() > 0) {
            System.out.println("Teleport cooldown for varPlayer " + varPlayer + " has not expired. Remaining time: " + remainingTime.getSeconds() + " seconds");
        } else {
            System.out.println("Teleport cooldown for varPlayer " + varPlayer + " has expired.");
        }

        // If remaining time is positive, cooldown has not expired
        return remainingTime.getSeconds() <= 0;
    }


    public static boolean minigameTeleport(String location) {
        // Check if the teleport cooldown has expired
        if (!isTeleportCooldownExpired(VarPlayer.LAST_MINIGAME_TELEPORT)) {
            System.out.println("Minigame teleport cooldown has not expired. Cannot teleport yet.");
            return false;
        }

        System.out.println("Clicking group tab widget...");
        Rs2Widget.clickWidget(GROUP_TAB_WIDGET_ID);
        System.out.println("Group tab widget clicked.");

        System.out.println("Clicking grouping icon widget...");
        Rs2Widget.clickWidget(GROUPING_ICON_WIDGET_ID);
        System.out.println("Grouping icon widget clicked.");
        sleep(1000);

        Widget selectedMinigameWidget = Rs2Widget.getWidget(MINIGAME_DROPDOWN_WIDGET_TEXT_ID);
        if (!selectedMinigameWidget.getText().equals(location)) {
            // Teleport isn't pre-selected, clicking the widget to open the dropdown
            Rs2Widget.clickWidget(MINIGAME_DROPDOWN_WIDGET_TEXT_ID);
            sleep(1000);
            // Get the teleport list widget
            Widget teleportListWidget = Rs2Widget.getWidget(TELEPORT_LIST_WIDGET_ID);
            if (teleportListWidget != null) {
                System.out.println("Dynamic Children: " + Arrays.toString(teleportListWidget.getDynamicChildren()));
                Widget[] optionsWidgets = teleportListWidget.getDynamicChildren();
                if (optionsWidgets != null) {
                    int index = 0; // Initialize index counter
                    for (Widget optionWidget : optionsWidgets) {
                        String optionText = optionWidget.getText();
                        System.out.println("Checking option: " + optionText);
                        // Check if the text of the option matches the provided location
                        if (optionText.equals(location)) {
                            System.out.println("Match found for location: " + location + " at index: " + index);
                            Rs2Widget.clickWidget(location);
                            // Teleport logic goes here
                            Rs2Widget.clickWidget(MINIGAME_TELEPORT_BUTTON_WIDGET_ID);
                            // Wait until the animation finishes
                            sleep(15000);
                            return true;
                        }
                        index++; // Increment index counter
                    }
                    // If no matching location is found
                    System.out.println("No match found for location: " + location);
                } else {
                    System.out.println("No option widgets found.");
                }
            } else {
                System.out.println("Teleport list widget is null.");
            }
        } else {
            // Click the Teleport Button, because we already got the desired teleport
            Rs2Widget.clickWidget(MINIGAME_TELEPORT_BUTTON_WIDGET_ID);
        }

        return true;
    }

    public static boolean homeTeleport() {
        // Check if the teleport cooldown has expired
        if (!isTeleportCooldownExpired(VarPlayer.LAST_HOME_TELEPORT)) {
            System.out.println("Home teleport cooldown has not expired. Cannot teleport yet.");
            return false;
        }

        System.out.println("Clicking mage tab widget...");
        Rs2Widget.clickWidget(MAGE_SPELLBOOK);
        System.out.println("Mage tab widget clicked.");

        System.out.println("Clicking Home Tele icon widget...");
        Rs2Widget.clickWidget(SPELLBOOK_LUMBRIDGE_HOME_TELEPORT);
        System.out.println("Home Teleport Clicked");
        sleep(10000);
        return true;
    }

}

