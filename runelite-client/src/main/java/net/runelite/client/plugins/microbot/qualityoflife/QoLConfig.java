package net.runelite.client.plugins.microbot.qualityoflife;

import net.runelite.client.config.*;
import net.runelite.client.plugins.microbot.qualityoflife.enums.WintertodtActions;
import net.runelite.client.plugins.microbot.util.misc.SpecialAttackWeaponEnum;

import java.awt.*;

@ConfigGroup("QoL")
public interface QoLConfig extends Config {

    // Section for Overlay Configurations
    @ConfigSection(
            name = "Overlay Configurations",
            description = "Settings related to overlay rendering",
            position = 0
    )
    String overlaySection = "overlaySection";
    // Section for Do-Last Configurations
    @ConfigSection(
            name = "Do-Last Configurations",
            description = "Settings related to Do-Last actions",
            position = 1
    )
    String doLastSection = "doLastSection";
    // Section for Camera Configurations
    @ConfigSection(
            name = "Camera Configurations",
            description = "Settings related to camera tracking",
            position = 2
    )
    String cameraSection = "cameraSection";
    // Section for Dialogue Configurations
    @ConfigSection(
            name = "Dialogue Configurations",
            description = "Settings related to dialogue interactions",
            position = 3
    )
    String dialogueSection = "dialogueSection";
    // Section for Upkeep Configurations
    @ConfigSection(
            name = "Upkeep Configurations",
            description = "Settings related to upkeep actions",
            position = 4
    )
    String upkeepSection = "upkeepSection";
    // Section for Inventory/Equipment Configurations
    @ConfigSection(
            name = "Inventory/Equipment",
            description = "Settings related to inventory/equipment actions",
            position = 5
    )
    String inventorySection = "inventorySection";

    // boolean to render Max Hit Overlay
    @ConfigItem(
            keyName = "renderMaxHitOverlay",
            name = "Render Max Hit Overlay",
            description = "Render Max Hit Overlay",
            position = 0,
            section = overlaySection
    )
    default boolean renderMaxHitOverlay() {
        return true;
    }

    // boolean to use Withdraw-Last from bank
    @ConfigItem(
            keyName = "useDoLastBank",
            name = "Use Do-Last Bank",
            description = "Use Do-Last Bank",
            position = 0,
            section = doLastSection
    )
    default boolean useDoLastBank() {
        return true;
    }

    // boolean to use DoLast action on furnace
    @ConfigItem(
            keyName = "useDoLastFurnace",
            name = "Use Do-Last Furnace",
            description = "Use Do-Last Furnace",
            position = 1,
            section = doLastSection
    )
    default boolean useDoLastFurnace() {
        return true;
    }

    // boolean to use DoLast action on anvil
    @ConfigItem(
            keyName = "useDoLastAnvil",
            name = "Use Do-Last Anvil",
            description = "Use Do-Last Anvil",
            position = 2,
            section = doLastSection
    )
    default boolean useDoLastAnvil() {
        return true;
    }

    // boolean for DoLast action on Workbench
    @ConfigItem(
            keyName = "useDoLastWorkbench",
            name = "Use Do-Last Workbench",
            description = "Use Do-Last Workbench",
            position = 3,
            section = doLastSection
    )
    default boolean useDoLastWorkbench() {
        return true;
    }

    // Right click camera tracking
    @ConfigItem(
            keyName = "rightClickCameraTracking",
            name = "Right Click NPC Tracking",
            description = "Right Click NPC Tracking",
            position = 0,
            section = cameraSection
    )
    default boolean rightClickCameraTracking() {
        return false;
    }

    // smooth camera tracking
    @ConfigItem(
            keyName = "smoothCameraTracking",
            name = "Smooth Camera Tracking",
            description = "Smooth Camera Tracking",
            position = 1,
            section = cameraSection
    )
    default boolean smoothCameraTracking() {
        return true;
    }

    // UI section
    @ConfigSection(
            name = "UI",
            description = "Settings related to UI",
            position = 6
    )
    String uiSection = "uiSection";
    // Wintertodt section
    @ConfigSection(
            name = "Wintertodt",
            description = "Wintertodt settings",
            position = 7
    )
    String wintertodtSection = "wintertodtSection";


