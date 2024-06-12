package net.runelite.client.plugins.hoseaplugins.disablerendering;

import net.runelite.api.Client;
import net.runelite.api.hooks.DrawCallbacks;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import javax.inject.Singleton;

@PluginDescriptor(
        name = "<html><font color=\"#32CD32\">Lucid </font>Disable Rendering</html>",
        description = "Disabled rendering graphics on the client while plugin is enabled.",
        enabledByDefault = false,
        tags = {"rendering", "performance", "disable", "freeze"}
)
@Singleton
public class LucidDisableRenderingPlugin extends Plugin
{

    @Inject
    private Client client;

    private DrawCallbacks originalDrawCallbacks;

    @Override
    protected void startUp()
    {
        if (client != null)
        {
            originalDrawCallbacks = client.getDrawCallbacks();
            client.setDrawCallbacks(new DisableRenderCallbacks());
        }
    }

    @Override
    protected void shutDown()
    {
        client.setDrawCallbacks(originalDrawCallbacks);
        originalDrawCallbacks = null;
    }
}