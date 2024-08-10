package net.runelite.client.plugins.microbot.util.antiban.enums;

import net.runelite.api.AnimationID;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.util.antiban.AntibanPlugin;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

public enum Category {
    COMBAT_MID("Combat/Mid") {
        @Override
        public boolean isBusy() {
            return Rs2Combat.inCombat();
        }
    },
    SKILLING_AGILITY("Skilling/Agility") {
        @Override
        public boolean isBusy() {
            return Rs2Player.isMoving();
        }
    },
    PROCESSING("Processing") {
        @Override
        public boolean isBusy() {
            return Rs2Player.isAnimating() || Microbot.isGainingExp;
        }
    },
    COLLECTING("Collecting") {
        @Override
        public boolean isBusy() {
            return Rs2Player.isMoving() || Rs2Player.isInteracting();
        }
    },
    SKILLING_CRAFTING("Skilling/Crafting") {
        @Override
        public boolean isBusy() {
            return !Rs2Antiban.isIdle();
        }
    },
    SKILLING_MAGIC("Skilling/Magic") {
        @Override
        public boolean isBusy() {
            return Rs2Player.isAnimating() || Microbot.isGainingExp;
        }
    },
    COMBAT_LOW("Combat/Low") {
        @Override
        public boolean isBusy() {
            return Rs2Combat.inCombat();
        }
    },
    SKILLING_HERBLORE("Skilling/Herblore") {
        @Override
        public boolean isBusy() {
            return !Rs2Antiban.isIdle() || Microbot.isGainingExp;
        }
    },
    SKILLING_FLETCHING("Skilling/Fletching") {
        @Override
        public boolean isBusy() {
            return !Rs2Antiban.isIdle();
        }
    },
    SKILLING_FISHING("Skilling/Fishing") {
        @Override
        public boolean isBusy() {
            return Rs2Player.isInteracting();
        }
    },
    PROCESSING_MAGIC("Processing/Magic") {
        @Override
        public boolean isBusy() {
            return Rs2Player.isAnimating() && Microbot.isGainingExp;
        }
    },
    SKILLING_COOKING("Skilling/Cooking") {
        @Override
        public boolean isBusy() {
            return AntibanPlugin.isCooking();
        }
    },
    SKILLING_FIREMAKING("Skilling/Firemaking") {
        @Override
        public boolean isBusy() {
            return Rs2Player.getPoseAnimation() == AnimationID.FIREMAKING;
        }
    },
    SKILLING_THIEVING("Skilling/Thieving") {
        @Override
        public boolean isBusy() {
            return !Rs2Player.isAnimating() || Rs2Inventory.isFull();
        }
    },
    SKILLING("Skilling") {
        @Override
        public boolean isBusy() {
            return !Rs2Player.isAnimating() || !Rs2Inventory.isFull();
        }
    },
    COLLECTING_NONE("Collecting/None") {
        @Override
        public boolean isBusy() {
            return Rs2Player.isMoving();
        }
    },
    COMBAT_HIGH("Combat/High") {
        @Override
        public boolean isBusy() {
            return Rs2Combat.inCombat();
        }
    },
    SKILLING_WOODCUTTING("Skilling/Woodcutting") {
        @Override
        public boolean isBusy() {
            return Rs2Antiban.isWoodcutting();
        }
    },
    SKILLING_MINING("Skilling/Mining") {
        @Override
        public boolean isBusy() {
            return AntibanPlugin.isMining();
        }
    },
    SKILLING_RUNECRAFT("Skilling/Runecraft") {
        @Override
        public boolean isBusy() {
            return Rs2Inventory.contains("pure essence", "rune essence", "Daeyalt essence", "Dark essence fragment", "blood essence");
        }
    },
    SKILLING_SMITHING("Skilling/Smithing") {
        /**
         * Checks if the player is busy.
         * <p>
         * TODO: This method has not been implemented correctly yet.
         * It currently only checks if the player is animating or if the inventory is full.
         * Additional conditions may need to be added to accurately determine if the player is busy.
         * Consider adding checks for:
         * <ul>
         *   <li>Whether the player is interacting with specific game objects.</li>
         *   <li>Other animations that might indicate a busy state.</li>
         *   <li>Game-specific conditions that represent the player being busy.</li>
         * </ul>
         *
         * @return {@code true} if the player is animating or the inventory is full; {@code false} otherwise.
         */
        @Override
        public boolean isBusy() {
            return !Rs2Antiban.isIdle();
        }
    },
    SKILLING_HUNTER("Skilling/Hunter") {
        /**
         * Checks if the player is busy.
         * <p>
         * TODO: This method has not been implemented correctly yet.
         * It currently only checks if the player is animating or if the inventory is full.
         * Additional conditions may need to be added to accurately determine if the player is busy.
         * Consider adding checks for:
         * <ul>
         *   <li>Whether the player is interacting with specific game objects.</li>
         *   <li>Other animations that might indicate a busy state.</li>
         *   <li>Game-specific conditions that represent the player being busy.</li>
         * </ul>
         *
         * @return {@code true} if the player is animating or the inventory is full; {@code false} otherwise.
         */
        @Override
        public boolean isBusy() {
            return !Rs2Antiban.isIdle();
        }
    },
    SKILLING_FARMING("Skilling/Farming") {
        /**
         * Checks if the player is busy.
         * <p>
         * TODO: This method has not been implemented correctly yet.
         * It currently only checks if the player is animating or if the inventory is full.
         * Additional conditions may need to be added to accurately determine if the player is busy.
         * Consider adding checks for:
         * <ul>
         *   <li>Whether the player is interacting with specific game objects.</li>
         *   <li>Other animations that might indicate a busy state.</li>
         *   <li>Game-specific conditions that represent the player being busy.</li>
         * </ul>
         *
         * @return {@code true} if the player is animating or the inventory is full; {@code false} otherwise.
         */
        @Override
        public boolean isBusy() {
            return Rs2Player.isAnimating() || Rs2Inventory.isFull();
        }
    },
    SKILLING_PRAYER("Skilling/Prayer") {
        /**
         * Checks if the player is busy.
         * <p>
         * TODO: This method has not been implemented correctly yet.
         * It currently only checks if the player is animating or if the inventory is full.
         * Additional conditions may need to be added to accurately determine if the player is busy.
         * Consider adding checks for:
         * <ul>
         *   <li>Whether the player is interacting with specific game objects.</li>
         *   <li>Other animations that might indicate a busy state.</li>
         *   <li>Game-specific conditions that represent the player being busy.</li>
         * </ul>
         *
         * @return {@code true} if the player is animating or the inventory is full; {@code false} otherwise.
         */
        @Override
        public boolean isBusy() {
            return Rs2Player.isAnimating() || Rs2Inventory.isFull();
        }
    },
    SKILLING_CONSTRUCTION("Skilling/Construction") {
        /**
         * Checks if the player is busy.
         * <p>
         * TODO: This method has not been implemented correctly yet.
         * It currently only checks if the player is animating or if the inventory is full.
         * Additional conditions may need to be added to accurately determine if the player is busy.
         * Consider adding checks for:
         * <ul>
         *   <li>Whether the player is interacting with specific game objects.</li>
         *   <li>Other animations that might indicate a busy state.</li>
         *   <li>Game-specific conditions that represent the player being busy.</li>
         * </ul>
         *
         * @return {@code true} if the player is animating or the inventory is full; {@code false} otherwise.
         */
        @Override
        public boolean isBusy() {
            return Rs2Player.isAnimating() || Rs2Inventory.isFull();
        }
    };

    private final String name;

    Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract boolean isBusy();
}
