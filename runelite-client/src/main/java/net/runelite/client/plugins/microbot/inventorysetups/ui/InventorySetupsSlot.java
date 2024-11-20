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
package net.runelite.client.plugins.microbot.inventorysetups.ui;

import lombok.Getter;
import lombok.Setter;
import net.runelite.client.plugins.microbot.inventorysetups.InventorySetup;
import net.runelite.client.plugins.microbot.inventorysetups.InventorySetupsSlotID;
import net.runelite.client.plugins.microbot.inventorysetups.InventorySetupsStackCompareID;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.AsyncBufferedImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class InventorySetupsSlot extends JPanel
{
	@Getter
	private final JLabel imageLabel;

	@Getter
	private final InventorySetupsSlotID slotID;

	@Getter
	@Setter
	private InventorySetup parentSetup;

	@Getter
	private int indexInSlot;

	@Getter
	private JLabel fuzzyIndicator;

	@Getter
	private JLabel stackIndicator;

	@Getter
	private JPopupMenu rightClickMenu;

	@Getter
	private JPopupMenu shiftRightClickMenu;

	public InventorySetupsSlot(Color color, InventorySetupsSlotID id, int indexInSlot)
	{
		this(color, id, indexInSlot, 46, 42);
	}

	public InventorySetupsSlot(Color color, InventorySetupsSlotID id, int indexInSlot, int width, int height)
	{
		this.slotID = id;
		this.imageLabel = new JLabel();
		this.parentSetup = null;
		this.fuzzyIndicator = new JLabel();
		this.stackIndicator = new JLabel();
		fuzzyIndicator.setFont(FontManager.getRunescapeSmallFont());
		stackIndicator.setFont(FontManager.getRunescapeSmallFont());
		this.indexInSlot = indexInSlot;
		this.rightClickMenu = new JPopupMenu();
		this.shiftRightClickMenu = new JPopupMenu();

		MouseAdapter menuAdapter = new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				showPopupMenu(e);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				showPopupMenu(e);
			}

			private void showPopupMenu(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					if ((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0)
					{
						shiftRightClickMenu.show(e.getComponent(), e.getX(), e.getY());
					}
					else
					{
						rightClickMenu.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			}
		};
		this.addMouseListener(menuAdapter);
		this.imageLabel.addMouseListener(menuAdapter);

		setPreferredSize(new Dimension(width, height));
		setBackground(color);
		setLayout(new GridBagLayout());
		// Set constraints to put it in the north east (top right)
		GridBagConstraints fuzzyConstraints = new GridBagConstraints(0, 0, 1, 1, 1, 1,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 0, 0);
		// Set constraints for the bottom right
		GridBagConstraints stackConstraints = new GridBagConstraints(0, 0, 1, 1, 1, 1,
				GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 0, 0);
		add(imageLabel);
		add(fuzzyIndicator, fuzzyConstraints);
		add(stackIndicator, stackConstraints);
	}

	public void setImageLabel(String toolTip, BufferedImage itemImage, boolean isFuzzy, InventorySetupsStackCompareID stackCompare)
	{
		if (itemImage == null || toolTip == null)
		{
			imageLabel.setToolTipText("");
			imageLabel.setIcon(null);
			imageLabel.revalidate();
		}
		else
		{
			imageLabel.setToolTipText(toolTip);
			if (itemImage instanceof AsyncBufferedImage) // if the slot is a spellbook, use these
			{
				AsyncBufferedImage itemImageAsync = (AsyncBufferedImage)itemImage;
				itemImageAsync.addTo(imageLabel);
			}
			else
			{
				imageLabel.setIcon(new ImageIcon(itemImage));
			}

		}

		fuzzyIndicator.setText(isFuzzy ? "*" : "");
		stackIndicator.setText(InventorySetupsStackCompareID.getStringFromValue(stackCompare));

		validate();
		repaint();
	}

	public void setImageLabel(String toolTip, BufferedImage itemImage)
	{
		setImageLabel(toolTip, itemImage, false, InventorySetupsStackCompareID.None);
	}

}
