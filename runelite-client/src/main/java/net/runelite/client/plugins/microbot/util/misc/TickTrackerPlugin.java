package net.runelite.client.plugins.microbot.util.misc;

import lombok.Getter;
import net.runelite.api.Skill;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.StatChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.HashMap;

@PluginDescriptor(
    name = PluginDescriptor.Default + "TickTracker",
    description = "Tick Tracker utility",
    tags = {"main", "microbot", "parent"},
    alwaysOn = true,
    hidden = true
)
public class TickTrackerPlugin extends Plugin {
    @Getter
    private static int currentTick = 0;
    @Getter
    private static int lastTickAnimating = 0;
    @Getter
    private static int lastTickMoving = 0;
    private static final HashMap<Skill, Integer> skillToLastTickGainedExperience = new HashMap<>();

    public static int getTicksSinceLastExperienceGain(Skill skill) {
        synchronized (skillToLastTickGainedExperience) {
            int lastTickGainedExperience = skillToLastTickGainedExperience.getOrDefault(skill, Integer.MAX_VALUE);

            if(lastTickGainedExperience == Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            }

            return currentTick - lastTickGainedExperience;
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        ++currentTick;

        if (Rs2Player.isAnimating()) {
            lastTickAnimating = currentTick;
        }
        if(Rs2Player.isMoving()) {
            lastTickMoving = currentTick;
        }
    }

    @Subscribe
    public void onStatChanged(StatChanged statChanged)
    {
        final Skill skill = statChanged.getSkill();

        synchronized (skillToLastTickGainedExperience) {
            skillToLastTickGainedExperience.put(skill, currentTick);
        }
    }
}
