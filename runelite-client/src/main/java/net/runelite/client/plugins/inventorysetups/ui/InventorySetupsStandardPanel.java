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


import net.runelite.client.plugins.inventorysetups.InventorySetup;
import net.runelite.client.plugins.inventorysetups.MInventorySetupsPlugin;
import net.runelite.client.plugins.inventorysetups.InventorySetupsSection;
import net.runelite.client.plugins.inventorysetups.InventorySetupsValidName;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static net.runelite.client.plugins.inventorysetups.MInventorySetupsPlugin.MAX_SETUP_NAME_LENGTH;

// Standard panel for inventory setups, which contains all the configuration buttons
public class InventorySetupsStandardPanel extends InventorySetupsPanel implements InventorySetupsValidName
{

	private static final int H_GAP_BTN = 4;

	private static final ImageIcon BANK_FILTER_ICON;
	private static final ImageIcon BANK_FILTER_HOVER_ICON;
	private static final ImageIcon NO_BANK_FILTER_ICON;
	private static final ImageIcon NO_BANK_FILTER_HOVER_ICON;

	private static final ImageIcon HIGHLIGHT_COLOR_ICON;
	private static final ImageIcon HIGHLIGHT_COLOR_HOVER_ICON;
	private static final ImageIcon NO_HIGHLIGHT_COLOR_ICON;
	private static final ImageIcon NO_HIGHLIGHT_COLOR_HOVER_ICON;

	private static final ImageIcon TOGGLE_HIGHLIGHT_ICON;
	private static final ImageIcon TOGGLE_HIGHLIGHT_HOVER_ICON;
	private static final ImageIcon NO_TOGGLE_HIGHLIGHT_ICON;
	private static final ImageIcon NO_TOGGLE_HIGHLIGHT_HOVER_ICON;

	private static final ImageIcon UNORDERED_HIGHLIGHT_ICON;
	private static final ImageIcon UNORDERED_HIGHLIGHT_HOVER_ICON;
	private static final ImageIcon NO_UNORDERED_HIGHLIGHT_ICON;
	private static final ImageIcon NO_UNORDERED_HIGHLIGHT_HOVER_ICON;

	private static final ImageIcon FAVORITE_ICON;
	private static final ImageIcon FAVORITE_HOVER_ICON;
	private static final ImageIcon NO_FAVORITE_ICON;
	private static final ImageIcon NO_FAVORITE_HOVER_ICON;

	private static final ImageIcon VIEW_SETUP_ICON;
	private static final ImageIcon VIEW_SETUP_HOVER_ICON;

	private static final ImageIcon DELETE_ICON;
	private static final ImageIcon DELETE_HOVER_ICON;

	private static final ImageIcon EXPORT_ICON;
	private static final ImageIcon EXPORT_HOVER_ICON;

	public static final ImageIcon DISPLAY_COLOR_ICON;
	public static final ImageIcon DISPLAY_COLOR_HOVER_ICON;

	private final JLabel bankFilterIndicator = new JLabel();
	private final JLabel highlightColorIndicator = new JLabel();
	private final JLabel unorderedHighlightIndicator = new JLabel();
	private final JLabel favoriteIndicator = new JLabel();
	private final JLabel highlightIndicator = new JLabel();
	private final JLabel viewSetupLabel = new JLabel();
	private final JLabel exportLabel = new JLabel();
	private final JLabel deleteLabel = new JLabel();

	static
	{
		final BufferedImage bankFilterImg = ImageUtil.loadImageResource(MInventorySetupsPlugin.class, "filter_icon.png");
		final BufferedImage bankFilterHover = ImageUtil.luminanceOffset(bankFilterImg, -150);
		BANK_FILTER_ICON = new ImageIcon(bankFilterImg);
		BANK_FILTER_HOVER_ICON = new ImageIcon(bankFilterHover);

		NO_BANK_FILTER_ICON = new ImageIcon(bankFilterHover);
		NO_BANK_FILTER_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(bankFilterHover, -100));

		final BufferedImage unorderedHighlightImg = ImageUtil.loadImageResource(MInventorySetupsPlugin.class, "unordered_highlight_icon.png");
		final BufferedImage unorderedHighlightHover = ImageUtil.luminanceOffset(unorderedHighlightImg, -150);
		UNORDERED_HIGHLIGHT_ICON = new ImageIcon(unorderedHighlightImg);
		UNORDERED_HIGHLIGHT_HOVER_ICON = new ImageIcon(unorderedHighlightHover);

