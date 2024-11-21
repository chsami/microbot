/*
 * Copyright (c) 2023, Zoinkwiz <https://github.com/Zoinkwiz>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.microbot.questhelper.helpers.quests.enlightenedjourney;


import net.runelite.api.ItemID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.questhelper.panel.PanelDetails;
import net.runelite.client.plugins.microbot.questhelper.questhelpers.ComplexStateQuestHelper;
import net.runelite.client.plugins.microbot.questhelper.questinfo.QuestHelperQuest;
import net.runelite.client.plugins.microbot.questhelper.requirements.Requirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.item.ItemRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.player.SkillRequirement;
import net.runelite.client.plugins.microbot.questhelper.requirements.quest.QuestRequirement;
import net.runelite.client.plugins.microbot.questhelper.rewards.ExperienceReward;
import net.runelite.client.plugins.microbot.questhelper.rewards.UnlockReward;
import net.runelite.client.plugins.microbot.questhelper.steps.QuestStep;

import java.util.*;


public class CraftingGuildBalloonFlight extends ComplexStateQuestHelper {
    BalloonFlightStep fly;


    ItemRequirement oakLogs;

    @Override
    public QuestStep loadStep() {
        initializeRequirements();

        HashMap<Integer, List<Integer>> sections = new HashMap<>();
        List<Integer> section1 = Arrays.asList(8, 8, 8, 8, 9, 10, 10, 10, 8, 8, 8, 8, 8, 9, 7, 7, 7, 6, 6, 7, 7);
        List<Integer> section2 = Arrays.asList(7, 7, 7, 6, 6, 7, 7, 7, 7, 7, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7);
        List<Integer> section3 = Arrays.asList(7, 7, 7, 7, 9, 9, 9, 9, 9, 9, 9, 7, 7, 7, 7, 6, 6, 6, 6, 5);
        sections.put(4, section1);
        sections.put(5, section2);
        sections.put(6, section3);

        fly = new BalloonFlightStep(this, "Navigate the balloon on Entrana to the Crafting Guild.", sections, oakLogs);
        return fly;
    }

    @Override
    protected void setupRequirements() {
        oakLogs = new ItemRequirement("Oak logs", ItemID.OAK_LOGS, 10);
    }

    @Override
    public List<ItemRequirement> getItemRequirements() {
        return Collections.singletonList(oakLogs);
    }


    @Override
    public List<Requirement> getGeneralRequirements() {
        List<Requirement> reqs = new ArrayList<>();
        reqs.add(new QuestRequirement(QuestHelperQuest.ENLIGHTENED_JOURNEY, QuestState.FINISHED));
        reqs.add(new SkillRequirement(Skill.FIREMAKING, 30));
        return reqs;
    }

    @Override
    public List<ExperienceReward> getExperienceRewards() {
        return Collections.singletonList(new ExperienceReward(Skill.FIREMAKING, 2000));
    }

    @Override
    public List<UnlockReward> getUnlockRewards() {
        return Collections.singletonList(
                new UnlockReward("Ability to fly via balloon to the Crafting Guild with 1 oak log")
        );
    }

    @Override
    public ArrayList<PanelDetails> getPanels() {
        ArrayList<PanelDetails> allSteps = new ArrayList<>();

        allSteps.add(new PanelDetails("Flying to the Crafting Guild", Collections.singletonList(fly), oakLogs));

        return allSteps;
    }
}
