package net.runelite.client.plugins.hoseaplugins.gauntletFlicker;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Equipment;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Inventory;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.query.QuickPrayer;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.EthanApiPlugin;
import net.runelite.client.plugins.hoseaplugins.ethanapi.InteractionApi.InteractionHelper;
import net.runelite.client.plugins.hoseaplugins.ethanapi.InteractionApi.InventoryInteraction;
import net.runelite.client.plugins.hoseaplugins.ethanapi.PacketUtils.PacketUtilsPlugin;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.MousePackets;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.WidgetPackets;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.HeadIcon;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.Skill;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;

@PluginDescriptor(
        name = "<html><font color=\"#FF9DF9\">[PP]</font> Gauntlet Assist</html>",
        description = "Made by Ethan Vann, maintained by Piggy Plugins",
        tags = {"ethan"}
)
@Slf4j
public class gauntletFlicker extends Plugin {
    @Inject
    Client client;
    @Inject
    ItemManager manager;
    String updatedWeapon = "";
    boolean forceTab = false;
    Set<Integer> HUNLLEF_IDS = Set.of(NpcID.CORRUPTED_HUNLLEF, NpcID.CORRUPTED_HUNLLEF_9036,
            NpcID.CORRUPTED_HUNLLEF_9037, NpcID.CORRUPTED_HUNLLEF_9038, NpcID.CRYSTALLINE_HUNLLEF,
            NpcID.CRYSTALLINE_HUNLLEF_9022, NpcID.CRYSTALLINE_HUNLLEF_9023, NpcID.CRYSTALLINE_HUNLLEF_9024);
    boolean isRange = true;


