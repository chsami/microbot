package net.runelite.client.plugins.microbot.util.dialogues;

import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static net.runelite.client.plugins.microbot.util.Global.sleepUntil;
import static net.runelite.client.plugins.microbot.util.Global.sleepUntilTrue;

public class Rs2Dialogue {

    /**
     * Checks if the player is currently in a dialogue state.
     * This includes NPC dialogues, option dialogues, and other types of interactive dialogues.
     * The method specifically checks if the scroll bar is not visible, indicating an ongoing dialogue.
     *
     * @return true if any dialogue-related widget is visible and the scroll bar is not visible, false otherwise.
     */
    public static boolean isInDialogue() {
        return !Rs2Widget.isWidgetVisible(162, 557) && (hasContinue() || hasSelectAnOption());
    }

    /**
     * Simulates pressing the space key to advance a dialogue that has a "Click here to continue" prompt.
     * This is used for dialogues that require a confirmation to proceed.
     */
    public static void clickContinue() {
        if (hasContinue())
            Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
    }

    /**
     * Checks if the current dialogue contains a "Click here to continue" option.
     *
     * @return true if the "Click here to continue" widget is visible, false otherwise.
     */
    public static boolean hasContinue() {
        return hasNPCContinue() || hasPlayerContinue() || hasDeathContinue() ||
                hasSpriteContinue() || hasTutContinue();
    }

    /**
     * Checks if there is a "Continue" option in an NPC dialogue.
     * This typically indicates the presence of an interactive NPC dialogue.
     *
     * @return true if the "Continue" option is visible in the NPC dialogue, false otherwise.
     */
    private static boolean hasNPCContinue() {
        return Rs2Widget.isWidgetVisible(InterfaceID.DIALOG_NPC, 5);
    }

    /**
     * Checks if there is a "Continue" option in a player dialogue.
     * This typically indicates the presence of an interactive player dialogue.
     *
     * @return true if the "Continue" option is visible in the player dialogue, false otherwise.
     */
    private static boolean hasPlayerContinue() {
        return Rs2Widget.isWidgetVisible(InterfaceID.DIALOG_PLAYER, 5);
    }

    /**
     * Checks if there is a "Continue" option in a death dialogue.
     * This typically indicates the presence of a death screen dialogue.
     *
     * @return true if the "Continue" option is visible in the death dialogue, false otherwise.
     */
    private static boolean hasDeathContinue() {
        return Rs2Widget.isWidgetVisible(663, 0);
    }

    /**
     * Checks if there is a "Continue" option in a sprite-based dialogue.
     * This includes single and double sprite dialogues.
     *
     * @return true if the "Continue" option is visible in either sprite-based dialogue, false otherwise.
     */
    
    private static boolean hasSpriteContinue() {
        return Rs2Widget.isWidgetVisible(InterfaceID.DIALOG_SPRITE, 0) || Rs2Widget.isWidgetVisible(InterfaceID.DIALOG_SPRITE, 3) || Rs2Widget.isWidgetVisible(InterfaceID.DIALOG_DOUBLE_SPRITE, 4);
    }

    /**
     * Checks if there is a "Continue" option in a tutorial dialogue.
     * This includes the presence of the "Continue" option in tutorial-based interfaces.
     *
     * @return true if the "Continue" option is visible in the tutorial dialogue, false otherwise.
     */
    private static boolean hasTutContinue() {
        return Rs2Widget.isWidgetVisible(229, 0) || Rs2Widget.isWidgetVisible(229, 2);
    }

    /**
     * Checks if the current dialogue contains selectable options.
     *
     * @return true if has dialog options is visible
     */
    public static boolean hasSelectAnOption() {
        boolean isWidgetVisible = Rs2Widget.isWidgetVisible(InterfaceID.DIALOG_OPTION, 1);
        if (!isWidgetVisible) return false;
        return Rs2Widget.getWidget(InterfaceID.DIALOG_OPTION, 1).getDynamicChildren() != null;
    }

    /**
     * Retrieves a list of dialogue option widgets currently visible to the player.
     * It skips the first dynamic child widget, as it is typically not an option.
     *
     * @return List of widgets representing the dialogue options, or an empty list if no options are found.
     */
    public static List<Widget> getDialogueOptions() {
        if (!hasSelectAnOption()) return Collections.emptyList();

        List<Widget> out = new ArrayList<>();
        Widget[] dynamicWidgetOptions = Rs2Widget.getWidget(InterfaceID.DIALOG_OPTION, 1).getDynamicChildren();

        // Skip the first dynamic widget option, as it is never an option
        for (int i = 1; i < dynamicWidgetOptions.length; i++) {
            if (dynamicWidgetOptions[i].getText().isBlank()) {
                continue;
            }

            out.add(dynamicWidgetOptions[i]);
        }

        return out;
    }

