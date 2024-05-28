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


import net.runelite.client.plugins.inventorysetups.*;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

import static net.runelite.client.plugins.inventorysetups.MInventorySetupsPlugin.CONFIG_KEY_UNASSIGNED_MAXIMIZED;
import static net.runelite.client.plugins.inventorysetups.MInventorySetupsPlugin.MAX_SETUP_NAME_LENGTH;

public class InventorySetupsSectionPanel extends JPanel implements InventorySetupsValidName, InventorySetupsMoveHandler<InventorySetupsSection>
{
	protected final MInventorySetupsPlugin plugin;
	protected final InventorySetupsPluginPanel panel;
	private final InventorySetupsSection section;

	private final JPanel panelWithSetups;
	private final JLabel minMaxLabel;

	private static final ImageIcon MIN_MAX_SECTION_ICON;
	private static final ImageIcon MIN_MAX_SECTION_HOVER_ICON;
	private static final ImageIcon NO_MIN_MAX_SECTION_ICON;
	private static final ImageIcon NO_MIN_MAX_SECTION_HOVER_ICON;
	private boolean forceMaximization;
	private boolean allowEditable;

	public static final int MAX_ICONS_PER_ROW = 4;

	static
	{
		final BufferedImage minMaxSectionImg = ImageUtil.loadImageResource(MInventorySetupsPlugin.class, "down_arrow.png");
		final BufferedImage minMaxSectionHoverImg = ImageUtil.luminanceOffset(minMaxSectionImg, -150);
		MIN_MAX_SECTION_ICON = new ImageIcon(minMaxSectionImg);
		MIN_MAX_SECTION_HOVER_ICON = new ImageIcon(minMaxSectionHoverImg);

		final BufferedImage noMinMaxSectionImg = ImageUtil.loadImageResource(MInventorySetupsPlugin.class, "right_arrow.png");
		final BufferedImage noMaxSectionHoverImg = ImageUtil.luminanceOffset(noMinMaxSectionImg, -150);
		NO_MIN_MAX_SECTION_ICON = new ImageIcon(noMinMaxSectionImg);
		NO_MIN_MAX_SECTION_HOVER_ICON = new ImageIcon(noMaxSectionHoverImg);
	}

