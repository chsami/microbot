package net.runelite.client.plugins.microbot.playerassist.skill;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.globval.enums.InterfaceTab;
import net.runelite.client.plugins.microbot.playerassist.PlayerAssistConfig;
import net.runelite.client.plugins.microbot.util.combat.Rs2Combat;
import net.runelite.client.plugins.microbot.util.tabs.Rs2Tab;

import java.util.*;
import java.util.concurrent.TimeUnit;


enum AttackStyle {
    ACCURATE("Accurate", Skill.ATTACK),
    AGGRESSIVE("Aggressive", Skill.STRENGTH),
    DEFENSIVE("Defensive", Skill.DEFENCE),
    CONTROLLED("Controlled", Skill.ATTACK, Skill.STRENGTH, Skill.DEFENCE),
    RANGING("Ranging", Skill.RANGED),
    LONGRANGE("Longrange", Skill.RANGED, Skill.DEFENCE),
    CASTING("Casting", Skill.MAGIC),
    DEFENSIVE_CASTING("Defensive Casting", Skill.MAGIC, Skill.DEFENCE),
    OTHER("Other");

    @Getter
    private final String name;
    @Getter
    private final Skill[] skills;

    AttackStyle(String name, Skill... skills) {
        this.name = name;
        this.skills = skills;
    }
}

@Slf4j
public class AttackStyleScript extends Script {

    public static int equippedWeaponTypeVarbit;
    private final Set<Skill> selectedSkills = EnumSet.noneOf(Skill.class);
    boolean initializedLevels = false;
    private AttackStyle attackStyle;
    private AttackStyle attackStyleToTrain;
    // Starting skill levels
    private int attackLevel;
    private int strengthLevel;
    private int defenceLevel;
    // Time delay to no change attack style to often
    private int attackStyleChangeDelay = 0;
    private int currentAttackStyleChangeDelayCounter = 0;

    public boolean run(PlayerAssistConfig config) {
        attackStyleChangeDelay = config.attackStyleChangeDelay();
        currentAttackStyleChangeDelayCounter = 0;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!Microbot.isLoggedIn() || !super.run() || disableIfMaxed(config.toggleDisableOnMaxCombat())) return;
            if (currentAttackStyleChangeDelayCounter-- > 0) return;

            if (!initializedLevels) {
                initializeLevels();
            }

            if (hasLeveledUp()) {
                resetLevels();
                log.info("Leveled up, resetting levels and timer.");
            }

