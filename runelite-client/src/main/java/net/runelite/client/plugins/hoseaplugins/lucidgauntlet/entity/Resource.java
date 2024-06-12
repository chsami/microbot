package net.runelite.client.plugins.hoseaplugins.lucidgauntlet.entity;

import lombok.Getter;
import net.runelite.api.GameObject;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;

public class Resource
{
    @Getter
    private final GameObject gameObject;

    private final BufferedImage originalIcon;

    private BufferedImage icon;

    private int iconSize;

    public Resource(final GameObject gameObject, final SkillIconManager skillIconManager, final int iconSize)
    {
        this.gameObject = gameObject;
        this.iconSize = iconSize;

        this.originalIcon = getOriginalIcon(skillIconManager, gameObject.getId());
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

    private static BufferedImage getOriginalIcon(final SkillIconManager skillIconManager, final int objectId)
    {
        switch (objectId)
        {
            case ObjectID.CRYSTAL_DEPOSIT:
            case ObjectID.CORRUPT_DEPOSIT:
                return skillIconManager.getSkillImage(Skill.MINING);
            case ObjectID.PHREN_ROOTS:
            case ObjectID.CORRUPT_PHREN_ROOTS:
                return skillIconManager.getSkillImage(Skill.WOODCUTTING);
            case ObjectID.FISHING_SPOT_36068:
            case ObjectID.CORRUPT_FISHING_SPOT:
                return skillIconManager.getSkillImage(Skill.FISHING);
            case ObjectID.GRYM_ROOT:
            case ObjectID.CORRUPT_GRYM_ROOT:
                return skillIconManager.getSkillImage(Skill.HERBLORE);
            case ObjectID.LINUM_TIRINUM:
            case ObjectID.CORRUPT_LINUM_TIRINUM:
                return skillIconManager.getSkillImage(Skill.FARMING);
            default:
                throw new IllegalArgumentException("Unsupported gauntlet resource gameobject id: " + objectId);
        }
    }
}