    // boolean to use Dialogue auto continue
    @ConfigItem(
            keyName = "useDialogueAutoContinue",
            name = "Dialogue Auto Continue",
            description = "Use Dialogue Auto Continue",
            position = 0,
            section = dialogueSection
    )
    default boolean useDialogueAutoContinue() {
        return true;
    }

    // boolean to auto eat food
    @ConfigItem(
            keyName = "autoEatFood",
            name = "Auto Eat Food",
            description = "Auto Eat Food",
            position = 0,
            section = upkeepSection
    )
    default boolean autoEatFood() {
        return false;
    }

    @Range(
            min = 10,
            max = 99
    )
    // percentage of health to eat food
    @ConfigItem(
            keyName = "eatFoodPercentage",
            name = "Eat Food Percentage",
            description = "Eat Food Percentage",
            position = 2,
            section = upkeepSection
    )
    default int eatFoodPercentage() {
        return 50;
    }

    // avoid logging out
    @ConfigItem(
            keyName = "neverLogOut",
            name = "Never log out",
            description = "Never log out",
            position = 3,
            section = upkeepSection
    )
    default boolean neverLogout() {
        return false;
    }

    @ConfigItem(
            keyName = "displayPouchCounter",
            name = "Display pouch counter",
            description = "Displays a counter above your runecrafting pouches",
            position = 4,
            section = upkeepSection
    )
    default boolean displayPouchCounter() {
        return false;
    }

    // boolean to use custom spec weapon
    @ConfigItem(
            keyName = "useSpecWeapon",
            name = "Use Spec Weapon",
            description = "Use Spec Weapon",
            position = 5,
            section = upkeepSection
    )
    default boolean useSpecWeapon() {
        return false;
    }

    // spec weapon
    @ConfigItem(
            keyName = "specWeapon",
            name = "Spec Weapon",
            description = "Spec Weapon",
            position = 6,
            section = upkeepSection
    )
    default SpecialAttackWeaponEnum specWeapon() {
        return SpecialAttackWeaponEnum.DRAGON_DAGGER;
    }


    // boolean to display Inventory setups as a menu option in the bank
    @ConfigItem(
            keyName = "displayInventorySetups",
            name = "Display Inventory Setups",
            description = "Display Inventory Setups",
            position = 0,
            section = inventorySection
    )
    default boolean displayInventorySetups() {
        return true;
    }

    // boolean to display Setup 1
    @ConfigItem(
            keyName = "displaySetup1",
            name = "Setup 1",
            description = "Display Setup 1",
            position = 1,
            section = inventorySection
    )
    default boolean displaySetup1() {
        return false;
    }

    // String for Setup 1
    @ConfigItem(
            keyName = "Setup1",
            name = "Name:",
            description = "Setup 1",
            position = 2,
            section = inventorySection
    )
    default String Setup1() {
        return "";
    }

    // boolean to display Setup 2
    @ConfigItem(
            keyName = "displaySetup2",
            name = "Setup 2",
            description = "Display Setup 2",
            position = 3,
            section = inventorySection
    )
    default boolean displaySetup2() {
        return false;
    }

    // String for Setup 2
    @ConfigItem(
            keyName = "Setup2",
            name = "Name:",
            description = "Setup 2",
            position = 4,
            section = inventorySection
    )
    default String Setup2() {
        return "";
    }

    // boolean to display Setup 3
    @ConfigItem(
            keyName = "displaySetup3",
            name = "Setup 3",
            description = "Display Setup 3",
            position = 5,
            section = inventorySection
    )
    default boolean displaySetup3() {
        return false;
    }

    // String for Setup 3
    @ConfigItem(
            keyName = "Setup3",
            name = "Name:",
            description = "Setup 3",
            position = 6,
            section = inventorySection
    )
    default String Setup3() {
        return "";
    }

    // boolean to display Setup 4
    @ConfigItem(
            keyName = "displaySetup4",
            name = "Setup 4",
            description = "Display Setup 4",
            position = 7,
            section = inventorySection
    )
    default boolean displaySetup4() {
        return false;
    }

