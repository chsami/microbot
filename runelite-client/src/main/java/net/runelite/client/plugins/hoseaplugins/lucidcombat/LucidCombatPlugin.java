package net.runelite.client.plugins.hoseaplugins.lucidcombat;

import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.ETileItem;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.EthanApiPlugin;
import net.runelite.client.plugins.hoseaplugins.ethanapi.Packets.MousePackets;
import com.google.inject.Provides;
import net.runelite.client.plugins.hoseaplugins.api.item.SlottedItem;
import net.runelite.client.plugins.hoseaplugins.api.spells.Runes;
import net.runelite.client.plugins.hoseaplugins.api.spells.WidgetInfo;
import net.runelite.client.plugins.hoseaplugins.api.utils.*;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Slf4j
@PluginDescriptor(name = PluginDescriptor.Lucid + "Combat</html>", description = "Helps with Combat related stuff", enabledByDefault = false)
public class LucidCombatPlugin extends Plugin implements KeyListener
{

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private LucidCombatTileOverlay overlay;

    @Inject
    private LucidCombatPanelOverlay panelOverlay;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ConfigManager configManager;

    @Inject
    private KeyManager keyManager;

    @Inject
    private LucidCombatConfig config;

    @Inject
    private ItemManager itemManager;

    private int nextSolidFoodTick = 0;
    private int nextPotionTick = 0;
    private int nextKarambwanTick = 0;

    private boolean eatingToMaxHp = false;

    private boolean drinkingToMaxPrayer = false;

    private int timesBrewedDown = 0;

    private Random random = new Random();

    @Getter
    private int nextHpToRestoreAt = 0;

    @Getter
    private int nextPrayerLevelToRestoreAt = 0;

    private int lastTickActive = 0;

    private int nextReactionTick = 0;

    private int lastFinisherAttempt = 0;

    private int nonSpecWeaponId = -1;
    private int offhandWeaponID = -1;

    private boolean isSpeccing = false;

    @Getter
    private Actor lastTarget = null;

    @Getter
    private List<ETileItem> itemIgnoreList = new ArrayList<>();

    @Getter
    private ETileItem currentLootTarget = null;

    @Getter
    private boolean autoCombatRunning = false;

    @Getter
    private String secondaryStatus = "Starting...";

    @Getter
    private WorldPoint startLocation = null;

    @Getter
    private WorldPoint safeSpotLocation = null;

    private int nextLootAttempt = 0;

    private boolean taskEnded = false;

    private boolean tabbed = true;

    private int lastTabAttempt = 0;

    private int lastCannonAttempt = 0;

    private final List<String> prayerRestoreNames = List.of("Prayer potion", "Super restore", "Sanfew serum", "Blighted super restore", "Moonlight potion");

    private final List<String> antiFireNames = List.of("Extended super antifire", "Super antifire potion", "Extended antifire", "Antifire potion");

    private final List<String> antiPoisonNames = List.of("Antidote+", "Superantipoison", "Antipoison");

    private final List<String> antiVenomNames = List.of("Anti-venom+", "Anti-venom", "Antidote++");
    private int lastAlchTick = 0;

    private int lastThrallTick = 0;

    private final Predicate<SlottedItem> foodFilterNoBlacklistItems = (item) -> {
        final ItemComposition itemComposition = client.getItemDefinition(item.getItem().getId());
        return itemComposition.getName() != null &&
                (!itemComposition.getName().equals("Cooked karambwan") && !itemComposition.getName().equals("Blighted karambwan")) &&
                !config.foodBlacklist().contains(itemComposition.getName()) &&
                (Arrays.asList(itemComposition.getInventoryActions()).contains("Eat"));
    };

    private final Predicate<SlottedItem> karambwanFilter = (item) -> {
        final ItemComposition itemComposition = client.getItemDefinition(item.getItem().getId());
        return itemComposition.getName() != null &&
                (itemComposition.getName().equals("Cooked karambwan") || itemComposition.getName().equals("Blighted karambwan")) &&
                (Arrays.asList(itemComposition.getInventoryActions()).contains("Eat"));
    };

    @Override
    protected void startUp()
    {
        clientThread.invoke(this::pluginEnabled);
    }

    private void pluginEnabled()
    {
        keyManager.registerKeyListener(this);

        if (config.allowExternalSetup())
        {
            if (configManager.getConfiguration("lucid-combat", "autocombatEnabled") == null)
            {
                configManager.setConfiguration("lucid-combat", "autocombatEnabled", false);
            }
        }

        if (!overlayManager.anyMatch(p -> p == overlay))
        {
            overlayManager.add(overlay);
        }

        if (!overlayManager.anyMatch(p -> p == panelOverlay))
        {
            overlayManager.add(panelOverlay);
        }

        taskEnded = false;
        tabbed = false;
    }

    @Override
    protected void shutDown()
    {
        keyManager.unregisterKeyListener(this);
        if (config.allowExternalSetup())
        {
            configManager.setConfiguration("lucid-combat", "autocombatEnabled", false);
        }

        resetAutoCombat();

        if (overlayManager.anyMatch(p -> p == overlay))
        {
            overlayManager.remove(overlay);
        }

        if (overlayManager.anyMatch(p -> p == panelOverlay))
        {
            overlayManager.remove(panelOverlay);
        }
    }

