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


import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.inventorysetups.InventorySetup;
import net.runelite.client.plugins.inventorysetups.InventorySetupsItem;
import net.runelite.client.plugins.inventorysetups.MInventorySetupsPlugin;
import net.runelite.client.plugins.inventorysetups.InventorySetupsSlotID;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// The additional filtered items panel that contains the additional filtered items list
public class InventorySetupsAdditionalItemsPanel extends InventorySetupsContainerPanel
{
	private final List<InventorySetupsSlot> additionalFilteredSlots;

	InventorySetupsAdditionalItemsPanel(ItemManager itemManager, MInventorySetupsPlugin plugin)
	{
		super(itemManager, plugin, "Additional Filtered Items");
		additionalFilteredSlots = new ArrayList<>();
	}

	@Override
	public void setupContainerPanel(JPanel containerSlotsPanel)
	{
		containerSlotsPanel.setLayout(new GridLayout(0, 4, 1, 1));
	}

	@Override
	public void highlightSlots(List<InventorySetupsItem> currContainer, InventorySetup inventorySetup)
	{
		// No highlighting for this panel
	}

	@Override
	public void updatePanelWithSetupInformation(InventorySetup setup)
	{
		final Map<Integer, InventorySetupsItem> setupAdditionalItems = setup.getAdditionalFilteredItems();
		final JPanel containerSlotsPanel = this.getContainerSlotsPanel();

		// Make final size a multiple of 4
		int totalNumberOfSlots = setupAdditionalItems.size();
		int remainder = totalNumberOfSlots % 4;
		if (totalNumberOfSlots % 4 != 0)
		{
			totalNumberOfSlots = totalNumberOfSlots + 4 - remainder;
		}

		// saturated the row, increase it now
		if (totalNumberOfSlots == setupAdditionalItems.size())
		{
			totalNumberOfSlots += 4;
		}

		// new component creation must be on event dispatch thread, hence invoke later
		final int totalNumberOfSlotsLambda = totalNumberOfSlots;
		SwingUtilities.invokeLater(() ->
		{

			// add new slots if the final size is larger than the number of slots
			for (int i = additionalFilteredSlots.size(); i < totalNumberOfSlotsLambda; i++)
			{
				final InventorySetupsSlot newSlot = new InventorySetupsSlot(ColorScheme.DARKER_GRAY_COLOR, InventorySetupsSlotID.ADDITIONAL_ITEMS, i);
				super.addFuzzyMouseListenerToSlot(newSlot);
				super.addUpdateFromSearchMouseListenerToSlot(newSlot, false);
				super.addRemoveMouseListenerToSlot(newSlot);
				additionalFilteredSlots.add(newSlot);
			}

			// remove the extra slots from the layout if needed so the panel fits as small as possible
			for (int i = containerSlotsPanel.getComponentCount() - 1; i >= totalNumberOfSlotsLambda; i--)
			{
				containerSlotsPanel.remove(i);
			}

			// remove the images and tool tips for the inventory slots that are not part of this setup
			for (int i = totalNumberOfSlotsLambda - 1; i >= setupAdditionalItems.size(); i--)
			{
				this.setSlotImageAndText(additionalFilteredSlots.get(i), setup, InventorySetupsItem.getDummyItem());
			}

			// add slots back to the layout if we need to
			for (int i = containerSlotsPanel.getComponentCount(); i < totalNumberOfSlotsLambda; i++)
			{
				containerSlotsPanel.add(additionalFilteredSlots.get(i));
			}

			// finally set the slots with the items and tool tips
			int j = 0;
			for (final Integer itemId : setupAdditionalItems.keySet())
			{
				this.setSlotImageAndText(additionalFilteredSlots.get(j), setup, setupAdditionalItems.get(itemId));
				j++;
			}

			validate();
			repaint();
		});

	}

	@Override
	public void resetSlotColors()
	{
	}

	public boolean isStackCompareForSlotAllowed(final int id)
	{
		return false;
	}

}
