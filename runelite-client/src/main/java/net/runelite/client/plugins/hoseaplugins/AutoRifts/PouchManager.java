package net.runelite.client.plugins.hoseaplugins.AutoRifts;

import net.runelite.client.plugins.hoseaplugins.AutoRifts.data.Pouch;
import net.runelite.client.plugins.hoseaplugins.AutoRifts.data.Constants;
import net.runelite.client.plugins.hoseaplugins.ethanapi.EthanApiPlugin.Collections.Inventory;
import net.runelite.client.plugins.hoseaplugins.ethanapi.InteractionApi.InventoryInteraction;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Singleton
public class PouchManager {
    private final List<Pouch> pouches = new ArrayList<>();
    @Inject
    private Client client;
    private final EventBus eventBus;
    @Setter
    private boolean started;
    private final List<Integer> DEGRADED_POUCH_IDS = ImmutableList.of(ItemID.MEDIUM_POUCH_5511, ItemID.LARGE_POUCH_5513, ItemID.GIANT_POUCH_5515,
            ItemID.COLOSSAL_POUCH_26786);

    @Inject
    public PouchManager(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void register() {
        eventBus.register(this);
    }

    public void deregister() {
        eventBus.unregister(this);
    }

    public void refreshPouches() {
        //pouches.clear();
        Optional<Widget> smallpouch = Inventory.search().withId(ItemID.SMALL_POUCH).first();
        Optional<Widget> medpouch = Inventory.search().withId(ItemID.MEDIUM_POUCH).first();
        Optional<Widget> largepouch = Inventory.search().withId(ItemID.LARGE_POUCH).first();
        Optional<Widget> giantpouch = Inventory.search().withId(ItemID.GIANT_POUCH).first();
        Optional<Widget> collosalpouch = Inventory.search().withId(ItemID.COLOSSAL_POUCH).first();
        if (smallpouch.isPresent()) {
            Pouch smallEssPouch = new Pouch(ItemID.SMALL_POUCH, 3);
            pouches.removeIf(p -> p.getPouchID() == ItemID.SMALL_POUCH);
            pouches.add(smallEssPouch);
        }

        if (medpouch.isPresent() && client.getRealSkillLevel(Skill.RUNECRAFT) >= 25) {
            Pouch medEssPouch = new Pouch(ItemID.MEDIUM_POUCH, 6);
            pouches.removeIf(p -> p.getPouchID() == ItemID.MEDIUM_POUCH);
            pouches.add(medEssPouch);
        }

        if (largepouch.isPresent() && client.getRealSkillLevel(Skill.RUNECRAFT) >= 50) {
            Pouch largeEssPouch = new Pouch(ItemID.LARGE_POUCH, 9);
            pouches.removeIf(p -> p.getPouchID() == ItemID.LARGE_POUCH);
            pouches.add(largeEssPouch);
        }

        if (giantpouch.isPresent() && client.getRealSkillLevel(Skill.RUNECRAFT) >= 75) {
            Pouch giantEssPouch = new Pouch(ItemID.GIANT_POUCH, 12);
            pouches.removeIf(p -> p.getPouchID() == ItemID.GIANT_POUCH);
            pouches.add(giantEssPouch);
        }

        if (collosalpouch.isPresent() && client.getRealSkillLevel(Skill.RUNECRAFT) >= 85) {
            Pouch colossalEssPouch = new Pouch(ItemID.COLOSSAL_POUCH, 40);
            pouches.removeIf(p -> p.getPouchID() == ItemID.COLOSSAL_POUCH);
            pouches.add(colossalEssPouch);
        }

        log.info("Setting Pouches: " + pouches);
    }


    @Subscribe
    private void onChatMessage(ChatMessage event) {
        //log.info(pouches.toString());
        if (client.getGameState() != GameState.LOGGED_IN || !started) {
            return;
        }

        if (event.getType() != ChatMessageType.SPAM && event.getType() != ChatMessageType.GAMEMESSAGE) return;

        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }

        if (event.getMessage().contains(Constants.GAME_STARTED)) {
            setEssenceInPouches(0);
        }

        if (event.getMessage().contains(Constants.GAME_WIN)) {
            setEssenceInPouches(0);
        }

        if (event.getMessage().contains(Constants.GAME_OVER)) {
            setEssenceInPouches(0);
        }
    }

    public List<Pouch> getFullPouches() {
        List<Pouch> result = new ArrayList<>();
        for (Pouch pouch : pouches) {
            if (pouch.getCurrentEssence() > 0) {
                result.add(pouch);
            }
        }
        return result;
    }

    public boolean hasFullPouch() {
        for (Pouch pouch : pouches) {
            if (pouch.getCurrentEssence() > 0) {
                return true;
            }
        }
        return false;
    }

    public void fillPouches() {
        int essenceAmount = Inventory.getItemAmount(ItemID.GUARDIAN_ESSENCE);
        List<Pouch> result = getEmptyPouches();
        for (Pouch pouch : result) {
            Optional<Widget> emptyPouch = Inventory.search().withId(pouch.getPouchID()).first();
            if (emptyPouch.isPresent()) {
                Widget p = emptyPouch.get();
                InventoryInteraction.useItem(p, "Fill");
                int essenceWithdrawn = pouch.getEssenceTotal() - pouch.getCurrentEssence();
                if (essenceAmount - essenceWithdrawn > 0) {
                    essenceAmount -= essenceWithdrawn;
                    pouch.setCurrentEssence(pouch.getEssenceTotal());
                } else {
                    pouch.setCurrentEssence(essenceAmount);
                    break;
                }
            }
        }
    }

    public void emptyPouches() {
        int spaces = Inventory.getEmptySlots();
        List<Pouch> result = getFullPouches();
        for (Pouch pouch : result) {
            Optional<Widget> emptyPouch = Inventory.search().withId(pouch.getPouchID()).first();
            if (emptyPouch.isPresent()) {
                InventoryInteraction.useItem(emptyPouch.get(), "Empty");
                if (pouch.getCurrentEssence() - spaces < 0) {
                    spaces -= pouch.getCurrentEssence();
                    pouch.setCurrentEssence(0);
                } else {
                    pouch.setCurrentEssence(pouch.getCurrentEssence() - spaces);
                    break;
                }
            }
        }
    }

    public List<Pouch> getEmptyPouches() {
        List<Pouch> result = new ArrayList<>();
        for (Pouch pouch : pouches) {
            if (!isPouchFull(pouch)) {
                result.add(pouch);
            }
        }
        return result;
    }

    public boolean hasEmptyPouches() {
        for (Pouch pouch : pouches) {
            if (!isPouchFull(pouch)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPouchFull(Pouch pouch) {
        return pouch.getCurrentEssence() == pouch.getEssenceTotal();
    }

    private void setEssenceInPouches(int amount) {
        for (Pouch curr : pouches) {
            curr.setCurrentEssence(amount);
        }
    }

    public boolean hasDegradedPouches() {
        return Inventory.search().idInList(DEGRADED_POUCH_IDS).first().isPresent();
    }

    public int getAvailableSpace() {
        return pouches.stream().mapToInt(pouch -> pouch.getEssenceTotal() - pouch.getCurrentEssence()).sum();
    }

    public int getEssenceInPouches() {
        return pouches.stream().mapToInt(Pouch::getCurrentEssence).sum();
    }
}
