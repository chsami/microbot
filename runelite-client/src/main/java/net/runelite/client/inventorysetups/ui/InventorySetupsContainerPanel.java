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

import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.inventorysetups.*;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.AsyncBufferedImage;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public abstract class InventorySetupsContainerPanel extends JPanel
{

	protected ItemManager itemManager;

	protected boolean isHighlighted;

	protected final MInventorySetupsPlugin plugin;

	@Getter(AccessLevel.PROTECTED)
	private final JPanel containerSlotsPanel;

	InventorySetupsContainerPanel(final ItemManager itemManager, final MInventorySetupsPlugin plugin, String captionText)
	{
		this.itemManager = itemManager;
		this.plugin = plugin;
		this.isHighlighted = false;
		JPanel containerPanel = new JPanel();

		this.containerSlotsPanel = new JPanel();

		// sets up the custom container panel
		setupContainerPanel(containerSlotsPanel);

		// caption
		final JLabel caption = new JLabel(captionText);
		caption.setHorizontalAlignment(JLabel.CENTER);
		caption.setVerticalAlignment(JLabel.CENTER);

		// panel that holds the caption and any other graphics
		final JPanel captionPanel = new JPanel();
		captionPanel.add(caption);

		containerPanel.setLayout(new BorderLayout());
		containerPanel.add(captionPanel, BorderLayout.NORTH);
		containerPanel.add(containerSlotsPanel, BorderLayout.CENTER);

		add(containerPanel);
	}

	// adds the menu option to update a slot from the container it presides in
	protected void addUpdateFromContainerMouseListenerToSlot(final InventorySetupsSlot slot)
	{
		String updateContainerFrom = getContainerString(slot);
		JMenuItem updateFromContainer = new JMenuItem("Update Slot from " + updateContainerFrom);
		slot.getRightClickMenu().add(updateFromContainer);
		updateFromContainer.addActionListener(e ->
		{
			plugin.updateSlotFromContainer(slot, false);
		});
	}

	// adds the option replace all slots containing the old item with the new item in all setups
	protected void addUpdateFromContainerToAllInstancesMouseListenerToSlot(final InventorySetupsSlot slot)
	{
		String updateContainerFrom = getContainerString(slot);
		JMenuItem updateFromContainer = new JMenuItem("Update ALL Slots from " + updateContainerFrom);
		slot.getShiftRightClickMenu().add(updateFromContainer);
		updateFromContainer.addActionListener(e ->
		{
			int confirm = JOptionPane.showConfirmDialog(this,
					"Do you want to update ALL setups which have this item to the new item?",
					"Update ALL Setups", JOptionPane.OK_CANCEL_OPTION);

			if (confirm == JOptionPane.YES_OPTION)
			{
				plugin.updateSlotFromContainer(slot, true);
			}
		});
	}

	// adds the menu option to update a slot from item search
	protected void addUpdateFromSearchMouseListenerToSlot(final InventorySetupsSlot slot, boolean allowStackable)
	{
		JMenuItem updateFromSearch = new JMenuItem("Update Slot from Search");
		slot.getRightClickMenu().add(updateFromSearch);
		updateFromSearch.addActionListener(e ->
		{
			plugin.updateSlotFromSearch(slot, allowStackable, false);
		});
	}

	// adds the option replace all slots containing the old item with the newly searched item in all setups
	protected void addUpdateFromSearchToAllInstancesMouseListenerToSlot(final InventorySetupsSlot slot, boolean allowStackable)
	{
		JMenuItem updateFromContainer = new JMenuItem("Update ALL Slots from Search");
		slot.getShiftRightClickMenu().add(updateFromContainer);
		updateFromContainer.addActionListener(e ->
		{
			int confirm = JOptionPane.showConfirmDialog(this,
					"Do you want to update ALL setups which have this item to the new item?",
					"Update ALL Setups", JOptionPane.OK_CANCEL_OPTION);

			if (confirm == JOptionPane.YES_OPTION)
			{
				plugin.updateSlotFromSearch(slot, allowStackable, true);
			}
		});
	}

	// adds the menu option to clear a slot
	protected void addRemoveMouseListenerToSlot(final InventorySetupsSlot slot)
	{
		JMenuItem removeSlot = new JMenuItem("Remove Item from Slot");
		slot.getRightClickMenu().add(removeSlot);
		removeSlot.addActionListener(e ->
		{
			plugin.removeItemFromSlot(slot);
		});
	}

	// adds the menu option to update set a slot to fuzzy
	protected void addFuzzyMouseListenerToSlot(final InventorySetupsSlot slot)
	{
		JMenuItem makeSlotFuzzy = new JMenuItem("Toggle Fuzzy");
		slot.getRightClickMenu().add(makeSlotFuzzy);
		makeSlotFuzzy.addActionListener(e ->
		{
			plugin.toggleFuzzyOnSlot(slot);
		});
	}

	// adds the menu option to update set a slot to fuzzy
	protected void addStackMouseListenerToSlot(final InventorySetupsSlot slot)
	{
		JMenuItem stackIndicatorNone = new JMenuItem("Stack Difference None");
		stackIndicatorNone.addActionListener(e ->
		{
			plugin.setStackCompareOnSlot(slot, InventorySetupsStackCompareID.None);
		});

		JMenuItem stackIndicatorStandard = new JMenuItem("Stack Difference Standard");
		stackIndicatorStandard.addActionListener(e ->
		{
			plugin.setStackCompareOnSlot(slot, InventorySetupsStackCompareID.Standard);
		});

		JMenuItem stackIndicatorGreaterThan = new JMenuItem("Stack Difference Greater Than");
		stackIndicatorGreaterThan.addActionListener(e ->
		{
			plugin.setStackCompareOnSlot(slot, InventorySetupsStackCompareID.Greater_Than);
		});

		JMenuItem stackIndicatorLessThan = new JMenuItem("Stack Difference Less Than");
		stackIndicatorLessThan.addActionListener(e ->
		{
			plugin.setStackCompareOnSlot(slot, InventorySetupsStackCompareID.Less_Than);
		});

		JMenu stackIndicatorMainMenu = new JMenu("Stack Indicator");
		stackIndicatorMainMenu.add(stackIndicatorNone);
		stackIndicatorMainMenu.add(stackIndicatorStandard);
		stackIndicatorMainMenu.add(stackIndicatorLessThan);
		stackIndicatorMainMenu.add(stackIndicatorGreaterThan);
		slot.getRightClickMenu().add(stackIndicatorMainMenu);
	}

	private String getContainerString(final InventorySetupsSlot slot)
	{
		String updateContainerFrom = "";
		switch (slot.getSlotID())
		{
			case INVENTORY:
				updateContainerFrom = "Inventory";
				break;
			case EQUIPMENT:
				updateContainerFrom = "Equipment";
				break;
			case RUNE_POUCH:
				updateContainerFrom = "Rune Pouch";
				break;
			case BOLT_POUCH:
				updateContainerFrom = "Bolt Pouch";
				break;
			default:
				assert false : "Wrong slot ID!";
				break;
		}
		return updateContainerFrom;
	}

	// Sets the image and tooltip text for a slot
	protected void setSlotImageAndText(final InventorySetupsSlot containerSlot, final InventorySetup setup, final InventorySetupsItem item)
	{
		containerSlot.setParentSetup(setup);

		if (item.getId() == -1)
		{
			containerSlot.setImageLabel(null, null, item.isFuzzy(), item.getStackCompare());
			return;
		}

		int itemId = item.getId();
		int quantity = item.getQuantity();
		final String itemName = item.getName();
		AsyncBufferedImage itemImg = itemManager.getImage(itemId, quantity, quantity > 1);
		String toolTip = itemName;
		if (quantity > 1)
		{
			toolTip += " (" + quantity + ")";
		}
		containerSlot.setImageLabel(toolTip, itemImg, item.isFuzzy(), item.getStackCompare());
	}

	// highlights the slot based on the configuration and the saved item vs item in the slot
	protected void highlightSlot(final InventorySetup setup, InventorySetupsItem savedItemFromSetup, InventorySetupsItem currentItemFromContainer, final InventorySetupsSlot containerSlot)
	{
		// important note: do not use item names for comparisons
		// they are all empty to avoid clientThread usage when highlighting

		// first check if stack differences are enabled and compare quantities
		if (shouldHighlightSlotBasedOnStack(savedItemFromSetup.getStackCompare(), savedItemFromSetup.getQuantity(), currentItemFromContainer.getQuantity()))
		{
			containerSlot.setBackground(setup.getHighlightColor());
			return;
		}

		// obtain the correct item ids using fuzzy mapping if applicable
		int currentItemId = currentItemFromContainer.getId();
		int savedItemId = savedItemFromSetup.getId();

		if (savedItemFromSetup.isFuzzy())
		{
			currentItemId = InventorySetupsVariationMapping.map(currentItemId);
			savedItemId = InventorySetupsVariationMapping.map(savedItemId);
		}

		// if the ids don't match, highlight the container slot
		if (currentItemId != savedItemId)
		{
			containerSlot.setBackground(setup.getHighlightColor());
			return;
		}

		// set the color back to the original, because they match
		containerSlot.setBackground(ColorScheme.DARKER_GRAY_COLOR);
	}

	protected boolean shouldHighlightSlotBasedOnStack(final InventorySetupsStackCompareID stackCompareType, final Integer savedItemQty, final Integer currItemQty)
	{
		final int stackCompareResult = Integer.compare(currItemQty, savedItemQty);
		return stackCompareType == InventorySetupsStackCompareID.Less_Than && stackCompareResult < 0 ||
				stackCompareType == InventorySetupsStackCompareID.Greater_Than && stackCompareResult > 0 ||
				stackCompareType == InventorySetupsStackCompareID.Standard && stackCompareResult != 0;
	}

	abstract public boolean isStackCompareForSlotAllowed(final int id);

	abstract public void setupContainerPanel(final JPanel containerSlotsPanel);

	abstract public void highlightSlots(final List<InventorySetupsItem> currContainer, final InventorySetup inventorySetup);

	abstract public void updatePanelWithSetupInformation(final InventorySetup setup);

	abstract public void resetSlotColors();
}
