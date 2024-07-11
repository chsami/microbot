package net.runelite.client.plugins.microbot.shortestpath;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.shortestpath.enums.*;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.ComboBoxListRenderer;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ShortestPathPanel extends PluginPanel {

    private final ShortestPathPlugin plugin;

    private JTextField xField, yField, zField;
    private JComboBox<Banks> bankComboBox;
    private JComboBox<SlayerMasters> slayerMasterComboBox;
    private JComboBox<Farming> farmingComboBox;
    private JComboBox<Allotments> allotmentsComboBox;
    private JComboBox<Bushes> bushesComboBox;
    private JComboBox<FruitTrees> fruitTreesComboBox;
    private JComboBox<Herbs> herbsComboBox;
    private JComboBox<Hops> hopsComboBox;
    private JComboBox<Trees> treesComboBox;

    @Inject
    private ShortestPathPanel(ShortestPathPlugin plugin) {
        super();
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

        xField = new JTextField("0", 5);
        yField = new JTextField("0", 5);
        zField = new JTextField("0", 5);

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

        startButton.addActionListener(e -> plugin.handleTravelToCustomLocation());
        stopButton.addActionListener(e -> plugin.stopTraveling());

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

        bankComboBox = new JComboBox<>(Banks.values());
        bankComboBox.setRenderer(new ComboBoxListRenderer());
        bankComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        bankComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, bankComboBox.getPreferredSize().height));
        ((JLabel)bankComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");

        startButton.addActionListener(e -> plugin.handleTravelToBank());
        stopButton.addActionListener(e -> plugin.stopTraveling());

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

        slayerMasterComboBox = new JComboBox<>(SlayerMasters.values());
        slayerMasterComboBox.setRenderer(new ComboBoxListRenderer());
        slayerMasterComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        slayerMasterComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, slayerMasterComboBox.getPreferredSize().height));
        ((JLabel)slayerMasterComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");

        startButton.addActionListener(e -> plugin.handleTravelToSlayerMaster());
        stopButton.addActionListener(e -> plugin.stopTraveling());

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

        farmingComboBox = new JComboBox<>(Farming.values());
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

        startButton.addActionListener(e -> plugin.handleTravelToFarmingLocation());
        stopButton.addActionListener(e -> plugin.stopTraveling());

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

    public WorldPoint getCustomLocation() {
        try {
            int x = Integer.parseInt(xField.getText());
            int y = Integer.parseInt(yField.getText());
            int z = Integer.parseInt(zField.getText());
            return new WorldPoint(x, y, z);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Banks getSelectedBank() {
        return (Banks) bankComboBox.getSelectedItem();
    }

    public SlayerMasters getSelectedSlayerMaster() {
        return (SlayerMasters) slayerMasterComboBox.getSelectedItem();
    }

    public Farming getSelectedFarmingCategory() {
        return (Farming) farmingComboBox.getSelectedItem();
    }

    public WorldPoint getSelectedFarmingLocation() {
        Farming selectedFarming = getSelectedFarmingCategory();
        switch (selectedFarming) {
            case ALLOTMENTS:
                return ((Allotments) allotmentsComboBox.getSelectedItem()).getWorldPoint();
            case BUSHES:
                return ((Bushes) bushesComboBox.getSelectedItem()).getWorldPoint();
            case FRUIT_TREES:
                return ((FruitTrees) fruitTreesComboBox.getSelectedItem()).getWorldPoint();
            case HERBS:
                return ((Herbs) herbsComboBox.getSelectedItem()).getWorldPoint();
            case HOPS:
                return ((Hops) hopsComboBox.getSelectedItem()).getWorldPoint();
            case TREES:
                return ((Trees) treesComboBox.getSelectedItem()).getWorldPoint();
            default:
                return null;
        }
    }
}