package net.runelite.client.plugins.hoseaplugins.lucidcustomprayers;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.image.BufferedImage;

public class DummyNPC implements NPC
{
    @Override
    public int getId()
    {
        return -1;
    }

    @Override
    public String getName()
    {
        return null;
    }

    @Override
    public boolean isInteracting()
    {
        return false;
    }

    @Override
    public Actor getInteracting()
    {
        return null;
    }

    @Override
    public int getHealthRatio()
    {
        return 0;
    }

    @Override
    public int getHealthScale()
    {
        return 0;
    }

    @Override
    public WorldPoint getWorldLocation()
    {
        return null;
    }

    @Override
    public LocalPoint getLocalLocation()
    {
        return null;
    }

    @Override
    public int getOrientation()
    {
        return 0;
    }

    @Override
    public int getCurrentOrientation()
    {
        return 0;
    }

    @Override
    public int getAnimation()
    {
        return 0;
    }

    @Override
    public int getPoseAnimation()
    {
        return 0;
    }

    @Override
    public void setPoseAnimation(int animation)
    {

    }

    @Override
    public int getPoseAnimationFrame()
    {
        return 0;
    }

    @Override
    public void setPoseAnimationFrame(int frame)
    {

    }

    @Override
    public int getIdlePoseAnimation()
    {
        return 0;
    }

    @Override
    public void setIdlePoseAnimation(int animation)
    {

    }

    @Override
    public int getIdleRotateLeft()
    {
        return 0;
    }

    @Override
    public void setIdleRotateLeft(int animationID)
    {

    }

    @Override
    public int getIdleRotateRight()
    {
        return 0;
    }

    @Override
    public void setIdleRotateRight(int animationID)
    {

    }

    @Override
    public int getWalkAnimation()
    {
        return 0;
    }

    @Override
    public void setWalkAnimation(int animationID)
    {

    }

    @Override
    public int getWalkRotateLeft()
    {
        return 0;
    }

    @Override
    public void setWalkRotateLeft(int animationID)
    {

    }

    @Override
    public int getWalkRotateRight()
    {
        return 0;
    }

    @Override
    public void setWalkRotateRight(int animationID)
    {

    }

    @Override
    public int getWalkRotate180()
    {
        return 0;
    }

    @Override
    public void setWalkRotate180(int animationID)
    {

    }

    @Override
    public int getRunAnimation()
    {
        return 0;
    }

    @Override
    public void setRunAnimation(int animationID)
    {

    }

    @Override
    public void setAnimation(int animation)
    {

    }

    @Override
    public int getAnimationFrame()
    {
        return 0;
    }

    @Override
    public void setActionFrame(int frame)
    {

    }

    @Override
    public void setAnimationFrame(int frame)
    {

    }

    @Override
    public IterableHashTable<ActorSpotAnim> getSpotAnims()
    {
        return null;
    }

    @Override
    public boolean hasSpotAnim(int spotAnimId)
    {
        return false;
    }

    @Override
    public void createSpotAnim(int id, int spotAnimId, int height, int delay)
    {

    }

    @Override
    public void removeSpotAnim(int id)
    {

    }

    @Override
    public void clearSpotAnims()
    {

    }

    @Override
    public int getGraphic()
    {
        return 0;
    }

    @Override
    public void setGraphic(int graphic)
    {

    }

    @Override
    public int getGraphicHeight()
    {
        return 0;
    }

    @Override
    public void setGraphicHeight(int height)
    {

    }

    @Override
    public int getSpotAnimFrame()
    {
        return 0;
    }

    @Override
    public void setSpotAnimFrame(int spotAnimFrame)
    {

    }

    @Override
    public Polygon getCanvasTilePoly()
    {
        return null;
    }

    @Nullable
    @Override
    public Point getCanvasTextLocation(Graphics2D graphics, String text, int zOffset)
    {
        return null;
    }

    @Override
    public Point getCanvasImageLocation(BufferedImage image, int zOffset)
    {
        return null;
    }

    @Override
    public Point getCanvasSpriteLocation(SpritePixels sprite, int zOffset)
    {
        return null;
    }

    @Override
    public Point getMinimapLocation()
    {
        return null;
    }

    @Override
    public int getLogicalHeight()
    {
        return 0;
    }

    @Override
    public Shape getConvexHull()
    {
        return null;
    }

    @Override
    public WorldArea getWorldArea()
    {
        return null;
    }

    @Override
    public String getOverheadText()
    {
        return null;
    }

    @Override
    public void setOverheadText(String overheadText)
    {

    }

    @Override
    public int getOverheadCycle()
    {
        return 0;
    }

    @Override
    public void setOverheadCycle(int cycles)
    {

    }

    @Override
    public boolean isDead()
    {
        return false;
    }

    @Override
    public void setDead(boolean dead)
    {

    }

    @Override
    public WorldView getWorldView()
    {
        return null;
    }

    @Override
    public int getCombatLevel()
    {
        return 0;
    }

    @Override
    public int getIndex()
    {
        return 0;
    }

    @Override
    public NPCComposition getComposition()
    {
        return null;
    }

    @Nullable
    @Override
    public NPCComposition getTransformedComposition()
    {
        return null;
    }

    @Nullable
    @Override
    public NpcOverrides getModelOverrides()
    {
        return null;
    }

    @Nullable
    @Override
    public NpcOverrides getChatheadOverrides()
    {
        return null;
    }

    @Override
    public Model getModel()
    {
        return null;
    }

    @Override
    public int getModelHeight()
    {
        return 0;
    }

    @Override
    public void setModelHeight(int modelHeight)
    {

    }

    @Override
    public Node getNext()
    {
        return null;
    }

    @Override
    public Node getPrevious()
    {
        return null;
    }

    @Override
    public long getHash()
    {
        return 0;
    }
}
