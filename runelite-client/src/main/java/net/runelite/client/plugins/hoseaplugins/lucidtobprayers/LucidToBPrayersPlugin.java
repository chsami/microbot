package net.runelite.client.plugins.hoseaplugins.lucidtobprayers;

import com.google.common.collect.Lists;
import com.google.inject.Provides;
import net.runelite.client.plugins.hoseaplugins.api.item.SlottedItem;
import net.runelite.client.plugins.hoseaplugins.api.utils.*;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@PluginDescriptor(
        name = PluginDescriptor.Lucid + "ToB Prayers</html>",
        description = "Helps with prayers (and tick-eating sote ball) at ToB",
        enabledByDefault = false,
        tags = {"prayer", "swap", "tob", "lucid"}
)
public class LucidToBPrayersPlugin extends Plugin
{

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private LucidToBPrayersConfig config;

    @Inject ConfigManager configManager;

    private Map<EventType, List<CustomPrayer>> eventMap = new HashMap<>();

    private List<Projectile> validProjectiles = new ArrayList<>();

    private List<ScheduledPrayer> scheduledPrayers = new ArrayList<>();

    private List<Integer> animationsThisTick = new ArrayList<>();

    private List<Integer> npcsSpawnedThisTick = new ArrayList<>();

    private List<Integer> npcsDespawnedThisTick = new ArrayList<>();

    private List<Integer> npcsChangedThisTick = new ArrayList<>();

    private List<Integer> projectilesSpawnedThisTick = new ArrayList<>();

    private List<Integer> graphicsCreatedThisTick = new ArrayList<>();

    private List<Integer> gameObjectsSpawnedThisTick = new ArrayList<>();

    private List<Integer> npcsInteractingWithYouThisTick = new ArrayList<>();

    private List<Integer> npcsYouInteractedWithThisTick = new ArrayList<>();

    private List<String> lastEquipmentList = new ArrayList<>();

    public Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private Projectile soteMageBall = null;

    @Provides
    LucidToBPrayersConfig getConfig(final ConfigManager configManager)
    {
        return configManager.getConfig(LucidToBPrayersConfig.class);
    }

    @Override
    protected void startUp()
    {
        populateCustomPrayers();
    }

    @Override
    protected void shutDown()
    {

    }

    @Subscribe
    private void onClientTick(final ClientTick tick)
    {
        if (soteMageBall != null)
        {
            if (soteMageBall.getRemainingCycles() <= 30)
            {
                if (config.mageBallTickEat() && !config.tickEatFood().isEmpty())
                {
                    String[] argsSplit = config.tickEatFood().split(":");
                    if (argsSplit.length == 2)
                    {
                        String option = argsSplit[0];
                        String name = argsSplit[1];
                        Item inventoryItem = InventoryUtils.getFirstItem(name);

                        if (inventoryItem != null)
                        {
                            MessageUtils.addMessage("Attempting to tick eat ball with id: " + inventoryItem.getId(), Color.RED);
                            InventoryUtils.itemInteract(inventoryItem.getId(), option);
                        }
                    }
                }
                soteMageBall = null;
            }
        }
    }

    @Subscribe
    private void onAnimationChanged(final AnimationChanged event)
    {
        if (event.getActor() == null)
        {
            return;
        }

        int animId = event.getActor().getAnimation();

        if (!animationsThisTick.contains(animId))
        {
            if (event.getActor() instanceof NPC)
            {
                final NPC npc = (NPC) event.getActor();
                eventFired(EventType.ANIMATION_CHANGED, animId, npc.getInteracting() == client.getLocalPlayer());

                if (npc.getName() != null && npc.getName().equalsIgnoreCase("Pestilent Bloat") && npc.getAnimation() == -1)
                {
                    if (client.isPrayerActive(Prayer.PIETY))
                    {
                        CombatUtils.togglePrayer(Prayer.PIETY);
                    }
                }
            }
            else
            {
                eventFired(EventType.ANIMATION_CHANGED, animId, false);
            }

            animationsThisTick.add(animId);
        }
    }

