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
package net.runelite.client.plugins.microbot.inventorysetups.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FontMetrics;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import net.runelite.client.ui.ColorScheme;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class InventorySetupsSelectionPanel
{
	private final JList<String> list;
	private ActionListener okEvent, cancelEvent;
	private final JDialog dialog;

	public InventorySetupsSelectionPanel(JPanel parent, String title, String message, String[] options)
	{
		this.list = new JList<>(options);
		this.list.setBackground(ColorScheme.DARKER_GRAY_HOVER_COLOR);
		JLabel label = new JLabel(message);
		JLabel ctrlClickLabel = new JLabel("Ctrl + Click to select multiple");
		JPanel topLabels = new JPanel(new BorderLayout());
		label.setHorizontalAlignment(SwingConstants.CENTER);
		ctrlClickLabel.setHorizontalAlignment(SwingConstants.CENTER);
		topLabels.add(label, BorderLayout.NORTH);
		topLabels.add(ctrlClickLabel, BorderLayout.CENTER);

		JButton okButton = new JButton("Ok");
		okButton.addActionListener(this::handleOkButtonClick);

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this::handleCancelButtonClick);

		// Center the list elements
		DefaultListCellRenderer renderer = (DefaultListCellRenderer) list.getCellRenderer();
		renderer.setHorizontalAlignment(SwingConstants.CENTER);

		JPanel panel = new JPanel(new BorderLayout(5, 5));
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(list);
		panel.add(topLabels, BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.CENTER);

		// Compute width and height
		// width is based on longest option, with a maximum
		FontMetrics metrics = new FontMetrics(list.getFont())
		{

		} ;
		String longestWidthString = Arrays.stream(options).max(Comparator.comparingInt(s -> (int)Math.ceil(metrics.getStringBounds(s, null).getWidth()))).get();

		Rectangle2D bounds = metrics.getStringBounds(longestWidthString, null);
		list.setFixedCellHeight((int)Math.ceil(bounds.getHeight() + 1));
		int widthInPixels = (int)Math.ceil(bounds.getWidth()) + 25;
		int max_char_height = metrics.getMaxAscent() + metrics.getMaxDescent();
		int heightInPixels = max_char_height * options.length + 50;

		int maxHeight = 400;
		int maxWidth = 500;
		panel.setPreferredSize(new Dimension(Math.min(widthInPixels, maxWidth), Math.min(heightInPixels, maxHeight)));

		JOptionPane optionPane = new JOptionPane(panel);
		optionPane.setOptions(new Object[]{okButton, cancelButton});

		dialog = optionPane.createDialog(parent, "Select option");
		dialog.setIconImage(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE));
		dialog.setTitle(title);
	}

	public void setOnOk(ActionListener event)
	{
		okEvent = event;
	}

	public void setOnClose(ActionListener event)
	{
		cancelEvent  = event;
	}

	private void handleOkButtonClick(ActionEvent e)
	{
		if (okEvent != null)
		{
			okEvent.actionPerformed(e);
		}
		hide();
	}

	private void handleCancelButtonClick(ActionEvent e)
	{
		if (cancelEvent != null)
		{
			cancelEvent.actionPerformed(e);
		}
		hide();
	}

	public void show()
	{
		dialog.setVisible(true);
	}

	private void hide()
	{
		dialog.setVisible(false);
	}

	public List<String> getSelectedItems()
	{
		return list.getSelectedValuesList();
	}
}