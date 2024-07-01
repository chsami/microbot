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


import lombok.Setter;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.inventorysetups.InventorySetup;
import net.runelite.client.plugins.inventorysetups.InventorySetupsItem;
import net.runelite.client.plugins.inventorysetups.MInventorySetupsPlugin;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;

public class InventorySetupsNotesPanel extends InventorySetupsContainerPanel
{

	private JTextArea notesEditor;
	private UndoManager undoRedo;

	@Setter
	private InventorySetup currentInventorySetup;

	InventorySetupsNotesPanel(ItemManager itemManager, MInventorySetupsPlugin plugin)
	{
		super(itemManager, plugin, "Notes");
	}

	@Override
	public void setupContainerPanel(JPanel containerSlotsPanel)
	{
		this.notesEditor = new JTextArea(10, 0);
		this.undoRedo = new UndoManager();
		this.currentInventorySetup = null;

		notesEditor.setTabSize(2);
		notesEditor.setLineWrap(true);
		notesEditor.setWrapStyleWord(true);
		notesEditor.setOpaque(false);

		// setting the limit to a 500 as UndoManager registers every key press,
		// which means that be default we would be able to undo only a sentence.
		// note: the default limit is 100
		undoRedo.setLimit(500);
		notesEditor.getDocument().addUndoableEditListener(e -> undoRedo.addEdit(e.getEdit()));
		notesEditor.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
		notesEditor.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");

		notesEditor.getActionMap().put("Undo", new AbstractAction("Undo")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					if (undoRedo.canUndo())
					{
						undoRedo.undo();
					}
				}
				catch (CannotUndoException ex)
				{
				}
			}
		});

		notesEditor.getActionMap().put("Redo", new AbstractAction("Redo")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					if (undoRedo.canRedo())
					{
						undoRedo.redo();
					}
				}
				catch (CannotUndoException ex)
				{
				}
			}
		});

		notesEditor.addFocusListener(new FocusListener()
		{

			@Override
			public void focusGained(FocusEvent e)
			{

			}

			@Override
			public void focusLost(FocusEvent e)
			{
				notesChanged(getNotes());
			}

			private void notesChanged(String data)
			{
				plugin.updateNotesInSetup(currentInventorySetup, data);
			}
		});

		int width = 46 * 4 + 3;
		containerSlotsPanel.setLayout(new BorderLayout());
		containerSlotsPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		containerSlotsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		notesEditor.setSize(new Dimension(width, 200));
		containerSlotsPanel.add(notesEditor);
	}

	@Override
	public void highlightSlots(List<InventorySetupsItem> currContainer, InventorySetup inventorySetup)
	{
	}

	@Override
	public void updatePanelWithSetupInformation(InventorySetup setup)
	{
		// Set the caret to not update right before setting the text
		// this stops it from scrolling down the parent scroll pane
		DefaultCaret caret = (DefaultCaret)notesEditor.getCaret();
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		notesEditor.setText(setup.getNotes());
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		currentInventorySetup = setup;
	}

	@Override
	public void resetSlotColors()
	{
	}

	public String getNotes()
	{
		try
		{
			Document doc = notesEditor.getDocument();
			return notesEditor.getDocument().getText(0, doc.getLength());
		}
		catch (BadLocationException ex)
		{
		}

		return "getNotes() Failed";
	}

	public boolean isStackCompareForSlotAllowed(final int id)
	{
		return false;
	}
}
