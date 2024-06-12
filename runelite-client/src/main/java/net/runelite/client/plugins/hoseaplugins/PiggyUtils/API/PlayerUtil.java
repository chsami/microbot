package net.runelite.client.plugins.hoseaplugins.PiggyUtils.API;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Inventory;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.NPCs;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Players;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.query.NPCQuery;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.VarPlayer;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldArea;

import java.util.List;

@Slf4j
public class PlayerUtil {
    @Inject
    private Client client;

    /**
     * 0- Auto retaliate on
     * 1- Auto retaliate off
     * @return
     */
    public boolean isAutoRetaliating() {
        return client.getVarpValue(172) == 0;
    }

    public boolean inArea(WorldArea area) {
        return area.contains(client.getLocalPlayer().getWorldLocation());
    }

    public boolean inRegion(int region) {
        return client.getLocalPlayer().getWorldLocation().getRegionID() == region;
    }

    /**
     * Checks if the player is in any of the given regions
     *
     * @param regions
     * @return
     */
    public boolean inRegion(int... regions) {
        for (int region : regions) {
            if (inRegion(region)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasItem(String name) {
        return Inventory.getItemAmount(name) > 0;
    }

    public boolean hasItem(int id) {
        return Inventory.getItemAmount(id) > 0;
    }

    /**
     * Run energy the way we read it
     *
     * @return
     */
    public int runEnergy() {
        return client.getEnergy() / 100;
    }

    /**
     * Spec energy the way we read it
     * @return
     */
    public int specEnergy() {
        return client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT) / 10;
    }

    public int hp() {
        return client.getBoostedSkillLevel(Skill.HITPOINTS);
    }


    public boolean isStaminaActive() {
        return client.getVarbitValue(Varbits.RUN_SLOWED_DEPLETION_ACTIVE) == 1;
    }

    public boolean isRunning() {
        return client.getVarpValue(173) == 0;
    }

    public boolean inMulti() {
        return client.getVarbitValue(Varbits.MULTICOMBAT_AREA) == 1;
    }

    public boolean isInteracting() {
        return client.getLocalPlayer().isInteracting();
    }

    public boolean isBeingInteracted() {
        return NPCs.search().interactingWithLocal().first().isPresent();
    }

    public boolean isBeingInteracted(String name) {
        return NPCs.search().filter(npc -> npc.getName() != null && npc.getName().equalsIgnoreCase(name)).interactingWithLocal().first().isPresent();
    }

    public NPCQuery getBeingInteracted(String name) {
        return NPCs.search().filter(npc -> npc.getName() != null && npc.getName().equalsIgnoreCase(name)).interactingWithLocal();
    }
    public NPCQuery getBeingInteracted(List<String> names) {
        return NPCs.search().filter(npc -> npc.getName() != null && names.contains(npc.getName())).interactingWithLocal();
    }

    /**
     * Slayer task count
     *
     * @return
     */
    public int getTaskCount() {
        return client.getVarpValue(VarPlayer.SLAYER_TASK_SIZE);
    }

}