    @Subscribe
    private void onNpcSpawned(final NpcSpawned event)
    {
        if (event.getNpc() == null)
        {
            return;
        }

        int npcId = event.getNpc().getId();

        if (!npcsSpawnedThisTick.contains(npcId))
        {
            eventFired(EventType.NPC_SPAWNED, npcId, false);
            npcsSpawnedThisTick.add(npcId);
        }
    }

    @Subscribe
    private void onNpcDespawned(final NpcDespawned event)
    {
        if (event.getNpc() == null)
        {
            return;
        }

        int npcId = event.getNpc().getId();

        if (!npcsDespawnedThisTick.contains(npcId))
        {
            eventFired(EventType.NPC_DESPAWNED, npcId, false);
            npcsDespawnedThisTick.add(npcId);
        }
    }

    @Subscribe
    private void onNpcChanged(final NpcChanged event)
    {
        if (event.getNpc() == null)
        {
            return;
        }

        int npcId = event.getNpc().getId();

        if (!npcsChangedThisTick.contains(npcId))
        {
            eventFired(EventType.NPC_CHANGED, npcId, event.getNpc().getInteracting() == client.getLocalPlayer());
            npcsChangedThisTick.add(npcId);
        }
    }

    @Subscribe
    private void onProjectileMoved(final ProjectileMoved event)
    {
        if (validProjectiles.contains(event.getProjectile()))
        {
            return;
        }

        if (event.getProjectile().getId() == 1604)
        {
            if (soteMageBall == null)
            {
                soteMageBall = event.getProjectile();
            }
        }

        validProjectiles.add(event.getProjectile());

        int projectileId = event.getProjectile().getId();
        if (!projectilesSpawnedThisTick.contains(projectileId))
        {
            eventFired(EventType.PROJECTILE_SPAWNED, projectileId, event.getProjectile().getTarget().equals(client.getLocalPlayer().getLocalLocation()) || event.getProjectile().getInteracting() == client.getLocalPlayer());
            projectilesSpawnedThisTick.add(projectileId);
        }
    }

    @Subscribe
    private void onGraphicsObjectCreated(final GraphicsObjectCreated event)
    {
        int graphicsId = event.getGraphicsObject().getId();

        if (!graphicsCreatedThisTick.contains(graphicsId))
        {
            eventFired(EventType.GRAPHICS_CREATED, graphicsId, false);
            graphicsCreatedThisTick.add(graphicsId);
        }
    }

    @Subscribe
    private void onGameObjectSpawned(final GameObjectSpawned event)
    {
        int objectId = event.getGameObject().getId();

        if (!gameObjectsSpawnedThisTick.contains(objectId))
        {
            eventFired(EventType.GAME_OBJECT_SPAWNED, objectId, false);
            gameObjectsSpawnedThisTick.add(objectId);
        }
    }

    @Subscribe
    private void onInteractingChanged(final InteractingChanged event)
    {
        Actor source = event.getSource();
        Actor interacting = event.getSource().getInteracting();

        if (interacting == null)
        {
            return;
        }

        if (interacting == client.getLocalPlayer() && !(source instanceof Player))
        {
            NPC sourceNpc = (NPC) source;
            if (!npcsInteractingWithYouThisTick.contains(sourceNpc.getId()))
            {
                eventFired(EventType.OTHER_INTERACT_YOU, sourceNpc.getId(), true);
                npcsInteractingWithYouThisTick.add(sourceNpc.getId());
            }
        }

        if (source == client.getLocalPlayer() && !(interacting instanceof Player))
        {
            NPC interactingNpc = (NPC) interacting;
            if (!npcsYouInteractedWithThisTick.contains(interactingNpc.getId()))
            {
                eventFired(EventType.YOU_INTERACT_OTHER, interactingNpc.getId(), interacting.getInteracting() == client.getLocalPlayer());
                npcsYouInteractedWithThisTick.add(interactingNpc.getId());
            }
        }
    }

