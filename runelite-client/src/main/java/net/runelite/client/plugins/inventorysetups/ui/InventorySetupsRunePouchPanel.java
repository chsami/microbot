/*
 * Copyright (c) 2019, dillydill123 <https://github.com/dillydill123>
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
package net.runelite.client.plugins.inventorysetups.ui;

import net.runelite.api.ItemID;
import net.runelite.api.Varbits;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.inventorysetups.InventorySetup;
import net.runelite.client.plugins.inventorysetups.InventorySetupsItem;
import net.runelite.client.plugins.inventorysetups.MInventorySetupsPlugin;
import net.runelite.client.plugins.inventorysetups.InventorySetupsSlotID;

import java.util.Arrays;
import java.util.List;

public class InventorySetupsRunePouchPanel extends InventorySetupsAmmunitionPanel
{
	// 23650 is what shows up when selecting a RunePouch from ChatBoxItemSearch, 27086 is likely lms
	public static final List<Integer> RUNE_POUCH_IDS = Arrays.asList(ItemID.RUNE_POUCH, ItemID.RUNE_POUCH_L, ItemID.RUNE_POUCH_23650, ItemID.RUNE_POUCH_27086);
	
	public static final List<Integer> RUNE_POUCH_DIVINE_IDS = Arrays.asList(ItemID.DIVINE_RUNE_POUCH, ItemID.DIVINE_RUNE_POUCH_L);

	public static final List<Integer> RUNE_POUCH_AMOUNT_VARBITS = Arrays.asList(Varbits.RUNE_POUCH_AMOUNT1, Varbits.RUNE_POUCH_AMOUNT2, Varbits.RUNE_POUCH_AMOUNT3, Varbits.RUNE_POUCH_AMOUNT4);

	public static final List<Integer> RUNE_POUCH_RUNE_VARBITS = Arrays.asList(Varbits.RUNE_POUCH_RUNE1, Varbits.RUNE_POUCH_RUNE2, Varbits.RUNE_POUCH_RUNE3, Varbits.RUNE_POUCH_RUNE4);

	InventorySetupsRunePouchPanel(ItemManager itemManager, MInventorySetupsPlugin plugin)
	{
		super(itemManager, plugin, "Rune Pouch");
	}

	@Override
	protected InventorySetupsSlotID getSlotId()
	{
		return InventorySetupsSlotID.RUNE_POUCH;
	}

	@Override
	protected int getSlotsCount()
	{
		return 4;
	}

	@Override
	protected List<InventorySetupsItem> getContainer(InventorySetup inventorySetup)
	{
		return inventorySetup.getRune_pouch();
	}
}
