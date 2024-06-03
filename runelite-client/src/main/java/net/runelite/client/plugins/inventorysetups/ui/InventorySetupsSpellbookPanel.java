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


import net.runelite.api.SpriteID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.inventorysetups.InventorySetup;
import net.runelite.client.plugins.inventorysetups.InventorySetupsItem;
import net.runelite.client.plugins.inventorysetups.MInventorySetupsPlugin;
import net.runelite.client.plugins.inventorysetups.InventorySetupsSlotID;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class InventorySetupsSpellbookPanel extends InventorySetupsContainerPanel
{

	private InventorySetupsSlot spellbookSlot;
	private List<BufferedImage> spellbookImages;

	InventorySetupsSpellbookPanel(ItemManager itemManager, MInventorySetupsPlugin plugin)
	{
		super(itemManager, plugin, "Spellbook");
		spellbookImages = new ArrayList<>();

		plugin.getClientThread().invokeLater(() ->
		{

			BufferedImage standardSpellbook = plugin.getSpriteManager().getSprite(SpriteID.TAB_MAGIC, 0);
			BufferedImage ancientsSpellbook = plugin.getSpriteManager().getSprite(SpriteID.TAB_MAGIC_SPELLBOOK_ANCIENT_MAGICKS, 0);
			BufferedImage lunarSpellbook = plugin.getSpriteManager().getSprite(SpriteID.TAB_MAGIC_SPELLBOOK_LUNAR, 0);
			BufferedImage arceuusSpellbook = plugin.getSpriteManager().getSprite(SpriteID.TAB_MAGIC_SPELLBOOK_ARCEUUS, 0);
			BufferedImage noneSpellbook = null;

			// might be null depending on game state
			if (standardSpellbook == null || ancientsSpellbook == null || lunarSpellbook == null || arceuusSpellbook == null)
			{
				return false;
			}

			spellbookImages.add(standardSpellbook);
			spellbookImages.add(ancientsSpellbook);
			spellbookImages.add(lunarSpellbook);
			spellbookImages.add(arceuusSpellbook);
			spellbookImages.add(noneSpellbook);

			return true;
		});

	}

	@Override
	public void setupContainerPanel(JPanel containerSlotsPanel)
	{
		final GridLayout gridLayout = new GridLayout(1, 2, 3, 1);
		containerSlotsPanel.setLayout(gridLayout);

		spellbookSlot = new InventorySetupsSlot(ColorScheme.DARKER_GRAY_COLOR, InventorySetupsSlotID.SPELL_BOOK, 0);

		// add options to easily change spellbook without having to do it manually in game
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem updateToStandard = new JMenuItem("Update Slot to Standard");
		JMenuItem updateToAncient = new JMenuItem("Update Slot to Ancient");
		JMenuItem updateToLunar = new JMenuItem("Update Slot to Lunar");
		JMenuItem updateToArceuus = new JMenuItem("Update Slot to Arceuus");
		JMenuItem updateToNone = new JMenuItem("Update Slot to None");

		popupMenu.add(updateToStandard);
		popupMenu.add(updateToAncient);
		popupMenu.add(updateToLunar);
		popupMenu.add(updateToArceuus);
		popupMenu.add(updateToNone);

		for (int i = 0; i < 5; i++)
		{
			JMenuItem item = (JMenuItem)popupMenu.getComponent(i);
			final int newSpellbook = i;
			item.addActionListener(e ->
			{
				plugin.updateSpellbookInSetup(newSpellbook);
			});
		}

		spellbookSlot.setComponentPopupMenu(popupMenu);
		spellbookSlot.getImageLabel().setComponentPopupMenu(popupMenu);
		containerSlotsPanel.add(spellbookSlot);
	}

	@Override
	public void highlightSlots(List<InventorySetupsItem> currContainer, InventorySetup inventorySetup)
	{
		plugin.getClientThread().invokeLater(() ->
		{
			if (inventorySetup.getSpellBook() != 4 && inventorySetup.getSpellBook() != plugin.getCurrentSpellbook())
			{
				spellbookSlot.setBackground(inventorySetup.getHighlightColor());
			}
			else
			{
				resetSlotColors();
			}
		});
	}

	@Override
	public void updatePanelWithSetupInformation(InventorySetup setup)
	{
		/* 0 = Standard
		   1 = Ancient
		   2 = Lunar
		   3 = Arceuus */
		String spellbookStr = "";
		switch (setup.getSpellBook())
		{
			case 0:
				spellbookStr = "Standard";
				break;
			case 1:
				spellbookStr = "Ancient";
				break;
			case 2:
				spellbookStr = "Lunar";
				break;
			case 3:
				spellbookStr = "Arceuus";
				break;
			case 4:
				spellbookStr = "None";
				break;
			default:
				spellbookStr = "Incorrect";
				break;
		}

		spellbookSlot.setImageLabel(spellbookStr + " Spellbook", spellbookImages.get(setup.getSpellBook()));

		validate();
		repaint();
	}

	@Override
	public void resetSlotColors()
	{
		spellbookSlot.setBackground(ColorScheme.DARKER_GRAY_COLOR);
	}

	public boolean isStackCompareForSlotAllowed(final int id)
	{
		return false;
	}

}
