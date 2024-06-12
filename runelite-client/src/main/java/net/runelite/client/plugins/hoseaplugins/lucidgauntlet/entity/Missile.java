package net.runelite.client.plugins.hoseaplugins.lucidgauntlet.entity;

import net.runelite.client.plugins.hoseaplugins.lucidgauntlet.ProjectileID;
import lombok.Getter;
import net.runelite.api.Projectile;
import net.runelite.api.Skill;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.util.ImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Missile
{
    private static final int FILL_ALPHA = 100;

    @Getter
    private final Projectile projectile;

    private final BufferedImage originalIcon;

    @Getter
    private final Color outlineColor;
    @Getter
    private final Color fillColor;

    private BufferedImage icon;

    private int iconSize;

    public Missile(final Projectile projectile, final SkillIconManager skillIconManager, final int iconSize)
    {
        this.projectile = projectile;
        this.iconSize = iconSize;

        this.originalIcon = getOriginalIcon(skillIconManager, projectile.getId());

        this.outlineColor = getOutlineColor(projectile.getId());
        this.fillColor = new Color(outlineColor.getRed(), outlineColor.getGreen(), outlineColor.getBlue(), FILL_ALPHA);
    }

    public void setIconSize(final int iconSize)
    {
        this.iconSize = iconSize;
        icon = ImageUtil.resizeImage(originalIcon, iconSize, iconSize);
    }

    public BufferedImage getIcon()
    {
        if (icon == null)
        {
            icon = ImageUtil.resizeImage(originalIcon, iconSize, iconSize);
        }

        return icon;
    }

    private static Color getOutlineColor(final int projectileId)
    {
        switch (projectileId)
        {
            case ProjectileID.HUNLLEF_MAGE_ATTACK:
            case ProjectileID.HUNLLEF_CORRUPTED_MAGE_ATTACK:
                return Color.CYAN;
            case ProjectileID.HUNLLEF_RANGE_ATTACK:
            case ProjectileID.HUNLLEF_CORRUPTED_RANGE_ATTACK:
                return Color.GREEN;
            case ProjectileID.HUNLLEF_PRAYER_ATTACK:
            case ProjectileID.HUNLLEF_CORRUPTED_PRAYER_ATTACK:
                return Color.MAGENTA;
            default:
                throw new IllegalArgumentException("Unsupported gauntlet projectile id: " + projectileId);
        }
    }

    private static BufferedImage getOriginalIcon(final SkillIconManager skillIconManager, final int projectileId)
    {
        switch (projectileId)
        {
            case ProjectileID.HUNLLEF_MAGE_ATTACK:
            case ProjectileID.HUNLLEF_CORRUPTED_MAGE_ATTACK:
                return skillIconManager.getSkillImage(Skill.MAGIC);
            case ProjectileID.HUNLLEF_RANGE_ATTACK:
            case ProjectileID.HUNLLEF_CORRUPTED_RANGE_ATTACK:
                return skillIconManager.getSkillImage(Skill.RANGED);
            case ProjectileID.HUNLLEF_PRAYER_ATTACK:
            case ProjectileID.HUNLLEF_CORRUPTED_PRAYER_ATTACK:
                return skillIconManager.getSkillImage(Skill.PRAYER);
            default:
                throw new IllegalArgumentException("Unsupported gauntlet projectile id: " + projectileId);
        }
    }
}
