package net.runelite.client.plugins.microbot.shortestpath;

import net.runelite.client.plugins.microbot.shortestpath.enums.*;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.ComboBoxListRenderer;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ShortestPathPanel extends PluginPanel {

    private final ShortestPathConfig config;
    private final ShortestPathPlugin plugin;

    private JComboBox<Allotments> allotmentsComboBox;
    private JComboBox<Bushes> bushesComboBox;
    private JComboBox<FruitTrees> fruitTreesComboBox;
    private JComboBox<Herbs> herbsComboBox;
    private JComboBox<Hops> hopsComboBox;
    private JComboBox<Trees> treesComboBox;

    @Inject
    private ShortestPathPanel(ShortestPathConfig config, ShortestPathPlugin plugin) {
        super();
        this.config = config;
        this.plugin = plugin;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createCustomLocationPanel());
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(createBankPanel());
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(createSlayerMasterPanel());
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(createFarmingPanel());
    }

    private TitledBorder createCenteredTitledBorder(String title) {
        TitledBorder titledBorder = BorderFactory.createTitledBorder(title);
        titledBorder.setTitleJustification(TitledBorder.CENTER);
        return titledBorder;
    }

    private JPanel createCustomLocationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(createCenteredTitledBorder("Travel to Custom Location"));

        JPanel coordinatesPanel = new JPanel(new GridLayout(2, 3, 5, 5));

        JLabel xLabel = new JLabel("X");
        JLabel yLabel = new JLabel("Y");
        JLabel zLabel = new JLabel("Z");
        xLabel.setHorizontalAlignment(SwingConstants.CENTER);
        yLabel.setHorizontalAlignment(SwingConstants.CENTER);
        zLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JTextField xField = new JTextField(String.valueOf(config.customLocationX()), 5);
        JTextField yField = new JTextField(String.valueOf(config.customLocationY()), 5);
        JTextField zField = new JTextField(String.valueOf(config.customLocationZ()), 5);

        xField.setHorizontalAlignment(JTextField.CENTER);
        yField.setHorizontalAlignment(JTextField.CENTER);
        zField.setHorizontalAlignment(JTextField.CENTER);

        coordinatesPanel.add(xLabel);
        coordinatesPanel.add(yLabel);
        coordinatesPanel.add(zLabel);
        coordinatesPanel.add(xField);
        coordinatesPanel.add(yField);
        coordinatesPanel.add(zField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");

        startButton.addActionListener(e -> {
            try {
                int x = Integer.parseInt(xField.getText());
                int y = Integer.parseInt(yField.getText());
                int z = Integer.parseInt(zField.getText());
                plugin.updateCustomLocation(x, y, z);
                config.travelToCustomLocation(true);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Please enter valid coordinates.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });

        stopButton.addActionListener(e -> config.travelToCustomLocation(false));

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);

        panel.add(coordinatesPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(buttonPanel);

        return panel;
    }

    private JPanel createBankPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(createCenteredTitledBorder("Travel to Bank"));

        JComboBox<Banks> bankComboBox = new JComboBox<>(Banks.values());
        bankComboBox.setRenderer(new ComboBoxListRenderer());
        bankComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        bankComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, bankComboBox.getPreferredSize().height));
        ((JLabel)bankComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");

        startButton.addActionListener(e -> {
            Banks selectedBank = (Banks) bankComboBox.getSelectedItem();
            config.selectedBank(selectedBank);
            config.travelToBank(true);
        });

        stopButton.addActionListener(e -> config.travelToBank(false));

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);

        panel.add(bankComboBox);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(buttonPanel);

        return panel;
    }

    private JPanel createSlayerMasterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(createCenteredTitledBorder("Travel to Slayer Master"));

        JComboBox<SlayerMasters> slayerMasterComboBox = new JComboBox<>(SlayerMasters.values());
        slayerMasterComboBox.setRenderer(new ComboBoxListRenderer());
        slayerMasterComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        slayerMasterComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, slayerMasterComboBox.getPreferredSize().height));
        ((JLabel)slayerMasterComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");

        startButton.addActionListener(e -> {
            SlayerMasters selectedSlayerMaster = (SlayerMasters) slayerMasterComboBox.getSelectedItem();
            config.selectedSlayerMaster(selectedSlayerMaster);
            config.travelToSlayerMaster(true);
        });

        stopButton.addActionListener(e -> config.travelToSlayerMaster(false));

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);

        panel.add(slayerMasterComboBox);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(buttonPanel);

        return panel;
    }

    private JPanel createFarmingPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(createCenteredTitledBorder("Travel to Farming Location"));

        JComboBox<Farming> farmingComboBox = new JComboBox<>(Farming.values());
        farmingComboBox.setRenderer(new ComboBoxListRenderer());
        farmingComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        farmingComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, farmingComboBox.getPreferredSize().height));
        ((JLabel)farmingComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        allotmentsComboBox = new JComboBox<>(Allotments.values());
        bushesComboBox = new JComboBox<>(Bushes.values());
        fruitTreesComboBox = new JComboBox<>(FruitTrees.values());
        herbsComboBox = new JComboBox<>(Herbs.values());
        hopsComboBox = new JComboBox<>(Hops.values());
        treesComboBox = new JComboBox<>(Trees.values());

        JComboBox<?>[] subComboBoxes = {allotmentsComboBox, bushesComboBox, fruitTreesComboBox, herbsComboBox, hopsComboBox, treesComboBox};

        for (JComboBox<?> comboBox : subComboBoxes) {
            comboBox.setRenderer(new ComboBoxListRenderer());
            comboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
            comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, comboBox.getPreferredSize().height));
            ((JLabel)comboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
            comboBox.setVisible(false);
        }

        farmingComboBox.addActionListener(e -> {
            Farming selectedFarming = (Farming) farmingComboBox.getSelectedItem();
            config.catFarming(selectedFarming);
            allotmentsComboBox.setVisible(selectedFarming == Farming.ALLOTMENTS);
            bushesComboBox.setVisible(selectedFarming == Farming.BUSHES);
            fruitTreesComboBox.setVisible(selectedFarming == Farming.FRUIT_TREES);
            herbsComboBox.setVisible(selectedFarming == Farming.HERBS);
            hopsComboBox.setVisible(selectedFarming == Farming.HOPS);
            treesComboBox.setVisible(selectedFarming == Farming.TREES);
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");

        startButton.addActionListener(e -> {
            Farming selectedFarming = (Farming) farmingComboBox.getSelectedItem();
            config.catFarming(selectedFarming);
            switch (selectedFarming) {
                case ALLOTMENTS:
                    config.selectedAllotment((Allotments) allotmentsComboBox.getSelectedItem());
                    break;
                case BUSHES:
                    config.selectedBush((Bushes) bushesComboBox.getSelectedItem());
                    break;
                case FRUIT_TREES:
                    config.selectedFruitTree((FruitTrees) fruitTreesComboBox.getSelectedItem());
                    break;
                case HERBS:
                    config.selectedHerb((Herbs) herbsComboBox.getSelectedItem());
                    break;
                case HOPS:
                    config.selectedHop((Hops) hopsComboBox.getSelectedItem());
                    break;
                case TREES:
                    config.selectedTree((Trees) treesComboBox.getSelectedItem());
                    break;
            }
            config.travelToFarming(true);
            plugin.handleTravelToFarmingLocation();
        });

        stopButton.addActionListener(e -> config.travelToFarming(false));

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);

        panel.add(farmingComboBox);
        for (JComboBox<?> comboBox : subComboBoxes) {
            panel.add(comboBox);
        }
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(buttonPanel);

        return panel;
    }
}