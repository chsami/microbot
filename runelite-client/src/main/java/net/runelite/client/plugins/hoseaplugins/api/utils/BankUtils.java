package net.runelite.client.plugins.hoseaplugins.api.utils;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Bank;
import net.runelite.client.plugins.hoseaplugins.ethanapi.InteractionApi.BankInteraction;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.MousePackets;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.WidgetPackets;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.RuneLite;

public class BankUtils
{

    static Client client = RuneLite.getInjector().getInstance(Client.class);
    public static boolean isOpen()
    {
        Widget bankWidget = client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER);
        if (bankWidget != null && !bankWidget.isSelfHidden())
        {
            return true;
        }

        return false;
    }

    public static void depositAll() {
        Widget depositInventory = client.getWidget(WidgetInfo.BANK_DEPOSIT_INVENTORY);
        if (depositInventory != null && !depositInventory.isSelfHidden()) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(depositInventory, "Deposit inventory");
        }
    }

    public static boolean withdraw1(int id)
    {
        if (Bank.search().withId(id).first().isEmpty())
        {
            return false;
        }

        BankInteraction.useItem(id, "Withdraw-1");
        return true;
    }

    public static boolean withdrawAll(int id)
    {
        if (Bank.search().withId(id).first().isEmpty())
        {
            return false;
        }

        BankInteraction.useItem(id, "Withdraw-All");
        return true;
    }

    public static void close()
    {
        if (isOpen())
        {
            client.runScript(29);
        }
    }
}