    @Subscribe
    private void onConfigChanged(final ConfigChanged event)
    {
        if (!event.getGroup().equals("lucid-tob-prayers"))
        {
            return;
        }

        populateCustomPrayers();
    }

    @Subscribe
    private void onGameTick(final GameTick event)
    {

        getEquipmentChanges();

        NPC bloat = NpcUtils.getNearestNpc("Pestilent Bloat");

        if (config.bloatDefense() && bloat != null)
        {
            if (InteractionUtils.distanceTo2DHypotenuse(client.getLocalPlayer().getWorldLocation(), bloat.getWorldLocation()) < 15)
            {
                activatePrayer(client, Prayer.PROTECT_FROM_MISSILES, false);
            }
        }

        for (ScheduledPrayer prayer : scheduledPrayers)
        {
            if (client.getTickCount() == prayer.getActivationTick())
            {
                activatePrayer(client, prayer.getPrayer(), prayer.isToggle());
            }
        }

        scheduledPrayers.removeIf(prayer -> prayer.getActivationTick() <= client.getTickCount() - 1);

        animationsThisTick.clear();
        npcsSpawnedThisTick.clear();
        npcsDespawnedThisTick.clear();
        npcsChangedThisTick.clear();
        projectilesSpawnedThisTick.clear();
        graphicsCreatedThisTick.clear();
        gameObjectsSpawnedThisTick.clear();
        npcsInteractingWithYouThisTick.clear();
        npcsYouInteractedWithThisTick.clear();

        validProjectiles.removeIf(proj -> proj.getRemainingCycles() < 1);
    }

    private void getEquipmentChanges()
    {
        Widget bankWidget = client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER);
        if (bankWidget != null && !bankWidget.isHidden())
        {
            return;
        }

        final List<SlottedItem> equippedItems = EquipmentUtils.getAll();
        final List<String> itemsMapped = equippedItems.stream().map(item -> client.getItemDefinition(item.getItem().getId()).getName()).collect(Collectors.toList());

