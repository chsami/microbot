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
package net.runelite.client.plugins.microbot.inventorysetups;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class InventorySetup implements InventorySetupsDisplayAttributes
{
	@Getter
	private List<InventorySetupsItem> inventory;

	@Getter
	private List<InventorySetupsItem> equipment;

	@Getter
	private List<InventorySetupsItem> rune_pouch;

	@Getter
	private List<InventorySetupsItem> boltPouch;

	@Getter
	private Map<Integer, InventorySetupsItem> additionalFilteredItems;

	@Getter
	@Setter
	private String name;

	@Getter
	@Setter
	private String notes;

	@Getter
	@Setter
	private Color highlightColor;

	@Getter
	@Setter
	private boolean highlightDifference;

	@Getter
	@Setter
	private Color displayColor;

	@Getter
	@Setter
	private boolean filterBank;

	@Getter
	@Setter
	private boolean unorderedHighlight;

	/*
		0 = Standard
		1 = Ancient
		2 = Lunar
		3 = Arceuus
		4 = NONE

		Avoiding Enum because won't work well with GSON (defaults to null)
	*/
	@Getter
	@Setter
	private int spellBook;

	@Getter
	@Setter
	private boolean favorite;

	@Getter
	@Setter
	private int iconID;

	public void updateInventory(final List<InventorySetupsItem> inv)
	{
		inventory = inv;
	}

	public void updateEquipment(final List<InventorySetupsItem> eqp)
	{
		equipment = eqp;
	}

	public void updateRunePouch(final List<InventorySetupsItem> rp)
	{
		rune_pouch = rp;
	}

	public void updateBoltPouch(final List<InventorySetupsItem> bp)
	{
		boltPouch = bp;
	}

	public void updateAdditionalItems(final Map<Integer, InventorySetupsItem> ai)
	{
		additionalFilteredItems = ai;
	}

	public void updateSpellbook(final int sb)
	{
		spellBook = sb;
	}

	public void updateNotes(final String text)
	{
		notes = text;
	}

}
