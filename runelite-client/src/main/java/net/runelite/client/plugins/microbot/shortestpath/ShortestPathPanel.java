package net.runelite.client.plugins.microbot.shortestpath;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.walker.enums.*;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.plugins.microbot.shortestpath.components.ComboBoxListRenderer;

import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ShortestPathPanel extends PluginPanel {

    private final ShortestPathPlugin plugin;

    private JTextField xField, yField, zField;
    private JComboBox<BankLocation> bankComboBox;
    private JComboBox<SlayerMasters> slayerMasterComboBox;
    private JComboBox<Farming> farmingComboBox;
    private JComboBox<Allotments> allotmentsComboBox;
    private JComboBox<Bushes> bushesComboBox;
    private JComboBox<FruitTrees> fruitTreesComboBox;
    private JComboBox<Herbs> herbsComboBox;
    private JComboBox<Hops> hopsComboBox;
    private JComboBox<Trees> treesComboBox;
    private JComboBox<CompostBins> compostBinsComboBox;

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

    private Border createCenteredTitledBorder(String title, String iconPath) {
        BufferedImage icon = ImageUtil.loadImageResource(getClass(), iconPath);
        ImageIcon imageIcon = new ImageIcon(icon);

        JLabel titleLabel = new JLabel("<html><b>" + title + "</b></html>", imageIcon, JLabel.CENTER);
        titleLabel.setHorizontalTextPosition(JLabel.RIGHT);
        titleLabel.setVerticalTextPosition(JLabel.CENTER);

        Border emptyBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        Border lineBorder = BorderFactory.createLineBorder(Color.GRAY);

        return BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        lineBorder,
                        BorderFactory.createEmptyBorder(2, 2, 2, 2)
                ),
                new TitledBorder(emptyBorder, title, TitledBorder.CENTER, TitledBorder.TOP, null, null) {
                    @Override
                    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.translate(x + width / 2 - titleLabel.getPreferredSize().width / 2, y);
                        titleLabel.setSize(titleLabel.getPreferredSize());
                        titleLabel.paint(g2d);
                        g2d.dispose();
                    }
                }
        );
    }

    private JPanel createCustomLocationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(createCenteredTitledBorder("Travel to Custom Location", "/net/runelite/client/plugins/microbot/shortestpath/Map_link_icon.png"));

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

        startButton.addActionListener(e -> startWalking(getCustomLocation()));
        stopButton.addActionListener(e -> stopWalking());

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
        panel.setBorder(createCenteredTitledBorder("Travel to Bank", "/net/runelite/client/plugins/microbot/shortestpath/Bank_icon.png"));

        bankComboBox = new JComboBox<>(BankLocation.values());
        bankComboBox.setRenderer(new ComboBoxListRenderer());
        bankComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        bankComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, bankComboBox.getPreferredSize().height));
        ((JLabel)bankComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");

        startButton.addActionListener(e -> startWalking(getSelectedBank().getWorldPoint()));
        stopButton.addActionListener(e -> stopWalking());

        JPanel nearestBankPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton useNearestBankButton = new JButton("Go To Nearest Bank");

        useNearestBankButton.addActionListener(e -> startWalking(Rs2Bank.getNearestBank().getWorldPoint()));
        
        nearestBankPanel.add(useNearestBankButton);

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);

        panel.add(bankComboBox);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(buttonPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 2)));
        panel.add(nearestBankPanel);

        return panel;
    }

    private JPanel createSlayerMasterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(createCenteredTitledBorder("Travel to Slayer Master", "/net/runelite/client/plugins/microbot/shortestpath/Slayer_Master_icon.png"));

        slayerMasterComboBox = new JComboBox<>(SlayerMasters.values());
        slayerMasterComboBox.setRenderer(new ComboBoxListRenderer());
        slayerMasterComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        slayerMasterComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, slayerMasterComboBox.getPreferredSize().height));
        ((JLabel)slayerMasterComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");

        startButton.addActionListener(e -> startWalking(getSelectedSlayerMaster().getWorldPoint()));
        stopButton.addActionListener(e -> stopWalking());

        JPanel turaelSkipPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton turaelSkipButton = new JButton("        Turael Skip        ");

        turaelSkipButton.addActionListener(e -> startWalking(SlayerMasters.TURAEL.getWorldPoint()));

        turaelSkipPanel.add(turaelSkipButton);

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);

        panel.add(slayerMasterComboBox);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(buttonPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 2)));
        panel.add(turaelSkipPanel);

        return panel;
    }

    private JPanel createFarmingPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(createCenteredTitledBorder("Travel to Farming Location", "/net/runelite/client/plugins/microbot/shortestpath/Farming_patch_icon.png"));

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
        compostBinsComboBox = new JComboBox<>(CompostBins.values());

        JComboBox<?>[] subComboBoxes = {allotmentsComboBox, bushesComboBox, fruitTreesComboBox, herbsComboBox, hopsComboBox, treesComboBox, compostBinsComboBox};

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
            compostBinsComboBox.setVisible(selectedFarming == Farming.COMPOST_BINS);
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton startButton = new JButton("Start");
        JButton stopButton = new JButton("Stop");

        startButton.addActionListener(e -> startWalking(getSelectedFarmingLocation()));
        stopButton.addActionListener(e -> stopWalking());

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

    public BankLocation getSelectedBank() {
        return (BankLocation) bankComboBox.getSelectedItem();
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
            case COMPOST_BINS:
                return ((CompostBins) compostBinsComboBox.getSelectedItem()).getWorldPoint();
            default:
                return null;
        }
    }

    public String getSelectedFarmingLocationName() {
        Farming selectedFarming = getSelectedFarmingCategory();
        switch (selectedFarming) {
            case ALLOTMENTS:
                return ((Allotments) allotmentsComboBox.getSelectedItem()).name();
            case BUSHES:
                return ((Bushes) bushesComboBox.getSelectedItem()).name();
            case FRUIT_TREES:
                return ((FruitTrees) fruitTreesComboBox.getSelectedItem()).name();
            case HERBS:
                return ((Herbs) herbsComboBox.getSelectedItem()).name();
            case HOPS:
                return ((Hops) hopsComboBox.getSelectedItem()).name();
            case TREES:
                return ((Trees) treesComboBox.getSelectedItem()).name();
            case COMPOST_BINS:
                return ((CompostBins) compostBinsComboBox.getSelectedItem()).name();
            default:
                return "Unknown";
        }
    }
    
    private void startWalking(WorldPoint point) {
        Microbot.log("Web walking starting. Traveling to Custom Location (" + point.getX() + ", " + point.getY() + ", " + point.getPlane() + ").");
        plugin.getShortestPathScript().setTriggerWalker(point);
    }

    private void stopWalking() {
        Microbot.log("Web walking stopping..");
        plugin.getShortestPathScript().setTriggerWalker(null);
        Rs2Walker.setTarget(null);
    }
}