	InventorySetupsSectionPanel(MInventorySetupsPlugin plugin,
                                InventorySetupsPluginPanel panel,
                                InventorySetupsSection section,
                                boolean forceMaximization, boolean allowEdits,
                                final Set<String> setupNamesToBeDisplayed,
                                Set<String> setupsInSection,
                                final java.util.List<InventorySetup> originalFilteredSetups)
	{
		this.plugin = plugin;
		this.panel = panel;
		this.section = section;
		this.forceMaximization = forceMaximization;
		this.allowEditable = allowEdits;
		this.panelWithSetups = new JPanel();

		this.setLayout(new BorderLayout());
		setBackground(ColorScheme.DARKER_GRAY_COLOR);

		// Label that will be used to minimize or maximize setups in section
		this.minMaxLabel = new JLabel();
		updateMinMaxLabel();
		minMaxLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				maximizationRequest(mouseEvent);
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				if (forceMaximization)
				{
					return;
				}

				minMaxLabel.setIcon(section.isMaximized() ? MIN_MAX_SECTION_HOVER_ICON : NO_MIN_MAX_SECTION_HOVER_ICON);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				if (forceMaximization)
				{
					return;
				}

				minMaxLabel.setIcon(section.isMaximized() ? MIN_MAX_SECTION_ICON : NO_MIN_MAX_SECTION_ICON);
			}
		});

		// Add the right click menu to delete sections
		JPopupMenu popupMenu = new InventorySetupsMoveMenu<>(plugin, panel, this, "Section", section);
		JMenuItem exportSection = new JMenuItem("Export Section");
		JMenuItem addSetupsToSection = new JMenuItem("Add setups to section..");
		JMenuItem deleteSection = new JMenuItem("Delete Section..");
		exportSection.addActionListener(e ->
		{
			plugin.exportSection(section);
		});
		addSetupsToSection.addActionListener(e ->
		{
			final String[] setupNames = plugin.getInventorySetups().stream().map(InventorySetup::getName).toArray(String[]::new);
			Arrays.sort(setupNames, String.CASE_INSENSITIVE_ORDER);
			final String message = "Select setups to add to this section";
			final String title = "Select Setups";
			InventorySetupsSelectionPanel selectionDialog = new InventorySetupsSelectionPanel(panel, title, message, setupNames);
			selectionDialog.setOnOk(e1 ->
			{
				java.util.List<String> selectedSetups = selectionDialog.getSelectedItems();
				if (!selectedSetups.isEmpty())
				{
					plugin.addSetupsToSection(section, selectedSetups);
				}
			});
			selectionDialog.show();

		});
		deleteSection.addActionListener(e ->
		{
			plugin.removeSection(section);
		});
		popupMenu.add(addSetupsToSection);
		popupMenu.add(exportSection);
		popupMenu.add(deleteSection);

		final MouseAdapter flatTextFieldMouseAdapter = new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				maximizationRequest(mouseEvent);
			}
		};
		// Add the button to nameActions so the color border will reach it as well
		final Color nameWrapperColor = new Color(20, 20, 20);
		final InventorySetupsNameActions<InventorySetupsSection> nameActions = new InventorySetupsNameActions<>(section,
																					plugin, panel, this,
																					popupMenu, MAX_SETUP_NAME_LENGTH,
																					nameWrapperColor, allowEditable, flatTextFieldMouseAdapter);
		final JPanel westNameActions = new JPanel(new BorderLayout());
		westNameActions.setBackground(nameWrapperColor);
		westNameActions.add(Box.createRigidArea(new Dimension(6, 0)), BorderLayout.WEST);
		westNameActions.add(minMaxLabel, BorderLayout.CENTER);

		nameActions.add(westNameActions, BorderLayout.WEST);

		JPanel nameWrapper = new JPanel();
		nameWrapper.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		nameWrapper.setLayout(new BorderLayout());
		nameWrapper.add(nameActions, BorderLayout.CENTER);

		// If we are in unassigned mode, don't allow the user to edit with the right click pop menu
		if (allowEditable)
		{
			nameWrapper.setComponentPopupMenu(popupMenu);
		}

		add(nameWrapper, BorderLayout.NORTH);
		addSetups(setupNamesToBeDisplayed, setupsInSection, originalFilteredSetups, allowEditable);

	}

	private void maximizationRequest(MouseEvent e)
	{
		if (SwingUtilities.isLeftMouseButton(e))
		{
			if (allowEditable && !forceMaximization)
			{
				section.setMaximized(!section.isMaximized());
				plugin.getDataManager().updateConfig(false, true);
				panel.redrawOverviewPanel(false);
			}
			else
			{
				// This is for the unassigned section.
				plugin.setConfigValue(CONFIG_KEY_UNASSIGNED_MAXIMIZED, !section.isMaximized());
			}
		}
	}

	private void addSetups(final Set<String> setupNamesToBeDisplayed, Set<String> setupsInSection, final java.util.List<InventorySetup> originalFilteredSetups, boolean allowEditable)
	{
		// Only add the setups if it's maximized. If we are searching, force maximization.
		if (section.isMaximized() || forceMaximization)
		{
			panelWithSetups.setLayout(new GridBagLayout());
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.weightx = 1;
			constraints.gridx = 0;
			constraints.gridy = 0;

			// If it's the default sorting mode (i.e., no sorting mode, use the order of setups in the section
			// Else use the order of the passed in setups, as they will be sorted according to the sorting mode
			if (plugin.getConfig().sortingMode() == InventorySetupsSortingID.DEFAULT)
			{
				if (plugin.getConfig().panelView() == InventorySetupsPanelViewID.ICON)
				{
					java.util.List<InventorySetup> setupObjectsInSection = section.getSetups().stream().map(setupName -> plugin.getCache().getInventorySetupNames().get(setupName)).collect(Collectors.toList());
					final JPanel iconGridPanel = createIconPanelGrid(plugin, panel, setupObjectsInSection, MAX_ICONS_PER_ROW, setupNamesToBeDisplayed, section, allowEditable);
					panelWithSetups.add(iconGridPanel, constraints);
					constraints.gridy++;
				}
				else
				{
					panelWithSetups.add(Box.createRigidArea(new Dimension(0, 10)), constraints);
					constraints.gridy++;
					for (final String name : section.getSetups())
					{
						// If we are searching and the setup doesn't match the search, don't add setup
						if (!setupNamesToBeDisplayed.contains(name))
						{
							continue;
						}
						final InventorySetup setupInSection = plugin.getCache().getInventorySetupNames().get(name);
						createSetupPanelForSection(setupInSection, section, constraints, allowEditable);
					}
				}
			}
			else
			{
				// Sorting mode is being used, so use the original setup array to determine the order.
				if (plugin.getConfig().panelView() == InventorySetupsPanelViewID.ICON)
				{
					// Use the original list of setups because this contains the order of the sorting mode
					final JPanel iconGridPanel = createIconPanelGrid(plugin, panel, originalFilteredSetups, MAX_ICONS_PER_ROW, setupsInSection, section, allowEditable);
					panelWithSetups.add(iconGridPanel, constraints);
					constraints.gridy++;
				}
				else
				{
					panelWithSetups.add(Box.createRigidArea(new Dimension(0, 10)), constraints);
					constraints.gridy++;
					// Use the original list of setups because this contains the order of the sorting mode
					for (final InventorySetup setup : originalFilteredSetups)
					{
						if (!setupsInSection.contains(setup.getName()))
						{
							continue;
						}
						createSetupPanelForSection(setup, section, constraints, allowEditable);
					}
				}
			}

			add(panelWithSetups, BorderLayout.SOUTH);
		}
	}

	private void createSetupPanelForSection(final InventorySetup setupInSection, final InventorySetupsSection section, final GridBagConstraints constraints, boolean allowEditable)
	{
		final JPanel wrapperPanelForSetup = new JPanel();
		wrapperPanelForSetup.setLayout(new BorderLayout());

		InventorySetupsPanel newPanel = null;
		if (plugin.getConfig().panelView() == InventorySetupsPanelViewID.COMPACT)
		{
			newPanel = new InventorySetupsCompactPanel(plugin, panel, setupInSection, section, allowEditable);
		}
		else
		{
			newPanel = new InventorySetupsStandardPanel(plugin, panel, setupInSection, section, allowEditable);
		}

		// Add an indentation to the setup
		wrapperPanelForSetup.add(Box.createRigidArea(new Dimension(12, 0)), BorderLayout.WEST);
		wrapperPanelForSetup.add(newPanel, BorderLayout.CENTER);

		panelWithSetups.add(wrapperPanelForSetup, constraints);
		constraints.gridy++;

		panelWithSetups.add(Box.createRigidArea(new Dimension(0, 10)), constraints);
		constraints.gridy++;
	}

	public static JPanel createIconPanelGrid(final MInventorySetupsPlugin plugin, final InventorySetupsPluginPanel panel, final List<InventorySetup> setups, int maxColSize, final Set<String> whitelistedNames, final InventorySetupsSection section, boolean allowEditable)
	{
		int added = 0;
		int width = 0;
		int height = 0;
		JPanel wrapperPanel = new JPanel(new GridLayout(0, maxColSize, 5, 5));
		for (final InventorySetup setup : setups)
		{
			// if whitelistedNames is null, don't attempt to filter at all
			if (whitelistedNames != null && !whitelistedNames.contains(setup.getName()))
			{
				continue;
			}
			InventorySetupsPanel newPanel = new InventorySetupsIconPanel(plugin, panel, setup, section, allowEditable);
			width = newPanel.getWidth();
			height = newPanel.getHeight();
			wrapperPanel.add(newPanel);
			added++;
		}
		return addExtraIconSlotsAndExpansionStopper(wrapperPanel, added, maxColSize, width, height);
	}

	public static JPanel addExtraIconSlotsAndExpansionStopper(final JPanel iconGridPanel, int size, int maxColSize, int width, int height)
	{
		// Add empty slots
		for (int i = size; i % maxColSize != 0; i++)
		{
			iconGridPanel.add(new InventorySetupsSlot(ColorScheme.DARK_GRAY_COLOR, InventorySetupsSlotID.INVENTORY, -1, width, height));
		}

		// This stops the Slots in the gridlayout from expanding to fill space
		JPanel stopExpansionLayout = new JPanel(new FlowLayout());
		stopExpansionLayout.add(iconGridPanel);
		return stopExpansionLayout;
	}

	@Override
	public boolean isNameValid(final String name)
	{
		return !name.isEmpty() &&
				!plugin.getCache().getSectionNames().containsKey(name) &&
				!section.getName().equals(name);
	}

	@Override
	public void updateName(final String newName)
	{
		plugin.updateSectionName(section, newName);
	}

	private void updateMinMaxLabel()
	{
		if (forceMaximization)
		{
			minMaxLabel.setToolTipText("");
			minMaxLabel.setIcon(MIN_MAX_SECTION_ICON);
		}
		else
		{
			minMaxLabel.setToolTipText(section.isMaximized() ? "Minimize section" : "Maximize section");
			minMaxLabel.setIcon(section.isMaximized() ? MIN_MAX_SECTION_ICON : NO_MIN_MAX_SECTION_ICON);
		}
	}

	@Override
	public void moveUp(final InventorySetupsSection section)
	{
		int sectionIndex = plugin.getSections().indexOf(section);
		plugin.moveSection(sectionIndex, sectionIndex - 1);
	}

	@Override
	public void moveDown(final InventorySetupsSection section)
	{
		int sectionIndex = plugin.getSections().indexOf(section);
		plugin.moveSection(sectionIndex, sectionIndex + 1);
	}

	@Override
	public void moveToTop(final InventorySetupsSection section)
	{
		int sectionIndex = plugin.getSections().indexOf(section);
		plugin.moveSection(sectionIndex, 0);
	}

	@Override
	public void moveToBottom(final InventorySetupsSection section)
	{
		int sectionIndex = plugin.getSections().indexOf(section);
		plugin.moveSection(sectionIndex, plugin.getSections().size() - 1);
	}

	@Override
	public void moveToPosition(final InventorySetupsSection section)
	{
		int sectionIndex = plugin.getSections().indexOf(section);
		final String posDialog = "Enter a position between 1 and " + String.valueOf(plugin.getSections().size()) +
				". Current section is in position " + String.valueOf(sectionIndex + 1) + ".";
		final String newPositionStr = JOptionPane.showInputDialog(panel,
				posDialog,
				"Move Section",
				JOptionPane.PLAIN_MESSAGE);

		// cancel button was clicked
		if (newPositionStr == null)
		{
			return;
		}

		try
		{
			int newPosition = Integer.parseInt(newPositionStr);
			if (newPosition < 1 || newPosition > plugin.getSections().size())
			{
				JOptionPane.showMessageDialog(panel,
						"Invalid position.",
						"Move Section Failed",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			plugin.moveSection(sectionIndex, newPosition - 1);
		}
		catch (NumberFormatException ex)
		{
			JOptionPane.showMessageDialog(panel,
					"Invalid position.",
					"Move Section Failed",
					JOptionPane.ERROR_MESSAGE);
		}
	}

}