    @Subscribe
    public void onGameTick(GameTick e) {
        if (client.getLocalPlayer().isDead() || client.getLocalPlayer().getHealthRatio() == 0) {
            forceTab = false;
            return;
        }
        if (client.getGameState() != GameState.LOGGED_IN) {
            forceTab = false;
            return;
        }

        String name = "";
        if(!Equipment.search().matchesWildCardNoCase("*staff*").empty()){
            name = "staff";
        }
        if(!Equipment.search().matchesWildCardNoCase("*bow*").empty()){
            name = "bow";
        }
        if(!Equipment.search().matchesWildCardNoCase("*halberd*").empty()){
            name = "halberd";
        }
        NPC hunllef = client.getNpcs().stream().filter(x -> HUNLLEF_IDS.contains(x.getId())).findFirst().orElse(null);
        if(hunllef!=null&&(hunllef.isDead()||hunllef.getHealthRatio()==0)){
            if(EthanApiPlugin.isQuickPrayerEnabled()){
                InteractionHelper.togglePrayer();
                return;
            }
        }
        if (client.getVarbitValue(9177) != 1) {
            forceTab = false;
            return;
        }
        if (client.getVarbitValue(9178) != 1) {
            isRange = true;
            forceTab = false;
            updatedWeapon = "";
            return;
        }
        if (forceTab) {
            client.runScript(915, 3);
            forceTab = false;
        }
        if (client.getWidget(5046276) == null) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(client.getWidget(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB), "Setup");
            forceTab = true;
        }
        if (isRange && !EthanApiPlugin.isQuickPrayerActive(QuickPrayer.PROTECT_FROM_MISSILES)) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 13); //quickPrayer range
        } else if (!isRange && !EthanApiPlugin.isQuickPrayerActive(QuickPrayer.PROTECT_FROM_MAGIC)) {
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 12); //quickPrayer magic
        }
        if (hunllef != null) {
            if (EthanApiPlugin.getHeadIcon(hunllef) == HeadIcon.MAGIC && (!name.contains("bow") && !name.contains("halberd"))) {
                Optional<Widget> bow = Inventory.search().matchesWildCardNoCase("*bow*").first();
                Optional<Widget> halberd = Inventory.search().matchesWildCardNoCase("*halberd*").first();
                if (bow.isPresent()) {
                    InventoryInteraction.useItem(bow.get(),"Equip", "Wear", "Wield");
                    updatedWeapon = "bow";
                } else if (halberd.isPresent()) {
                    InventoryInteraction.useItem(halberd.get(),"Equip", "Wear", "Wield");
                    updatedWeapon = "halberd";
                }
            }
            if (EthanApiPlugin.getHeadIcon(hunllef) == HeadIcon.RANGED && (!name.contains("staff") && !name.contains("halberd"))) {
                Optional<Widget> staff = Inventory.search().matchesWildCardNoCase("*staff*").first();
                Optional<Widget> halberd = Inventory.search().matchesWildCardNoCase("*halberd*").first();
                if (staff.isPresent()) {
                    InventoryInteraction.useItem(staff.get(),"Equip", "Wear", "Wield");
                    updatedWeapon = "staff";
                } else if (halberd.isPresent()) {
                    InventoryInteraction.useItem(halberd.get(),"Equip", "Wear", "Wield");
                    updatedWeapon = "halberd";
                }
            }
            if (EthanApiPlugin.getHeadIcon(hunllef) == HeadIcon.MELEE && (!name.contains("staff") && !name.contains("bow"))) {
                Optional<Widget> staff = Inventory.search().matchesWildCardNoCase("*staff*").first();
                Optional<Widget> bow = Inventory.search().matchesWildCardNoCase("*bow*").first();
                if (staff.isPresent()) {
                    InventoryInteraction.useItem(staff.get(),"Equip", "Wear", "Wield");
                    updatedWeapon = "staff";
                } else if (bow.isPresent()) {
                    InventoryInteraction.useItem(bow.get(),"Equip", "Wear", "Wield");
                    updatedWeapon = "bow";
                }
            }
            String weaponTesting = updatedWeapon.isEmpty() ? name : updatedWeapon;
            if (weaponTesting.contains("bow")) {
                if (rigourUnlocked() && !EthanApiPlugin.isQuickPrayerActive(QuickPrayer.RIGOUR)) {
                    MousePackets.queueClickPacket();
                    WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 24); //quickPrayer rigour
                } else if (!rigourUnlocked() && !EthanApiPlugin.isQuickPrayerActive(QuickPrayer.EAGLE_EYE)) {
                    MousePackets.queueClickPacket();
                    WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 22); //quickPrayer eagle eye
                }
            } else if (weaponTesting.contains("staff")) {
                if (auguryUnlucked() && !EthanApiPlugin.isQuickPrayerActive(QuickPrayer.AUGURY)) {
                    MousePackets.queueClickPacket();
                    WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 27); //quickPrayer augury
                } else if (!auguryUnlucked() && !EthanApiPlugin.isQuickPrayerActive(QuickPrayer.MYSTIC_MIGHT)) {
                    MousePackets.queueClickPacket();
                    WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 23); //quickPrayer mystic might
                }
            } else if (weaponTesting.contains("halberd")) {
                if (pietyUnlocked() && !EthanApiPlugin.isQuickPrayerActive(QuickPrayer.PIETY)) {
                    MousePackets.queueClickPacket();
                    WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 26);
                } else if (!pietyUnlocked() && !EthanApiPlugin.isQuickPrayerActive(QuickPrayer.ULTIMATE_STRENGTH)) {
                    MousePackets.queueClickPacket();
                    WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 10);
                    if (!EthanApiPlugin.isQuickPrayerActive(QuickPrayer.INCREDIBLE_REFLEXES)) {
                        MousePackets.queueClickPacket();
                        WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, 11);
                    }
                } else if (!pietyUnlocked() && !EthanApiPlugin.isQuickPrayerActive(QuickPrayer.STEEL_SKIN)) {
                    MousePackets.queueClickPacket();
                    WidgetPackets.queueWidgetActionPacket(1, 5046276, -1, QuickPrayer.STEEL_SKIN.getIndex());
                }
            }
        }
        if (EthanApiPlugin.isQuickPrayerEnabled()) {
            InteractionHelper.togglePrayer();
        }
        InteractionHelper.togglePrayer();
    }

    public boolean rigourUnlocked() {
        return !(client.getVarbitValue(5451) == 0) && client.getRealSkillLevel(Skill.PRAYER) >= 74 && client.getRealSkillLevel(Skill.DEFENCE) >= 70;
    }

    public boolean pietyUnlocked() {
        return client.getVarbitValue(3909) == 8 && client.getRealSkillLevel(Skill.PRAYER) >= 70 && client.getRealSkillLevel(Skill.DEFENCE) >= 70;
    }

    public boolean auguryUnlucked() {
        return !(client.getVarbitValue(5452) == 0) && client.getRealSkillLevel(Skill.PRAYER) >= 77 && client.getRealSkillLevel(Skill.DEFENCE) >= 70;
    }


    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        if (event.getMenuOption().toLowerCase().contains("pass")) {
            isRange = true;
        }
    }

    @Override
    protected void startUp() {
        isRange = true;
        forceTab = false;
        updatedWeapon = "";
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged e) {
        if (e.getActor() == null) {
            return;
        }
        if (!(e.getActor() instanceof NPC)) {
            return;
        }

        NPC npc = (NPC) e.getActor();
        if (!HUNLLEF_IDS.contains(npc.getId())) {
            return;
        }
        if (e.getActor().getAnimation() == 8754) {
            isRange = false;
        }
        if (e.getActor().getAnimation() == 8755) {
            isRange = true;
        }
    }

    private boolean isHunllefVarbitSet() {
        return client.getVarbitValue(9177) == 1;
    }
}