package net.runelite.client.plugins.microbot.questhelper.helpers.quests.thefremennikisles;


import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.microbot.questhelper.steps.NpcStep;
import net.runelite.client.plugins.microbot.questhelper.questhelpers.QuestHelper;

public class KillTrolls extends NpcStep {
    public KillTrolls(QuestHelper questHelper) {
        super(questHelper, NpcID.FRENZIED_ICE_TROLL_MALE, new WorldPoint(2390, 10280, 1), "Kill 10 ice trolls.", true);
        this.addAlternateNpcs(NpcID.ICE_TROLL_MALE_5829, NpcID.FRENZIED_ICE_TROLL_FEMALE, NpcID.ICE_TROLL_FEMALE_5830, NpcID.FRENZIED_ICE_TROLL_RUNT, NpcID.ICE_TROLL_RUNT_5828);
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        updateSteps();
    }

    protected void updateSteps() {
        int numToKill = client.getVarbitValue(3312);
        this.setText("Kill " + numToKill + " trolls to continue.");
    }
}

