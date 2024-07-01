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


import net.runelite.api.EquipmentInventorySlot;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.inventorysetups.InventorySetup;
import net.runelite.client.plugins.inventorysetups.InventorySetupsItem;
import net.runelite.client.plugins.inventorysetups.MInventorySetupsPlugin;
import net.runelite.client.plugins.inventorysetups.InventorySetupsSlotID;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

// The panel that contains the equipment slots
public class InventorySetupsEquipmentPanel extends InventorySetupsContainerPanel
{
	private Map<EquipmentInventorySlot, InventorySetupsSlot> equipmentSlots;

	InventorySetupsEquipmentPanel(final ItemManager itemManager, final MInventorySetupsPlugin plugin)
	{
		super(itemManager, plugin, "Equipment");
	}

	@Override
	public void setupContainerPanel(final JPanel containerSlotsPanel)
	{
		this.equipmentSlots = new HashMap<>();
		for (final EquipmentInventorySlot slot : EquipmentInventorySlot.values())
		{
			final InventorySetupsSlot setupSlot = new InventorySetupsSlot(ColorScheme.DARKER_GRAY_COLOR, InventorySetupsSlotID.EQUIPMENT, slot.getSlotIdx());
			super.addFuzzyMouseListenerToSlot(setupSlot);

			// add stackable configurations for ammo and weapon slots
			if (slot == EquipmentInventorySlot.AMMO || slot == EquipmentInventorySlot.WEAPON)
			{
				super.addStackMouseListenerToSlot(setupSlot);
			}

			super.addUpdateFromContainerMouseListenerToSlot(setupSlot);
			super.addUpdateFromSearchMouseListenerToSlot(setupSlot, true);
			super.addRemoveMouseListenerToSlot(setupSlot);

			// Shift menu
			super.addUpdateFromContainerToAllInstancesMouseListenerToSlot(setupSlot);
			super.addUpdateFromSearchToAllInstancesMouseListenerToSlot(setupSlot, true);

			equipmentSlots.put(slot, setupSlot);
		}

		final GridLayout gridLayout = new GridLayout(5, 3, 1, 1);
		containerSlotsPanel.setLayout(gridLayout);

		// add the grid layouts, including invisible ones
		containerSlotsPanel.add(new InventorySetupsSlot(ColorScheme.DARK_GRAY_COLOR, InventorySetupsSlotID.EQUIPMENT, -1));
		containerSlotsPanel.add(equipmentSlots.get(EquipmentInventorySlot.HEAD));
		containerSlotsPanel.add(new InventorySetupsSlot(ColorScheme.DARK_GRAY_COLOR, InventorySetupsSlotID.EQUIPMENT, -1));
		containerSlotsPanel.add(equipmentSlots.get(EquipmentInventorySlot.CAPE));
		containerSlotsPanel.add(equipmentSlots.get(EquipmentInventorySlot.AMULET));
		containerSlotsPanel.add(equipmentSlots.get(EquipmentInventorySlot.AMMO));
		containerSlotsPanel.add(equipmentSlots.get(EquipmentInventorySlot.WEAPON));
		containerSlotsPanel.add(equipmentSlots.get(EquipmentInventorySlot.BODY));
		containerSlotsPanel.add(equipmentSlots.get(EquipmentInventorySlot.SHIELD));
		containerSlotsPanel.add(new InventorySetupsSlot(ColorScheme.DARK_GRAY_COLOR, InventorySetupsSlotID.EQUIPMENT, -1));
		containerSlotsPanel.add(equipmentSlots.get(EquipmentInventorySlot.LEGS));
		containerSlotsPanel.add(new InventorySetupsSlot(ColorScheme.DARK_GRAY_COLOR, InventorySetupsSlotID.EQUIPMENT, -1));
		containerSlotsPanel.add(equipmentSlots.get(EquipmentInventorySlot.GLOVES));
		containerSlotsPanel.add(equipmentSlots.get(EquipmentInventorySlot.BOOTS));
		containerSlotsPanel.add(equipmentSlots.get(EquipmentInventorySlot.RING));

	}

	@Override
	public void updatePanelWithSetupInformation(final InventorySetup setup)
	{
		for (final EquipmentInventorySlot slot : EquipmentInventorySlot.values())
		{
			int i = slot.getSlotIdx();
			super.setSlotImageAndText(equipmentSlots.get(slot), setup, setup.getEquipment().get(i));
		}

		validate();
		repaint();
	}

	@Override
	public void highlightSlots(final List<InventorySetupsItem> currentEquipment, final InventorySetup inventorySetup)
	{
		final List<InventorySetupsItem> savedEquipmentFromSetup = inventorySetup.getEquipment();

		assert currentEquipment.size() == savedEquipmentFromSetup.size() : "size mismatch";

		isHighlighted = true;

		for (final EquipmentInventorySlot slot : EquipmentInventorySlot.values())
		{
			int slotIdx = slot.getSlotIdx();
			super.highlightSlot(inventorySetup, savedEquipmentFromSetup.get(slotIdx), currentEquipment.get(slotIdx), equipmentSlots.get(slot));
		}
	}

	@Override
	public void resetSlotColors()
	{
		// Don't waste time resetting if we were never highlighted to begin with
		if (!isHighlighted)
		{
			return;
		}

		for (final EquipmentInventorySlot slot : EquipmentInventorySlot.values())
		{
			equipmentSlots.get(slot).setBackground(ColorScheme.DARKER_GRAY_COLOR);
		}

		isHighlighted = false;
	}

	public boolean isStackCompareForSlotAllowed(final int id)
	{
		return id == EquipmentInventorySlot.AMMO.getSlotIdx() || id == EquipmentInventorySlot.WEAPON.getSlotIdx();
	}
}