    /**
     * Finds a dialogue option widget that matches the specified text.
     *
     * @param text  the text to match with the dialogue option.
     * @param exact whether to match the text exactly or allow partial matches.
     * @return the widget representing the matching dialogue option, or null if no match is found.
     */
    public static Widget getDialogueOption(String text, boolean exact) {
        if (!hasSelectAnOption() || getDialogueOptions().isEmpty()) return null;

        Widget dialogueOption;

        if (exact) {
            dialogueOption = getDialogueOptions().stream()
                    .filter(dialop -> dialop.getText().equalsIgnoreCase(text))
                    .findFirst()
                    .orElse(null);
        } else {
            dialogueOption = getDialogueOptions().stream()
                    .filter(dialop -> dialop.getText().toLowerCase().contains(text.toLowerCase()))
                    .findFirst()
                    .orElse(null);
        }

        return dialogueOption;
    }

    /**
     * Finds a dialogue option widget that matches the specified text, allowing partial matches.
     *
     * @param text the text to match with the dialogue option.
     * @return the widget representing the matching dialogue option, or null if no match is found.
     */
    public static Widget getDialogueOption(String text) {
        return getDialogueOption(text, false);
    }

    /**
     * Checks if the specified dialogue option text is present among the current options.
     *
     * @param text  the text to look for.
     * @param exact whether to match the text exactly or allow partial matches.
     * @return true if the specified text is found among the dialogue options, false otherwise.
     */
    public static boolean hasDialogueOption(String text, boolean exact) {
        if (!hasSelectAnOption()) return false;
        List<Widget> dialogueOptions = Rs2Dialogue.getDialogueOptions();
        List<String> dialogueText = dialogueOptions.stream().map(Widget::getText).collect(Collectors.toList());

        if (exact) {
            return dialogueText.stream().anyMatch(dialtxt -> dialtxt.equalsIgnoreCase(text));
        } else {
            return dialogueText.stream().anyMatch(dialtxt -> dialtxt.toLowerCase().contains(text.toLowerCase()));
        }
    }

    /**
     * Checks if the specified dialogue option text is present among the current options, allowing partial matches.
     *
     * @param text the text to look for.
     * @return true if the specified text is found among the dialogue options, false otherwise.
     */
    public static boolean hasDialogueOption(String text) {
        return hasDialogueOption(text, false);
    }

    /**
     * Simulates a key press to select a dialogue option that matches the specified text.
     *
     * @param text  the text to match with the dialogue option.
     * @param exact whether to match the text exactly or allow partial matches.
     */
    public static boolean keyPressForDialogueOption(String text, boolean exact) {
        if (!hasSelectAnOption()) return false;

        Widget dialogueOption = getDialogueOption(text, exact);
        if (dialogueOption == null) return false;

        Rs2Keyboard.keyPress(dialogueOption.getOnKeyListener()[7].toString().charAt(0));
        return true;
    }

    /**
     * Simulates a key press to select a dialogue option that matches the specified text, allowing partial matches.
     *
     * @param text the text to match with the dialogue option.
     */
    public static boolean keyPressForDialogueOption(String text) {
        return keyPressForDialogueOption(text, false);
    }

    /**
     * Simulates a key press to select a dialogue option for the specified index
     *
     * @param index the index of the dialogue option
     */
    public static boolean keyPressForDialogueOption(int index) {
        if (!hasSelectAnOption()) return false;

        Rs2Keyboard.keyPress(String.valueOf(index).charAt(0));
        return true;
    }

    /**
     * Attempts to click on a dialogue option widget with the specified text.
     *
     * <p>This method searches for a widget that contains the specified option text within the dialogue.
     * If such a widget is found, it triggers a click action on it using the {@code Rs2Widget.clickWidget} method.
     * If no matching widget is found, the method returns {@code false}.
     *
     * @param text the text of the dialogue option to click, e.g., "Yes" or "No"
     * @return {@code true} if the widget was found and clicked successfully; {@code false} if no matching widget was found
     */
    public static boolean clickOption(String text) {
        if (!hasSelectAnOption()) return false;
        
        Widget dialogueOption = getDialogueOption(text);
        if (dialogueOption == null) return false;
        
        return Rs2Widget.clickWidget(dialogueOption);
    }

    /**
     * Pauses the current thread until a specified dialogue option becomes available.
     *
     * @param text the text of the dialogue option to wait for
     * @return true if the specified dialogue option appears within the timeout period, otherwise false
     */
    public static boolean sleepUntilHasDialogueOption(String text) {
        return sleepUntilTrue(() -> hasDialogueOption(text));
    }

    /**
     * Pauses the current thread until the player is in a dialogue.
     *
     * @return true if the player enters a dialogue within the timeout period, otherwise false
     */
    public static boolean sleepUntilInDialogue() {
        return sleepUntilTrue(Rs2Dialogue::isInDialogue);
    }

    /**
     * Pauses the current thread until the "Select an Option" dialogue appears.
     *
     * @return true if the "Select an Option" dialogue appears within the timeout period, otherwise false
     */
    public static boolean sleepUntilSelectAnOption() {
        return sleepUntilTrue(Rs2Dialogue::hasSelectAnOption);
    }

    /**
     * Pauses the current thread until a "Continue" option becomes available in the dialogue.
     *
     * @return true if the "Continue" option appears within the timeout period, otherwise false
     */
    public static boolean sleepUntilHasContinue() {
        return sleepUntilTrue(Rs2Dialogue::hasContinue);
    }
}