            int attackStyleVarbit = Microbot.getVarbitPlayerValue(VarPlayer.ATTACK_STYLE);
            equippedWeaponTypeVarbit = Microbot.getVarbitValue(Varbits.EQUIPPED_WEAPON_TYPE);
            int castingModeVarbit = Microbot.getVarbitValue(Varbits.DEFENSIVE_CASTING_MODE);
            updateAttackStyle(equippedWeaponTypeVarbit, attackStyleVarbit, castingModeVarbit);
            selectSkills(config);
            WidgetInfo componentToDisplay = getComponentToDisplay(config);
            if (attackStyle != attackStyleToTrain) {
                changeAttackStyle(config, componentToDisplay);
            }
            currentAttackStyleChangeDelayCounter = attackStyleChangeDelay;
        }, 0, 1, TimeUnit.SECONDS);
        return true;
    }

    private void initializeLevels() {
        attackLevel = getSkillLevel(Skill.ATTACK);
        strengthLevel = getSkillLevel(Skill.STRENGTH);
        defenceLevel = getSkillLevel(Skill.DEFENCE);
        initializedLevels = true;
    }

    private void changeAttackStyle(PlayerAssistConfig config, WidgetInfo attackStyleWidgetInfo) {
        if (Rs2Tab.getCurrentTab() != InterfaceTab.COMBAT) {
            Rs2Tab.switchToCombatOptionsTab();
            sleepUntil(() -> Rs2Tab.getCurrentTab() == InterfaceTab.COMBAT, 2000);
        }
        log.info("Changing Attack Style to: {}", attackStyleToTrain);
        Rs2Combat.setAttackStyle(attackStyleWidgetInfo);
    }

    // has any of the skills leveled up
    private boolean hasLeveledUp() {
        return attackLevel < getSkillLevel(Skill.ATTACK) ||
                strengthLevel < getSkillLevel(Skill.STRENGTH) ||
                defenceLevel < getSkillLevel(Skill.DEFENCE);
    }

    // if we have leveled up, reset the levels and set delay to 0
    private void resetLevels() {
        attackLevel = getSkillLevel(Skill.ATTACK);
        strengthLevel = getSkillLevel(Skill.STRENGTH);
        defenceLevel = getSkillLevel(Skill.DEFENCE);
        currentAttackStyleChangeDelayCounter = 0;
    }

    private void updateSelectedSkills(boolean enabled, Skill skill) {
        if (enabled) {
            selectedSkills.add(skill);
        } else {
            selectedSkills.remove(skill);
        }
    }

    private void updateAttackStyle(int equippedWeaponType, int attackStyleIndex, int castingMode) {
        AttackStyle[] attackStyles = getWeaponTypeStyles(equippedWeaponType);
        if (attackStyleIndex < attackStyles.length) {
            attackStyleIndex = (attackStyleIndex == 4) ? attackStyleIndex + castingMode : attackStyleIndex;
            attackStyle = (attackStyles[attackStyleIndex] != null) ? attackStyles[attackStyleIndex] : AttackStyle.OTHER;
        }
    }

    private AttackStyle[] getWeaponTypeStyles(int weaponType) {
        int weaponStyleEnum = Microbot.getEnum(EnumID.WEAPON_STYLES).getIntValue(weaponType);
        int[] weaponStyleStructs = Microbot.getEnum(weaponStyleEnum).getIntVals();

        AttackStyle[] styles = new AttackStyle[weaponStyleStructs.length];
        int i = 0;
        for (int style : weaponStyleStructs) {
            String attackStyleName = Microbot.getStructComposition(style).getStringValue(ParamID.ATTACK_STYLE_NAME);
            AttackStyle attackStyle = AttackStyle.valueOf(attackStyleName.toUpperCase());

            if (attackStyle != AttackStyle.OTHER) {
                if (i == 5 && attackStyle == AttackStyle.DEFENSIVE) {
                    attackStyle = AttackStyle.DEFENSIVE_CASTING;
                }
                styles[i++] = attackStyle;
            }
        }
        return styles;
    }

    // compare Players skill levels to the level target in the config
    private boolean needLevel(int levelRequired, Skill skill) {
        if (isMaxed()) {
            log.info("Maxed, switching between any combat style.");
            return true;
        }

        log.info("Skill: {}, Level Required: {} Current Level: {}", skill, levelRequired, getSkillLevel(skill));
        return getSkillLevel(skill) < levelRequired;
    }

    private void selectSkills(PlayerAssistConfig config) {
        boolean balanceCombatSkills = config.toggleBalanceCombatSkills();

        boolean needAttack = needLevel(config.attackSkillTarget(), Skill.ATTACK);
        boolean needStrength = needLevel(config.strengthSkillTarget(), Skill.STRENGTH);
        boolean needDefence = needLevel(config.defenceSkillTarget(), Skill.DEFENCE);

        if (balanceCombatSkills) {
            Skill lowestSkill = getLowestSkill(
                    needAttack ? Skill.ATTACK : null,
                    needStrength ? Skill.STRENGTH : null,
                    needDefence ? Skill.DEFENCE : null
            );

            if (lowestSkill != null) {
                resetSelectedSkills();
                updateSelectedSkills(true, lowestSkill);
            }
        } else {
            updateSelectedSkills(needAttack, Skill.ATTACK);
            updateSelectedSkills(needStrength, Skill.STRENGTH);
            updateSelectedSkills(needDefence, Skill.DEFENCE);
        }
    }

    // reset selected skills
    private void resetSelectedSkills() {
        selectedSkills.clear();
    }

    private Skill getLowestSkill(Skill... skills) {
        Skill lowestSkill = null;
        int lowestLevel = Integer.MAX_VALUE;

        for (Skill skill : skills) {
            if (skill == null) {
                continue;
            }

            int level = getSkillLevel(skill);
            if (level < lowestLevel) {
                lowestSkill = skill;
                lowestLevel = level;
            }
        }

        // Check for multiple skills with the same lowest level
        List<Skill> lowestSkills = new ArrayList<>();
        for (Skill skill : skills) {
            if (skill != null && getSkillLevel(skill) == lowestLevel) {
                lowestSkills.add(skill);
            }
        }

        // If there are more than one skill with the lowest level, select any of them
        if (lowestSkills.size() > 1) {
            log.info("Multiple lowest skills: {}", lowestSkills);
            return lowestSkills.get(0); // or any other selection logic if needed
        }
        log.info("Lowest Skill: {}", lowestSkill);
        return lowestSkill;
    }

    private int getSkillLevel(Skill skill) {
        return Microbot.getClient().getRealSkillLevel(skill);
    }

    private boolean isSkillControlled(AttackStyle attackStyle) {
        return attackStyle.getSkills().length > 1;
    }

    private WidgetInfo getComponentToDisplay(PlayerAssistConfig config) {
        List<WidgetInfo> componentsToDisplay = new ArrayList<>();
        AttackStyle[] attackStyles = getWeaponTypeStyles(equippedWeaponTypeVarbit);
        Random random = new Random();

        // Iterate over attack styles
        for (int i = 0; i < attackStyles.length; i++) {
            AttackStyle attackStyle = attackStyles[i];
            if (attackStyle == null) {
                continue;
            }

            boolean selectedSkill = false;
            for (Skill skill : attackStyle.getSkills()) {
                if (isSkillControlled(attackStyle) && config.toggleAvoidControlled()) {
                    continue;
                }
                if (selectedSkills.contains(skill)) {
                    log.info("Selected skill: {}", skill);
                    selectedSkill = true;
                    break;
                }
            }

            // Add appropriate combat option to the list
            if (selectedSkill) {
                attackStyleToTrain = attackStyle;
                switch (i) {
                    case 0:
                        componentsToDisplay.add(WidgetInfo.COMBAT_STYLE_ONE);
                        break;
                    case 1:
                        componentsToDisplay.add(WidgetInfo.COMBAT_STYLE_TWO);
                        break;
                    case 2:
                        componentsToDisplay.add(WidgetInfo.COMBAT_STYLE_THREE);
                        break;
                    case 3:
                        componentsToDisplay.add(WidgetInfo.COMBAT_STYLE_FOUR);
                        break;
                    case 4:
                        componentsToDisplay.add(WidgetInfo.COMBAT_SPELLS);
                        break;
                    case 5:
                        // Magic staves defensive casting mode
                        componentsToDisplay.add(WidgetInfo.COMBAT_DEFENSIVE_SPELL_BOX);
                        componentsToDisplay.add(WidgetInfo.COMBAT_DEFENSIVE_SPELL_ICON);
                        componentsToDisplay.add(WidgetInfo.COMBAT_DEFENSIVE_SPELL_SHIELD);
                        componentsToDisplay.add(WidgetInfo.COMBAT_DEFENSIVE_SPELL_TEXT);
                        break;
                }
            }
        }
        WidgetInfo componentToDisplay = componentsToDisplay.get(random.nextInt(componentsToDisplay.size()));
        attackStyleToTrain = attackStyles[componentToDisplay.ordinal() - 233];
        // Return a random component if the list is not empty, otherwise return null
        return componentToDisplay;
    }

    private boolean isMaxed() {
        return getSkillLevel(Skill.ATTACK) == 99 && getSkillLevel(Skill.STRENGTH) == 99 && getSkillLevel(Skill.DEFENCE) == 99;
    }

    private boolean disableIfMaxed(boolean disable) {
        return isMaxed() && disable;
    }

    // shutdown
    public void shutdown() {
        currentAttackStyleChangeDelayCounter = 0;
        super.shutdown();

    }
}