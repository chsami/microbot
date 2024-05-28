/*
 * Copyright (c) 2022, dillydill123 <https://github.com/dillydill123>
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


import net.runelite.client.plugins.inventorysetups.MInventorySetupsPlugin;
import net.runelite.client.plugins.inventorysetups.InventorySetupsSortingID;

import javax.swing.*;

public class InventorySetupsMoveMenu<T> extends JPopupMenu
{
	private final InventorySetupsPluginPanel panel;
	private final MInventorySetupsPlugin plugin;

	public InventorySetupsMoveMenu(final MInventorySetupsPlugin plugin, final InventorySetupsPluginPanel panel, InventorySetupsMoveHandler<T> moveHandler, final String type, final T datum)
	{
		this.panel = panel;
		this.plugin = plugin;
		JMenuItem moveUp = new JMenuItem("Move " + type + " Up");
		JMenuItem moveDown = new JMenuItem("Move " + type + " Down");
		JMenuItem moveToTop = new JMenuItem("Move " + type + " to Top");
		JMenuItem moveToBottom = new JMenuItem("Move " + type + " to Bottom");
		JMenuItem moveToPosition = new JMenuItem("Move " + type + " to Position..");
		add(moveUp);
		add(moveDown);
		add(moveToTop);
		add(moveToBottom);
		add(moveToPosition);

		moveUp.addActionListener(e ->
		{
			if (!checkSortingMode())
			{
				return;
			}
			moveHandler.moveUp(datum);
		});

		moveDown.addActionListener(e ->
		{
			if (!checkSortingMode())
			{
				return;
			}
			moveHandler.moveDown(datum);
		});

		moveToTop.addActionListener(e ->
		{
			if (!checkSortingMode())
			{
				return;
			}
			moveHandler.moveToTop(datum);
		});
		moveToBottom.addActionListener(e ->
		{
			if (!checkSortingMode())
			{
				return;
			}
			moveHandler.moveToBottom(datum);
		});
		moveToPosition.addActionListener(e ->
		{
			if (!checkSortingMode())
			{
				return;
			}
			moveHandler.moveToPosition(datum);
		});

	}

	private boolean checkSortingMode()
	{
		if (plugin.getConfig().sortingMode() != InventorySetupsSortingID.DEFAULT)
		{
			JOptionPane.showMessageDialog(panel,
					"You cannot move this while a sorting mode is enabled.",
					"Move Failed",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		return true;
	}

}