        if (!listsMatch(itemsMapped, lastEquipmentList))
        {
            for (SlottedItem slottedItem : equippedItems)
            {
                String name = client.getItemDefinition(slottedItem.getItem().getId()).getName();
                if (!lastEquipmentList.contains(name))
                {
                    eventFired(EventType.ITEM_EQUIPPED, slottedItem.getItem().getId(), false);
                }
            }
            lastEquipmentList.clear();
            lastEquipmentList.addAll(itemsMapped);
        }
    }

    private static void activatePrayer(Client client, Prayer prayer, boolean toggle)
    {
        if (toggle)
        {
            CombatUtils.togglePrayer(prayer);
        }
        else
        {
            CombatUtils.activatePrayer(prayer);
        }
    }

    private void eventFired(EventType type, int id, boolean isTargetingPlayer)
    {
        List<CustomPrayer> prayers = eventMap.get(type);
        if (prayers == null || prayers.isEmpty())
        {
            return;
        }

        for (final CustomPrayer prayer : prayers)
        {
            if (prayer.getActivationId() == id)
            {
                if (prayer.isIgnoreNonTargetEvent())
                {
                    if (!isTargetingPlayer)
                    {
                        continue;
                    }
                }

                scheduledPrayers.add(new ScheduledPrayer(prayer.getPrayerToActivate(), client.getTickCount() + prayer.getTickDelay(), prayer.isToggle()));
            }
        }
    }

    public boolean listsMatch(List<String> list1, List<String> list2)
    {
        if (list1.size() != list2.size())
        {
            return false;
        }

        List<String> list2Copy = Lists.newArrayList(list2);
        for (String element : list1)
        {
            if (!list2Copy.remove(element))
            {
                return false;
            }
        }

        return list2Copy.isEmpty();
    }

    public void populateCustomPrayers()
    {
        eventMap.clear();

        for (EventType eventType : EventType.values())
        {
            List<CustomPrayer> customPrayers = new ArrayList<>();
            switch (eventType)
            {
                case PROJECTILE_SPAWNED:
                    if (config.soteDefense())
                    {
                        customPrayers.add(new CustomPrayer(1607, Prayer.PROTECT_FROM_MISSILES, 4, false, true));
                        customPrayers.add(new CustomPrayer(1606, Prayer.PROTECT_FROM_MAGIC, 4, false, true));
                    }

                    if (config.verzikDefense3())
                    {
                        customPrayers.add(new CustomPrayer(1593, Prayer.PROTECT_FROM_MISSILES, 1, false, false));
                        customPrayers.add(new CustomPrayer(1594, Prayer.PROTECT_FROM_MAGIC, 1, false, false));
                    }

                    break;
                case GRAPHICS_CREATED:
                    break;
                case ANIMATION_CHANGED:
                    if (config.bloatOffense() != LucidToBPrayersConfig.OffensivePrayer.NONE)
                    {
                        customPrayers.add(new CustomPrayer(8082, config.bloatOffense().getPrayer(), 1, false, false));
                    }

                    break;
                case NPC_SPAWNED:
                    if (config.maidenDefense())
                    {
                        customPrayers.add(new CustomPrayer(8360, Prayer.PROTECT_FROM_MAGIC, 2, false, false));

                        //Entry mode version
                        customPrayers.add(new CustomPrayer(10814, Prayer.PROTECT_FROM_MAGIC, 2, false, false));
                    }
                    if (config.maidenOffense() != LucidToBPrayersConfig.OffensivePrayer.NONE)
                    {
                        customPrayers.add(new CustomPrayer(8360, config.maidenOffense().getPrayer(), 2, false, false));

                        //Entry mode version
                        customPrayers.add(new CustomPrayer(10814, config.maidenOffense().getPrayer(), 2, false, false));
                    }

                    if (config.nyloBossDefense())
                    {
                        customPrayers.add(new CustomPrayer(8354, Prayer.PROTECT_FROM_MELEE, 1, false, false));

                        //Entry mode version
                        customPrayers.add(new CustomPrayer(10786, Prayer.PROTECT_FROM_MELEE, 1, false, false));
                    }
                    if (config.nyloBossOffense())
                    {
                        customPrayers.add(new CustomPrayer(8354, Prayer.PIETY, 1, false, false));

                        //Entry mode version
                        customPrayers.add(new CustomPrayer(10786, Prayer.PIETY, 1, false, false));
                    }

                    if (config.verzikDefense2())
                    {
                        customPrayers.add(new CustomPrayer(8385, Prayer.PROTECT_FROM_MAGIC, 0, false, false));

                        //Entry mode version
                        customPrayers.add(new CustomPrayer(10832, Prayer.PROTECT_FROM_MAGIC, 0, false, false));
                    }
                    if (config.verzikOffense2() != LucidToBPrayersConfig.OffensivePrayer.NONE)
                    {
                        customPrayers.add(new CustomPrayer(8385, config.verzikOffense2().getPrayer(), 0, false, false));

                        //Entry mode version
                        customPrayers.add(new CustomPrayer(10832, config.verzikOffense2().getPrayer(), 0, false, false));
                    }
                    break;
                case NPC_CHANGED:
                    if (config.bloatDefense())
                    {
                        customPrayers.add(new CustomPrayer(8359, Prayer.PROTECT_FROM_MISSILES, 2, false, false));

                        //Entry mode version
                        customPrayers.add(new CustomPrayer(10812, Prayer.PROTECT_FROM_MISSILES, 2, false, false));
                    }

                    if (config.nyloBossDefense())
                    {
                        customPrayers.add(new CustomPrayer(8355, Prayer.PROTECT_FROM_MELEE, 0, false, false));
                        customPrayers.add(new CustomPrayer(8356, Prayer.PROTECT_FROM_MAGIC, 0, false, false));
                        customPrayers.add(new CustomPrayer(8357, Prayer.PROTECT_FROM_MISSILES, 0, false, false));

                        //Entry mode version
                        customPrayers.add(new CustomPrayer(10787, Prayer.PROTECT_FROM_MELEE, 0, false, false));
                        customPrayers.add(new CustomPrayer(10788, Prayer.PROTECT_FROM_MAGIC, 0, false, false));
                        customPrayers.add(new CustomPrayer(10789, Prayer.PROTECT_FROM_MISSILES, 0, false, false));
                    }
                    if (config.nyloBossOffense())
                    {
                        customPrayers.add(new CustomPrayer(8355, Prayer.PIETY, 0, false, false));
                        customPrayers.add(new CustomPrayer(8356, Prayer.AUGURY, 0, false, false));
                        customPrayers.add(new CustomPrayer(8357, Prayer.RIGOUR, 0, false, false));

                        //Entry mode version
                        customPrayers.add(new CustomPrayer(10787, Prayer.PIETY, 0, false, false));
                        customPrayers.add(new CustomPrayer(10788, Prayer.AUGURY, 0, false, false));
                        customPrayers.add(new CustomPrayer(10789, Prayer.RIGOUR, 0, false, false));
                    }

                    if (config.soteOffense() != LucidToBPrayersConfig.OffensivePrayer.NONE)
                    {
                        customPrayers.add(new CustomPrayer(8388, config.soteOffense().getPrayer(), 0, false, false));

                        //Entry mode version
                        customPrayers.add(new CustomPrayer(10865, config.soteOffense().getPrayer(), 0, false, false));
                    }

                    if (config.xarpusDefense())
                    {
                        customPrayers.add(new CustomPrayer(8340, Prayer.REDEMPTION, 1, false, false));
                    }
                    if (config.xarpusOffense() != LucidToBPrayersConfig.OffensivePrayer.NONE)
                    {
                        customPrayers.add(new CustomPrayer(8340, config.xarpusOffense().getPrayer(), 1, false, false));

                        //Entry mode version
                        customPrayers.add(new CustomPrayer(10767, config.xarpusOffense().getPrayer(), 1, false, false));

                    }

                    if (config.verzikDefense1())
                    {
                        customPrayers.add(new CustomPrayer(8370, Prayer.PROTECT_FROM_MAGIC, 0, false, false));

                        //Entry mode version
                        customPrayers.add(new CustomPrayer(10831, Prayer.PROTECT_FROM_MAGIC, 0, false, false));
                    }
                    if (config.verzikOffense1() != LucidToBPrayersConfig.OffensivePrayer.NONE)
                    {
                        customPrayers.add(new CustomPrayer(8370, config.verzikOffense1().getPrayer(), 0, false, false));

                        //Entry mode version
                        customPrayers.add(new CustomPrayer(10831, config.verzikOffense1().getPrayer(), 0, false, false));

                    }

                    if (config.verzikDefense2())
                    {
                        customPrayers.add(new CustomPrayer(8372, Prayer.PROTECT_FROM_MISSILES, 0, false, false));

                        //Entry mode version
                        customPrayers.add(new CustomPrayer(10832, Prayer.PROTECT_FROM_MISSILES, 0, false, false));
                    }
                    if (config.verzikOffense2() != LucidToBPrayersConfig.OffensivePrayer.NONE)
                    {
                        customPrayers.add(new CustomPrayer(8372, config.verzikOffense2().getPrayer(), 0, false, false));

                        //Entry mode version
                        customPrayers.add(new CustomPrayer(10832, config.verzikOffense2().getPrayer(), 0, false, false));
                    }
                    break;
            }
            eventMap.put(eventType, customPrayers);
        }

    }
}