    // String for Setup 4
    @ConfigItem(
            keyName = "Setup4",
            name = "Name:",
            description = "Setup 4",
            position = 8,
            section = inventorySection
    )
    default String Setup4() {
        return "";
    }

    // boolean to fix camera pitch on login
    @ConfigItem(
            keyName = "fixCameraPitch",
            name = "Fix Login Camera Pitch",
            description = "Fixes the camera pitch on login",
            position = 2,
            section = cameraSection
    )
    default boolean fixCameraPitch() {
        return true;
    }

    // boolean to fix camera zoom on login
    @ConfigItem(
            keyName = "fixCameraZoom",
            name = "Fix Login Camera Zoom",
            description = "Fixes the camera zoom on login",
            position = 3,
            section = cameraSection
    )
    default boolean fixCameraZoom() {
        return true;
    }

    // color picker for Accent Color
    @ConfigItem(
            keyName = "accentColor",
            name = "Accent Color",
            description = "Accent Color",
            position = 0,
            section = uiSection
    )
    default Color accentColor() {
        return new Color(220, 138, 0);
    }

    // color picker for toggle button color
    @ConfigItem(
            keyName = "toggleButtonColor",
            name = "Toggle Button Color",
            description = "Toggle Button Color",
            position = 1,
            section = uiSection
    )
    default Color toggleButtonColor() {
        return new Color(220, 138, 0);
    }

    // color picker for plugin label color
    @ConfigItem(
            keyName = "pluginLabelColor",
            name = "Plugin Label Color",
            description = "Plugin Label Color",
            position = 2,
            section = uiSection
    )
    default Color pluginLabelColor() {
        return new Color(255, 255, 255);
    }

    // boolean to quick fletch kindling
    @ConfigItem(
            keyName = "quickFletchKindling",
            name = "Quick Fletch Kindling",
            description = "Quick Fletch Kindling",
            position = 0,
            section = wintertodtSection
    )
    default boolean quickFletchKindling() {
        return true;
    }

    // boolean to resume fletching kindling if interrupted
    @ConfigItem(
            keyName = "resumeFletchingKindling",
            name = "Resume Fletching",
            description = "Resume Fletching Kindling if interrupted",
            position = 1,
            section = wintertodtSection
    )
    default boolean resumeFletchingKindling() {
        return true;
    }

    // boolean to resume feeding brazier if interrupted
    @ConfigItem(
            keyName = "resumeFeedingBrazier",
            name = "Resume Feeding Brazier",
            description = "Resume Feeding Brazier if interrupted",
            position = 2,
            section = wintertodtSection
    )
    default boolean resumeFeedingBrazier() {
        return true;
    }

    // boolean to fix brazier

    @ConfigItem(
            keyName = "fixBrazier",
            name = "Fix Brazier",
            description = "Fix Brazier",
            position = 3,
            section = wintertodtSection
    )
    default boolean fixBrokenBrazier() {
        return true;
    }

    // boolean to light unlit brazier
    @ConfigItem(
            keyName = "lightUnlitBrazier",
            name = "Light Unlit Brazier",
            description = "Light Unlit Brazier",
            position = 4,
            section = wintertodtSection
    )
    default boolean lightUnlitBrazier() {
        return true;
    }

    // boolean to heal Pyromancer
    @ConfigItem(
            keyName = "healPyromancer",
            name = "Heal Pyromancer",
            description = "Heal Pyromancer",
            position = 5,
            section = wintertodtSection
    )
    default boolean healPyromancer() {
        return true;
    }


    // Hidden enum for Wintertodt actions
    @ConfigItem(
            keyName = "wintertodtActions",
            name = "Wintertodt Actions",
            description = "Wintertodt Actions",
            hidden = true
    )
    default WintertodtActions wintertodtActions() {
        return WintertodtActions.NONE;
    }

    // Hidden boolean to check if we were interrupted
    @ConfigItem(
            keyName = "interrupted",
            name = "Interrupted",
            description = "Interrupted",
            hidden = true
    )
    default boolean interrupted() {
        return false;
    }


}