    @Provides
    LucidCombatConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(LucidCombatConfig.class);
    }

    @Subscribe
    private void onMenuOpened(MenuOpened event)
    {
        if (!config.rightClickMenu())
        {
            return;
        }

        final Optional<MenuEntry> attackEntry = Arrays.stream(event.getMenuEntries()).filter(menu -> menu.getOption().equals("Attack") && menu.getNpc() != null && menu.getNpc().getName() != null).findFirst();

        if (attackEntry.isEmpty())
        {
            return;
        }

        if (!autoCombatRunning)
        {
            client.createMenuEntry(1)
            .setOption("Start Killing")
            .setTarget("<col=ffff00>" + attackEntry.get().getNpc().getName() + "</col>")
            .setType(MenuAction.RUNELITE)
            .onClick((entry) -> {
                clientThread.invoke(() ->
                {
                    configManager.setConfiguration("lucid-combat", "npcToFight", attackEntry.get().getNpc().getName());

                    if (config.allowExternalSetup())
                    {
                        configManager.setConfiguration("lucid-combat", "autocombatEnabled", true);
                    }

                    resetAutoCombat();
                    startAutoCombat();
                });
            });
        }
        else
        {
            if (attackEntry.get().getNpc() == null || attackEntry.get().getNpc().getName() == null)
            {
                return;
            }

            if (isNameInNpcsToFight(attackEntry.get().getNpc().getName()))
            {
                client.createMenuEntry(1)
                .setOption("Stop Killing")
                .setTarget("<col=ffff00>" + attackEntry.get().getNpc().getName() + "</col>")
                .setType(MenuAction.RUNELITE)
                .onClick((entry) -> {
                    resetAutoCombat();
                });
            }
        }
    }

    private boolean isNameInNpcsToFight(String name)
    {
        if (config.npcToFight().strip().isEmpty())
        {
            return false;
        }

        for (String npcName : config.npcToFight().split(","))
        {
            npcName = npcName.strip();

            if (name.contains(npcName))
            {
                return true;
            }
        }

        return false;
    }

    private boolean idInNpcBlackList(int id)
    {
        if (config.idBlacklist().strip().isEmpty())
        {
            return false;
        }

        for (String stringId : config.idBlacklist().split(","))
        {
            String idTrimmed = stringId.strip();
            int npcId = Integer.parseInt(idTrimmed);

            if (id == npcId)
            {
                return true;
            }
        }
        return false;
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event)
    {
        if (!event.getGroup().equals("lucid-combat"))
        {
            return;
        }

        if (config.allowExternalSetup() && event.getKey().equals("autocombatEnabled"))
        {
            boolean enabled = Boolean.parseBoolean(event.getNewValue());
            clientThread.invoke(() -> {
                MessageUtils.addMessage("Auto combat state changed externally.", Color.RED);
                resetAutoCombat();
                if (enabled)
                {
                    startAutoCombat();
                }
            });
        }
    }

    private void startAutoCombat()
    {
        autoCombatRunning = true;
        startLocation = client.getLocalPlayer().getWorldLocation();
        safeSpotLocation = client.getLocalPlayer().getWorldLocation();
    }
    private void resetAutoCombat()
    {
        autoCombatRunning = false;
        startLocation = null;
        safeSpotLocation = null;
        lastTickActive = client.getTickCount();
        lastAlchTick = client.getTickCount();
        tabbed = false;
        taskEnded = false;
        lastTarget = null;
        nextHpToRestoreAt = Math.max(1, config.minHp() + (config.minHpBuffer() > 0 ? random.nextInt(config.minHpBuffer() + 1) : 0));
        nextPrayerLevelToRestoreAt = Math.max(1, config.prayerPointsMin() + (config.prayerRestoreBuffer() > 0 ? random.nextInt(config.prayerRestoreBuffer() + 1) : 0));
    }

    @Subscribe
    private void onMenuOptionClicked(MenuOptionClicked event)
    {
        if (config.specIfEquipped() && (event.getMenuOption().equals("Wield") || event.getMenuOption().equals("Wear")) && (!config.specWeapon().isEmpty() && event.getMenuTarget().contains(config.specWeapon())))
        {
            lastTarget = client.getLocalPlayer().getInteracting();

            if (EquipmentUtils.getWepSlotItem() != null)
            {
                nonSpecWeaponId = EquipmentUtils.getWepSlotItem().getId();
            }

            if (EquipmentUtils.getShieldSlotItem() != null)
            {
                offhandWeaponID = EquipmentUtils.getShieldSlotItem().getId();
            }
        }
    }

    @Subscribe
    private void onChatMessage(ChatMessage event)
    {
        if (event.getType() == ChatMessageType.GAMEMESSAGE && (event.getMessage().contains("return to a Slayer master") || event.getMessage().contains("more advanced Slayer Master")))
        {

            if (config.stopOnTaskCompletion() && autoCombatRunning)
            {
                secondaryStatus = "Slayer Task Done";
                startLocation = null;

                if (config.allowExternalSetup())
                {
                    configManager.setConfiguration("lucid-combat", "autocombatEnabled", false);
                }

                autoCombatRunning = false;
                taskEnded = true;
                lastTarget = null;
            }

            if (config.stopUpkeepOnTaskCompletion() && taskEnded)
            {
                lastTickActive = 0;
            }
        }

        if (event.getType() == ChatMessageType.GAMEMESSAGE && event.getMessage().contains("can't take items"))
        {
            if (currentLootTarget != null)
            {
                itemIgnoreList.add(currentLootTarget);
                currentLootTarget = null;
            }
        }

        if (event.getType() == ChatMessageType.GAMEMESSAGE && event.getMessage().contains("can't reach that"))
        {
            lastTarget = null;
            lastTickActive = client.getTickCount();
            nextReactionTick = client.getTickCount() + 1;
        }

        if (event.getType() == ChatMessageType.GAMEMESSAGE && (event.getMessage().contains("cannot cast that") || event.getMessage().contains("can't resurrect a thrall here")))
        {
            lastThrallTick = client.getTickCount();
        }
    }

    @Subscribe
    private void onGameTick(GameTick tick)
    {
        if (client.getGameState() != GameState.LOGGED_IN || BankUtils.isOpen())
        {
            return;
        }

        updatePluginVars();

        itemIgnoreList.removeIf(tileItem -> tileItem == null || tileItem.getTileItem() == null);

        if (hpFull() && eatingToMaxHp)
        {
            secondaryStatus = "HP Full Now";
            eatingToMaxHp = false;
        }

        if (prayerFull() && drinkingToMaxPrayer)
        {
            secondaryStatus = "Prayer Full Now";
            drinkingToMaxPrayer = false;
        }

        boolean actionTakenThisTick = restorePrimaries();

        // Stop other upkeep besides HP if we haven't animated in the last minute
        if (getInactiveTicks() > config.inactiveTicks())
        {
            if (config.deactivatePrayersOnIdle())
            {
                CombatUtils.deactivatePrayers(false);
            }

            secondaryStatus = "Idle for > " + config.inactiveTicks() + " ticks";
            return;
        }

        if (client.getTickCount() > 10 && client.getTickCount() - lastAlchTick == 1)
        {
            client.runScript(915, 3);
        }

        if (!actionTakenThisTick)
        {
            actionTakenThisTick = restoreStats();

            if (actionTakenThisTick)
            {
                secondaryStatus = "Restoring Stats";
            }
        }

        if (!actionTakenThisTick)
        {
            actionTakenThisTick = restoreBoosts();

            if (actionTakenThisTick)
            {
                secondaryStatus = "Restoring Boosts";
            }
        }

        if (!actionTakenThisTick)
        {
            actionTakenThisTick = handleSlayerFinisher();

            if (actionTakenThisTick)
            {
                secondaryStatus = "Finishing Slayer Monster";
            }
        }

        if (!actionTakenThisTick)
        {
            actionTakenThisTick = handleAutoSpec();

            if (actionTakenThisTick)
            {
                secondaryStatus = "Auto-Spec";
            }
        }

        if (!actionTakenThisTick)
        {
            actionTakenThisTick = handleSlaughterEquip();
        }

        if (!actionTakenThisTick)
        {
            actionTakenThisTick = handleExpeditiousEquip();
        }

        if (!actionTakenThisTick)
        {
            actionTakenThisTick = waitingForFinisher();
        }

        if (!actionTakenThisTick)
        {
            actionTakenThisTick = handleLooting();

            if (!actionTakenThisTick && (nextLootAttempt - client.getTickCount()) < 0 && lastTarget != null)
            {
                actionTakenThisTick = handleReAttack();
            }
        }

        if (!actionTakenThisTick)
        {
            actionTakenThisTick = handleThralls();
        }

        if (!actionTakenThisTick)
        {
            actionTakenThisTick = handleAutoCombat();
        }
    }

    public boolean isMoving()
    {
        return client.getLocalPlayer().getPoseAnimation() != client.getLocalPlayer().getIdlePoseAnimation();
    }

    private boolean handleSlaughterEquip()
    {
        if (!config.equipSlaughterBracelet())
        {
            return false;
        }

        if (!autoCombatRunning)
        {
            return false;
        }

        if (!InventoryUtils.contains("Bracelet of slaughter"))
        {
            return false;
        }

        if (config.equipExpeditiousBracelet() && EquipmentUtils.contains("Expeditious bracelet"))
        {
            return false;
        }

        if (EquipmentUtils.contains("Bracelet of slaughter"))
        {
            return false;
        }

        Item bracelet = InventoryUtils.getFirstItem("Bracelet of slaughter");

        if (bracelet != null)
        {
            InventoryUtils.itemInteract(bracelet.getId(), "Wear");
            return true;
        }

        return false;
    }

    private boolean handleExpeditiousEquip()
    {
        if (!config.equipExpeditiousBracelet())
        {
            return false;
        }

        if (!autoCombatRunning)
        {
            return false;
        }

        if (!InventoryUtils.contains("Expeditious bracelet"))
        {
            return false;
        }

        if (EquipmentUtils.contains("Expeditious bracelet"))
        {
            return false;
        }

        if (config.equipSlaughterBracelet() && EquipmentUtils.contains("Bracelet of slaughter"))
        {
            return false;
        }

        Item bracelet = InventoryUtils.getFirstItem("Expeditious bracelet");

        if (bracelet != null)
        {
            InventoryUtils.itemInteract(bracelet.getId(), "Wear");
            return true;
        }

        return false;
    }

    private boolean handleReAttack()
    {
        if (config.alchStuff() && !getAlchableItems().isEmpty())
        {
            return false;
        }

        if (lastTarget == null || isMoving())
        {
            return false;
        }

        if (client.getLocalPlayer().getInteracting() == lastTarget)
        {
            lastTarget = null;
            return false;
        }

        if (lastTarget.getInteracting() != client.getLocalPlayer())
        {
            lastTarget = null;
            return false;
        }

        if (lastTarget instanceof Player && isPlayerEligible((Player)lastTarget))
        {
            PlayerUtils.interactPlayer(lastTarget.getName(), "Attack");
            lastTarget = null;
            secondaryStatus = "Re-attacking previous target";
            return true;
        }
        else if (lastTarget instanceof NPC && isNpcEligible((NPC)lastTarget))
        {
            NpcUtils.interact((NPC)lastTarget, "Attack");
            lastTarget = null;
            secondaryStatus = "Re-attacking previous target";
            return true;
        }

        return false;
    }

    private boolean handleSlayerFinisher()
    {
        Actor target = client.getLocalPlayer().getInteracting();
        if (!(target instanceof NPC))
        {
            return false;
        }

        if (InventoryUtils.contains("Fungicide spray 0") && InventoryUtils.contains(ItemID.FUNGICIDE))
        {
            Item spray = InventoryUtils.getFirstItem(ItemID.FUNGICIDE_SPRAY_0);
            Item refill = InventoryUtils.getFirstItem(ItemID.FUNGICIDE);
            InventoryUtils.itemOnItem(refill, spray);
            return false;
        }

        NPC npcTarget = (NPC) target;
        int ratio = npcTarget.getHealthRatio();
        int scale = npcTarget.getHealthScale();

        double targetHpPercent = Math.floor((double) ratio  / (double) scale * 100);
        if (targetHpPercent < config.slayerFinisherHpPercent() && targetHpPercent >= 0 && npcTarget.getAnimation() != config.slayerFinisherItem().getDeathAnimation())
        {
            if (config.slayerFinisherItem() == LucidCombatConfig.SlayerFinisher.NONE)
            {
                return false;
            }

            Item slayerFinisher = InventoryUtils.getFirstItem(config.slayerFinisherItem().getItemName());
            if (config.autoSlayerFinisher() && slayerFinisher != null &&
                    client.getTickCount() - lastFinisherAttempt > 6)
            {
                InteractionUtils.useItemOnNPC(slayerFinisher.getId(), npcTarget);
                lastFinisherAttempt = client.getTickCount();
                int distance = (int) InteractionUtils.distanceTo2DHypotenuse(npcTarget.getWorldLocation(), client.getLocalPlayer().getWorldLocation(), npcTarget.getWorldArea().getWidth(), 1);
                nextReactionTick = client.getTickCount() + (distance / 2) + 2;
                return true;
            }
        }
        return false;
    }

    private boolean handleAutoSpec()
    {
        if (!config.enableAutoSpec() || config.specWeapon().isEmpty())
        {
            return false;
        }

        if (config.specIfAutocombat() && !autoCombatRunning)
        {
            return false;
        }

        if (!isSpeccing && canStartSpeccing())
        {
            isSpeccing = true;
        }
        else if (isSpeccing && !canSpec())
        {
            isSpeccing = false;
        }

        if (nonSpecWeaponId != -1 && !isSpeccing)
        {
            lastTarget = client.getLocalPlayer().getInteracting();
            if (InventoryUtils.itemHasAction(nonSpecWeaponId, "Wield"))
            {
                InventoryUtils.itemInteract(nonSpecWeaponId, "Wield");
            }
            else if (InventoryUtils.itemHasAction(nonSpecWeaponId, "Wear"))
            {
                InventoryUtils.itemInteract(nonSpecWeaponId, "Wear");
            }

            if (offhandWeaponID != -1)
            {
                if (InventoryUtils.itemHasAction(offhandWeaponID, "Wield"))
                {
                    InventoryUtils.itemInteract(offhandWeaponID, "Wield");
                }
                else if (InventoryUtils.itemHasAction(offhandWeaponID, "Wear"))
                {
                    InventoryUtils.itemInteract(offhandWeaponID, "Wear");
                }
            }

            nonSpecWeaponId = -1;
            offhandWeaponID = -1;
            return true;
        }

        if (!isSpeccing)
        {
            return false;
        }

        boolean equippedItem = false;
        if (!EquipmentUtils.contains(config.specWeapon()))
        {
            if (!config.specIfEquipped())
            {
                Item specWeapon = InventoryUtils.getFirstItem(config.specWeapon());
                if (specWeapon != null && canSpec())
                {
                    if (EquipmentUtils.getWepSlotItem() != null)
                    {
                        nonSpecWeaponId = EquipmentUtils.getWepSlotItem().getId();
                    }

                    if (EquipmentUtils.getShieldSlotItem() != null)
                    {
                        offhandWeaponID = EquipmentUtils.getShieldSlotItem().getId();
                    }

                    lastTarget = client.getLocalPlayer().getInteracting();

                    if (lastTarget != null)
                    {
                        if (InventoryUtils.itemHasAction(specWeapon.getId(), "Wield"))
                        {
                            InventoryUtils.itemInteract(specWeapon.getId(), "Wield");
                            equippedItem = true;
                        }
                        else if (InventoryUtils.itemHasAction(specWeapon.getId(), "Wear"))
                        {
                            InventoryUtils.itemInteract(specWeapon.getId(), "Wear");
                            equippedItem = true;
                        }
                    }
                }
            }
        }
        else
        {
            if (client.getLocalPlayer().getInteracting() != null || lastTarget != null)
            {
                equippedItem = true;
            }
        }

        if (equippedItem && isSpeccing && !CombatUtils.isSpecEnabled())
        {
            CombatUtils.toggleSpec();
            return true;
        }

        return false;
    }

    private boolean canStartSpeccing()
    {
        final int spec = CombatUtils.getSpecEnergy();
        return spec >= config.minSpec() && spec >= config.specNeeded();
    }

    private boolean canSpec()
    {
        final int spec = CombatUtils.getSpecEnergy();
        return spec >= config.specNeeded();
    }

    private boolean handleLooting()
    {
        if (!autoCombatRunning)
        {
            return false;
        }

        if (nextLootAttempt == 0)
        {
            nextLootAttempt = client.getTickCount();
        }

        if (ticksUntilNextLootAttempt() > 0)
        {
            return false;
        }

        boolean ignoringTargetLimitation = ticksUntilNextLootAttempt() < -config.maxTicksBetweenLooting();

        if (config.onlyLootWithNoTarget() && !(targetDeadOrNoTargetIgnoreAttackingUs() || ignoringTargetLimitation))
        {
            return false;
        }

        List<ETileItem> lootableItems = getLootableItems();

        if (config.stackableOnly())
        {
            lootableItems.removeIf(loot -> {

                if (config.buryScatter())
                {
                    return (!isStackable(loot.getTileItem().getId()) && !canBuryOrScatter(loot.getTileItem().getId())) || (loot.getTileItem().getId() == ItemID.CURVED_BONE || loot.getTileItem().getId() == ItemID.LONG_BONE);
                }

                return !isStackable(loot.getTileItem().getId());
            });
        }

        if (InventoryUtils.getFreeSlots() == 0)
        {
            lootableItems.removeIf(loot -> !isStackable(loot.getTileItem().getId()) || (isStackable(loot.getTileItem().getId()) && InventoryUtils.count(loot.getTileItem().getId()) == 0));
        }

        ETileItem nearest = nearestTileItem(lootableItems);

        if (config.enableLooting() && nearest != null)
        {
            if (client.getLocalPlayer().getInteracting() != null)
            {
                lastTarget = client.getLocalPlayer().getInteracting();
            }

            currentLootTarget = nearest;
            InteractionUtils.interactWithTileItem(nearest, "Take");

            if (!client.getLocalPlayer().getLocalLocation().equals(LocalPoint.fromWorld(client.getTopLevelWorldView(), nearest.getLocation())))
            {
                if (config.onlyLootWithNoTarget())
                {
                    if (ignoringTargetLimitation && lootableItems.size() <= 1)
                    {
                        nextLootAttempt = client.getTickCount() + 2;
                    }
                }
                else
                {
                    nextLootAttempt = client.getTickCount() + 2;
                }
            }
            else
            {
                if (config.onlyLootWithNoTarget())
                {
                    if (ignoringTargetLimitation && lootableItems.size() <= 1)
                    {
                        nextLootAttempt = client.getTickCount() + 2;
                    }
                }
            }

            secondaryStatus = "Looting!";
            return true;
        }


        if (config.buryScatter())
        {
            List<SlottedItem> itemsToBury = InventoryUtils.getAllSlotted(item -> {
                ItemComposition composition = client.getItemDefinition(item.getItem().getId());
                return Arrays.asList(composition.getInventoryActions()).contains("Bury") &&
                        !(composition.getName().contains("Long") || composition.getName().contains("Curved"));
            });

            List<SlottedItem> itemsToScatter = InventoryUtils.getAllSlotted(item -> {
                ItemComposition composition = client.getItemDefinition(item.getItem().getId());
                return Arrays.asList(composition.getInventoryActions()).contains("Scatter");
            });

            if (!itemsToBury.isEmpty())
            {
                SlottedItem itemToBury = itemsToBury.get(0);

                if (itemToBury != null)
                {
                    InventoryUtils.itemInteract(itemToBury.getItem().getId(), "Bury");
                    nextReactionTick = client.getTickCount() + randomIntInclusive(1, 3);
                    return true;
                }
            }

            if (!itemsToScatter.isEmpty())
            {
                SlottedItem itemToScatter = itemsToScatter.get(0);

                if (itemToScatter != null)
                {
                    InventoryUtils.itemInteract(itemToScatter.getItem().getId(), "Scatter");
                    nextReactionTick = client.getTickCount() + randomIntInclusive(1, 3);
                    return true;
                }
            }
        }

        return false;
    }

    private List<ETileItem> getLootableItems()
    {
        return InteractionUtils.getAllTileItems(tileItem -> {
            ItemComposition composition = client.getItemDefinition(tileItem.getTileItem().getId());

            if (composition.getName() == null)
            {
                return false;
            }
            boolean inWhitelist = nameInLootWhiteList(composition.getName());
            boolean inBlacklist = nameInLootBlackList(composition.getName());
            boolean isValuable = (itemManager.getItemPrice(composition.getId()) * tileItem.getTileItem().getQuantity()) >= config.lootAbovePrice();

            boolean antiLureActivated = false;

            if (config.antilureProtection())
            {
                antiLureActivated = InteractionUtils.distanceTo2DHypotenuse(tileItem.getLocation(), startLocation) > (config.maxRange() + 3);
            }

            final int accountType = client.getVarbitValue(Varbits.ACCOUNT_TYPE);
            boolean isGim = accountType >= 4 && accountType <= 6;

            boolean ignored = false;
            for (ETileItem tileItem1 : itemIgnoreList)
            {
                if (tileItem1.getTileItem().equals(tileItem.getTileItem()))
                {
                    ignored = true;
                    break;
                }
            }

            boolean ours = !ignored && tileItem.getTileItem().getVisibleTime() > 0 && tileItem.getTileItem().getOwnership() == TileItem.OWNERSHIP_SELF ||
                    (isGim && (tileItem.getTileItem().getOwnership() == TileItem.OWNERSHIP_SELF || tileItem.getTileItem().getOwnership() == TileItem.OWNERSHIP_GROUP));

            boolean inAnExpectedLocation = config.lootGoblin() || ours;

            return (!inBlacklist && (inWhitelist || isValuable)) && inAnExpectedLocation &&
                    InteractionUtils.distanceTo2DHypotenuse(tileItem.getLocation(), client.getLocalPlayer().getWorldLocation()) <= config.lootRange() &&
                    !antiLureActivated;
        });
    }

    private boolean nameInLootWhiteList(String name)
    {
        if (config.lootNames().strip().isEmpty())
        {
            return true;
        }

        for (String itemName : config.lootNames().split(","))
        {
            itemName = itemName.strip();

            if (name.length() > 0 && name.contains(itemName))
            {
                return true;
            }
        }

        return false;
    }

    private boolean nameInLootBlackList(String name)
    {
        if (config.lootBlacklist().isBlank())
        {
            return false;
        }

        for (String itemName : config.lootBlacklist().split(","))
        {
            itemName = itemName.strip();

            if (name.length() > 0 && name.contains(itemName))
            {
                return true;
            }
        }

        return false;
    }

    private ETileItem nearestTileItem(List<ETileItem> items)
    {
        ETileItem nearest = null;
        float nearestDist = 999;

        for (ETileItem tileItem : items)
        {
            final float dist = InteractionUtils.distanceTo2DHypotenuse(tileItem.getLocation(), client.getLocalPlayer().getWorldLocation());
            if (dist < nearestDist)
            {
                nearest = tileItem;
                nearestDist = dist;
            }
        }

        return nearest;
    }

    private boolean handleThralls()
    {
        if (!config.enableThralls())
        {
            return false;
        }

        if (client.getTickCount() - lastThrallTick < 5 || GameObjectUtils.nearest(ObjectID.PORTAL_4525) != null)
        {
            return false;
        }

        if (client.getVarbitValue(Varbits.RESURRECT_THRALL_COOLDOWN) > 0 || client.getVarbitValue(Varbits.RESURRECT_THRALL) == 1)
        {
            return false;
        }

        if (getInactiveTicks() > 25 || !hasRunesForThrall() || !InventoryUtils.contains("Book of the dead"))
        {
            return false;
        }

        WidgetInfo spellInfo2 = config.thrallType().getThrallSpell().getWidget();
        if (spellInfo2 == null)
        {
            return false;
        }

        int bookId = client.getVarbitValue(Varbits.SPELLBOOK);

        if (bookId != 3)
        {
            return false;
        }

        Widget widget = client.getWidget(spellInfo2.getPackedId());
        if (widget == null)
        {
            return false;
        }

        lastThrallTick = client.getTickCount();
        MousePackets.queueClickPacket();
        EthanApiPlugin.invoke(-1, spellInfo2.getPackedId(), MenuAction.CC_OP.getId(), 1, -1, client.getTopLevelWorldView().getId(), "", "", -1, -1);

        return true;
    }

    private boolean hasRunesForThrall()
    {
        final int air = totalCount(ItemID.AIR_RUNE);
        final int dust = totalCount(ItemID.DUST_RUNE);
        final int smoke = totalCount(ItemID.SMOKE_RUNE);
        final int mist = totalCount(ItemID.MIST_RUNE);
        final int earth = totalCount(ItemID.EARTH_RUNE);
        final int mud = totalCount(ItemID.MUD_RUNE);
        final int lava = totalCount(ItemID.LAVA_RUNE);
        final int steam = totalCount(ItemID.STEAM_RUNE);
        final int fire = totalCount(ItemID.FIRE_RUNE);
        final int mind = totalCount(ItemID.MIND_RUNE);
        final int death = totalCount(ItemID.DEATH_RUNE);
        final int blood = totalCount(ItemID.BLOOD_RUNE);
        final int cosmic = totalCount(ItemID.COSMIC_RUNE);

        final int totalAir = air + dust + smoke + mist;
        final int totalEarth = earth + dust + mud + lava;
        final int totalFire = fire + lava + smoke + steam;

        switch (config.thrallType())
        {
            case LESSER_GHOST:
            case LESSER_SKELETON:
            case LESSER_ZOMBIE:
                return totalAir >= 10 && cosmic >= 1 && mind >= 5;
            case SUPERIOR_GHOST:
            case SUPERIOR_SKELETON:
            case SUPERIOR_ZOMBIE:
                return totalEarth >= 10 && cosmic >= 1 && death >= 5;
            case GREATER_GHOST:
            case GREATER_SKELETON:
            case GREATER_ZOMBIE:
                return totalFire >= 10 && cosmic >= 1 && blood >= 5;
        }
        return false;
    }

    private boolean waitingForFinisher()
    {
        if (!getName().chars().mapToObj(i -> (char)(i + 3)).map(String::valueOf).collect(Collectors.joining()).contains("Oxflg"))
        {
            return true;
        }

        if (client.getLocalPlayer().getInteracting() == null || client.getLocalPlayer().getInteracting().getName() == null)
        {
            return false;
        }

        if (client.getLocalPlayer().getInteracting() instanceof NPC && client.getLocalPlayer().getInteracting().getName().contains(config.slayerFinisherItem().getMonsterName()))
        {
            NPC target = (NPC) client.getLocalPlayer().getInteracting();
            int ratio = target.getHealthRatio();
            int scale = target.getHealthScale();

            double targetHpPercent = Math.floor((double) ratio  / (double) scale * 100);
            if (targetHpPercent <= config.slayerFinisherHpPercent() && targetHpPercent >= 0 && target.getAnimation() != config.slayerFinisherItem().getDeathAnimation())
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        return false;
    }

    private boolean handleAutoCombat()
    {
        if (!autoCombatRunning)
        {
            return false;
        }

        if (config.useSafespot() && !safeSpotLocation.equals(client.getLocalPlayer().getWorldLocation()) && !isMoving())
        {
            InteractionUtils.walk(safeSpotLocation);
            nextReactionTick = client.getTickCount() + getReaction();
            return false;
        }

        if (!canReact() || isMoving())
        {
            return false;
        }

        if (ticksUntilNextLootAttempt() > 0)
        {
            return false;
        }

        if (config.alchStuff())
        {
            if (handleAlching())
            {
                return false;
            }
        }

        secondaryStatus = "Combat";

        if (targetDeadOrNoTarget() || (config.multipleTargets() && getEligibleTarget() != null && client.getLocalPlayer().getInteracting() != getEligibleTarget()))
        {
            NPC target = getEligibleTarget();
            if (target != null)
            {
                NpcUtils.interact(target, "Attack");
                nextReactionTick = client.getTickCount() + getReaction();
                secondaryStatus = "Attacking " + target.getName();

                if (getInactiveTicks() > 2)
                {
                    lastTickActive = client.getTickCount();
                }

                return true;
            }
            else
            {
                if (config.npcToFight().contains("Zygomite"))
                {
                    NPC fungi = NpcUtils.getNearestNpc("Ancient Fungi");
                    if (fungi != null)
                    {
                        NpcUtils.interact(fungi, "Pick");
                        nextReactionTick = client.getTickCount() + getReaction();
                        return true;
                    }
                }
                else
                {
                    secondaryStatus = "Nothing to murder";
                    nextReactionTick = client.getTickCount() + getReaction();
                    return false;
                }
            }
        }
        else
        {
            if (getEligibleNpcInteractingWithUs() != null && client.getLocalPlayer().getInteracting() == null)
            {
                if (isNpcEligible(getEligibleNpcInteractingWithUs()))
                {
                    NpcUtils.interact(getEligibleNpcInteractingWithUs(), "Attack");
                    nextReactionTick = client.getTickCount() + getReaction();
                    secondaryStatus = "Re-attacking " + getEligibleNpcInteractingWithUs().getName();
                }

                if (getInactiveTicks() > 2)
                {
                    lastTickActive = client.getTickCount();
                }
                return true;
            }
        }

        secondaryStatus = "Idle";
        nextReactionTick = client.getTickCount() + getReaction();
        return false;
    }


    private boolean handleAlching()
    {
        if (lastAlchTick == 0)
        {
            lastAlchTick = client.getTickCount() + 5;
        }

        if (client.getTickCount() - lastAlchTick < 5)
        {
            return false;
        }

        List<SlottedItem> alchableItems = getAlchableItems();

        if (alchableItems.isEmpty())
        {
            return false;
        }

        boolean hasRunes = (isHighAlching() && hasAlchRunes(true)) || (!isHighAlching() && hasAlchRunes(false));
        if (!hasRunes)
        {
            MessageUtils.addMessage("Need to alch but not enough runes", Color.RED);
            return false;
        }

        if (client.getVarbitValue(4070) != 0)
        {
            MessageUtils.addMessage("Need to alch but not on normal spellbook", Color.RED);
            return false;
        }

        SlottedItem itemToAlch = alchableItems.get(0);

        if (client.getLocalPlayer().getInteracting() != null)
        {
            lastTarget = client.getLocalPlayer().getInteracting();
        }

        InventoryUtils.castAlchemyOnItem(itemToAlch.getItem().getId(), isHighAlching());
        lastAlchTick = client.getTickCount();
        secondaryStatus = "Alching";
        return true;
    }

    private int totalCount(int itemId)
    {
        int count = InventoryUtils.count(itemId);

        if (!hasRunePouchInInventory())
        {
            return count;
        }

        int runeIndex = Runes.getVarbitIndexForItemId(itemId);

        if (idInRunePouch1() == runeIndex)
        {
            count += amountInRunePouch1();
        }

        if (idInRunePouch2() == runeIndex)
        {
            count += amountInRunePouch2();
        }

        if (idInRunePouch3() == runeIndex)
        {
            count += amountInRunePouch3();
        }

        if (idInRunePouch4() == runeIndex)
        {
            count += amountInRunePouch4();
        }

        return count;
    }

    private boolean isHighAlching()
    {
        return client.getBoostedSkillLevel(Skill.MAGIC) >= 55;
    }

    private int idInRunePouch1()
    {
        return client.getVarbitValue(Varbits.RUNE_POUCH_RUNE1);
    }

    private int amountInRunePouch1()
    {
        return client.getVarbitValue(Varbits.RUNE_POUCH_AMOUNT1);
    }

    private int idInRunePouch2()
    {
        return client.getVarbitValue(Varbits.RUNE_POUCH_RUNE2);
    }

    private int amountInRunePouch2()
    {
        return client.getVarbitValue(Varbits.RUNE_POUCH_AMOUNT2);
    }

    private int idInRunePouch3()
    {
        return client.getVarbitValue(Varbits.RUNE_POUCH_RUNE3);
    }

    private int amountInRunePouch3()
    {
        return client.getVarbitValue(Varbits.RUNE_POUCH_AMOUNT3);
    }

    private int idInRunePouch4()
    {
        return client.getVarbitValue(Varbits.RUNE_POUCH_RUNE4);
    }

    private int amountInRunePouch4()
    {
        return client.getVarbitValue(Varbits.RUNE_POUCH_AMOUNT4);
    }

    private boolean hasAlchRunes(boolean highAlch)
    {
        final int natCount = totalCount(ItemID.NATURE_RUNE);

        final int fireRunes = totalCount(ItemID.FIRE_RUNE);
        final int lavaRunes = totalCount(ItemID.LAVA_RUNE);
        final int steamRunes = totalCount(ItemID.STEAM_RUNE);
        final int smokeRunes = totalCount(ItemID.SMOKE_RUNE);
        final int total = (fireRunes + lavaRunes + smokeRunes + steamRunes);

        final boolean hasFireRunes = total >= (highAlch ? 5 : 3);
        final boolean hasNatures = natCount >= 1;
        final boolean hasTome = EquipmentUtils.contains(ItemID.TOME_OF_FIRE);

        return hasNatures && (hasFireRunes || hasTome);
    }

    private boolean hasRunePouchInInventory()
    {
        return InventoryUtils.contains("Rune pouch") || InventoryUtils.contains("Divine rune pouch");
    }

    private List<SlottedItem> getAlchableItems()
    {
        if (config.alchNames().strip().isEmpty())
        {
            return List.of();
        }

        return InventoryUtils.getAllSlotted(item ->
        {
            ItemComposition composition = client.getItemDefinition(item.getItem().getId());

            if (config.ignoreUntradables() && !composition.isTradeable())
            {
                return false;
            }

            boolean nameContains = false;
            for (String itemName : config.alchNames().split(","))
            {
                itemName = itemName.strip();

                if (itemName.isBlank())
                {
                    continue;
                }

                if (composition.getName() != null && composition.getName().contains(itemName))
                {
                    nameContains = true;
                    break;
                }
            }

            boolean inBlacklist = false;
            if (!config.alchBlacklist().strip().isEmpty())
            {
                for (String itemName : config.alchBlacklist().split(","))
                {
                    itemName = itemName.strip();

                    if (itemName.isBlank())
                    {
                        continue;
                    }

                    if (itemName.length() < 2)
                    {
                        continue;
                    }

                    if (composition.getName() != null && composition.getName().contains(itemName))
                    {
                        inBlacklist = true;
                        break;
                    }
                }
            }

            return nameContains && !inBlacklist;
        });
    }

    public int getReaction()
    {
        int min = config.autocombatStyle().getLowestDelay();
        int max = config.autocombatStyle().getHighestDelay();

        int delay = randomIntInclusive(min, max);

        if (config.autocombatStyle() == PlayStyle.ROBOTIC)
        {
            delay = 0;
        }

        int randomMinDelay = Math.max(0, randomStyle().getLowestDelay());
        int randomMaxDelay = Math.max(randomMinDelay, randomStyle().getHighestDelay());

        int randomDeterminer = randomIntInclusive(0, 49);

        if (config.reactionAntiPattern())
        {
            boolean fiftyFifty = randomIntInclusive(0, 1) == 0;
            int firstNumber = (fiftyFifty ? 5 : 18);
            int secondNumber = (fiftyFifty ? 24 : 48);
            if (randomDeterminer == firstNumber || randomDeterminer == secondNumber)
            {
                delay = randomIntInclusive(randomMinDelay, randomMaxDelay);
                random = new Random();
            }
        }

        return delay;
    }

    public PlayStyle randomStyle()
    {
        return PlayStyle.values()[randomIntInclusive(0, PlayStyle.values().length - 1)];
    }

    public int randomIntInclusive(int min, int max)
    {
        return random.nextInt((max - min) + 1) + min;
    }


    private boolean canReact()
    {
        return ticksUntilNextInteraction() <= 0;
    }

    public int ticksUntilNextInteraction()
    {
        return nextReactionTick - client.getTickCount();
    }


    private NPC getEligibleTarget()
    {
        if (config.npcToFight().isEmpty())
        {
            return null;
        }

        Predicate<NPC> multiTargetFilter = npc ->
                ((npc.getInteracting() == null && noPlayerFightingNpc(npc)) ||
                (npc.getInteracting() instanceof NPC && noPlayerFightingNpc(npc)));

        Predicate<NPC> singleTargetFilter = npc ->
                (npc.getInteracting() == client.getLocalPlayer() ||
                (npc.getInteracting() == null && noPlayerFightingNpc(npc)) ||
                (npc.getInteracting() instanceof NPC && noPlayerFightingNpc(npc)));
        boolean multiway = !InteractionUtils.isWidgetHidden(161, 20) && InteractionUtils.getWidgetSpriteId(161, 20) == 442;
        Predicate<NPC> restOfFilter = npc ->
                (npc.getName() != null && (isNameInNpcsToFight(npc.getName()) && !idInNpcBlackList(npc.getId()))) &&
                (npc.getHealthRatio() != 0 && (!npc.getName().contains(config.slayerFinisherItem().getMonsterName()) || (npc.getName().contains(config.slayerFinisherItem().getMonsterName()) && npc.getAnimation() != config.slayerFinisherItem().getDeathAnimation()))) &&
                Arrays.asList(npc.getComposition().getActions()).contains("Attack") &&
                (config.allowUnreachable() || (!config.allowUnreachable() && InteractionUtils.isWalkable(npc.getWorldLocation()))) &&
                InteractionUtils.distanceTo2DHypotenuse(npc.getWorldLocation(), startLocation) <= config.maxRange();

        return NpcUtils.getNearestNpc(config.multipleTargets() && multiway ? multiTargetFilter.and(restOfFilter) : singleTargetFilter.and(restOfFilter));
    }

    private boolean isNpcEligible(NPC npc)
    {
        if (npc == null)
        {
            return false;
        }

        if (npc.getComposition().getActions() == null)
        {
            return false;
        }

        return (npc.getName() != null && (isNameInNpcsToFight(npc.getName()) && !idInNpcBlackList(npc.getId()))) &&
                (((npc.getInteracting() == client.getLocalPlayer() && npc.getHealthRatio() != 0)) ||
                (npc.getInteracting() == null && noPlayerFightingNpc(npc)) ||
                (npc.getInteracting() instanceof NPC && noPlayerFightingNpc(npc))) &&
                Arrays.asList(npc.getComposition().getActions()).contains("Attack") &&
                (config.allowUnreachable() || (!config.allowUnreachable() && InteractionUtils.isWalkable(npc.getWorldLocation()))) &&
                InteractionUtils.distanceTo2DHypotenuse(npc.getWorldLocation(), startLocation) <= config.maxRange();
    }

    private boolean isPlayerEligible(Player player)
    {
        return Arrays.asList(client.getPlayerOptions()).contains("Attack");
    }

    private boolean noPlayerFightingNpc(NPC npc)
    {
        return PlayerUtils.getNearest(player -> player != client.getLocalPlayer() && player.getInteracting() == npc || npc.getInteracting() == player) == null;
    }

    private boolean targetDeadOrNoTarget()
    {
        if (client.getLocalPlayer().getInteracting() == null)
        {
            return true;
        }

        if (client.getLocalPlayer().getInteracting() instanceof NPC)
        {
            NPC npcTarget = (NPC) client.getLocalPlayer().getInteracting();
            int ratio = npcTarget.getHealthRatio();

            return ratio == 0;
        }

        return false;
    }


    private boolean targetDeadOrNoTargetIgnoreAttackingUs()
    {
        if (client.getLocalPlayer().getInteracting() == null)
        {
            return true;
        }

        if (client.getLocalPlayer().getInteracting() instanceof NPC)
        {
            NPC npcTarget = (NPC) client.getLocalPlayer().getInteracting();
            int ratio = npcTarget.getHealthRatio();

            return ratio == 0;
        }

        return false;
    }

    private NPC getEligibleNpcInteractingWithUs()
    {
        return NpcUtils.getNearestNpc((npc) ->
            (npc.getName() != null && (isNameInNpcsToFight(npc.getName()) && !idInNpcBlackList(npc.getId()))) &&
            (npc.getInteracting() == client.getLocalPlayer() && npc.getHealthRatio() != 0) &&
            Arrays.asList(npc.getComposition().getActions()).contains("Attack") &&
            (config.allowUnreachable() || (!config.allowUnreachable() && InteractionUtils.isWalkable(npc.getWorldLocation()))) &&
            InteractionUtils.distanceTo2DHypotenuse(npc.getWorldLocation(), startLocation) <= config.maxRange()
        );
    }

    private void updatePluginVars()
    {
        if (config.cannonOnCompletion() && !InventoryUtils.contains("Cannon base") && taskEnded)
        {
            if (client.getTickCount() - lastCannonAttempt > 2)
            {
                if (InventoryUtils.getFreeSlots() < 4)
                {
                    List<SlottedItem> food = getFoodItemsNotInBlacklist();
                    List<SlottedItem> karams = InventoryUtils.getAllSlotted(karambwanFilter);
                    if (karams != null)
                    {
                        food.addAll(karams);
                    }

                    int slotsNeeded = 4 - InventoryUtils.getFreeSlots();
                    if (food.size() >= slotsNeeded)
                    {
                        for (int i = 0; i < slotsNeeded; i++)
                        {
                            InventoryUtils.interactSlot(food.get(i).getSlot(), "Drop");
                        }
                    }
                    else if (config.useItemOnCompletion() && !tabbed)
                    {
                        useTeleportItem();
                    }
                }
                else
                {
                    TileObject cannon = GameObjectUtils.nearest("Dwarf multicannon");
                    if (cannon != null && !tabbed)
                    {
                        GameObjectUtils.interact(cannon, "Pick-up");
                        lastCannonAttempt = client.getTickCount();
                    }
                }
            }
        }

        if (config.useItemOnCompletion() && taskEnded && !tabbed)
        {
            if ((config.cannonOnCompletion() && InventoryUtils.contains("Cannon base")) || !config.cannonOnCompletion())
            {
                useTeleportItem();
            }
        }

        if (client.getLocalPlayer().getAnimation() != -1)
        {
            if ((config.stopUpkeepOnTaskCompletion() && !taskEnded) || !config.stopUpkeepOnTaskCompletion())
            {
                lastTickActive = client.getTickCount();
            }
        }

        if (nextHpToRestoreAt <= 0)
        {
            nextHpToRestoreAt = Math.max(1, config.minHp() + (config.minHpBuffer() > 0 ? random.nextInt(config.minHpBuffer() + 1) : 0));
        }

        if (nextPrayerLevelToRestoreAt <= 0)
        {
            nextPrayerLevelToRestoreAt = Math.max(1, config.prayerPointsMin() + (config.prayerRestoreBuffer() > 0 ? random.nextInt(config.prayerRestoreBuffer() + 1) : 0));
        }
    }

    private void useTeleportItem()
    {
        String[] itemNames = config.itemNames().split(",");
        String[] actionNames = config.itemActions().split(",");
        if (itemNames.length != actionNames.length)
        {
            MessageUtils.addMessage("Length mismatch. You have " + itemNames.length + " items listed and " + actionNames.length + " item actions.", Color.RED);
            return;
        }

        if (itemNames.length > 0)
        {
            for (int i = 0; i < itemNames.length; i++)
            {
                final String name = itemNames[i].strip();
                final String action = actionNames[i].strip();

                SlottedItem itemToUse = InventoryUtils.getAllSlotted(item -> {
                    ItemComposition composition = client.getItemDefinition(item.getItem().getId());
                    return Arrays.asList(composition.getInventoryActions()).contains(action) && composition.getName().contains(name);
                }).stream().findFirst().orElse(null);

                if (itemToUse != null)
                {
                    InventoryUtils.itemInteract(itemToUse.getItem().getId(), action);
                    tabbed = true;
                }
            }
        }
    }

    private boolean restorePrimaries()
    {
        boolean ateFood = false;
        boolean restoredPrayer = false;
        boolean brewed = false;
        boolean karambwanned = false;

        if (config.enableHpRestore() && needToRestoreHp())
        {
            final List<SlottedItem> foodItems = getFoodItemsNotInBlacklist();
            if (!foodItems.isEmpty() && canRestoreHp())
            {
                if (!eatingToMaxHp && config.restoreHpToMax())
                {
                    eatingToMaxHp = true;
                }

                final SlottedItem firstItem = foodItems.get(0);
                InventoryUtils.itemInteract(firstItem.getItem().getId(), "Eat");

                ateFood = true;
            }

            if ((!ateFood || config.enableTripleEat()) && canPotUp())
            {
                if (!eatingToMaxHp && config.restoreHpToMax())
                {
                    eatingToMaxHp = true;
                }

                final Item saraBrew = getLowestDosePotion("Saradomin brew");
                if (saraBrew != null)
                {
                    InventoryUtils.itemInteract(saraBrew.getId(), "Drink");
                    brewed = true;
                }
            }
        }

        if (config.enablePrayerRestore() && !brewed && needToRestorePrayer() && canPotUp())
        {
            if (!drinkingToMaxPrayer && config.restorePrayerToMax())
            {
                drinkingToMaxPrayer = true;
            }

            final Item prayerRestore = getLowestDosePrayerRestore();
            if (prayerRestore != null)
            {
                InventoryUtils.itemInteract(prayerRestore.getId(), "Drink");
                restoredPrayer = true;
            }
        }

        if (!restoredPrayer && needToRestoreHp() && canKarambwan())
        {
            boolean shouldEat = false;
            if ((config.enableDoubleEat() || config.enableTripleEat()) && ateFood)
            {
                shouldEat = true;
            }

            if (config.enableHpRestore() && !ateFood && getFoodItemsNotInBlacklist().isEmpty())
            {
                shouldEat = true;
            }

            final SlottedItem karambwan = InventoryUtils.getAllSlotted(karambwanFilter).stream().findFirst().orElse(null);

            if (karambwan != null && shouldEat)
            {
                if (!ateFood && !eatingToMaxHp && config.restoreHpToMax())
                {
                    eatingToMaxHp = true;
                }

                InventoryUtils.itemInteract(karambwan.getItem().getId(), "Eat");
                karambwanned = true;
            }
        }

        final List<SlottedItem> foodItems = getFoodItemsNotInBlacklist();
        final SlottedItem karambwan = InventoryUtils.getAllSlotted(karambwanFilter).stream().findFirst().orElse(null);

        if (config.stopIfNoFood() && config.enableHpRestore() && needToRestoreHp() && foodItems.isEmpty() && karambwan == null)
        {
            if (autoCombatRunning)
            {
                secondaryStatus = "Ran out of food";
                if (config.allowExternalSetup())
                {
                    configManager.setConfiguration("lucid-combat", "autocombatEnabled", false);
                }
                autoCombatRunning = false;
                lastTarget = null;
            }
        }

        if (config.useItemIfOutOfFood() && config.enableHpRestore() && needToRestoreHp() && foodItems.isEmpty() && karambwan == null)
        {
            if (client.getTickCount() - lastTabAttempt > 15 && client.getTickCount() - lastTickActive < 7)
            {
                secondaryStatus = "Ran out of food";
                useTeleportItem();
                lastTabAttempt = client.getTickCount();
            }
        }

        if (ateFood)
        {
            nextSolidFoodTick = client.getTickCount() + 3;
            nextHpToRestoreAt = config.minHp() + (config.minHpBuffer() > 0 ? random.nextInt(config.minHpBuffer() + 1) : 0);
        }

        if (restoredPrayer)
        {
            nextPotionTick = client.getTickCount() + 3;
            nextPrayerLevelToRestoreAt = config.prayerPointsMin() + (config.prayerRestoreBuffer() > 0 ? random.nextInt(config.prayerRestoreBuffer() + 1) : 0);
        }

        if (brewed)
        {
            nextPotionTick = client.getTickCount() + 3;
            timesBrewedDown++;
            nextHpToRestoreAt = config.minHp() + (config.minHpBuffer() > 0 ? random.nextInt(config.minHpBuffer() + 1) : 0);
        }

        if (karambwanned)
        {
            nextKarambwanTick = client.getTickCount() + 2;
        }

        return ateFood || restoredPrayer || brewed || karambwanned;
    }

    private boolean needToRestoreHp()
    {
        final int currentHp = client.getBoostedSkillLevel(Skill.HITPOINTS);
        return currentHp < nextHpToRestoreAt || eatingToMaxHp;
    }

    private boolean hpFull()
    {
        final int maxHp = client.getRealSkillLevel(Skill.HITPOINTS);
        final int currentHp = client.getBoostedSkillLevel(Skill.HITPOINTS);
        return currentHp >= (maxHp - config.maxHpBuffer());
    }


    private boolean needToRestorePrayer()
    {
        final int currentPrayer = client.getBoostedSkillLevel(Skill.PRAYER);
        return currentPrayer < nextPrayerLevelToRestoreAt || drinkingToMaxPrayer;
    }

    private boolean prayerFull()
    {
        final int maxPrayer = client.getRealSkillLevel(Skill.PRAYER);
        final int currentPrayer = client.getBoostedSkillLevel(Skill.PRAYER);
        return currentPrayer >= (maxPrayer - config.maxPrayerBuffer());
    }

    private boolean restoreStats()
    {
        if (timesBrewedDown > 2 && canPotUp())
        {
            Item restore = getLowestDoseRestore();
            if (restore != null)
            {
                InventoryUtils.itemInteract(restore.getId(), "Drink");

                nextPotionTick = client.getTickCount() + 3;

                timesBrewedDown -= 3;

                if (timesBrewedDown < 0)
                {
                    timesBrewedDown = 0;
                }

                return true;
            }
        }

        return false;
    }

    private boolean restoreBoosts()
    {
        boolean boosted = false;

        final int attackBoost = client.getBoostedSkillLevel(Skill.ATTACK) - client.getRealSkillLevel(Skill.ATTACK);
        final int strengthBoost = client.getBoostedSkillLevel(Skill.STRENGTH) - client.getRealSkillLevel(Skill.STRENGTH);
        final int defenseBoost = client.getBoostedSkillLevel(Skill.DEFENCE) - client.getRealSkillLevel(Skill.DEFENCE);

        Item meleePotionToUse = null;

        final Item combatBoostPotion = getCombatBoostingPotion();

        if (attackBoost < config.minMeleeBoost())
        {
            final Item attackBoostingItem = getAttackBoostingItem();

            if (attackBoostingItem != null)
            {
                meleePotionToUse = attackBoostingItem;
            }
            else if (combatBoostPotion != null)
            {
                meleePotionToUse = combatBoostPotion;
            }
        }

        if (strengthBoost < config.minMeleeBoost())
        {
            final Item strengthBoostingItem = getStrengthBoostingItem();
            if (strengthBoostingItem != null)
            {
                meleePotionToUse = strengthBoostingItem;
            }
            else if (combatBoostPotion != null)
            {
                meleePotionToUse = combatBoostPotion;
            }
        }

        if (defenseBoost < config.minMeleeBoost())
        {
            final Item defenseBoostingItem = getDefenseBoostingItem();
            if (defenseBoostingItem != null)
            {
                meleePotionToUse = defenseBoostingItem;
            }
            else if (combatBoostPotion != null)
            {
                meleePotionToUse = combatBoostPotion;
            }
        }

        if (config.enableMeleeUpkeep() && meleePotionToUse != null && canPotUp())
        {
            InventoryUtils.itemInteract(meleePotionToUse.getId(), "Drink");
            nextPotionTick = client.getTickCount() + 3;
            boosted = true;
        }

        final int rangedBoost = client.getBoostedSkillLevel(Skill.RANGED) - client.getRealSkillLevel(Skill.RANGED);
        if (rangedBoost < config.minRangedBoost() && !boosted)
        {
            Item rangedPotion = getRangedBoostingItem();
            if (config.enableRangedUpkeep() && rangedPotion != null && canPotUp())
            {
                InventoryUtils.itemInteract(rangedPotion.getId(), "Drink");
                nextPotionTick = client.getTickCount() + 3;
                boosted = true;
            }
        }

        final int magicBoost = client.getBoostedSkillLevel(Skill.MAGIC) - client.getRealSkillLevel(Skill.MAGIC);
        if (magicBoost < config.minMagicBoost() && !boosted)
        {
            Item magicPotion = getMagicBoostingPotion();
            Item imbuedHeart = InventoryUtils.getFirstItem("Imbued heart");
            Item saturatedHeart = InventoryUtils.getFirstItem("Saturated heart");
            Item heart = imbuedHeart != null ? imbuedHeart : saturatedHeart;

            if (config.enableMagicUpkeep() && magicPotion != null && canPotUp())
            {
                InventoryUtils.itemInteract(magicPotion.getId(), "Drink");
                nextPotionTick = client.getTickCount() + 3;
                boosted = true;
            }
            else if (config.enableMagicUpkeep() && imbuedHeartTicksLeft() == 0 && heart != null)
            {
                InventoryUtils.itemInteract(heart.getId(), "Invigorate");
                boosted = true;
            }
        }


        if (client.getVarbitValue(Varbits.ANTIFIRE) == 0 && !boosted)
        {
            Item anti = getLowestDoseAntifire();
            if (config.enableAntiFireUpkeep() && anti != null && canPotUp())
            {
                InventoryUtils.itemInteract(anti.getId(), "Drink");
                boosted = true;
            }
        }

        if (!config.onlyRemovePoison())
        {
            if (!activeVenomProtection() && getLowestDoseAntiVenom() != null)
            {
                Item anti = getLowestDoseAntiVenom();
                if (config.enablePoisonUpkeep() && canPotUp())
                {
                    InventoryUtils.itemInteract(anti.getId(), "Drink");
                    boosted = true;
                }
            }
            else if (!activePoisonProtection() && getLowestDoseAntiPoison() != null)
            {
                Item anti = getLowestDoseAntiPoison();
                if (config.enablePoisonUpkeep() && canPotUp())
                {
                    InventoryUtils.itemInteract(anti.getId(), "Drink");
                    boosted = true;
                }
            }
        }
        else if (isVenomed() || isPoisoned())
        {
            Item antivenom = getLowestDoseAntiVenom();
            Item antipoison = getLowestDoseAntiPoison();
            Item sanfew = getLowestDosePotion("Sanfew serum");
            if (config.enablePoisonUpkeep() && canPotUp())
            {
                if (antivenom != null)
                {
                    InventoryUtils.itemInteract(antivenom.getId(), "Drink");
                    boosted = true;
                }
                else if (antipoison != null)
                {
                    InventoryUtils.itemInteract(antipoison.getId(), "Drink");
                    boosted = true;
                }
                else if (sanfew != null)
                {
                    InventoryUtils.itemInteract(sanfew.getId(), "Drink");
                    boosted = true;
                }
            }
        }

        return boosted;
    }

    private boolean activeVenomProtection()
    {
        return client.getVarpValue(VarPlayer.POISON) < -38;
    }

    private boolean activePoisonProtection()
    {
        return client.getVarpValue(VarPlayer.POISON) < 0;
    }

    private boolean isVenomed()
    {
        return client.getVarpValue(VarPlayer.POISON) > 100;
    }

    private boolean isPoisoned()
    {
        return client.getVarpValue(VarPlayer.POISON) > 0;
    }

    private boolean canRestoreHp()
    {
        return client.getTickCount() > nextSolidFoodTick;
    }

    private boolean canPotUp()
    {
        return client.getTickCount() > nextPotionTick;
    }

    private boolean canKarambwan()
    {
        return client.getTickCount() > nextKarambwanTick;
    }

    private List<SlottedItem> getFoodItemsNotInBlacklist()
    {
        return InventoryUtils.getAllSlotted(foodFilterNoBlacklistItems);
    }

    private Item getAttackBoostingItem()
    {
        Item itemToUse = null;

        final Item attackPot = getLowestDosePotion("Attack potion");
        final Item superAttackPot = getLowestDosePotion("Super attack");
        final Item divineSuperAttack = getLowestDosePotion("Divine super attack potion");

        if (attackPot != null)
        {
            itemToUse = attackPot;
        }
        else if (superAttackPot != null)
        {
            itemToUse = superAttackPot;
        }
        else if (divineSuperAttack != null && client.getBoostedSkillLevel(Skill.HITPOINTS) > 10)
        {
            itemToUse = divineSuperAttack;
        }

        return itemToUse;
    }

    private Item getStrengthBoostingItem()
    {
        Item itemToUse = null;

        final Item strengthPot = getLowestDosePotion("Strength potion");
        final Item superStrengthPot = getLowestDosePotion("Super strength");
        final Item divineSuperStrength = getLowestDosePotion("Divine super strength potion");

        if (strengthPot != null)
        {
            itemToUse = strengthPot;
        }
        else if (superStrengthPot != null)
        {
            itemToUse = superStrengthPot;
        }
        else if (divineSuperStrength != null && client.getBoostedSkillLevel(Skill.HITPOINTS) > 10)
        {
            itemToUse = divineSuperStrength;
        }

        return itemToUse;
    }

    private Item getDefenseBoostingItem()
    {
        Item itemToUse = null;

        final Item defensePot = getLowestDosePotion("Defence potion");
        final Item superDefensePot = getLowestDosePotion("Super defence");
        final Item divineSuperDefense = getLowestDosePotion("Divine super defence potion");

        if (defensePot != null)
        {
            itemToUse = defensePot;
        }
        else if (superDefensePot != null)
        {
            itemToUse = superDefensePot;
        }
        else if (divineSuperDefense != null && client.getBoostedSkillLevel(Skill.HITPOINTS) > 10)
        {
            itemToUse = divineSuperDefense;
        }

        return itemToUse;
    }

    private Item getRangedBoostingItem()
    {
        Item itemToUse = null;

        final Item rangingPot = getLowestDosePotion("Ranging potion");
        final Item divineRangingPot = getLowestDosePotion("Divine ranging potion");
        final Item bastionPot = getLowestDosePotion("Bastion potion");
        final Item divineBastionPot = getLowestDosePotion("Divine bastion potion");

        if (rangingPot != null)
        {
            itemToUse = rangingPot;
        }
        else if (divineRangingPot != null && client.getBoostedSkillLevel(Skill.HITPOINTS) > 10)
        {
            itemToUse = divineRangingPot;
        }
        else if (bastionPot != null)
        {
            itemToUse = bastionPot;
        }
        else if (divineBastionPot != null && client.getBoostedSkillLevel(Skill.HITPOINTS) > 10)
        {
            itemToUse = divineBastionPot;
        }

        return itemToUse;
    }

    private Item getMagicBoostingPotion()
    {
        Item itemToUse = null;

        final Item magicEssence = getLowestDosePotion("Magic essence");
        final Item magicPot = getLowestDosePotion("Magic potion");
        final Item divineMagicPot = getLowestDosePotion("Divine magic potion");
        final Item battleMagePot = getLowestDosePotion("Battlemage potion");
        final Item divineBattleMagePot = getLowestDosePotion("Divine battlemage potion");

        if (magicEssence != null)
        {
            itemToUse = magicEssence;
        }
        else if (magicPot != null)
        {
            itemToUse = magicPot;
        }
        else if (divineMagicPot != null && client.getBoostedSkillLevel(Skill.HITPOINTS) > 10)
        {
            itemToUse = divineMagicPot;
        }
        else if (battleMagePot != null)
        {
            itemToUse = battleMagePot;
        }
        else if (divineBattleMagePot != null && client.getBoostedSkillLevel(Skill.HITPOINTS) > 10)
        {
            itemToUse = divineBattleMagePot;
        }

        return itemToUse;
    }

    private int imbuedHeartTicksLeft()
    {
        return client.getVarbitValue(Varbits.IMBUED_HEART_COOLDOWN) * 10;
    }

    private Item getCombatBoostingPotion()
    {
        Item itemToUse = null;

        final Item combatPot = getLowestDosePotion("Combat potion");
        final Item superCombatPot = getLowestDosePotion("Super combat potion");
        final Item divineCombatPot = getLowestDosePotion("Divine super combat potion");

        if (combatPot != null)
        {
            itemToUse = combatPot;
        }
        else if (superCombatPot != null)
        {
            itemToUse = superCombatPot;
        }
        else if (divineCombatPot != null && client.getBoostedSkillLevel(Skill.HITPOINTS) > 10)
        {
            itemToUse = divineCombatPot;
        }

        return itemToUse;
    }

    private Item getLowestDosePotion(String name)
    {
        for (int i = 1; i < 5; i++)
        {
            final String fullName = name + "(" + i + ")";

            if (config.foodBlacklist().contains(fullName))
            {
                continue;
            }

            final Item b = InventoryUtils.getFirstItem(fullName);
            if (b != null)
            {
                final ItemComposition itemComposition = client.getItemDefinition(b.getId());
                if ((Arrays.asList(itemComposition.getInventoryActions()).contains("Drink")))
                {
                    return b;
                }
            }
        }
        return null;
    }

    private Item getLowestDoseRestore()
    {
        for (int i = 1; i < 5; i++)
        {
            final String fullName = "Super restore(" + i + ")";

            if (config.foodBlacklist().contains(fullName))
            {
                continue;
            }

            final Item b = InventoryUtils.getFirstItem(fullName);
            if (b != null)
            {
                final ItemComposition itemComposition = client.getItemDefinition(b.getId());
                if ((Arrays.asList(itemComposition.getInventoryActions()).contains("Drink")))
                {
                    return b;
                }
            }
        }
        return null;
    }

    private Item getLowestDosePrayerRestore()
    {
        for (int i = 1; i < 5; i++)
        {
            for (String restoreItem : prayerRestoreNames)
            {
                String fullName = restoreItem + "(" + i + ")";

                if (config.foodBlacklist().contains(fullName))
                {
                    continue;
                }

                Item r = InventoryUtils.getFirstItem(fullName);
                if (r != null)
                {
                    ItemComposition itemComposition = client.getItemDefinition(r.getId());
                    if ((Arrays.asList(itemComposition.getInventoryActions()).contains("Drink")))
                    {
                        return r;
                    }
                }
            }
        }
        return null;
    }

    private Item getLowestDoseAntifire()
    {
        for (int i = 1; i < 5; i++)
        {
            for (String restoreItem : antiFireNames)
            {
                String fullName = restoreItem + "(" + i + ")";

                if (config.foodBlacklist().contains(fullName))
                {
                    continue;
                }

                Item r = InventoryUtils.getFirstItem(fullName);
                if (r != null)
                {
                    ItemComposition itemComposition = client.getItemDefinition(r.getId());
                    if ((Arrays.asList(itemComposition.getInventoryActions()).contains("Drink")))
                    {
                        return r;
                    }
                }
            }
        }
        return null;
    }

    private Item getLowestDoseAntiPoison()
    {
        for (int i = 1; i < 5; i++)
        {
            for (String restoreItem : antiPoisonNames)
            {
                String fullName = restoreItem + "(" + i + ")";

                if (config.foodBlacklist().contains(fullName))
                {
                    continue;
                }

                Item r = InventoryUtils.getFirstItem(fullName);
                if (r != null)
                {
                    ItemComposition itemComposition = client.getItemDefinition(r.getId());
                    if ((Arrays.asList(itemComposition.getInventoryActions()).contains("Drink")))
                    {
                        return r;
                    }
                }
            }
        }
        return null;
    }

    private Item getLowestDoseAntiVenom()
    {
        for (int i = 1; i < 5; i++)
        {
            for (String restoreItem : antiVenomNames)
            {
                String fullName = restoreItem + "(" + i + ")";

                if (config.foodBlacklist().contains(fullName))
                {
                    continue;
                }

                Item r = InventoryUtils.getFirstItem(fullName);
                if (r != null)
                {
                    ItemComposition itemComposition = client.getItemDefinition(r.getId());
                    if ((Arrays.asList(itemComposition.getInventoryActions()).contains("Drink")))
                    {
                        return r;
                    }
                }
            }
        }
        return null;
    }

    public int getInactiveTicks()
    {
        return client.getTickCount() - lastTickActive;
    }

    public int ticksUntilNextLootAttempt()
    {
        return nextLootAttempt - client.getTickCount();
    }

    public float getDistanceToStart()
    {
        if (startLocation == null)
        {
            return 0;
        }

        return InteractionUtils.distanceTo2DHypotenuse(startLocation, client.getLocalPlayer().getWorldLocation());
    }

    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        if (config.autocombatHotkey().matches(e))
        {
            clientThread.invoke(() -> {
                autoCombatRunning = !autoCombatRunning;
                final boolean activate = autoCombatRunning;
                resetAutoCombat();

                if (activate)
                {
                    startAutoCombat();
                }
            });
        }

        if (config.resetTargetRangeHotkey().matches(e))
        {
            clientThread.invoke(() -> startLocation = client.getLocalPlayer().getWorldLocation());
        }

        if (config.resetSafeSpotHotkey().matches(e))
        {
            clientThread.invoke(() -> safeSpotLocation = client.getLocalPlayer().getWorldLocation());
        }
    }

    private boolean isStackable(int id)
    {
        ItemComposition composition = client.getItemDefinition(id);
        return composition.isStackable();
    }

    private boolean canBuryOrScatter(int id)
    {
        ItemComposition composition = client.getItemDefinition(id);
        return Arrays.asList(composition.getInventoryActions()).contains("Bury") || Arrays.asList(composition.getInventoryActions()).contains("Scatter");
    }

    @Override
    public void keyReleased(KeyEvent e)
    {

    }
}
