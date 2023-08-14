package net.runelite.client.plugins.microbot.util.magic;

import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.camera.Camera;
import net.runelite.client.plugins.microbot.util.globval.enums.InterfaceTab;
import net.runelite.client.plugins.microbot.util.tabs.Tab;
import net.runelite.client.plugins.skillcalculator.skills.MagicAction;

import static net.runelite.client.plugins.microbot.util.Global.sleep;
import static net.runelite.client.plugins.microbot.util.Global.sleepUntil;

public class Rs2Magic {
    public static int widgetId = 0;
    public static MenuAction widgetAction;
    public static String widgetName;

    public boolean canCast(MagicAction magicSpell) {
        return Microbot.getClient().getRealSkillLevel(Skill.MAGIC) >= magicSpell.getLevel();
    }

    public static void cast(MagicAction magicSpell) {
        if (magicSpell.getWidgetAction() == null) {
            if (magicSpell.getName().toLowerCase().contains("teleport") || magicSpell.getName().toLowerCase().contains("enchant")) {
                widgetAction = MenuAction.CC_OP;
            } else {
                widgetAction = MenuAction.WIDGET_TARGET;
            }
        } else {
            widgetAction = magicSpell.getWidgetAction();
        }
        widgetId = magicSpell.getWidgetId();
        widgetName = magicSpell.getName();
        Microbot.getMouse().click();
        sleep(100);
        widgetId = 0;
    }

    public static void castOn(MagicAction magicSpell, Actor actor) {
        if (actor == null) return;
        if (!Camera.isTileOnScreen(actor.getLocalLocation())) {
            Camera.turnTo(actor.getLocalLocation());
            return;
        }
        cast(magicSpell);
        Point point = Perspective.localToCanvas(Microbot.getClient(), actor.getLocalLocation(), Microbot.getClient().getPlane());
        Microbot.getMouse().click(point);
    }

    public static void highAlch() {
        Widget highAlch = Microbot.getClient().getWidget(MagicAction.HIGH_LEVEL_ALCHEMY.getWidgetId());
        alch(highAlch);
    }

    public static void lowAlch() {
        Widget lowAlch = Microbot.getClient().getWidget(MagicAction.LOW_LEVEL_ALCHEMY.getWidgetId());
        alch(lowAlch);
    }

    private static void alch(Widget alch) {
        if (alch == null) return;
        Point point = new Point((int) alch.getBounds().getCenterX(), (int) alch.getBounds().getCenterY());
        sleepUntil(() -> Microbot.getClientThread().runOnClientThread(() -> Tab.getCurrentTab() == InterfaceTab.MAGIC), 5000);
        sleep(300, 600);
        Microbot.getMouse().click(point);
        sleepUntil(() -> Microbot.getClientThread().runOnClientThread(() -> Tab.getCurrentTab() == InterfaceTab.INVENTORY), 5000);
        sleep(300, 600);
        Microbot.getMouse().click(point);
    }

    public static void handleMenuSwapper(MenuEntry menuEntry) {
        if (widgetId == 0) return;
        menuEntry.setOption("Cast");
        menuEntry.setIdentifier(1);
        menuEntry.setParam0(-1);
        menuEntry.setTarget("<col=00ff00>" + Rs2Magic.widgetName + "</col>");
        menuEntry.setType(Rs2Magic.widgetAction);
        menuEntry.setParam1(Rs2Magic.widgetId);
    }
}