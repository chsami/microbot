package net.runelite.client.plugins.hoseaplugins.Trapper;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Inventory;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.NPCs;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.TileObjects;
import com.google.inject.Inject;
import net.runelite.client.plugins.hoseaplugins.PiggyUtils.API.PlayerUtil;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Skill;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.game.ItemManager;

import java.util.Optional;


public class AutoTrapperHelper {
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private PlayerUtil playerUtil;

    public boolean inRegion(int regionId) {
        return client.getLocalPlayer().getWorldLocation().getRegionID() == regionId;
    }

    public int getCaughtTraps() {
        int size = TileObjects.search().withName("Net trap").withAction("Check").withinDistance(10).result().size();
        if (size > 0) size = size / 2;
        return size;
    }

    public int getSetTraps() {
        return TileObjects.search().withName("Young tree").withAction("Dismantle").withinDistance(10).result().size();
    }

    public boolean hasTrapSupplies() {
        return playerUtil.hasItem("Small fishing net") && playerUtil.hasItem("Rope");
    }

    public int getMaxTraps() {
        int lvl = client.getBoostedSkillLevel(Skill.HUNTER);
        if (lvl >= 80) {
            return 5;
        } else if (lvl >= 60) {
            return 4;
        } else if (lvl >= 40) {
            return 3;
        } else if (lvl >= 20) {
            return 2;
        }
        return 1;
    }
}