package net.runelite.client.plugins.hoseaplugins.api.utils;

import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.WidgetPackets;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.RuneLite;

import java.util.*;
import java.util.List;

public class DialogUtils
{
    static Client client = RuneLite.getInjector().getInstance(Client.class);
    private static List<Integer> continueParentIds = List.of(193, 229, 229, 231, 217, 11);
    private static List<Integer> continueChildIds = List.of(   0,   0,   2,   5,   5,  4);

    public static void queueResumePauseDialog(int widgetId, int childId)
    {
        WidgetPackets.queueResumePause(widgetId, childId);
    }

    public static List<DialogOption> getOptions()
    {
        List<DialogOption> out = new ArrayList<>();
        Widget widget = client.getWidget(219, 1);
        if (widget == null || widget.isSelfHidden())
        {
            return out;
        }

        Widget[] children = widget.getChildren();
        if (children == null)
        {
            return out;
        }

        for (int i = 1; i < children.length; ++i)
        {
            if (children[i] != null && !children[i].getText().isBlank())
            {
                out.add(new DialogOption(i, children[i].getText(), children[i].getTextColor()));
            }
        }

        return out;
    }

    public static boolean canContinue()
    {
        for (int i = 0; i < continueParentIds.size(); i++)
        {
            if (!InteractionUtils.isWidgetHidden(continueParentIds.get(i), continueChildIds.get(i)))
            {
                return true;
            }
        }

        return false;
    }

    public static void sendContinueDialog()
    {
        for (int i = 0; i < continueParentIds.size(); i++)
        {
            if (!InteractionUtils.isWidgetHidden(continueParentIds.get(i), continueChildIds.get(i)))
            {
                queueResumePauseDialog(continueParentIds.get(i) << 16 | continueChildIds.get(i), -1);
            }
        }
    }

    public static void selectOptionIndex(int index)
    {
        Widget widget = client.getWidget(219, 1);
        if (widget == null || widget.isSelfHidden())
        {
            return;
        }

        queueResumePauseDialog(219 << 16 | 1, index);

    }

    public static int getOptionIndex(String option)
    {
        List<DialogOption> options = getOptions();
        if (options.isEmpty())
        {
            return -1;
        }

        for (DialogOption opt : options)
        {
            if (opt.getOptionText().contains(option))
            {
                return opt.getIndex();
            }
        }

        return -1;
    }
}
