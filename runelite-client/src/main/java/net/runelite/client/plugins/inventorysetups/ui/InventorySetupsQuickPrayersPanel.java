package net.runelite.client.plugins.inventorysetups.ui;

import net.runelite.api.SpriteID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.inventorysetups.InventorySetup;
import net.runelite.client.plugins.inventorysetups.InventorySetupsItem;
import net.runelite.client.plugins.inventorysetups.MInventorySetupsPlugin;
import net.runelite.client.plugins.inventorysetups.QuickPrayerSetup;
import net.runelite.client.plugins.inventorysetups.InventorySetupsSlotID;
import net.runelite.client.plugins.microbot.util.prayer.Rs2PrayerEnum;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class InventorySetupsQuickPrayersPanel extends InventorySetupsContainerPanel {

    private List<InventorySetupsSlot> quickPrayerSlots;
    private List<BufferedImage> quickPrayerImages;

    InventorySetupsQuickPrayersPanel(ItemManager itemManager, MInventorySetupsPlugin plugin) {
        super(itemManager, plugin, "Quick Prayers");
        quickPrayerImages = new ArrayList<>();

        plugin.getClientThread().invokeLater(() -> {
            BufferedImage incredibleReflexes = plugin.getSpriteManager().getSprite(SpriteID.PRAYER_INCREDIBLE_REFLEXES, 0);
            BufferedImage ultimateStrength = plugin.getSpriteManager().getSprite(SpriteID.PRAYER_ULTIMATE_STRENGTH, 0);
            BufferedImage protectFromMagic = plugin.getSpriteManager().getSprite(SpriteID.PRAYER_PROTECT_FROM_MAGIC, 0);
            BufferedImage protectFromMelee = plugin.getSpriteManager().getSprite(SpriteID.PRAYER_PROTECT_FROM_MELEE, 0);
            BufferedImage protectFromMissiles = plugin.getSpriteManager().getSprite(SpriteID.PRAYER_PROTECT_FROM_MISSILES, 0);
            BufferedImage augury = plugin.getSpriteManager().getSprite(SpriteID.PRAYER_AUGURY, 0);
            BufferedImage rigour = plugin.getSpriteManager().getSprite(SpriteID.PRAYER_RIGOUR, 0);
            BufferedImage piety = plugin.getSpriteManager().getSprite(SpriteID.PRAYER_PIETY, 0);
            BufferedImage chivalry = plugin.getSpriteManager().getSprite(SpriteID.PRAYER_CHIVALRY, 0);
            BufferedImage noPrayer = null;

            // Debug statements to verify image loading
            System.out.println("Incredible Reflexes Image: " + (incredibleReflexes != null));
            System.out.println("Ultimate Strength Image: " + (ultimateStrength != null));
            System.out.println("Protect From Magic Image: " + (protectFromMagic != null));
            System.out.println("Protect From Melee Image: " + (protectFromMelee != null));
            System.out.println("Protect From Missiles Image: " + (protectFromMissiles != null));
            System.out.println("Augury Image: " + (augury != null));
            System.out.println("Rigour Image: " + (rigour != null));
            System.out.println("Piety Image: " + (piety != null));
            System.out.println("Chivalry Image: " + (chivalry != null));

            // Add images to quickPrayerImages list, fallback to noPrayer if any image is null
            quickPrayerImages.add(incredibleReflexes);
            quickPrayerImages.add(ultimateStrength);
            quickPrayerImages.add(protectFromMagic);
            quickPrayerImages.add(protectFromMelee);
            quickPrayerImages.add(protectFromMissiles);
            quickPrayerImages.add(augury);
            quickPrayerImages.add(rigour);
            quickPrayerImages.add(piety);
            quickPrayerImages.add(chivalry);
            quickPrayerImages.add(noPrayer);

            return true;
        });

    }

    private void setupQuickPrayerSlots() {
        if (quickPrayerSlots == null) {
            quickPrayerSlots = new ArrayList<>();
        }

        for (int i = 0; i < 4; i++) {
            final InventorySetupsSlot newSlot = new InventorySetupsSlot(ColorScheme.DARKER_GRAY_COLOR, InventorySetupsSlotID.QUICK_PRAYERS, i);
            quickPrayerSlots.add(newSlot);
        }
    }


    @Override
    public void setupContainerPanel(JPanel containerSlotsPanel) {
       if (quickPrayerSlots == null || quickPrayerSlots.isEmpty()) {
           setupQuickPrayerSlots();
        }
        containerSlotsPanel.setLayout(new GridLayout(1, 4, 1, 1));

        String[] prayerNames = {
                "Incredible Reflexes", "Ultimate Strength", "Protect from Magic",
                "Protect from Melee", "Protect from Missiles", "Augury",
                "Rigour", "Piety", "Chivalry", "No Prayer"
        };

        Rs2PrayerEnum[] prayerEnums = {
                Rs2PrayerEnum.INCREDIBLE_REFLEXES, Rs2PrayerEnum.ULTIMATE_STRENGTH, Rs2PrayerEnum.PROTECT_MAGIC,
                Rs2PrayerEnum.PROTECT_MELEE, Rs2PrayerEnum.PROTECT_RANGE, Rs2PrayerEnum.AUGURY,
                Rs2PrayerEnum.RIGOUR, Rs2PrayerEnum.PIETY, Rs2PrayerEnum.CHIVALRY, null
        };

        for (int i = 0; i < quickPrayerSlots.size(); i++) {
            InventorySetupsSlot slot = quickPrayerSlots.get(i);

            // Create popup menu
            JPopupMenu popupMenu = new JPopupMenu();

            for (int j = 0; j < prayerNames.length; j++) {
                JMenuItem menuItem = new JMenuItem("Update Slot to " + prayerNames[j]);
                final Rs2PrayerEnum selectedPrayer = prayerEnums[j];
                final int slotIndex = i;  // Create a final copy of the index
                menuItem.addActionListener(e -> {
                    // Call the method to update the prayer for this slot
                    System.out.println("Updating Slot " + slotIndex + " to :" + selectedPrayer);
                    plugin.updateQuickPrayerInSetup(slotIndex, selectedPrayer);
                    updatePanelWithSetupInformation(plugin.getPanel().getCurrentSelectedSetup());
                });
                popupMenu.add(menuItem);
            }

            slot.setComponentPopupMenu(popupMenu);
            slot.getImageLabel().setComponentPopupMenu(popupMenu);
            containerSlotsPanel.add(slot);
            // Debugging
            System.out.println("Adding slot: " + i + " to container panel.");
        }
    }

    @Override
    public void highlightSlots(List<InventorySetupsItem> currContainer, InventorySetup inventorySetup) {
        // This method is required to be implemented but is not used in this panel.
        // We leave it empty as quick prayers don't have highlighting logic.
    }

    @Override
    public void updatePanelWithSetupInformation(InventorySetup setup) {
        final List<QuickPrayerSetup> setupQuickPrayers = setup.getQuickPrayers();

        SwingUtilities.invokeLater(() -> {
           int j = 0;
            for (final QuickPrayerSetup quickPrayerSetup : setupQuickPrayers) {
                if (j >= 4) break;  // Ensure we do not go beyond the 4 slots
                this.setQuickPrayerSlotImageAndText(quickPrayerSlots.get(j), quickPrayerSetup.getPrayer());
                j++;
            }

            // Remove images and tool tips for the slots not used
            for (int i = j; i < 4; i++) {
                this.clearQuickPrayerSlot(quickPrayerSlots.get(i)); // Use a separate method to clear the slot
            }

            // Ensure the panel is revalidated and repainted
            for (final QuickPrayerSetup quickPrayerSetup : setupQuickPrayers) {
                if (j >= 4) break;  // Ensure we do not go beyond the 4 slots
                this.setQuickPrayerSlotImageAndText(quickPrayerSlots.get(j), quickPrayerSetup.getPrayer());
                j++;
            }


            this.revalidate();
            this.repaint();
        });
    }

    private void clearQuickPrayerSlot(InventorySetupsSlot slot) {
        setSlotImageIfDifferent(slot, "No Prayer", quickPrayerImages.get(9)); // Use placeholder for no prayer
    }

    private void setQuickPrayerSlotImageAndText(InventorySetupsSlot slot, Rs2PrayerEnum prayer) {
        BufferedImage image = null;
        String tooltip = "";

        if (prayer == null) {
            image = quickPrayerImages.get(9); // Assuming the placeholder for no prayer
            tooltip = "No Prayer";
        } else {
            switch (prayer) {
                case INCREDIBLE_REFLEXES:
                    image = quickPrayerImages.get(0);
                    tooltip = "Incredible Reflexes";
                    break;
                case ULTIMATE_STRENGTH:
                    image = quickPrayerImages.get(1);
                    tooltip = "Ultimate Strength";
                    break;
                case PROTECT_MAGIC:
                    image = quickPrayerImages.get(2);
                    tooltip = "Protect from Magic";
                    break;
                case PROTECT_MELEE:
                    image = quickPrayerImages.get(3);
                    tooltip = "Protect from Melee";
                    break;
                case PROTECT_RANGE:
                    image = quickPrayerImages.get(4);
                    tooltip = "Protect from Missiles";
                    break;
                case AUGURY:
                    image = quickPrayerImages.get(5);
                    tooltip = "Augury";
                    break;
                case RIGOUR:
                    image = quickPrayerImages.get(6);
                    tooltip = "Rigour";
                    break;
                case PIETY:
                    image = quickPrayerImages.get(7);
                    tooltip = "Piety";
                    break;
                case CHIVALRY:
                    image = quickPrayerImages.get(8);
                    tooltip = "Chivalry";
                    break;
            }
        }

        // Debug logging
        System.out.println("Setting slot image: " + tooltip + " for prayer: " + (prayer != null ? prayer.name() : "None"));
        System.out.println("Image is null: " + (image == null));

        // Set the image and tooltip
        setSlotImageIfDifferent(slot, tooltip, image);
    }


    private void setSlotImageIfDifferent(InventorySetupsSlot slot, String tooltip, BufferedImage image) {
        if (!tooltip.equals(slot.getImageLabel().getToolTipText()) || !isSameImage(slot.getImageLabel().getIcon(), image)) {
            slot.setImageLabel(tooltip,  image);
        }
    }

    private boolean isSameImage(Icon icon, BufferedImage image) {
        if (icon instanceof ImageIcon) {
            return ((ImageIcon) icon).getImage().equals(image);
        }
        return false;
    }



    @Override
    public void resetSlotColors() {
        // Not needed
    }

    public boolean isStackCompareForSlotAllowed(final int id) {
        return false;
    }
}
