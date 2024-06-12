package net.runelite.client.plugins.hoseaplugins.disablerendering;

import net.runelite.api.*;
import net.runelite.api.hooks.DrawCallbacks;

public class DisableRenderCallbacks implements DrawCallbacks
{

    @Override
    public void draw(Projection projection, Scene scene, Renderable renderable, int orientation, int x, int y, int z, long hash)
    {

    }

    @Override
    public void drawScenePaint(Scene scene, SceneTilePaint paint, int plane, int tileX, int tileZ)
    {

    }

    @Override
    public void drawSceneTileModel(Scene scene, SceneTileModel model, int tileX, int tileZ)
    {

    }

    @Override
    public void draw(int overlayColor)
    {

    }

    @Override
    public void drawScene(double cameraX, double cameraY, double cameraZ, double cameraPitch, double cameraYaw, int plane)
    {

    }

    @Override
    public void postDrawScene()
    {

    }

    @Override
    public void animate(Texture texture, int diff)
    {

    }

    @Override
    public void loadScene(Scene scene)
    {

    }

    @Override
    public void swapScene(Scene scene)
    {

    }

    @Override
    public boolean tileInFrustum(Scene scene, int pitchSin, int pitchCos, int yawSin, int yawCos, int cameraX, int cameraY, int cameraZ, int plane, int msx, int msy)
    {
        return false;
    }
}
