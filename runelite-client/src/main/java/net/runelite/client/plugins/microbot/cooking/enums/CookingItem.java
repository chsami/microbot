package net.runelite.client.plugins.microbot.cooking.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

@Getter
@RequiredArgsConstructor
public enum CookingItem {

    RAW_MANTA_RAY("raw manta ray", ItemID.RAW_MANTA_RAY, 91, "manta ray", ItemID.MANTA_RAY, "burnt manta ray", ItemID.BURNT_MANTA_RAY, CookingAreaType.BOTH),
    RAW_DARK_CRAB("raw dark crab", ItemID.RAW_DARK_CRAB, 90, "dark crab", ItemID.DARK_CRAB, "burnt dark crab", ItemID.BURNT_DARK_CRAB, CookingAreaType.BOTH),
    RAW_ANGLERFISH("raw anglerfish", ItemID.RAW_ANGLERFISH, 84, "anglerfish", ItemID.ANGLERFISH, "burnt anglerfish", ItemID.BURNT_ANGLERFISH, CookingAreaType.BOTH),
    RAW_SEA_TURTLE("raw sea turtle", ItemID.RAW_SEA_TURTLE, 82, "sea turtle", ItemID.SEA_TURTLE, "burnt sea turtle", ItemID.BURNT_SEA_TURTLE, CookingAreaType.BOTH),
    RAW_SHARK("raw shark", ItemID.RAW_SHARK, 80, "shark", ItemID.SHARK, "burnt shark", ItemID.BURNT_SHARK, CookingAreaType.BOTH),
    RAW_MONKFISH("raw monkfish", ItemID.RAW_MONKFISH, 62, "monkfish", ItemID.MONKFISH, "burnt monkfish", ItemID.BURNT_MONKFISH, CookingAreaType.BOTH),
    RAW_SWORDFISH("raw swordfish", ItemID.RAW_SWORDFISH, 45, "swordfish", ItemID.SWORDFISH, "burnt swordfish", ItemID.BURNT_SWORDFISH, CookingAreaType.BOTH),
    RAW_BASS("raw bass", ItemID.RAW_BASS, 43, "bass", ItemID.BASS, "burnt bass", ItemID.BURNT_FISH, CookingAreaType.BOTH),
    RAW_LOBSTER("raw lobster", ItemID.RAW_LOBSTER, 40, "lobster", ItemID.LOBSTER, "burnt lobster", ItemID.BURNT_LOBSTER, CookingAreaType.BOTH),
    UNCOOKED_PIZZA("uncooked pizza", ItemID.UNCOOKED_PIZZA, 35, "plain pizza", ItemID.PLAIN_PIZZA, "burnt pizza", ItemID.BURNT_PIZZA, CookingAreaType.RANGE),
    RAW_KARAMBWAN("raw karambwan", ItemID.RAW_KARAMBWAN, 30, "cooked karambwan", ItemID.COOKED_KARAMBWAN, "burnt karambwan", ItemID.BURNT_KARAMBWAN, CookingAreaType.BOTH),
    RAW_TUNA("raw tuna", ItemID.RAW_TUNA, 30, "tuna", ItemID.TUNA, "burnt tuna", ItemID.BURNT_FISH, CookingAreaType.BOTH),
    RAW_SALMON("raw salmon", ItemID.RAW_SALMON, 25, "salmon", ItemID.SALMON, "burnt salmon", ItemID.BURNT_FISH, CookingAreaType.BOTH),
    RAW_PIKE("raw pike", ItemID.RAW_PIKE, 20, "pike", ItemID.PIKE, "burnt pike", ItemID.BURNT_FISH, CookingAreaType.BOTH),
    RAW_COD("raw cod", ItemID.RAW_COD, 18, "cod", ItemID.COD, "burnt cod", ItemID.BURNT_FISH, CookingAreaType.BOTH),
    RAW_TROUT("raw trout", ItemID.RAW_TROUT, 15, "trout", ItemID.TROUT, "burnt trout", ItemID.BURNT_FISH, CookingAreaType.BOTH),
    RAW_SHRIMP("raw shrimps", ItemID.RAW_SHRIMPS, 1, "shrimps", ItemID.SHRIMPS, "burnt shrimps", ItemID.BURNT_SHRIMP, CookingAreaType.BOTH),
    RAW_CHICKEN("raw chicken", ItemID.RAW_CHICKEN, 1, "chicken", ItemID.CHICKEN, "burnt chicken", ItemID.BURNT_CHICKEN, CookingAreaType.BOTH),
    RAW_ANCHOVIES("raw anchovies", ItemID.RAW_ANCHOVIES, 1, "anchovies", ItemID.ANCHOVIES, "burnt anchovies", ItemID.BURNT_FISH, CookingAreaType.BOTH),
    RAW_SARDINE("raw sardine", ItemID.RAW_SARDINE, 1, "sardine", ItemID.SARDINE, "burnt sardine", ItemID.BURNT_FISH, CookingAreaType.BOTH);

    private final String rawItemName;
    private final int rawItemID;
    private final int levelRequired;
    private final String cookedItemName;
    private final int cookedItemID;
    private final String burntItemName;
    private final int burntItemID;
    private final CookingAreaType cookingAreaType;

    private boolean hasLevelRequired() {
        return Rs2Player.getSkillRequirement(Skill.COOKING, this.getLevelRequired(), false);
    }

    public boolean hasRequirements() {
        switch (this) {
            case RAW_COD:
            case RAW_KARAMBWAN:
            case RAW_BASS:
            case RAW_MONKFISH:
            case RAW_SHARK:
            case RAW_SEA_TURTLE:
            case RAW_DARK_CRAB:
            case RAW_MANTA_RAY:
                return hasLevelRequired() && Rs2Player.isMember();
            default:
                return true;
        }
    }
}
