package net.runelite.client.plugins.hoseaplugins.lucidgauntlet.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.NPC;
import net.runelite.api.Prayer;
import net.runelite.api.Skill;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.util.ImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Hunllef
{
    private static final int ATTACK_TICK_SPEED = 6;

    private static final int MAX_ATTACK_COUNT = 4;
    private static final int MAX_PLAYER_ATTACK_COUNT = 6;

    @Getter
    private final NPC npc;

    private final BufferedImage originalMagicIcon;
    private final BufferedImage originalRangeIcon;

    private BufferedImage magicIcon;
    private BufferedImage rangeIcon;

    @Getter
    private AttackPhase attackPhase;

    @Getter
    private int attackCount;
    @Getter
    private int playerAttackCount;
    @Getter
    private int ticksUntilNextAttack;

    private int iconSize;

    public Hunllef(final NPC npc, final SkillIconManager skillIconManager, final int iconSize)
    {
        this.npc = npc;

        this.originalMagicIcon = skillIconManager.getSkillImage(Skill.MAGIC);
        this.originalRangeIcon = skillIconManager.getSkillImage(Skill.RANGED);
        this.iconSize = iconSize;

        this.attackCount = MAX_ATTACK_COUNT;
        this.playerAttackCount = MAX_PLAYER_ATTACK_COUNT;
        this.ticksUntilNextAttack = 0;

        this.attackPhase = AttackPhase.RANGE;
    }

    public void decrementTicksUntilNextAttack()
    {
        if (ticksUntilNextAttack > 0)
        {
            ticksUntilNextAttack--;
        }
    }

    public void updatePlayerAttackCount()
    {
        if (--playerAttackCount <= 0)
        {
            playerAttackCount = MAX_PLAYER_ATTACK_COUNT;
        }
    }

    public void updateAttackCount()
    {
        ticksUntilNextAttack = ATTACK_TICK_SPEED;

        if (--attackCount <= 0)
        {

            attackCount = MAX_ATTACK_COUNT;
        }
    }

    public void toggleAttackHunllefAttackStyle()
    {
        attackPhase = attackPhase == AttackPhase.RANGE ? AttackPhase.MAGIC : AttackPhase.RANGE;
    }

    public void setIconSize(final int iconSize)
    {
        this.iconSize = iconSize;
        magicIcon = ImageUtil.resizeImage(originalMagicIcon, iconSize, iconSize);
        rangeIcon = ImageUtil.resizeImage(originalRangeIcon, iconSize, iconSize);
    }

    public BufferedImage getIcon()
    {
        switch (attackPhase)
        {
            case MAGIC:
                if (magicIcon == null)
                {
                    magicIcon = ImageUtil.resizeImage(originalMagicIcon, iconSize, iconSize);
                }

                return magicIcon;
            case RANGE:
                if (rangeIcon == null)
                {
                    rangeIcon = ImageUtil.resizeImage(originalRangeIcon, iconSize, iconSize);
                }

                return rangeIcon;
            default:
                throw new IllegalStateException("Unexpected boss attack phase: " + attackPhase);
        }
    }

    @AllArgsConstructor
    @Getter
    public enum AttackPhase
    {
        MAGIC(Color.CYAN, Prayer.PROTECT_FROM_MAGIC),
        RANGE(Color.GREEN, Prayer.PROTECT_FROM_MISSILES);

        private final Color color;
        private final Prayer prayer;
    }
}
