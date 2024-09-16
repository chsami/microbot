package net.runelite.client.plugins.microbot.qualityoflife.scripts.pouch;/*
 * Copyright (c) 2019 Adam <Adam@sigterm.info>
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

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;

import javax.inject.Inject;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.min;

@Slf4j
@PluginDescriptor(
        name = "Essence Pouch",
        description = "Shows how many essence are in an essence pouch",
        tags = {"pouch", "runecraft"},
        enabledByDefault = false
)
public class PouchScript extends Script {
    private final int INVENTORY_SIZE = 28;

    private final Pattern POUCH_CHECK_MESSAGE = Pattern.compile("^There (?:is|are) ([a-z]+)(?: pure| daeyalt)? essences? in this pouch\\.$");
    private final ImmutableMap<String, Integer> TEXT_TO_NUMBER = ImmutableMap.<String, Integer>builder()
            .put("no", 0)
            .put("one", 1)
            .put("two", 2)
            .put("three", 3)
            .put("four", 4)
            .put("five", 5)
            .put("six", 6)
            .put("seven", 7)
            .put("eight", 8)
            .put("nine", 9)
            .put("ten", 10)
            .put("eleven", 11)
            .put("twelve", 12)
            .build();

    @Inject
    private PouchOverlay essencePouchOverlay;

    private final Deque<ClickOperation> clickedItems = new ArrayDeque<>();
    private final Deque<ClickOperation> checkedPouches = new ArrayDeque<>();
    private int lastEssence;
    private int lastSpace;

    public void startUp() {
        // Reset pouch state
        for (Pouch pouch : Pouch.values()) {
            pouch.setHolding(0);
            pouch.setUnknown(true);
            pouch.degrade(false);
        }

        lastEssence = lastSpace = -1;
    }


    @Override
    public void shutdown() {
        super.shutdown();
    }

    public void onChatMessage(ChatMessage event) {
        if (event.getType() != ChatMessageType.GAMEMESSAGE) {
            return;
        }

        if (!checkedPouches.isEmpty()) {
            Matcher matcher = POUCH_CHECK_MESSAGE.matcher(event.getMessage());
            if (matcher.matches()) {
                final int num = TEXT_TO_NUMBER.get(matcher.group(1));
                // Keep getting operations until we get a valid one
                do {
                    final ClickOperation op = checkedPouches.pop();
                    if (op.tick >= Microbot.getClient().getTickCount()) {
                        Pouch pouch = op.pouch;
                        pouch.setHolding(num);
                        pouch.setUnknown(false);
                        break;
                    }
                }
                while (!checkedPouches.isEmpty());
            }
        }
    }

    public void onItemContainerChanged(ItemContainerChanged event) {
        if (InventoryID.INVENTORY.getId() != event.getContainerId()) {
            return;
        }

        final Item[] items = event.getItemContainer().getItems();

        int newEss = 0;
        int newSpace = 0;

        // Count ess/space, and change pouch states
        for (Item item : items) {
            switch (item.getId()) {
                case ItemID.PURE_ESSENCE:
                case ItemID.DAEYALT_ESSENCE:
                case ItemID.GUARDIAN_ESSENCE:
                    newEss += 1;
                    break;
                case -1:
                    newSpace += 1;
                    break;
                case ItemID.MEDIUM_POUCH:
                case ItemID.LARGE_POUCH:
                case ItemID.GIANT_POUCH:
                    Pouch pouch = Pouch.forItem(item.getId());
                    pouch.degrade(false);
                    break;
                case ItemID.MEDIUM_POUCH_5511:
                case ItemID.LARGE_POUCH_5513:
                case ItemID.GIANT_POUCH_5515:
                    pouch = Pouch.forItem(item.getId());
                    pouch.degrade(true);
                    break;
            }
        }
        if (items.length < INVENTORY_SIZE) {
            // Pad newSpace for unallocated inventory slots
            newSpace += INVENTORY_SIZE - items.length;
        }

        if (clickedItems.isEmpty()) {
            lastSpace = newSpace;
            lastEssence = newEss;
            return;
        }

        if (lastEssence == -1 || lastSpace == -1) {
            lastSpace = newSpace;
            lastEssence = newEss;
            clickedItems.clear();
            return;
        }

        final int tick = Microbot.getClient().getTickCount();

        int essence = lastEssence;
        int space = lastSpace;


        while (essence != newEss) {
            ClickOperation op = clickedItems.poll();
            if (op == null) {
                break;
            }

            if (tick > op.tick) {
                continue;
            }

            Pouch pouch = op.pouch;

            final boolean fill = op.delta > 0;
            // How much ess can either be deposited or withdrawn
            final int required = fill ? pouch.getRemaining() : pouch.getHolding();
            // Bound to how much ess or free space we actually have, and optionally negate
            final int essenceGot = op.delta * min(required, fill ? essence : space);

            // if we have enough essence or space to fill or empty the entire pouch, it no
            // longer becomes unknown
            if (pouch.isUnknown() && (fill ? essence : space) >= pouch.getHoldAmount()) {
                pouch.setUnknown(false);
            }

            log.debug("{}: {}", pouch.name(), essenceGot);

            essence -= essenceGot;
            space += essenceGot;

            pouch.addHolding(essenceGot);
        }

        if (!clickedItems.isEmpty()) {
            log.debug("End processing with {} events left", clickedItems.size());
        }

        lastSpace = newSpace;
        lastEssence = newEss;
    }

    public void onMenuOptionClicked(MenuOptionClicked event) {
        int itemId = event.getItemId();

        if (itemId == -1) {
            return;
        }

        final Pouch pouch = Pouch.forItem(itemId);
        if (pouch == null) {
            return;
        }
        final int tick = Microbot.getClient().getTickCount() + 3;
        System.out.println(event.getMenuOption());
        switch (event.getMenuOption()) {
            case "Fill":
                clickedItems.add(new ClickOperation(pouch, tick, 1));
                break;
            case "Empty":
                clickedItems.add(new ClickOperation(pouch, tick, -1));
                break;
            case "Check":
                checkedPouches.add(new ClickOperation(pouch, tick));
                break;
            case "Take":
                // Dropping pouches clears them, so clear when picked up
                pouch.setHolding(0);
                break;
        }
    }
}