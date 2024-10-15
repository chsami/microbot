package net.runelite.client.plugins.microbot.zerozeroQOL;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;

import java.awt.Color;

public class zerozeroQOLScript extends Script {

    public static String version = "V1.0";
    private final zerozeroQOLConfig config;
    private final ChatMessageManager chatMessageManager;
    private final WalkLocation location;

    // Varrock tree coordinates
    private static final WorldPoint TREE_RUN_VARROCK = new WorldPoint(3226, 3458, 0);

    // Constructor to inject the config, chatMessageManager, and the destination location
    public zerozeroQOLScript(zerozeroQOLConfig config, ChatMessageManager chatMessageManager, WalkLocation location) {
        this.config = config;
        this.chatMessageManager = chatMessageManager;
        this.location = location;
    }

    @Override
    public boolean run() {
        // Apply antiban settings (e.g., simulate human-like behavior)
        Rs2Antiban.antibanSetupTemplates.applyFarmingSetup();

        // Use switch case to decide where to walk based on the location enum
        switch (location) {
            case BANK:
                sendChatMessage("Walking to the nearest bank...", Color.GREEN);
                Rs2Bank.walkToBankAndUseBank();
                sendChatMessage("Bank opened!", Color.GREEN);
                break;
            case VARROCK_TREE:
                sendChatMessage("Walking to the tree in Varrock...", Color.GREEN);
                Rs2Walker.walkTo(TREE_RUN_VARROCK);
                sendChatMessage("Reached the tree in Varrock!", Color.GREEN);
                break;
            default:
                sendChatMessage("Unknown location. Cannot walk anywhere.", Color.RED);
                break;
        }

        return false; // Stop execution after running the action
    }

    // Utility method to send chat messages to the in-game chatbox
    private void sendChatMessage(String message, Color color) {
        final String builtMessage = new ChatMessageBuilder()
                .append(color, message)
                .build();
        chatMessageManager.queue(QueuedMessage.builder()
                .type(net.runelite.api.ChatMessageType.GAMEMESSAGE)
                .runeLiteFormattedMessage(builtMessage)
                .build());
    }
}
