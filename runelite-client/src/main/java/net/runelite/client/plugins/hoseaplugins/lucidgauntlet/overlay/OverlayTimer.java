package net.runelite.client.plugins.hoseaplugins.lucidgauntlet.overlay;

import net.runelite.client.plugins.hoseaplugins.lucidgauntlet.LucidGauntletConfig;
import net.runelite.client.plugins.hoseaplugins.lucidgauntlet.LucidGauntletPlugin;
import net.runelite.client.plugins.hoseaplugins.lucidgauntlet.table.TableAlignment;
import net.runelite.client.plugins.hoseaplugins.lucidgauntlet.table.TableComponent;
import net.runelite.client.plugins.hoseaplugins.lucidgauntlet.table.TableElement;
import net.runelite.client.plugins.hoseaplugins.lucidgauntlet.table.TableRow;
import net.runelite.api.ChatMessageType;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.time.Instant;
import java.util.Arrays;

@Singleton
public class OverlayTimer extends Overlay
{
    private final LucidGauntletConfig config;
    private final ChatMessageManager chatMessageManager;

    private final PanelComponent panelComponent;
    private final TableComponent tableComponent;

    private final TableRow rowPrepTime;
    private final TableRow rowTotalTime;

    private long timeGauntletStart;
    private long timeHunllefStart;

    @Inject
    public OverlayTimer(final LucidGauntletPlugin plugin, final LucidGauntletConfig config, final ChatMessageManager chatMessageManager)
    {
        super(plugin);

        this.config = config;

        this.chatMessageManager = chatMessageManager;

        this.panelComponent = new PanelComponent();
        this.tableComponent = new TableComponent();

        panelComponent.getChildren().add(TitleComponent.builder().text("Gauntlet Timer").build());
        panelComponent.getChildren().add(tableComponent);

        tableComponent.setColumnAlignments(TableAlignment.LEFT, TableAlignment.RIGHT);

        this.rowPrepTime = TableRow.builder()
                .elements(Arrays.asList(
                        TableElement.builder()
                                .content("Prep Time:")
                                .build(),
                        TableElement.builder()
                                .content("")
                                .build()))
                .build();

        this.rowTotalTime = TableRow.builder()
                .elements(Arrays.asList(
                        TableElement.builder()
                                .content("Total Time:")
                                .build(),
                        TableElement.builder()
                                .content("")
                                .build()))
                .build();

        this.timeGauntletStart = -1;
        this.timeHunllefStart = -1;

        setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
        setPriority(OverlayPriority.HIGH);
        determineLayer();
    }

    @Override
    public Dimension render(final Graphics2D graphics2D)
    {
        if (!config.timerOverlay() || timeGauntletStart == -1)
        {
            return null;
        }

        final TableRow tableRow = timeHunllefStart == -1 ? rowPrepTime : rowTotalTime;

        tableRow.getElements()
                .get(1)
                .setContent(calculateElapsedTime(Instant.now().getEpochSecond(), timeGauntletStart));

        return panelComponent.render(graphics2D);
    }

    public void determineLayer()
    {
        setLayer(OverlayLayer.UNDER_WIDGETS);
    }

    public void reset()
    {
        timeGauntletStart = -1;
        timeHunllefStart = -1;
        rowPrepTime.getElements().get(1).setContent("");
        rowTotalTime.getElements().get(1).setContent("");
        tableComponent.getRows().clear();
    }

    public void setGauntletStart()
    {
        timeGauntletStart = Instant.now().getEpochSecond();
        rowPrepTime.setRowColor(Color.WHITE);
        tableComponent.setRows(rowPrepTime);
    }

    public void setHunllefStart()
    {
        timeHunllefStart = Instant.now().getEpochSecond();
        rowPrepTime.setRowColor(Color.LIGHT_GRAY);
        tableComponent.setRows(rowPrepTime);
        tableComponent.addRows(rowTotalTime);
    }

    public void onPlayerDeath()
    {
        if (!config.timerChatMessage())
        {
            return;
        }

        printTime();
        reset();
    }

    private void printTime()
    {
        if (timeGauntletStart == -1 || timeHunllefStart == -1)
        {
            return;
        }

        final long current = Instant.now().getEpochSecond();

        final String elapsedPrepTime = calculateElapsedTime(timeHunllefStart, timeGauntletStart);
        final String elapsedBossTime = calculateElapsedTime(current, timeHunllefStart);
        final String elapsedTotalTime = calculateElapsedTime(current, timeGauntletStart);

        final ChatMessageBuilder chatMessageBuilder = new ChatMessageBuilder()
                .append(ChatColorType.NORMAL)
                .append("Preparation time: ")
                .append(ChatColorType.HIGHLIGHT)
                .append(elapsedPrepTime)
                .append(ChatColorType.NORMAL)
                .append(". Hunllef kill time: ")
                .append(ChatColorType.HIGHLIGHT)
                .append(elapsedBossTime)
                .append(ChatColorType.NORMAL)
                .append(". Total time: ")
                .append(ChatColorType.HIGHLIGHT)
                .append(elapsedTotalTime + ".");

        chatMessageManager.queue(QueuedMessage.builder()
                .type(ChatMessageType.CONSOLE)
                .runeLiteFormattedMessage(chatMessageBuilder.build())
                .build());
    }

    private static String calculateElapsedTime(final long end, final long start)
    {
        final long elapsed = end - start;

        final long minutes = elapsed % 3600 / 60;
        final long seconds = elapsed % 60;

        return String.format("%01d:%02d", minutes, seconds);
    }
}