		NO_UNORDERED_HIGHLIGHT_ICON = new ImageIcon(unorderedHighlightHover);
		NO_UNORDERED_HIGHLIGHT_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(unorderedHighlightHover, -100));

		final BufferedImage favoriteImg = ImageUtil.loadImageResource(MInventorySetupsPlugin.class, "favorite_icon.png");
		final BufferedImage favoriteHover = ImageUtil.luminanceOffset(favoriteImg, -150);
		FAVORITE_ICON = new ImageIcon(favoriteImg);
		FAVORITE_HOVER_ICON = new ImageIcon(favoriteHover);

		NO_FAVORITE_ICON = new ImageIcon(favoriteHover);
		NO_FAVORITE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(favoriteHover, -100));

		final BufferedImage highlightToggleImg = ImageUtil.loadImageResource(MInventorySetupsPlugin.class, "highlight_icon.png");
		final BufferedImage highlightToggleHover = ImageUtil.luminanceOffset(highlightToggleImg, -150);
		TOGGLE_HIGHLIGHT_ICON = new ImageIcon(highlightToggleImg);
		TOGGLE_HIGHLIGHT_HOVER_ICON = new ImageIcon(highlightToggleHover);

		NO_TOGGLE_HIGHLIGHT_ICON = new ImageIcon(highlightToggleHover);
		NO_TOGGLE_HIGHLIGHT_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(highlightToggleHover, -100));

		final BufferedImage highlightImg = ImageUtil.loadImageResource(MInventorySetupsPlugin.class, "highlight_color_icon.png");
		final BufferedImage highlightHover = ImageUtil.luminanceOffset(highlightImg, -150);
		HIGHLIGHT_COLOR_ICON = new ImageIcon(highlightImg);
		HIGHLIGHT_COLOR_HOVER_ICON = new ImageIcon(highlightHover);

		NO_HIGHLIGHT_COLOR_ICON = new ImageIcon(highlightHover);
		NO_HIGHLIGHT_COLOR_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(highlightHover, -100));

		final BufferedImage viewImg = ImageUtil.loadImageResource(MInventorySetupsPlugin.class, "visible_icon.png");
		final BufferedImage viewImgHover = ImageUtil.luminanceOffset(viewImg, -150);
		VIEW_SETUP_ICON = new ImageIcon(viewImg);
		VIEW_SETUP_HOVER_ICON = new ImageIcon(viewImgHover);

		final BufferedImage exportImg = ImageUtil.loadImageResource(MInventorySetupsPlugin.class, "export_icon.png");
		final BufferedImage exportImgHover = ImageUtil.luminanceOffset(exportImg, -150);
		EXPORT_ICON = new ImageIcon(exportImg);
		EXPORT_HOVER_ICON = new ImageIcon(exportImgHover);

		final BufferedImage deleteImg = ImageUtil.loadImageResource(MInventorySetupsPlugin.class, "delete_icon.png");
		DELETE_ICON = new ImageIcon(deleteImg);
		DELETE_HOVER_ICON = new ImageIcon(ImageUtil.luminanceOffset(deleteImg, -100));

		DISPLAY_COLOR_ICON = new ImageIcon(highlightImg);
		DISPLAY_COLOR_HOVER_ICON = new ImageIcon(highlightHover);
	}

	InventorySetupsStandardPanel(MInventorySetupsPlugin plugin, InventorySetupsPluginPanel panel, InventorySetup invSetup, InventorySetupsSection section)
	{
		this(plugin, panel, invSetup, section, true);
	}

	InventorySetupsStandardPanel(MInventorySetupsPlugin plugin, InventorySetupsPluginPanel panel, InventorySetup invSetup, InventorySetupsSection section, boolean allowEditable)
	{
		super(plugin, panel, invSetup, section, allowEditable);

		setLayout(new BorderLayout());
		setBackground(ColorScheme.DARKER_GRAY_COLOR);

		// Always allow the name actions to work for the standard panel
		JPanel nameActions = new InventorySetupsNameActions<>(invSetup, plugin, panel, this,
				popupMenu, MAX_SETUP_NAME_LENGTH,
				ColorScheme.DARKER_GRAY_COLOR, true, null);

		JPanel bottomContainer = new JPanel(new BorderLayout());
		bottomContainer.setBorder(new EmptyBorder(8, 0, 8, 0));
		bottomContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		bankFilterIndicator.setToolTipText("Enable bank filtering");
		bankFilterIndicator.setIcon(inventorySetup.isFilterBank() ? BANK_FILTER_ICON : NO_BANK_FILTER_ICON);
		bankFilterIndicator.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e))
				{
					inventorySetup.setFilterBank(!inventorySetup.isFilterBank());
					bankFilterIndicator.setToolTipText(inventorySetup.isFilterBank() ? "Disable bank filtering" : "Enable bank filtering");
					updateBankFilterLabel();
					plugin.getDataManager().updateConfig(true, false);
					panel.redrawOverviewPanel(false);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				bankFilterIndicator.setIcon(inventorySetup.isFilterBank() ? BANK_FILTER_HOVER_ICON : NO_BANK_FILTER_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				bankFilterIndicator.setIcon(inventorySetup.isFilterBank() ? BANK_FILTER_ICON : NO_BANK_FILTER_ICON);
			}
		});

		unorderedHighlightIndicator.setToolTipText("Only highlight items that are missing from the inventory and ignore order");
		unorderedHighlightIndicator.setIcon(inventorySetup.isUnorderedHighlight() ? UNORDERED_HIGHLIGHT_ICON : NO_UNORDERED_HIGHLIGHT_ICON);
		unorderedHighlightIndicator.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e))
				{
					inventorySetup.setUnorderedHighlight(!inventorySetup.isUnorderedHighlight());
					unorderedHighlightIndicator.setToolTipText(inventorySetup.isUnorderedHighlight() ? "Enable default ordered highlighting" : "Only highlight items that are missing from the inventory and ignore order");
					updateUnorderedHighlightIndicator();
					plugin.getDataManager().updateConfig(true, false);
					panel.redrawOverviewPanel(false);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				unorderedHighlightIndicator.setIcon(inventorySetup.isUnorderedHighlight() ? UNORDERED_HIGHLIGHT_HOVER_ICON : NO_UNORDERED_HIGHLIGHT_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				unorderedHighlightIndicator.setIcon(inventorySetup.isUnorderedHighlight() ? UNORDERED_HIGHLIGHT_ICON : NO_UNORDERED_HIGHLIGHT_ICON);
			}
		});

		favoriteIndicator.setToolTipText(inventorySetup.isFavorite() ? "Remove this setup from the list of favorites" : "Favorite this setup so it appears at the top of the list");
		favoriteIndicator.setIcon(inventorySetup.isFavorite() ? FAVORITE_ICON : NO_FAVORITE_ICON);
		favoriteIndicator.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				if (SwingUtilities.isLeftMouseButton(e))
				{
					inventorySetup.setFavorite(!inventorySetup.isFavorite());
					favoriteIndicator.setToolTipText(inventorySetup.isFavorite() ? "Remove this setup from the list of favorites" : "Favorite this setup so it appears at the top of the list");
					updateFavoriteIndicator();
					plugin.getDataManager().updateConfig(true, false);
					// rebuild the panel so this panel will move positions from being favorited/unfavorited
					panel.redrawOverviewPanel(false);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				favoriteIndicator.setIcon(inventorySetup.isFavorite() ? FAVORITE_HOVER_ICON : NO_FAVORITE_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				favoriteIndicator.setIcon(inventorySetup.isFavorite() ? FAVORITE_ICON : NO_FAVORITE_ICON);
			}
		});

		highlightIndicator.setToolTipText("Enable highlighting");
		highlightIndicator.setIcon(inventorySetup.isHighlightDifference() ? TOGGLE_HIGHLIGHT_ICON : NO_TOGGLE_HIGHLIGHT_ICON);
		highlightIndicator.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (SwingUtilities.isLeftMouseButton(mouseEvent))
				{
					inventorySetup.setHighlightDifference(!inventorySetup.isHighlightDifference());
					highlightIndicator.setToolTipText(inventorySetup.isHighlightDifference() ? "Disable highlighting" : "Enable highlighting");
					updateToggleHighlightLabel();
					updateHighlightColorLabel();
					plugin.getDataManager().updateConfig(true, false);
					panel.redrawOverviewPanel(false);
				}
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				highlightIndicator.setIcon(inventorySetup.isHighlightDifference() ? TOGGLE_HIGHLIGHT_HOVER_ICON : NO_TOGGLE_HIGHLIGHT_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				highlightIndicator.setIcon(inventorySetup.isHighlightDifference() ? TOGGLE_HIGHLIGHT_ICON : NO_TOGGLE_HIGHLIGHT_ICON);
			}
		});

		highlightColorIndicator.setToolTipText("Edit highlight color");
		highlightColorIndicator.setIcon(inventorySetup.isHighlightDifference() ? HIGHLIGHT_COLOR_ICON : NO_HIGHLIGHT_COLOR_ICON);
		highlightColorIndicator.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (SwingUtilities.isLeftMouseButton(mouseEvent))
				{
					plugin.openColorPicker("Choose a Highlight color", invSetup.getHighlightColor(),
							c ->
							{
								inventorySetup.setHighlightColor(c);
								updateHighlightColorLabel();
								panel.redrawOverviewPanel(false);
							}
					);
				}
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				highlightColorIndicator.setIcon(inventorySetup.isHighlightDifference() ? HIGHLIGHT_COLOR_HOVER_ICON : NO_HIGHLIGHT_COLOR_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				highlightColorIndicator.setIcon(inventorySetup.isHighlightDifference() ? HIGHLIGHT_COLOR_ICON : NO_HIGHLIGHT_COLOR_ICON);
			}
		});

		JPanel leftActions = new JPanel(new FlowLayout(FlowLayout.LEFT, H_GAP_BTN, 0));
		leftActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		leftActions.add(bankFilterIndicator);
		leftActions.add(unorderedHighlightIndicator);
		leftActions.add(highlightIndicator);
		leftActions.add(highlightColorIndicator);
		leftActions.add(favoriteIndicator);

		viewSetupLabel.setToolTipText("View setup");
		viewSetupLabel.setIcon(VIEW_SETUP_ICON);
		viewSetupLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (SwingUtilities.isLeftMouseButton(mouseEvent))
				{
					panel.setCurrentInventorySetup(inventorySetup, true);
				}
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				viewSetupLabel.setIcon(VIEW_SETUP_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				viewSetupLabel.setIcon(VIEW_SETUP_ICON);
			}
		});

		exportLabel.setToolTipText("Export setup");
		exportLabel.setIcon(EXPORT_ICON);
		exportLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (SwingUtilities.isLeftMouseButton(mouseEvent))
				{
					plugin.exportSetup(inventorySetup);
				}
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				exportLabel.setIcon(EXPORT_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				exportLabel.setIcon(EXPORT_ICON);
			}
		});

		deleteLabel.setToolTipText("Delete setup");
		deleteLabel.setIcon(DELETE_ICON);
		deleteLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (SwingUtilities.isLeftMouseButton(mouseEvent))
				{
					plugin.removeInventorySetup(inventorySetup);
				}
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				deleteLabel.setIcon(DELETE_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				deleteLabel.setIcon(DELETE_ICON);
			}
		});

		JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, H_GAP_BTN, 0));
		rightActions.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		rightActions.add(viewSetupLabel);
		rightActions.add(exportLabel);
		rightActions.add(deleteLabel);

		bottomContainer.add(leftActions, BorderLayout.WEST);
		bottomContainer.add(rightActions, BorderLayout.EAST);

		add(nameActions, BorderLayout.NORTH);
		add(bottomContainer, BorderLayout.CENTER);

		updateHighlightColorLabel();
		updateToggleHighlightLabel();
		updateFavoriteIndicator();

	}

	@Override
	public boolean isNameValid(final String name)
	{
		return !name.isEmpty() &&
				!plugin.getCache().getInventorySetupNames().containsKey(name) &&
				!inventorySetup.getName().equals(name);
	}

	@Override
	public void updateName(final String newName)
	{
		plugin.updateSetupName(inventorySetup, newName);

	}

	private void updateHighlightColorLabel()
	{
		Color color = inventorySetup.getHighlightColor();
		highlightColorIndicator.setBorder(new MatteBorder(0, 0, 3, 0, color));
		highlightColorIndicator.setIcon(inventorySetup.isHighlightDifference() ? HIGHLIGHT_COLOR_ICON : NO_HIGHLIGHT_COLOR_ICON);
	}

	private void updateBankFilterLabel()
	{
		bankFilterIndicator.setIcon(inventorySetup.isFilterBank() ? BANK_FILTER_ICON : NO_BANK_FILTER_ICON);
	}

	private void updateUnorderedHighlightIndicator()
	{
		unorderedHighlightIndicator.setIcon(inventorySetup.isUnorderedHighlight() ? UNORDERED_HIGHLIGHT_ICON : NO_UNORDERED_HIGHLIGHT_ICON);
	}

	private void updateFavoriteIndicator()
	{
		favoriteIndicator.setIcon(inventorySetup.isFavorite() ? FAVORITE_ICON : NO_FAVORITE_ICON);
	}

	private void updateToggleHighlightLabel()
	{
		highlightIndicator.setIcon(inventorySetup.isHighlightDifference() ? TOGGLE_HIGHLIGHT_ICON : NO_TOGGLE_HIGHLIGHT_ICON);
	}

}
