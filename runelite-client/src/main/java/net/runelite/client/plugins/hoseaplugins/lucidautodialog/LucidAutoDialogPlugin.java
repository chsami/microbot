package net.runelite.client.plugins.hoseaplugins.lucidautodialog;

import com.google.inject.Provides;
import net.runelite.client.plugins.hoseaplugins.api.utils.*;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

@PluginDescriptor(
        name = "<html><font color=\"#32CD32\">Lucid </font>Auto Dialog</html>",
        description = "Does various dialog options automatically.",
        enabledByDefault = false,
        tags = {"dialog", "quest", "continue"}
)
@Singleton
public class LucidAutoDialogPlugin extends Plugin
{

    @Inject
    private LucidAutoDialogConfig config;

    @Inject
    private ConfigManager configManager;

    @Inject
    private Client client;

    int timeout = 0;

    @Provides
    LucidAutoDialogConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(LucidAutoDialogConfig.class);
    }

    @Subscribe
    private void onGameTick(GameTick gameTick)
    {
        boolean didSomething = false;

        if (config.autoQuestDialog())
        {
           didSomething = doQuestDialog();
        }

        if (config.autoContinue() && !didSomething)
        {
            didSomething = doContinue();
        }

        if (config.autoSelectHighlight() && !didSomething)
        {
            didSomething = doAutoSelectHighlight();
        }

        if (timeout > 0)
        {
            timeout--;
            return;
        }

        if (!didSomething)
        {
            didSomething = handleRandoms();
        }

        if (didSomething)
        {
            timeout = 7;
        }
    }

    private boolean handleRandoms()
    {
        if (config.dismissNotAnimating() && client.getLocalPlayer().getAnimation() != -1)
        {
            return false;
        }

        NPC random = nearestFollower();

        if (random == null || random.getName() == null)
        {
            return false;
        }

        if (shouldDismiss(random.getName()))
        {
            NpcUtils.interact(random, "Dismiss");
            return true;
        }
        else if (shouldTalkTo(random.getName()) && InventoryUtils.getFreeSlots() > 0)
        {
            NpcUtils.interact(random, "Talk-to");
            return true;
        }

        return false;
    }

    private boolean shouldDismiss(String name)
    {
        switch (name)
        {
            case "Bee keeper":
                return config.dismissBeekeeper();
            case "Capt' Arnav":
                return config.dismissArnav();
            case "Niles":
            case "Miles":
            case "Giles":
                return config.dismissGiles();
            case "Count Check":
                return config.dismissCountCheck();
            case "Sergeant Damien":
                return config.dismissDrillDemon();
            case "Drunken Dwarf":
                return config.dismissDrunkenDwarf();
            case "Evil Bob":
                return config.dismissEvilBob();
            case "Postie Pete":
                return config.dismissEvilTwin();
            case "Freaky Forester":
                return config.dismissFreakyForester();
            case "Genie":
                return config.dismissGenie();
            case "Leo":
                return config.dismissGravedigger();
            case "Dr Jekyll":
                return config.dismissJekyllAndHyde();
            case "Frog":
                return config.dismissKissTheFrog();
            case "Mysterious Old Man":
                return config.dismissMysteriousOldMan();
            case "Pillory Guard":
                return config.dismissPillory();
            case "Flippa":
            case "Tilt":
                return config.dismissPinball();
            case "Quiz Master":
                return config.dismissQuizMaster();
            case "Rick Turpentine":
                return config.dismissRickTurpentine();
            case "Sandwich lady":
                return config.dismissSandwichLady();
            case "Strange plant":
                return config.dismissStrangePlant();
            case "Dunce":
                return config.dismissSurpriseExam();
            default:
                return false;
        }
    }

    private boolean shouldTalkTo(String name)
    {
        switch (name)
        {
            case "Genie":
                return config.takeGenieLamp();
            case "Count Check":
                return config.takeCountCheckLamp();
        }
        return false;
    }

    private NPC nearestFollower()
    {
        return NpcUtils.getNearestNpc(npc ->
            (npc.getComposition() != null && npc.getComposition().getActions() != null && Arrays.asList(npc.getComposition().getActions()).contains("Dismiss")) &&
            npc.getInteracting() == client.getLocalPlayer());
    }

    private boolean doQuestDialog()
    {
        List<DialogOption> options = DialogUtils.getOptions();
        if (options.isEmpty())
        {
            return false;
        }

        for (DialogOption option : options)
        {
            if (option.getOptionText().startsWith("["))
            {
                DialogUtils.selectOptionIndex(option.getIndex());
                return true;
            }
        }

        return false;
    }

    private boolean doAutoSelectHighlight()
    {
        List<DialogOption> options = DialogUtils.getOptions();
        if (options.isEmpty())
        {
            return false;
        }

        String hilCol = configManager.getConfiguration("DIALOGUE_ASSISTANT", "optionHighlightColour");

        if (hilCol == null)
        {
            return false;
        }

        Color highlight = Color.decode(hilCol);

        for (DialogOption option : options)
        {
            if (option.getTextColor() != 0 && option.getTextColor() == highlight.getRGB())
            {
                DialogUtils.selectOptionIndex(option.getIndex());
                return true;
            }
        }

        return false;
    }

    private boolean doContinue()
    {
        if (!DialogUtils.canContinue())
        {
            return false;
        }

        DialogUtils.sendContinueDialog();
        return true;
    }
}
